package de.fs92.defi.util;

import de.fs92.defi.compounddai.CompoundDai;
import de.fs92.defi.dai.Dai;
import de.fs92.defi.medianizer.MedianException;
import de.fs92.defi.medianizer.Medianizer;
import de.fs92.defi.numberutil.Wad18;
import de.fs92.defi.weth.Weth;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static de.fs92.defi.Main.shutdown;
import static de.fs92.defi.numberutil.NumberUtil.getMachineReadable;

public class Balances {
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

  // contracts
  public final Dai dai;
  public final Weth weth;
  public final CompoundDai compoundDai;
  public final Ethereum ethereum;

  public final Wad18 minimumTradeProfit;
  private Wad18 minimumTradeProfitBuyDai;
  private Wad18 minimumTradeProfitSellDai;

  // profit and loss calculation
  Wad18 usd;
  private Wad18 initialTotalUSD;
  private Wad18 sumEstimatedProfits;
  private Wad18 sumEstimatedMissedProfits;
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
    usd = Wad18.ZERO;
    sumEstimatedProfits = Wad18.ZERO;
    sumEstimatedMissedProfits = Wad18.ZERO;
    pastTime = System.currentTimeMillis();
    totalDaiRatio = 0.0;
    totalEthRatio = 0.0;

    minimumTradeProfit = new Wad18(getMachineReadable(1.0));
    minimumTradeProfitBuyDai = new Wad18(getMachineReadable(1.0));
    minimumTradeProfitSellDai = new Wad18(getMachineReadable(1.0));

    initialTotalUSD = Wad18.ZERO;
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

  public void updateBalanceInformation(Wad18 medianEthereumPrice) {
    try {
      ethereum.updateBalance();
      weth.getAccount().update();
      dai.getAccount().update();
      compoundDai.getAccount().update();

      Wad18 wethBalance = weth.getAccount().getBalance();
      Wad18 daiBalance = dai.getAccount().getBalance();
      Wad18 cdaiBalance = compoundDai.getBalanceInDai();
      Wad18 ethBalance = ethereum.getBalance();

      usd =
          ethBalance
              .multiply(medianEthereumPrice)
              .add(wethBalance.multiply(medianEthereumPrice))
              .add(daiBalance)
              .add(cdaiBalance);

      // Gets executed just once at the beginning. Initializes initialTotalUSD.
      if (initialTotalUSDCounter == 0) {
        initialTotalUSD = usd;
        initialTotalUSDCounter++;
      }

      if (sumEstimatedProfits.compareTo(Wad18.ZERO) != 0)
        logger.trace("TOTAL ARBITRAGE P&L {}{}", sumEstimatedProfits, " USD");
      if (sumEstimatedMissedProfits.compareTo(Wad18.ZERO) != 0)
        logger.trace("TOTAL MISSED PROFITS {}{}", sumEstimatedMissedProfits, " USD");
      if (usd.subtract(initialTotalUSD).compareTo(Wad18.ZERO) != 0)
        logger.trace("TOTAL P&L DURING EXECUTION {}{}", usd.subtract(initialTotalUSD), " USD");
      if (usd.compareTo(Wad18.ZERO) != 0)
        logger.trace("TOTAL IN USD {}{}", usd.toString(2), " USD");

      minimumTradeProfitBuyDai = usd.multiply(new Wad18(getMachineReadable(0.00025)));
      logger.trace(
          "MINIMUM TRADE PROFIT BUY DAI {}{}", minimumTradeProfitBuyDai.toString(2), " DAI");

      minimumTradeProfitSellDai = usd.multiply(new Wad18(getMachineReadable(0.0025)));
      logger.trace(
          "MINIMUM TRADE PROFIT SELL DAI {}{}", minimumTradeProfitSellDai.toString(2), " DAI");

      // Checks if the bot made a big loss.
      // For some reason the DAI balance can be wrongly zero instead of the actual value. This means
      // that this If condition can be wrongly true.
      // If the DAI balance gets wrongly updated, the bot will do nothing for 60 secs until the next
      // update.
      if (usd.compareTo(initialTotalUSD.multiply(new Wad18(getMachineReadable(0.5)))) < 1) {
        logger.warn("USD BALANCE MIGHT BE ZERO EXCEPTION");
        updateBalanceInformation(Medianizer.getPrice());
      }

      logger.trace(
          "HOLDING {}% DAI + CDAI AS A PERCENTAGE OVER TIME OF TOTAL ASSET VALUE", // todo:
          // negative???
          round(
              currentOwnershipRatio(medianEthereumPrice, ethBalance, daiBalance, wethBalance), 2));

    } catch (Exception e) {
      logger.error("Exception", e);
      updateBalanceInformation(medianEthereumPrice);
    }
  }

  /**
   * @return 0 if no dai/cdai was owned during the execution of the program, 100 if no eth/weth was
   * owned
   */
  double currentOwnershipRatio(
          Wad18 medianEthereumPrice,
          @NotNull Wad18 ethBalance,
          @NotNull Wad18 daiBalance,
          Wad18 wethBalance) {

    long timeDifference = System.currentTimeMillis() - pastTime;
    pastTime = System.currentTimeMillis();

    totalDaiRatio +=
            timeDifference * (daiBalance.add(compoundDai.getBalanceInDai())).divide(usd).doubleValue();

    totalEthRatio +=
            timeDifference
                    * (ethBalance.add(wethBalance).multiply(medianEthereumPrice)).divide(usd).doubleValue();

    if (totalDaiRatio == 0.0) return 0.0;
    return Math.round(totalDaiRatio / (totalEthRatio + totalDaiRatio) * 10000.0) / 100.0;
  }

  public void updateBalance(int duration) {
    logger.trace("");
    logger.trace("UPDATING BALANCE EVERY {} SECONDS", duration);
    long currentTime = System.currentTimeMillis();
    if (currentTime
            >= (pastTimeBalances
            + duration * 1000)) { // multiply by 1000 to getMedianEthereumPrice milliseconds
      try {
        Wad18 medianEthereumPrice = Medianizer.getPrice();
        updateBalanceInformation(medianEthereumPrice);
      } catch (MedianException e) {
        logger.error("MedianIsZeroException", e);
      }

      pastTimeBalances = currentTime;
    }
  }

  public void checkEnoughEthereumForGas(@NotNull Ethereum ethereum) {
    logger.trace("");
    logger.trace("CHECKING IF ENOUGH ETHEREUM FOR GAS");
    // TODO: test this method (might unwrap without updating the gas fee to eth balance from
    // previous transaction)
    Wad18 ethereumBalance = ethereum.getBalance();
    Wad18 wethBalance = weth.getAccount().getBalance();
    Wad18 ethereumAndWethBalance = wethBalance.add(ethereumBalance);

    if (ethereumBalance.compareTo(ethereum.minimumEthereumReserveLowerLimit) < 0
            && wethBalance.compareTo(new Wad18(10000000000000000L)) > 0) {

      Wad18 toUnwrap =
          (ethereum.getBalanceWithoutMinimumEthereumReserveUpperLimit()).min(wethBalance);

      logger.info("UNWRAP {}", toUnwrap);
      try {
        Wad18 medianEthereumPrice = Medianizer.getPrice();
        weth.weth2Eth(this, Wad18.ZERO, medianEthereumPrice, toUnwrap);
      } catch (MedianException e) {
        logger.error("MedianIsZeroException", e);
      }
    } else if (ethereumAndWethBalance.compareTo(ethereum.minimumEthereumReserveLowerLimit) < 0) {
      logger.error(
              "ETH + WETH ARE LOWER THAN MINIMUM ETHEREUM RESERVE LOWER LIMIT, THEREFORE SHUTDOWN");
      shutdown();
    }
  }

  public boolean isThereTooFewDaiAndDaiInCompoundForSale() { // todo: test this method and class
    return dai.getAccount()
            .getBalance()
            .add(compoundDai.getBalanceInDai())
            .compareTo(dai.minimumDaiNecessaryForSaleAndLending)
            <= 0;
  }

  public Wad18 getMaxDaiToSell() { // todo: test this method
    Wad18 maxDaiToSell = dai.getAccount().getBalance().add(compoundDai.getBalanceInDai());
    logger.trace("MAX DAI TO SELL {}", maxDaiToSell);
    return maxDaiToSell;
  }

  public Wad18 getTotalBalanceInUSD() {
    return usd;
  }

  public void addToSumEstimatedProfits(Wad18 potentialProfit) {
    sumEstimatedProfits = sumEstimatedProfits.add(potentialProfit);
  }

  public void addToSumEstimatedMissedProfits(Wad18 potentialProfit) {
    sumEstimatedMissedProfits = sumEstimatedMissedProfits.add(potentialProfit);
  }

  public long getLastSuccessfulTransaction() {
    return lastSuccessfulTransaction;
  }

  public void refreshLastSuccessfulTransaction() {
    lastSuccessfulTransaction = System.currentTimeMillis();
  }

  public boolean isThereTooFewEthAndWethForSaleAndLending(@NotNull Ethereum ethereum) {
    Wad18 wethBalance = weth.getAccount().getBalance();
    Wad18 ethAndWethBalance =
        wethBalance.add(ethereum.getBalanceWithoutMinimumEthereumReserveUpperLimit());
    return ethAndWethBalance.compareTo(ethereum.minimumEthereumNecessaryForSale) <= 0;
  }

  public Wad18 getMinimumTradeProfitSellDai() {
    return minimumTradeProfitSellDai;
  }

  public Wad18 getMinimumTradeProfitBuyDai() {
    return minimumTradeProfitBuyDai;
  }
}
