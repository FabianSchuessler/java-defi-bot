package de.fs92.defi.compounddai;

import de.fs92.defi.contractneedsprovider.CircuitBreaker;
import de.fs92.defi.contractneedsprovider.ContractNeedsProvider;
import de.fs92.defi.contractneedsprovider.Permissions;
import de.fs92.defi.gasprovider.GasProvider;
import de.fs92.defi.medianizer.MedianException;
import de.fs92.defi.medianizer.Medianizer;
import de.fs92.defi.util.Balances;
import de.fs92.defi.util.BigNumberUtil;
import de.fs92.defi.util.IContract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import static de.fs92.defi.util.BigNumberUtil.multiply;

public class CompoundDai implements IContract {
  public static final String ADDRESS = "0x5d3a536E4D6DbD6114cc1Ead35777bAB948E3643";
  public static final BigInteger gasLimit =
      BigInteger.valueOf(200000); // https://compound.finance/developers#gas-costs
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
  private static final BigInteger secondsPerYear = BigInteger.valueOf(31557600);
  private static final BigInteger timeBetweenBlocks = BigInteger.valueOf(15);
  private static final BigInteger supplyRatePerYearMultiplicand =
      secondsPerYear.divide(timeBetweenBlocks);
  private static final String EXCEPTION = "Exception";
  private final CompoundDaiContract contract;
  private final GasProvider gasProvider;
  private final Permissions permissions;

  public CompoundDai(@NotNull ContractNeedsProvider contractNeedsProvider) {
    Web3j web3j = contractNeedsProvider.getWeb3j();
    Credentials credentials = contractNeedsProvider.getCredentials();
    gasProvider = contractNeedsProvider.getGasProvider();
    permissions = contractNeedsProvider.getPermissions();
    contract = CompoundDaiContract.load(ADDRESS, web3j, credentials, gasProvider);
    isContractValid();
  }

  void isContractValid() {
    try {
      contract.isValid();
      logger.trace("COMPOUND DAI CONTRACT IS VALID");
    } catch (IOException e) {
      CircuitBreaker.stopRunning();
      logger.error(EXCEPTION, e);
    }
  }

  public void mint(@NotNull Balances balances, BigDecimal medianEthereumPrice) {
    BigInteger mintAmount = balances.getDaiBalance().toBigInteger();
    if (permissions.check(
        "COMPOUND DAI MINT " + BigNumberUtil.makeBigNumberHumanReadableFullPrecision(mintAmount))) {
      try {
        gasProvider.updateSlowGasPrice();
        logger.debug(
            "MINT DAI {}", BigNumberUtil.makeBigNumberHumanReadableFullPrecision(mintAmount));
        TransactionReceipt transferReceipt = contract.mint(mintAmount).send();
        afterTransaction(balances, medianEthereumPrice, transferReceipt);
      } catch (Exception e) {
        logger.error(EXCEPTION, e);
      }
    }
  }

  public void redeemAll(
      Balances balances, BigDecimal potentialProfit, BigDecimal medianEthereumPrice) {
    logger.debug("REDEEM ALL");
    redeem(balances, potentialProfit, medianEthereumPrice);
  }

  public void redeem(
      @NotNull Balances balances, BigDecimal potentialProfit, BigDecimal medianEthereumPrice) {
    BigInteger redeemAmount = balances.getCdaiBalance();
    if (permissions.check(
        "COMPOUND DAI REDEEM "
            + BigNumberUtil.makeBigNumberHumanReadableFullPrecision(redeemAmount, 8)
            + " CDAI")) {
      try {
        gasProvider.updateFastGasPrice(medianEthereumPrice, potentialProfit);
        logger.debug(
            "REDEEM CDAI {}",
            BigNumberUtil.makeBigNumberHumanReadableFullPrecision(redeemAmount, 8));
        TransactionReceipt transferReceipt = contract.redeem(redeemAmount).send();
        afterTransaction(balances, medianEthereumPrice, transferReceipt);
      } catch (Exception e) {
        logger.error(EXCEPTION, e);
      }
    }
  }

  private void afterTransaction(
      @NotNull Balances balances,
      BigDecimal medianEthereumPrice,
      TransactionReceipt transferReceipt)
      throws InterruptedException {
    TimeUnit.SECONDS.sleep(1);
    balances.updateBalanceInformation(medianEthereumPrice);
    logger.info(
        "Transaction complete, view it at https://etherscan.io/tx/{}",
        transferReceipt.getTransactionHash());
  }

  void borrow(BigInteger borrowAmount) {
    if (permissions.check(
        "COMPOUND DAI BORROW "
            + BigNumberUtil.makeBigNumberHumanReadableFullPrecision(borrowAmount))) {
      try {
        contract.borrow(borrowAmount).send();
        logger.debug(
            "BORROW DAI {}", BigNumberUtil.makeBigNumberHumanReadableFullPrecision(borrowAmount));
      } catch (Exception e) {
        logger.error(EXCEPTION, e);
      }
    }
  }

  void repayBorrow(BigInteger repayAmount) {
    if (permissions.check(
        "COMPOUND DAI REPAY "
            + BigNumberUtil.makeBigNumberHumanReadableFullPrecision(repayAmount))) {
      try {
        contract.repayBorrow(repayAmount).send();
        logger.debug(
            "REPAY DAI {}", BigNumberUtil.makeBigNumberHumanReadableFullPrecision(repayAmount));
      } catch (Exception e) {
        logger.error(EXCEPTION, e);
      }
    }
  }

  private BigInteger getExchangeRate() {
    try {
      return contract.exchangeRateStored().send();
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
    }
    return BigInteger.ZERO;
  }

  public BigInteger getCDaiBalance(String ethereumAddress) {
    try {
      return contract.balanceOf(ethereumAddress).send();
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
    }
    return BigInteger.ZERO;
  }

  public BigDecimal getBalanceInDai(BigInteger cdai) {
    return new BigDecimal(multiply(getExchangeRate(), cdai));
  }

  // lend dai on compound to earn interest, if dai price is low
  public void lendDai(@NotNull Balances balances) {
    // do if transaction costs are lower than the interest of one day
    // example: 2 transactions * gas price * used gas * median eth price < dai *
    // compound_interest_rate / 365

    if (balances.checkEnoughDai()) {
      BigInteger slowGasPrice = gasProvider.updateSlowGasPrice();
      if (slowGasPrice.compareTo(BigInteger.ZERO) == 0) {
        return;
      }

      BigInteger medianEthereumPrice;
      try {
        medianEthereumPrice = Medianizer.getPrice().toBigInteger();
      } catch (MedianException e) {
        logger.error(EXCEPTION, e);
        return;
      }

      // 2 * 222.53 * 300,000 * 0.00000001 = 1.33518
      BigInteger transactionCosts =
          BigInteger.valueOf(2)
              .multiply(gasLimit)
              .multiply(multiply(slowGasPrice, medianEthereumPrice)); // in USD
      if (transactionCosts.compareTo(getPossibleDailyInterest(balances)) < 0) {
        logger.trace("SUFFICIENT INTEREST TO LEND DAI ON COMPOUND");

        if (System.currentTimeMillis()
            >= balances.getLastSuccessfulTransaction() + 60 * 60 * 1000) { // 60 minutes
          mint(balances, new BigDecimal(medianEthereumPrice));
        } else {
          logger.warn("NOT ENOUGH TIME PASSED SINCE LAST SUCCESSFUL TRANSACTION");
        }
      } else {
        logger.warn("NOT ENOUGH INTEREST TO LEND DAI ON COMPOUND");
      }
      logger.trace(
          "SLOW GAS PRICE {}{}",
          Convert.fromWei(slowGasPrice.toString(), Convert.Unit.GWEI),
          " GWEI");
      logger.trace(
          "TRANSACTION COSTS {}{}",
          BigNumberUtil.makeBigNumberCurrencyHumanReadable(transactionCosts),
          " DAI");
    }
    logger.info("NOT ENOUGH DAI TO LEND DAI ON COMPOUND");
  }

  private BigInteger getSupplyRate() {
    BigInteger supplyRate = BigInteger.ZERO;
    try {
      BigInteger supplyRatePerBlock = contract.supplyRatePerBlock().send();
      supplyRate = supplyRatePerYearMultiplicand.multiply(supplyRatePerBlock);
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
    }
    logger.info(
        "SUPPLY RATE {}{}",
        BigNumberUtil.makeBigNumberHumanReadableFullPrecision(supplyRate),
        " %");
    return supplyRate;
  }

  private BigInteger getCurrentDailyInterest(@NotNull Balances balances) {
    BigInteger daiSupplied = balances.getDaiInCompound().toBigInteger();
    return getDailyInterest(daiSupplied);
  }

  private BigInteger getPossibleDailyInterest(@NotNull Balances balances) {
    BigInteger dai = balances.getDaiBalance().toBigInteger();
    return getDailyInterest(dai);
  }

  private BigInteger getDailyInterest(BigInteger amount) {
    logger.info(
        "DAI OR SUPPLIED DAI BALANCE {}{}",
        BigNumberUtil.makeBigNumberHumanReadableFullPrecision(amount),
        " DAI");
    BigInteger dailyInterest = multiply(amount, getSupplyRate()).divide(BigInteger.valueOf(365));
    logger.info(
        "DAILY INTEREST {}{}",
        BigNumberUtil.makeBigNumberHumanReadableFullPrecision(dailyInterest),
        " DAI");
    return dailyInterest;
  }

  private boolean isAlternativeMoreProfitableThanLendingDai(
      Balances balances, BigInteger profitComparator, BigDecimal medianEthereumPrice) {
    BigInteger dailyInterest = getCurrentDailyInterest(balances);
    logger.info(
        "PROFIT COMPARATOR {}{}",
        BigNumberUtil.makeBigNumberHumanReadableFullPrecision(profitComparator),
        " DAI");
    if (profitComparator.compareTo(dailyInterest.add(balances.minimumTradeProfit.toBigInteger()))
        > 0) {
      logger.info("ALTERNATIVE IS MORE PROFITABLE");
      redeemAll(balances, new BigDecimal(profitComparator), medianEthereumPrice);
      return true;
    }
    logger.info("ALTERNATIVE IS LESS PROFITABLE");
    return false;
  }

  public boolean canOtherProfitMethodsWorkWithoutCDaiConversion(
      @NotNull Balances balances, BigDecimal profitComparator, BigDecimal medianEthereumPrice) {
    if (balances.getDaiInCompound().compareTo(BigDecimal.ZERO) == 0) {
      logger.info("CDAI CONVERSION NOT NECESSARY");
      return true;
    }
    return isAlternativeMoreProfitableThanLendingDai(
        balances, profitComparator.toBigInteger(), medianEthereumPrice);
  }

  public String getAddress() {
    return ADDRESS;
  }
}
