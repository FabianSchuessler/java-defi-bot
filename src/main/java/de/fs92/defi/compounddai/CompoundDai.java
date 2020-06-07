package de.fs92.defi.compounddai;

import de.fs92.defi.contractneedsprovider.ContractNeedsProvider;
import de.fs92.defi.contractneedsprovider.Permissions;
import de.fs92.defi.contractuserutil.AddressMethod;
import de.fs92.defi.contractutil.Account;
import de.fs92.defi.gasprovider.GasProvider;
import de.fs92.defi.medianizer.MedianException;
import de.fs92.defi.medianizer.Medianizer;
import de.fs92.defi.numberutil.Sth28;
import de.fs92.defi.numberutil.Wad18;
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

public class CompoundDai implements AddressMethod {
  public static final String ADDRESS = "0x5d3a536E4D6DbD6114cc1Ead35777bAB948E3643";
  public static final Wad18 gasLimit =
      new Wad18(BigInteger.valueOf(200000)); // https://compound.finance/developers#gas-costs
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
  private static final Wad18 secondsPerYear = new Wad18(BigInteger.valueOf(31557600));
  private static final Wad18 timeBetweenBlocks = new Wad18(BigInteger.valueOf(15));
  private static final int WAIT_TIME = 60 * 60 * 1000; // 60 minutes
  private static final Wad18 supplyRatePerYearMultiplicand =
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

  public void mint(@NotNull Balances balances, Wad18 medianEthereumPrice) {
    Wad18 mintAmount = balances.dai.getAccount().getBalance();
    if (permissions.check("COMPOUND DAI MINT " + mintAmount)) {
      try {
        gasProvider.updateSlowGasPrice();
        logger.debug("MINT DAI {}", mintAmount);
        TransactionReceipt transferReceipt =
            compoundDaiContract.mint(mintAmount.toBigInteger()).send();
        afterTransaction(balances, medianEthereumPrice, transferReceipt);
      } catch (Exception e) {
        logger.error(EXCEPTION, e);
      }
    }
  }

  public void redeemAll(Balances balances, Wad18 potentialProfit, Wad18 medianEthereumPrice) {
    logger.debug("REDEEM ALL");
    redeem(balances, potentialProfit, medianEthereumPrice);
  }

  public void redeem(@NotNull Balances balances, Wad18 potentialProfit, Wad18 medianEthereumPrice) {
    Wad18 redeemAmount = account.getBalance();
    if (permissions.check("COMPOUND DAI REDEEM " + redeemAmount + " CDAI")) {
      try {
        gasProvider.updateFastGasPrice(medianEthereumPrice, potentialProfit);
        logger.debug("REDEEM CDAI {}", redeemAmount);
        TransactionReceipt transferReceipt =
            compoundDaiContract.redeem(redeemAmount.toBigInteger()).send();
        afterTransaction(balances, medianEthereumPrice, transferReceipt);
      } catch (Exception e) {
        logger.error(EXCEPTION, e);
      }
    }
  }

  private void afterTransaction(
      @NotNull Balances balances, Wad18 medianEthereumPrice, TransactionReceipt transferReceipt)
      throws InterruptedException {
    TimeUnit.SECONDS.sleep(1);
    balances.updateBalanceInformation(medianEthereumPrice);
    logger.info(
        "Transaction complete, view it at https://etherscan.io/tx/{}",
        transferReceipt.getTransactionHash());
  }

  void borrow(Wad18 borrowAmount) {
    if (permissions.check("COMPOUND DAI BORROW " + borrowAmount)) {
      try {
        compoundDaiContract.borrow(borrowAmount.toBigInteger()).send();
        logger.debug("BORROW DAI {}", borrowAmount);
      } catch (Exception e) {
        logger.error(EXCEPTION, e);
      }
    }
  }

  void repayBorrow(Wad18 repayAmount) {
    if (permissions.check("COMPOUND DAI REPAY " + repayAmount)) {
      try {
        compoundDaiContract.repayBorrow(repayAmount.toBigInteger()).send();
        logger.debug("REPAY DAI {}", repayAmount);
      } catch (Exception e) {
        logger.error(EXCEPTION, e);
      }
    }
  }

  Sth28 getExchangeRate() {
    try {
      Sth28 exchangeRate = new Sth28(compoundDaiContract.exchangeRateStored().send());
      logger.trace("CURRENT CDAI EXCHANGE RATE {}", exchangeRate);
      return exchangeRate;
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
    }
    return Sth28.ZERO;
  }

  public Account getAccount() {
    return account;
  }

  public Wad18 getBalanceInDai() {
    Wad18 wad18 = account.getBalance().multiply(getExchangeRate());
    logger.trace("CDAI BALANCE IN DAI {}", wad18);
    return wad18;
  }

  /**
   * Lend DAI on Compound to earn interest, if DAI price is below peg. Execute if transaction costs
   * are lower than the interest of one day. Example: two transactions * gas price * used gas *
   * median eth price < dai * compound_interest_rate / 365
   *
   * @param balances provides access to all balances
   */
  public void lendDai(@NotNull Balances balances) {
    logger.trace("");
    logger.trace("CHECKING IF LENDING DAI IS PROFITABLE");
    if (balances.dai.isThereEnoughDaiForLending()) {
      Wad18 slowGasPrice = gasProvider.updateSlowGasPrice();
      if (slowGasPrice.compareTo(Wad18.ZERO) == 0) {
        return;
      }

      Wad18 medianEthereumPrice;
      try {
        medianEthereumPrice = Medianizer.getPrice();
      } catch (MedianException e) {
        logger.error(EXCEPTION, e);
        return;
      }

      // 2 * 222.53 * 300,000 * 0.00000001 = 1.33518
      Wad18 transactionCosts =
          new Wad18(BigInteger.TWO)
              .multiply(gasLimit)
              .multiply(slowGasPrice.multiply(medianEthereumPrice)); // in USD
      Wad18 possibleDailyInterest = getDailyInterest(balances.dai.getAccount().getBalance());
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
            transactionCosts,
            possibleDailyInterest);
      }
      logger.trace(
          "SLOW GAS PRICE {}{}",
          Convert.fromWei(slowGasPrice.toBigDecimal(), Convert.Unit.GWEI),
          " GWEI");
      logger.trace("TRANSACTION COSTS {}{}", transactionCosts.toString(2), " DAI");
    } else {
      logger.info("NOT ENOUGH DAI TO LEND DAI ON COMPOUND");
    }
  }

  private Wad18 getSupplyRate() {
    Wad18 supplyRate = Wad18.ZERO;
    try {
      Wad18 supplyRatePerBlock = new Wad18(compoundDaiContract.supplyRatePerBlock().send());
      supplyRate = supplyRatePerYearMultiplicand.multiply(supplyRatePerBlock);
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
    }
    logger.info("SUPPLY RATE {}{}", supplyRate, " %"); // TODO: test this function: Wad18 introduced a bug here
    return supplyRate;
  }

  private Wad18 getCurrentDailyInterest() {
    Wad18 daiSupplied = getBalanceInDai();
    return getDailyInterest(daiSupplied);
  }

  private Wad18 getDailyInterest(Wad18 amount) {
    logger.info("DAI OR SUPPLIED DAI BALANCE {}{}", amount, " DAI");
    Wad18 dailyInterest =
        amount.multiply(getSupplyRate()).divide(new Wad18(BigInteger.valueOf(365)));
    logger.info("DAILY INTEREST {}{}", dailyInterest, " DAI");
    return dailyInterest;
  }

  private boolean isAlternativeMoreProfitableThanLendingDai(
      Balances balances, Wad18 profitComparator, Wad18 medianEthereumPrice) {
    Wad18 dailyInterest = getCurrentDailyInterest();
    logger.info("PROFIT COMPARATOR {}{}", profitComparator, " DAI");
    if (profitComparator.compareTo(dailyInterest.add(balances.minimumTradeProfit)) > 0) {
      logger.info("ALTERNATIVE IS MORE PROFITABLE");
      redeemAll(balances, profitComparator, medianEthereumPrice);
      return true;
    }
    logger.info("ALTERNATIVE IS LESS PROFITABLE");
    return false;
  }

  public boolean canOtherProfitMethodsWorkWithoutCDaiConversion(
      @NotNull Balances balances, Wad18 profitComparator, Wad18 medianEthereumPrice) {
    if (getBalanceInDai().compareTo(Wad18.ZERO) == 0) {
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
