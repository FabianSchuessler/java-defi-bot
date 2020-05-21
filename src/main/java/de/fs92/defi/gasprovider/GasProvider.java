package de.fs92.defi.gasprovider;

import de.fs92.defi.compounddai.CompoundDaiContract;
import de.fs92.defi.dai.DaiContract;
import de.fs92.defi.numberutil.Wad18;
import de.fs92.defi.oasis.OasisContract;
import de.fs92.defi.uniswap.UniswapContract;
import de.fs92.defi.weth.WethContract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static de.fs92.defi.numberutil.NumberUtil.getMachineReadable;

public class GasProvider implements ContractGasProvider {
  static final String GWEI = " GWEI";
  private static final String GAS_PRICE_EXCEPTION = "GasPriceException";
  private static final org.slf4j.Logger logger =
          LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
  final Wad18 minimumGasPrice;
  final Wad18 maximumGasPrice;
  final Web3j web3j;
  Wad18 gasPrice;
  private List<Long> failedTransactionsWithinTheLastTwelveHoursForGasPriceArrayList =
          new ArrayList<>();

  public GasProvider(Web3j web3j, Wad18 minimumGasPrice, Wad18 maximumGasPrice) {
    this.web3j = web3j;
    this.minimumGasPrice = minimumGasPrice;
    this.maximumGasPrice = maximumGasPrice;
    this.gasPrice = new Wad18(BigInteger.valueOf(1_000000000));
  }

  public void updateFailedTransactions(List<Long> list) {
    failedTransactionsWithinTheLastTwelveHoursForGasPriceArrayList.addAll(list);
    setFailedTransactionsWithinTheLastTwelveHoursForGasPriceArrayList(
        ArrayListUtil.removeDuplicates(
            failedTransactionsWithinTheLastTwelveHoursForGasPriceArrayList));
    if (System.currentTimeMillis()
        >= failedTransactionsWithinTheLastTwelveHoursForGasPriceArrayList.get(0)
            + 12 * 60 * 60 * 1000) { // 12 hours
      logger.trace("FAILED TRANSACTION REMOVED 12 HOURS");
      failedTransactionsWithinTheLastTwelveHoursForGasPriceArrayList.remove(0);
    }
  }

  @Override
  public BigInteger getGasLimit(@NotNull String contractFunc) {
    switch (contractFunc) {
      case CompoundDaiContract.FUNC_MINT:
      case CompoundDaiContract.FUNC_REDEEM:
        return BigInteger.valueOf(200_000);
      case UniswapContract.FUNC_TOKENTOETHSWAPINPUT:
      case UniswapContract.FUNC_ETHTOTOKENSWAPINPUT:
        return BigInteger.valueOf(65_000);
      case OasisContract.FUNC_BUY:
        return BigInteger.valueOf(300_000);
      case WethContract.FUNC_WITHDRAW:
      case WethContract.FUNC_DEPOSIT:
      case DaiContract.FUNC_APPROVE:
        return BigInteger.valueOf(50_000);
      default:
        logger.trace("{} FUNCTION HAS NO CUSTOMIZED GAS LIMIT YET", contractFunc);
        return BigInteger.valueOf(500_000);
    }
  }

  @Override
  public BigInteger getGasPrice(String contractFunc) {
    return gasPrice.toBigInteger();
  }

  public Wad18 updateFastGasPrice(Wad18 medianEthereumPrice, Wad18 potentialProfit) {
    Wad18 fastGasPrice = minimumGasPrice;
    try {
      Wad18 etherchainResult = Etherchain.getFastestGasPrice();
      fastGasPrice = fastGasPrice.max(etherchainResult);
    } catch (GasPriceException e) {
      logger.error(GAS_PRICE_EXCEPTION, e);
    }
    try {
      Wad18 ethGasStationResult = ETHGasStation.getFastestGasPrice();
      fastGasPrice = fastGasPrice.max(ethGasStationResult);
    } catch (GasPriceException e) {
      logger.error(GAS_PRICE_EXCEPTION, e);
    }
    try {
      double percentageOfProfitAsFee =
              getPercentageOfProfitAsFee(
                      failedTransactionsWithinTheLastTwelveHoursForGasPriceArrayList.size());
      Wad18 gasPriceBasedOnProfit =
              calculateGasPriceAsAPercentageOfProfit(
                      medianEthereumPrice, potentialProfit, 300000.0, percentageOfProfitAsFee);
      // instead of fixed
      fastGasPrice = fastGasPrice.max(gasPriceBasedOnProfit);
    } catch (GasPriceException e) {
      logger.error(GAS_PRICE_EXCEPTION, e);
    }
    gasPrice = fastGasPrice.min(maximumGasPrice);
    logger.trace("GAS PRICE {}{}", Convert.fromWei(gasPrice.toString(), Convert.Unit.GWEI), GWEI);
    return gasPrice;
  }

  public Wad18 updateSlowGasPrice() {
    Wad18 slowGasPrice = maximumGasPrice;
    try {
      Wad18 ethGasStationResult = ETHGasStation.getSafeLowGasPrice();
      slowGasPrice = slowGasPrice.min(ethGasStationResult);
    } catch (GasPriceException e) {
      logger.error(GAS_PRICE_EXCEPTION, e);
    }
    try {
      Wad18 web3jResult = new Wad18(web3j.ethGasPrice().send().getGasPrice());
      logger.trace(
              "WEB3J SUGGESTS GP {}{}",
              Convert.fromWei(web3jResult.toString(), Convert.Unit.GWEI),
              GWEI);
      slowGasPrice = slowGasPrice.min(web3jResult);
    } catch (IOException e) {
      logger.error("IOException", e);
    }
    gasPrice = slowGasPrice;
    logger.trace("GAS PRICE {}{}", Convert.fromWei(gasPrice.toString(), Convert.Unit.GWEI), GWEI);
    return gasPrice;
  }

  public double getPercentageOfProfitAsFee(
      int failedTransactionsWithinTheLastTwelveHoursForGasPriceArrayListSize) {
    double percentageOfProfitAsFee =
            Math.min(
                    0.35,
                    ((double) failedTransactionsWithinTheLastTwelveHoursForGasPriceArrayListSize * 5.0
                            + 10.0)
                            / 100.0);
    logger.trace("GP PERCENTAGE OF PROFIT {}", percentageOfProfitAsFee);

    return percentageOfProfitAsFee;
  }

  Wad18 calculateGasPriceAsAPercentageOfProfit(
          @NotNull Wad18 medianEthereumPrice,
          Wad18 potentialProfit,
          double gasLimit,
          double percentageOfProfitAsFee)
          throws GasPriceException {
    if (medianEthereumPrice.compareTo(BigInteger.ZERO) == 0
            || potentialProfit.compareTo(BigInteger.ZERO) == 0
            || gasLimit == 0.0)
      throw new GasPriceException("calculateGasPriceAsAPercentageOfProfit Exception");
    // TODO: debug this method call
    Wad18 feeInEth =
            potentialProfit
                    .multiply(new Wad18(getMachineReadable(percentageOfProfitAsFee)))
                    .divide(medianEthereumPrice); // 0.049307620043223397 0.04930762004
    logger.trace("EST. TRANSACTION FEE {}{}", feeInEth.multiply(medianEthereumPrice), " DAI");

    Wad18 gasPriceBasedOnProfit = feeInEth.divide(new Wad18(getMachineReadable(gasLimit)));
    logger.trace(
            "PROFIT SUGGESTS GP {}{}",
            Convert.fromWei(gasPriceBasedOnProfit.toBigInteger().toString(), Convert.Unit.GWEI),
            GWEI);

    return gasPriceBasedOnProfit;
  }

  /** @deprecated in the interface       */
  @Override
  @Deprecated(since = "0.0.1", forRemoval = false)
  public BigInteger getGasPrice() {
    return null;
  }

  /** @deprecated in the interface       */
  @Override
  @Deprecated(since = "0.0.1", forRemoval = false)
  public BigInteger getGasLimit() {
    return null;
  }

  public int getFailedTransactionsWithinTheLastTwelveHoursForGasPriceArrayList() {
    return failedTransactionsWithinTheLastTwelveHoursForGasPriceArrayList.size();
  }

  public void setFailedTransactionsWithinTheLastTwelveHoursForGasPriceArrayList(
      List<Long> failedTransactionsWithinTheLastTwelveHoursForGasPriceArrayList) {
    this.failedTransactionsWithinTheLastTwelveHoursForGasPriceArrayList =
        failedTransactionsWithinTheLastTwelveHoursForGasPriceArrayList;
  }
}
