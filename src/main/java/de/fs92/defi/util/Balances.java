package de.fs92.defi.util;

import de.fs92.defi.Main;
import de.fs92.defi.compounddai.CompoundDai;
import de.fs92.defi.dai.Dai;
import de.fs92.defi.medianizer.MedianException;
import de.fs92.defi.medianizer.Medianizer;
import de.fs92.defi.weth.Weth;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import static de.fs92.defi.util.BigNumberUtil.*;

public class Balances {
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

  // contracts
  private final Dai dai;
  private final Weth weth;
  private final CompoundDai compoundDai;
  public final Ethereum ethereum;

  // balances
  private BigDecimal ethBalance;
  private BigDecimal daiBalance;
  private BigDecimal wethBalance;
  private BigInteger cdaiBalance;
  private BigDecimal daiInCompound;

  public final BigDecimal minimumTradeProfit;
  private BigDecimal minimumTradeProfitBuyDai;
  private BigDecimal minimumTradeProfitSellDai;

  // profit and loss calculation
  private BigDecimal usd;
  private BigDecimal initialTotalUSD;
  private BigDecimal sumEstimatedProfits;
  private BigDecimal sumEstimatedMissedProfits;
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
    ethBalance = BigDecimal.ZERO;
    daiBalance = BigDecimal.ZERO;
    wethBalance = BigDecimal.ZERO;
    usd = BigDecimal.ZERO;
    cdaiBalance = BigInteger.ZERO;
    daiInCompound = BigDecimal.ZERO;
    sumEstimatedProfits = BigDecimal.ZERO;
    sumEstimatedMissedProfits = BigDecimal.ZERO;
    pastTime = System.currentTimeMillis();
    totalDaiRatio = 0.0;
    totalEthRatio = 0.0;

    minimumTradeProfit = makeDoubleMachineReadable(1.0);
    minimumTradeProfitBuyDai = makeDoubleMachineReadable(1.0);
    minimumTradeProfitSellDai = makeDoubleMachineReadable(1.0);

    initialTotalUSD = BigDecimal.ZERO;
    initialTotalUSDCounter = 0;
    lastSuccessfulTransaction = System.currentTimeMillis();

    this.dai = dai;
    this.weth = weth;
    this.compoundDai = compoundDai;
    this.ethereum = ethereum;
  }

  // INFO: just for testing
  public Balances(
      BigDecimal ethBalance,
      BigDecimal daiBalance,
      BigDecimal wethBalance,
      Dai dai,
      Weth weth,
      CompoundDai compoundDai,
      Ethereum ethereum,
      BigDecimal daiInCompound) {
    this.ethBalance = ethBalance;
    this.daiBalance = daiBalance;
    this.wethBalance = wethBalance;

    this.dai = dai;
    this.weth = weth;
    this.compoundDai = compoundDai;
    this.ethereum = ethereum;
    this.daiInCompound = daiInCompound;

    minimumTradeProfit = makeDoubleMachineReadable(1.0);
    minimumTradeProfitBuyDai = makeDoubleMachineReadable(1.0);
    minimumTradeProfitSellDai = makeDoubleMachineReadable(1.0);
  }

  public void checkEnoughEthereumForGas(Web3j web3j) {
    // TODO: test this method (might unwrap without updating the gas fee to eth balance from
    // previous transaction)
    if (ethBalance.compareTo(ethereum.minimumEthereumReserveLowerLimit) < 0
        && wethBalance.compareTo(new BigDecimal("10000000000000000")) > 0) {

      BigInteger toUnwrap =
          (ethereum.minimumEthereumReserveUpperLimit.subtract(ethBalance).toBigInteger())
              .min(wethBalance.toBigInteger());

      logger.info("UNWRAP {}", makeBigNumberHumanReadableFullPrecision(toUnwrap));
      try {
        BigDecimal medianEthereumPrice = Medianizer.getPrice();
        weth.weth2Eth(this, BigDecimal.ZERO, medianEthereumPrice, toUnwrap);
      } catch (MedianException e) {
        logger.error("MedianIsZeroException", e);
      }
    } else if (ethBalance.add(wethBalance).compareTo(ethereum.minimumEthereumReserveLowerLimit)
        < 0) {
      logger.error(
          "ETH + WETH ARE LOWER THAN MINIMUM ETHEREUM RESERVE LOWER LIMIT, THEREFORE SHUTDOWN");
      Main.shutdown(web3j);
    }
  }

  public void updateBalanceInformation(BigDecimal medianEthereumPrice) {
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
      ethBalance = ethereum.getBalance();
      daiBalance = dai.getBalance(ethereum.getAddress());
      wethBalance = weth.getBalance(ethereum.getAddress());
      cdaiBalance = compoundDai.getCDaiBalance(ethereum.getAddress());
      daiInCompound = compoundDai.getBalanceInDai(cdaiBalance);
      usd =
          multiply(ethBalance, medianEthereumPrice)
              .add(multiply(wethBalance, medianEthereumPrice))
              .add(daiBalance)
              .add(daiInCompound); // US-Dollar

      // Gets executed just once at the beginning. Initializes initialTotalUSD.
      if (initialTotalUSDCounter == 0) {
        initialTotalUSD = usd;
        initialTotalUSDCounter++;
      }

      logger.trace("BALANCES");
      if (ethBalance.compareTo(BigDecimal.ZERO) != 0)
        logger.trace(
            "ETH BALANCE {}{}", makeBigNumberHumanReadableFullPrecision(ethBalance), " ETH");
      if (wethBalance.compareTo(BigDecimal.ZERO) != 0)
        logger.trace(
            "WETH BALANCE {}{}", makeBigNumberHumanReadableFullPrecision(wethBalance), " WETH");
      if (daiBalance.compareTo(BigDecimal.ZERO) != 0)
        logger.trace(
            "DAI BALANCE {}{}", makeBigNumberHumanReadableFullPrecision(daiBalance), " DAI");
      if (cdaiBalance.compareTo(BigInteger.ZERO) != 0)
        logger.trace(
            "CDAI BALANCE {}{}", makeBigNumberHumanReadableFullPrecision(cdaiBalance, 8), " CDAI");
      if (daiInCompound.compareTo(BigDecimal.ZERO) != 0)
        logger.trace(
            "DAI SUPPLIED BALANCE {}{}",
            makeBigNumberHumanReadableFullPrecision(daiInCompound),
            " DAI");
      if (sumEstimatedProfits.compareTo(BigDecimal.ZERO) != 0)
        logger.trace(
            "TOTAL ARBITRAGE P&L {}{}",
            makeBigNumberCurrencyHumanReadable(sumEstimatedProfits),
            " USD");
      if (sumEstimatedMissedProfits.compareTo(BigDecimal.ZERO) != 0)
        logger.trace(
            "TOTAL MISSED PROFITS {}{}",
            makeBigNumberCurrencyHumanReadable(sumEstimatedMissedProfits),
            " USD");
      if (usd.subtract(initialTotalUSD).compareTo(BigDecimal.ZERO) != 0)
        logger.trace(
            "TOTAL P&L DURING EXECUTION {}{}",
            makeBigNumberCurrencyHumanReadable(usd.subtract(initialTotalUSD)),
            " USD");
      // TODO: add information about interest earned
      if (usd.compareTo(BigDecimal.ZERO) != 0)
        logger.trace("TOTAL IN USD {}{}", makeBigNumberCurrencyHumanReadable(usd), " USD");

      minimumTradeProfitBuyDai = multiply(usd, makeDoubleMachineReadable(0.00025));
      logger.trace(
          "MINIMUM TRADE PROFIT BUY DAI {}{}",
          makeBigNumberCurrencyHumanReadable(minimumTradeProfitBuyDai),
          " DAI");

      minimumTradeProfitSellDai = multiply(usd, makeDoubleMachineReadable(0.0025));
      logger.trace(
          "MINIMUM TRADE PROFIT SELL DAI {}{}",
          makeBigNumberCurrencyHumanReadable(minimumTradeProfitSellDai),
          " DAI");

      // updateCDPInformation();

      // Checks if the bot made a big loss.
      // For some reason the DAI balance can be wrongly zero instead of the actual value. This means
      // that this If condition can be wrongly true.
      // If the DAI balance gets wrongly updated, the bot will do nothing for 60 secs until the next
      // update.
      if (usd.compareTo(multiply(initialTotalUSD, makeDoubleMachineReadable(0.5))) < 1) {
        logger.warn("USD BALANCE MIGHT BE ZERO EXCEPTION");

        updateBalanceInformation(Medianizer.getPrice());
        // everythingIsFine = false;
      }
    } catch (Exception e) {
      logger.error("Exception", e);
      updateBalanceInformation(medianEthereumPrice);
    }

    logger.trace(
        "DAI & CDAI TO ETH & WETH RATIO {}{}",
        round(currentOwnershipRatio(medianEthereumPrice), 2),
        "%");
  }

  /**
   * @return 0 if no dai/cdai was owned during the execution of the program, 100 if no eth/weth was
   *     owned
   */
  private double currentOwnershipRatio(BigDecimal medianEthereumPrice) {
    // TODO: add tests for this method

    long timeDifference = System.currentTimeMillis() - pastTime;
    pastTime = System.currentTimeMillis();

    totalDaiRatio +=
        timeDifference
            * makeBigNumberHumanReadable(daiBalance.add(daiInCompound))
            / makeBigNumberHumanReadable(usd);

    totalEthRatio +=
        timeDifference
            * makeBigNumberHumanReadable(multiply(ethBalance.add(wethBalance), medianEthereumPrice))
            / makeBigNumberHumanReadable(usd);

    if (totalDaiRatio == 0.0) return 0.0;
    return (totalDaiRatio * 10000) / ((totalEthRatio + totalDaiRatio) * 100);
  }

  public void updateBalance(int duration) {
    long currentTime = System.currentTimeMillis();
    if (currentTime
        >= (pastTimeBalances
            + duration * 1000)) { // multiply by 1000 to getMedianEthereumPrice milliseconds
      try {
        BigDecimal medianEthereumPrice = Medianizer.getPrice();
        updateBalanceInformation(medianEthereumPrice);
      } catch (MedianException e) {
        logger.error("MedianIsZeroException", e);
      }

      pastTimeBalances = currentTime;
    }
  }

  public boolean checkTooFewDaiAndDaiInCompound() {
    return daiBalance.add(daiInCompound).compareTo(dai.minimumDaiNecessaryForSale) <= 0;
  }

  public BigDecimal getMaxDaiToSell() {
    BigDecimal maxDaiToSell = daiBalance.add(daiInCompound);
    logger.trace("MAX DAI TO SELL {}", makeBigNumberHumanReadableFullPrecision(maxDaiToSell));
    return maxDaiToSell;
  }

  public boolean checkEnoughDai() {
    boolean isEnough = daiBalance.compareTo(dai.minimumDaiNecessaryForSale) > 0;
    if (isEnough) {
      logger.trace("ENOUGH DAI {}", makeBigNumberHumanReadableFullPrecision(daiBalance));
    } else {
      logger.trace("TOO FEW DAI {}", makeBigNumberHumanReadableFullPrecision(daiBalance));
    }
    return isEnough;
  }

  public boolean checkTooFewEthOrWeth() {
    BigDecimal ethAndWethBalance =
        wethBalance.add(
            BigDecimal.ZERO.max(ethBalance.subtract(ethereum.minimumEthereumReserveUpperLimit)));
    boolean isEnough = ethAndWethBalance.compareTo(ethereum.minimumEthereumNecessaryForSale) <= 0;
    if (isEnough) {
      logger.trace(
          "ENOUGH AND WETH {}", makeBigNumberHumanReadableFullPrecision(ethAndWethBalance));
    } else {
      logger.trace(
          "TOO FEW ETH ETH AND WETH {}",
          makeBigNumberHumanReadableFullPrecision(ethAndWethBalance));
    }
    return isEnough;
  }

  public void addToSumEstimatedProfits(BigDecimal potentialProfit) {
    sumEstimatedProfits = sumEstimatedProfits.add(potentialProfit);
  }

  public void addToSumEstimatedMissedProfits(BigDecimal potentialProfit) {
    sumEstimatedMissedProfits = sumEstimatedMissedProfits.add(potentialProfit);
  }

  public BigDecimal getEthBalance() {
    return ethBalance;
  }

  public BigDecimal getDaiBalance() {
    return daiBalance;
  }

  public BigDecimal getWethBalance() {
    return wethBalance;
  }

  public BigInteger getCdaiBalance() {
    return cdaiBalance;
  }

  public BigDecimal getDaiInCompound() {
    return daiInCompound;
  }

  public BigDecimal getMinimumTradeProfitBuyDai() {
    return minimumTradeProfitBuyDai;
  }

  public BigDecimal getMinimumTradeProfitSellDai() {
    return minimumTradeProfitSellDai;
  }

  public long getLastSuccessfulTransaction() {
    return lastSuccessfulTransaction;
  }

  public void refreshLastSuccessfulTransaction() {
    lastSuccessfulTransaction = System.currentTimeMillis();
  }

  public static double round(double value, int places) {
    if (places < 0) throw new IllegalArgumentException();

    BigDecimal bd = BigDecimal.valueOf(value);
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }
}
