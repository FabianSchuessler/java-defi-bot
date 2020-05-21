package de.fs92.defi.compoundeth;

import de.fs92.defi.contractneedsprovider.ContractNeedsProvider;
import de.fs92.defi.contractneedsprovider.Permissions;
import de.fs92.defi.numberutil.Wad18;
import de.fs92.defi.util.Balances;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.math.BigInteger;

/**
 * @deprecated This class is currently unused. It can be used to borrow DAI against WETH, but
 *     currently MakerDAO is cheaper.
 */
@Deprecated(since = "0.0.1", forRemoval = false)
public class CompoundEth {
  public static final String ADDRESS = "0x4Ddc2D193948926D02f9B1fE9e1daa0718270ED5";
  public static final BigInteger gasLimit =
      BigInteger.valueOf(100000); // https://compound.finance/developers#gas-costs
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
  private static final String EXCEPTION = "Exception";
  private static CompoundEthContract contract;
  private final Permissions permissions;

  public CompoundEth(@NotNull ContractNeedsProvider contractNeedsProvider) {
    this.permissions = contractNeedsProvider.getPermissions();
  }

  static Wad18 getExchangeRate() {
    try {
      return new Wad18(contract.exchangeRateStored().send());
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
    }
    return Wad18.ZERO;
  }

  void borrow(Wad18 borrowAmount) {
    if (permissions.check("COMPOUND ETH BORROW " + borrowAmount)) {
      try {
        contract.borrow(borrowAmount.toBigInteger()).send();
        logger.warn("BORROW ETH {}", borrowAmount);
      } catch (Exception e) {
        logger.error(EXCEPTION, e);
      }
    }
  }

  void repayBorrow(Wad18 repayAmount) {
    if (permissions.check("COMPOUND ETH REPAY " + repayAmount)) {
      try {
        contract.repayBorrow(repayAmount.toBigInteger()).send();
        logger.warn("REPAY ETH {}", repayAmount);
      } catch (Exception e) {
        logger.error(EXCEPTION, e);
      }
    }
  }

  Wad18 getCTokenBalance(String ethereumAddress) {
    try {
      return new Wad18(contract.balanceOf(ethereumAddress).send());
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
    }
    return Wad18.ZERO;
  }

  Wad18 getBalance(String ethereumAddress) {
    return getExchangeRate().multiply(getCTokenBalance(ethereumAddress));
  }

  // borrow dai against weth and sell it, if dai price is high
  void checkBorrowDaiOpportunity(@NotNull Balances balances) {
    if (balances.isThereTooFewEthAndWethForSaleAndLending(balances.ethereum)) {
      logger.warn("NOT ENOUGH ETH OR WETH TO BORROW DAI ON COMPOUND");
    }
  }
}
