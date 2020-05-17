package de.fs92.defi.compoundeth;

import de.fs92.defi.contractneedsprovider.ContractNeedsProvider;
import de.fs92.defi.contractneedsprovider.Permissions;
import de.fs92.defi.util.Balances;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.math.BigInteger;

import static de.fs92.defi.util.NumberUtil.getFullPrecision;
import static de.fs92.defi.util.NumberUtil.multiply;

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

  static BigInteger getExchangeRate() {
    try {
      return contract.exchangeRateStored().send();
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
    }
    return BigInteger.ZERO;
  }

  void borrow(BigInteger borrowAmount) {
    if (permissions.check("COMPOUND ETH BORROW " + getFullPrecision(borrowAmount))) {
      try {
        contract.borrow(borrowAmount).send();
        logger.warn("BORROW ETH {}", getFullPrecision(borrowAmount));
      } catch (Exception e) {
        logger.error(EXCEPTION, e);
      }
    }
  }

  void repayBorrow(BigInteger repayAmount) {
    if (permissions.check("COMPOUND ETH REPAY " + getFullPrecision(repayAmount))) {
      try {
        contract.repayBorrow(repayAmount).send();
        logger.warn("REPAY ETH {}", getFullPrecision(repayAmount));
      } catch (Exception e) {
        logger.error(EXCEPTION, e);
      }
    }
  }

  BigInteger getCTokenBalance(String ethereumAddress) {
    try {
      return contract.balanceOf(ethereumAddress).send();
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
    }
    return BigInteger.ZERO;
  }

  BigInteger getBalance(String ethereumAddress) {
    return multiply(getExchangeRate(), getCTokenBalance(ethereumAddress));
  }

  // borrow dai against weth and sell it, if dai price is high
  void checkBorrowDaiOpportunity(@NotNull Balances balances) {
    if (balances.isThereTooFewEthAndWethForSaleAndLending(balances.ethereum)) {
      logger.warn("NOT ENOUGH ETH OR WETH TO BORROW DAI ON COMPOUND");
    }
  }
}
