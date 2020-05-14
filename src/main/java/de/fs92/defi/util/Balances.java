package de.fs92.defi.util;

import de.fs92.defi.Main;
import de.fs92.defi.compounddai.CompoundDai;
import de.fs92.defi.dai.Dai;
import de.fs92.defi.medianizer.MedianException;
import de.fs92.defi.medianizer.Medianizer;
import de.fs92.defi.weth.Weth;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import static de.fs92.defi.util.NumberUtil.*;

public class Balances {
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

  // contracts
  public final Dai dai;
  public final Weth weth;
  public final CompoundDai compoundDai;
  public final Ethereum ethereum;

  public final BigInteger minimumTradeProfit;
  private BigInteger minimumTradeProfitBuyDai;
  private BigInteger minimumTradeProfitSellDai;

  // profit and loss calculation
  private BigInteger usd;
  private BigInteger initialTotalUSD;
  private BigInteger sumEstimatedProfits;
  private BigInteger sumEstimatedMissedProfits;
  private int initialTotalUSDCounter;

  // update balances
  private long pastTimeBalances;

  // lend dai
  private long lastSuccessfulTransaction;

  // current ownership ratio
  private double totalEthRatio;
  private double totalDaiRatio;
  private long pastTime;

  public Balances(Dai dai, Weth weth, CompoundDai compoundDai, Ethereum ethereum) {
    usd = BigInteger.ZERO;
    sumEstimatedProfits = BigInteger.ZERO;
    sumEstimatedMissedProfits = BigInteger.ZERO;
    pastTime = System.currentTimeMillis();
    totalDaiRatio = 0.0;
    totalEthRatio = 0.0;

    minimumTradeProfit = getMachineReadable(1.0);
    minimumTradeProfitBuyDai = getMachineReadable(1.0);
    minimumTradeProfitSellDai = getMachineReadable(1.0);

    initialTotalUSD = BigInteger.ZERO;
    initialTotalUSDCounter = 0;
    lastSuccessfulTransaction = System.currentTimeMillis();

    this.dai = dai;
    this.weth = weth;
    this.compoundDai = compoundDai;
    this.ethereum = ethereum;
  }

  public static double round(double value, int places) {
    if (places < 0) throw new IllegalArgumentException();

    BigDecimal bd = BigDecimal.valueOf(value);
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }

  public void updateBalanceInformation(BigInteger medianEthereumPrice) {
    // Changes minimum trade profit depending on the time of the last successful transaction.
    /*
    if (System.currentTimeMillis() < lastSuccessfulTransaction + 5 * 60 * 1000) { // < 5 min
        minimumTradeProfit = makeDoubleMachineReadable(5.0);
    } else if (System.currentTimeMillis() < lastSuccessfulTransaction + 10 * 60 * 1000) { // < 10 min
        minimumTradeProfit = makeDoubleMachineReadable(2.5);
    } else if (System.currentTimeMillis() < lastSuccessfulTransaction + 30 * 60 * 1000) { // < 30 min
        minimumTradeProfit = makeDoubleMachineReadable(1.0);
    } else if (System.currentTimeMillis() < lastSuccessfulTransaction + 3 * 60 * 60 * 1000) { // < 3 h
        minimumTradeProfit = makeDoubleMachineReadable(0.5);
    } else { // > 3 h
        minimumTradeProfit = makeDoubleMachineReadable(0.01);
    }
    */
    // usingBufferedWriter("MINIMUM TRADE PROFIT          " +
    // makeBigNumberHumanReadable(minimumTradeProfit));

    try {
      // TODO: think about putting this method into each class such as ethereum, dai and weth
      logger.trace("BALANCES");
      ethereum.updateBalance();
      weth.getAccount().update();
      dai.getAccount().update();
      compoundDai.getAccount().update();

      BigInteger wethBalance = weth.getAccount().getBalance();
      BigInteger daiBalance = dai.getAccount().getBalance();
      BigInteger cdaiBalance = compoundDai.getBalanceInDai();
      BigInteger ethBalance = ethereum.getBalance();

      usd =
          multiply(ethBalance, medianEthereumPrice)
              .add(multiply(wethBalance, medianEthereumPrice))
              .add(daiBalance)
              .add(cdaiBalance); // US-Dollar

      // Gets executed just once at the beginning. Initializes initialTotalUSD.
      if (initialTotalUSDCounter == 0) {
        initialTotalUSD = usd;
        initialTotalUSDCounter++;
      }

      if (sumEstimatedProfits.compareTo(BigInteger.ZERO) != 0)
        logger.trace("TOTAL ARBITRAGE P&L {}{}", getCurrency(sumEstimatedProfits), " USD");
      if (sumEstimatedMissedProfits.compareTo(BigInteger.ZERO) != 0)
        logger.trace("TOTAL MISSED PROFITS {}{}", getCurrency(sumEstimatedMissedProfits), " USD");
      if (usd.subtract(initialTotalUSD).compareTo(BigInteger.ZERO) != 0)
        logger.trace(
            "TOTAL P&L DURING EXECUTION {}{}", getCurrency(usd.subtract(initialTotalUSD)), " USD");
      if (usd.compareTo(BigInteger.ZERO) != 0)
        logger.trace("TOTAL IN USD {}{}", getCurrency(usd), " USD");

      minimumTradeProfitBuyDai = multiply(usd, getMachineReadable(0.00025));
      logger.trace(
          "MINIMUM TRADE PROFIT BUY DAI {}{}", getCurrency(minimumTradeProfitBuyDai), " DAI");

      minimumTradeProfitSellDai = multiply(usd, getMachineReadable(0.0025));
      logger.trace(
          "MINIMUM TRADE PROFIT SELL DAI {}{}", getCurrency(minimumTradeProfitSellDai), " DAI");

      // updateCDPInformation();

      // Checks if the bot made a big loss.
      // For some reason the DAI balance can be wrongly zero instead of the actual value. This means
      // that this If condition can be wrongly true.
      // If the DAI balance gets wrongly updated, the bot will do nothing for 60 secs until the next
      // update.
      if (usd.compareTo(multiply(initialTotalUSD, getMachineReadable(0.5))) < 1) {
        logger.warn("USD BALANCE MIGHT BE ZERO EXCEPTION");

        updateBalanceInformation(Medianizer.getPrice());
        // everythingIsFine = false;
      }

      logger.trace(
          "HOLDING {}% DAI + CDAI AS A PERCENTAGE OVER TIME OF TOTAL ASSET VALUE",
          round(
              currentOwnershipRatio(medianEthereumPrice, ethBalance, daiBalance, wethBalance), 2));

    } catch (Exception e) {
      logger.error("Exception", e);
      updateBalanceInformation(medianEthereumPrice);
    }
  }

  /**
   * @return 0 if no dai/cdai was owned during the execution of the program, 100 if no eth/weth was
   *     owned
   */
  private double currentOwnershipRatio(
      BigInteger medianEthereumPrice,
      BigInteger ethBalance,
      BigInteger daiBalance,
      BigInteger wethBalance) {
    // TODO: add tests for this method

    long timeDifference = System.currentTimeMillis() - pastTime;
    pastTime = System.currentTimeMillis();

    totalDaiRatio +=
        timeDifference * divide(daiBalance.add(compoundDai.getBalanceInDai()), usd).longValue();

    totalEthRatio +=
        timeDifference
            * divide(multiply(ethBalance.add(wethBalance), medianEthereumPrice), usd).longValue();

    if (totalDaiRatio == 0.0) return 0.0;
    return (totalDaiRatio * 10000) / ((totalEthRatio + totalDaiRatio) * 100);
  }

  public void updateBalance(int duration) {
    long currentTime = System.currentTimeMillis();
    if (currentTime
        >= (pastTimeBalances
            + duration * 1000)) { // multiply by 1000 to getMedianEthereumPrice milliseconds
      try {
        BigInteger medianEthereumPrice = Medianizer.getPrice();
        updateBalanceInformation(medianEthereumPrice);
      } catch (MedianException e) {
        logger.error("MedianIsZeroException", e);
      }

      pastTimeBalances = currentTime;
    }
  }

  public void checkEnoughEthereumForGas(Web3j web3j, @NotNull Ethereum ethereum) {
    // TODO: test this method (might unwrap without updating the gas fee to eth balance from
    // previous transaction)
    BigInteger ethereumBalance = ethereum.getBalance();
    BigInteger wethBalance = weth.getAccount().getBalance();
    BigInteger ethereumAndWethBalance = wethBalance.add(ethereumBalance);

    if (ethereumBalance.compareTo(ethereum.minimumEthereumReserveLowerLimit) < 0
        && wethBalance.compareTo(BigInteger.valueOf(10000000000000000L)) > 0) {

      BigInteger toUnwrap =
          (ethereum.getBalanceWithoutMinimumEthereumReserveUpperLimit()).min(wethBalance);

      logger.info("UNWRAP {}", getFullPrecision(toUnwrap));
      try {
        BigInteger medianEthereumPrice = Medianizer.getPrice();
        weth.weth2Eth(this, BigInteger.ZERO, medianEthereumPrice, toUnwrap);
      } catch (MedianException e) {
        logger.error("MedianIsZeroException", e);
      }
    } else if (ethereumAndWethBalance.compareTo(ethereum.minimumEthereumReserveLowerLimit) < 0) {
      logger.error(
          "ETH + WETH ARE LOWER THAN MINIMUM ETHEREUM RESERVE LOWER LIMIT, THEREFORE SHUTDOWN");
      Main.shutdown(web3j);
    }
  }

  public boolean isThereEnoughDaiAndDaiInCompoundForSale() { // todo: test this method and class
    return dai.getAccount()
            .getBalance()
            .add(compoundDai.getBalanceInDai())
            .compareTo(dai.minimumDaiNecessaryForSaleAndLending)
        > 0;
  }

  public BigInteger getMaxDaiToSell() { // todo: test this method
    BigInteger maxDaiToSell = dai.getAccount().getBalance().add(compoundDai.getBalanceInDai());
    logger.trace("MAX DAI TO SELL {}", getFullPrecision(maxDaiToSell));
    return maxDaiToSell;
  }

  public void addToSumEstimatedProfits(BigInteger potentialProfit) {
    sumEstimatedProfits = sumEstimatedProfits.add(potentialProfit);
  }

  public void addToSumEstimatedMissedProfits(BigInteger potentialProfit) {
    sumEstimatedMissedProfits = sumEstimatedMissedProfits.add(potentialProfit);
  }

  public long getLastSuccessfulTransaction() {
    return lastSuccessfulTransaction;
  }

  public void refreshLastSuccessfulTransaction() {
    lastSuccessfulTransaction = System.currentTimeMillis();
  }

  public boolean isThereEnoughEthAndWethForSaleAndLending(@NotNull Ethereum ethereum) {
    BigInteger wethBalance = weth.getAccount().getBalance();
    BigInteger ethAndWethBalance =
        wethBalance.add(ethereum.getBalanceWithoutMinimumEthereumReserveUpperLimit());
    return ethAndWethBalance.compareTo(ethereum.minimumEthereumNecessaryForSale) > 0;
  }

  public BigInteger getMinimumTradeProfitSellDai() {
    return minimumTradeProfitSellDai;
  }

  public BigInteger getMinimumTradeProfitBuyDai() {
    return minimumTradeProfitBuyDai;
  }
}
