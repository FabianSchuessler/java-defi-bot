package de.fs92.defi.compounddai;

import de.fs92.defi.contractneedsprovider.ContractNeedsProvider;
import de.fs92.defi.contractneedsprovider.Permissions;
import de.fs92.defi.contractuserutil.AddressMethod;
import de.fs92.defi.contractutil.Account;
import de.fs92.defi.gasprovider.GasProvider;
import de.fs92.defi.medianizer.MedianException;
import de.fs92.defi.medianizer.Medianizer;
import de.fs92.defi.util.Balances;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;

import java.lang.invoke.MethodHandles;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import static de.fs92.defi.util.NumberUtil.*;

public class CompoundDai implements AddressMethod {
  public static final String ADDRESS = "0x5d3a536E4D6DbD6114cc1Ead35777bAB948E3643";
  public static final BigInteger gasLimit =
      BigInteger.valueOf(200000); // https://compound.finance/developers#gas-costs
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
  private static final BigInteger secondsPerYear = BigInteger.valueOf(31557600);
  private static final BigInteger timeBetweenBlocks = BigInteger.valueOf(15);
  private static final int WAIT_TIME = 60 * 60 * 1000; // 60 minutes
  private static final BigInteger supplyRatePerYearMultiplicand =
      secondsPerYear.divide(timeBetweenBlocks);
  private static final String EXCEPTION = "Exception";
  private final CompoundDaiContract compoundDaiContract;
  private final GasProvider gasProvider;
  private final Permissions permissions;
  private final Credentials credentials;
  private final Account account;

  public CompoundDai(@NotNull ContractNeedsProvider contractNeedsProvider) {
    Web3j web3j = contractNeedsProvider.getWeb3j();
    credentials = contractNeedsProvider.getCredentials();
    gasProvider = contractNeedsProvider.getGasProvider();
    permissions = contractNeedsProvider.getPermissions();
    compoundDaiContract = CompoundDaiContract.load(ADDRESS, web3j, credentials, gasProvider);
    account = new Account(compoundDaiContract, credentials, "CDAI");
  }

  public void mint(@NotNull Balances balances, BigInteger medianEthereumPrice) {
    BigInteger mintAmount = balances.dai.getAccount().getBalance();
    if (permissions.check("COMPOUND DAI MINT " + getFullPrecision(mintAmount))) {
      try {
        gasProvider.updateSlowGasPrice();
        logger.debug("MINT DAI {}", getFullPrecision(mintAmount));
        TransactionReceipt transferReceipt = compoundDaiContract.mint(mintAmount).send();
        afterTransaction(balances, medianEthereumPrice, transferReceipt);
      } catch (Exception e) {
        logger.error(EXCEPTION, e);
      }
    }
  }

  public void redeemAll(
      Balances balances, BigInteger potentialProfit, BigInteger medianEthereumPrice) {
    logger.debug("REDEEM ALL");
    redeem(balances, potentialProfit, medianEthereumPrice);
  }

  public void redeem(
      @NotNull Balances balances, BigInteger potentialProfit, BigInteger medianEthereumPrice) {
    BigInteger redeemAmount = account.getBalance();
    if (permissions.check("COMPOUND DAI REDEEM " + getFullPrecision(redeemAmount, 8) + " CDAI")) {
      try {
        gasProvider.updateFastGasPrice(medianEthereumPrice, potentialProfit);
        logger.debug("REDEEM CDAI {}", getFullPrecision(redeemAmount, 8));
        TransactionReceipt transferReceipt = compoundDaiContract.redeem(redeemAmount).send();
        afterTransaction(balances, medianEthereumPrice, transferReceipt);
      } catch (Exception e) {
        logger.error(EXCEPTION, e);
      }
    }
  }

  private void afterTransaction(
      @NotNull Balances balances,
      BigInteger medianEthereumPrice,
      TransactionReceipt transferReceipt)
      throws InterruptedException {
    TimeUnit.SECONDS.sleep(1);
    balances.updateBalanceInformation(medianEthereumPrice);
    logger.info(
        "Transaction complete, view it at https://etherscan.io/tx/{}",
        transferReceipt.getTransactionHash());
  }

  void borrow(BigInteger borrowAmount) {
    if (permissions.check("COMPOUND DAI BORROW " + getFullPrecision(borrowAmount))) {
      try {
        compoundDaiContract.borrow(borrowAmount).send();
        logger.debug("BORROW DAI {}", getFullPrecision(borrowAmount));
      } catch (Exception e) {
        logger.error(EXCEPTION, e);
      }
    }
  }

  void repayBorrow(BigInteger repayAmount) {
    if (permissions.check("COMPOUND DAI REPAY " + getFullPrecision(repayAmount))) {
      try {
        compoundDaiContract.repayBorrow(repayAmount).send();
        logger.debug("REPAY DAI {}", getFullPrecision(repayAmount));
      } catch (Exception e) {
        logger.error(EXCEPTION, e);
      }
    }
  }

  private BigInteger getExchangeRate() {
    try {
      return compoundDaiContract.exchangeRateStored().send();
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
    }
    return BigInteger.ZERO;
  }

  public Account getAccount() {
    return account;
  }

  public BigInteger getBalanceInDai() {
    return multiply(getExchangeRate(), account.getBalance());
  }

  /**
   * Lend DAI on Compound to earn interest, if DAI price is below peg. Execute if transaction costs
   * are lower than the interest of one day. Example: two transactions * gas price * used gas *
   * median eth price < dai * compound_interest_rate / 365
   *
   * @param balances provides access to all balances
   */
  public void lendDai(@NotNull Balances balances) {
    if (balances.dai.isThereEnoughDaiForLending()) {
      BigInteger slowGasPrice = gasProvider.updateSlowGasPrice();
      if (slowGasPrice.compareTo(BigInteger.ZERO) == 0) {
        return;
      }

      BigInteger medianEthereumPrice;
      try {
        medianEthereumPrice = Medianizer.getPrice();
      } catch (MedianException e) {
        logger.error(EXCEPTION, e);
        return;
      }

      // 2 * 222.53 * 300,000 * 0.00000001 = 1.33518
      BigInteger transactionCosts =
          BigInteger.valueOf(2)
              .multiply(gasLimit)
              .multiply(multiply(slowGasPrice, medianEthereumPrice)); // in USD
      BigInteger possibleDailyInterest = getDailyInterest(balances.dai.getAccount().getBalance());
      if (transactionCosts.compareTo(possibleDailyInterest) < 0) {
        logger.trace("SUFFICIENT INTEREST TO LEND DAI ON COMPOUND");

        if (System.currentTimeMillis() >= balances.getLastSuccessfulTransaction() + WAIT_TIME) {
          mint(balances, medianEthereumPrice);
        } else {
          logger.warn(
              "CURRENT CODE REQUIRES {} MINUTES BETWEEN LAST SUCCESSFUL TRANSACTION AND MINTING CDAI",
              WAIT_TIME);
        }
      } else {
        logger.warn(
            "CURRENT CODE REQUIRES THAT THE TRANSACTION COSTS {} ARE LOWER THAN THE DAILY INTEREST {}",
            getFullPrecision(transactionCosts),
            getFullPrecision(possibleDailyInterest));
      }
      logger.trace(
          "SLOW GAS PRICE {}{}",
          Convert.fromWei(slowGasPrice.toString(), Convert.Unit.GWEI),
          " GWEI");
      logger.trace("TRANSACTION COSTS {}{}", getCurrency(transactionCosts), " DAI");
    } else {
      logger.info("NOT ENOUGH DAI TO LEND DAI ON COMPOUND");
    }
  }

  private BigInteger getSupplyRate() {
    BigInteger supplyRate = BigInteger.ZERO;
    try {
      BigInteger supplyRatePerBlock = compoundDaiContract.supplyRatePerBlock().send();
      supplyRate = supplyRatePerYearMultiplicand.multiply(supplyRatePerBlock);
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
    }
    logger.info("SUPPLY RATE {}{}", getFullPrecision(supplyRate), " %");
    return supplyRate;
  }

  private BigInteger getCurrentDailyInterest() {
    BigInteger daiSupplied = getBalanceInDai();
    return getDailyInterest(daiSupplied);
  }

  private BigInteger getDailyInterest(BigInteger amount) {
    logger.info("DAI OR SUPPLIED DAI BALANCE {}{}", getFullPrecision(amount), " DAI");
    BigInteger dailyInterest = multiply(amount, getSupplyRate()).divide(BigInteger.valueOf(365));
    logger.info("DAILY INTEREST {}{}", getFullPrecision(dailyInterest), " DAI");
    return dailyInterest;
  }

  private boolean isAlternativeMoreProfitableThanLendingDai(
      Balances balances, BigInteger profitComparator, BigInteger medianEthereumPrice) {
    BigInteger dailyInterest = getCurrentDailyInterest();
    logger.info("PROFIT COMPARATOR {}{}", getFullPrecision(profitComparator), " DAI");
    if (profitComparator.compareTo(dailyInterest.add(balances.minimumTradeProfit)) > 0) {
      logger.info("ALTERNATIVE IS MORE PROFITABLE");
      redeemAll(balances, profitComparator, medianEthereumPrice);
      return true;
    }
    logger.info("ALTERNATIVE IS LESS PROFITABLE");
    return false;
  }

  public boolean canOtherProfitMethodsWorkWithoutCDaiConversion(
      @NotNull Balances balances, BigInteger profitComparator, BigInteger medianEthereumPrice) {
    if (getBalanceInDai().compareTo(BigInteger.ZERO) == 0) {
      logger.info("CDAI CONVERSION NOT NECESSARY");
      return true;
    }
    return isAlternativeMoreProfitableThanLendingDai(
        balances, profitComparator, medianEthereumPrice);
  }

  public String getAddress() {
    return ADDRESS;
  }
}
