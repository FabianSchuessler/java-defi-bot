package de.fs92.defi.uniswap;

import de.fs92.defi.compounddai.CompoundDai;
import de.fs92.defi.contractneedsprovider.CircuitBreaker;
import de.fs92.defi.contractneedsprovider.ContractNeedsProvider;
import de.fs92.defi.contractneedsprovider.Permissions;
import de.fs92.defi.contractuserutil.AddressMethod;
import de.fs92.defi.gasprovider.GasProvider;
import de.fs92.defi.medianizer.Medianizer;
import de.fs92.defi.numberutil.Wad18;
import de.fs92.defi.util.Balances;
import de.fs92.defi.util.JavaProperties;
import de.fs92.defi.weth.Weth;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.math.BigInteger;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static de.fs92.defi.numberutil.NumberUtil.getMachineReadable;
import static de.fs92.defi.util.ProfitCalculator.getPotentialProfit;

public class Uniswap implements AddressMethod {
  public static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd HH_mm_ss");
  private static final String EXCEPTION = "Exception";
  private static final String PROFIT = "Profit {}";
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
  private static final String ADDRESS = "0x2a1530C4C41db0B0b2bB646CB5Eb1A67b7158667";
  private static final String UNISWAP_BUY_PROFIT_PERCENTAGE = "uniswapBuyProfitPercentage";
  private static final String UNISWAP_SELL_PROFIT_PERCENTAGE = "uniswapSellProfitPercentage";

  private final UniswapContract uniswapContract;
  private final GasProvider gasProvider;
  private final JavaProperties javaProperties;
  private final Permissions permissions;
  private final CompoundDai compoundDai;
  private final CircuitBreaker circuitBreaker;
  private final Weth weth;
  private final Web3j web3j;
  private double buyProfitPercentage;
  private double sellProfitPercentage;

  public Uniswap(
      @NotNull ContractNeedsProvider contractNeedsProvider,
      @NotNull JavaProperties javaProperties,
      CompoundDai compoundDai,
      Weth weth) {
    web3j = contractNeedsProvider.getWeb3j();
    Credentials credentials = contractNeedsProvider.getCredentials();
    gasProvider = contractNeedsProvider.getGasProvider();
    uniswapContract = UniswapContract.load(ADDRESS, web3j, credentials, gasProvider);
    permissions = contractNeedsProvider.getPermissions();
    circuitBreaker = contractNeedsProvider.getCircuitBreaker();
    this.javaProperties = javaProperties;
    this.weth = weth;
    this.compoundDai = compoundDai;
    sellProfitPercentage =
        Double.parseDouble(javaProperties.getValue(UNISWAP_SELL_PROFIT_PERCENTAGE));
    buyProfitPercentage =
        Double.parseDouble(javaProperties.getValue(UNISWAP_BUY_PROFIT_PERCENTAGE));
  }

  public void checkIfBuyDaiIsProfitableThenDoIt(@NotNull Balances balances) {
    if (balances.isThereTooFewEthAndWethForSaleAndLending(balances.ethereum)) {
      logger.trace("NOT ENOUGH WETH AND ETH TO BUY DAI ON UNISWAP");
      return;
    }
    try {
      logger.trace("UNISWAP BUY DAI PROFIT CALCULATION");
      Wad18 medianEthereumPrice = Medianizer.getPrice();
      EthToTokenSwapInput ethToTokenSwapInput = getBuyDaiParameters(balances, medianEthereumPrice);

      if (ethToTokenSwapInput != null
          && compoundDai.canOtherProfitMethodsWorkWithoutCDaiConversion(
              balances, ethToTokenSwapInput.potentialProfit, medianEthereumPrice)) {
        buyDaiTransaction(ethToTokenSwapInput, medianEthereumPrice, balances);
      }
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
    }
  }

  public void checkIfSellDaiIsProfitableThenDoIt(@NotNull Balances balances) {
    if (balances.isThereTooFewDaiAndDaiInCompoundForSale()) {
      logger.trace("NOT ENOUGH DAI TO SELL DAI ON UNISWAP");
      return;
    }

    try {
      logger.trace("UNISWAP SELL DAI PROFIT CALCULATION");
      Wad18 medianEthereumPrice = Medianizer.getPrice();
      TokenToEthSwapInput tokenToEthSwapInput = getSellDaiParameters(balances, medianEthereumPrice);
      if (tokenToEthSwapInput != null
          && compoundDai.canOtherProfitMethodsWorkWithoutCDaiConversion(
              balances, tokenToEthSwapInput.potentialProfit, medianEthereumPrice)) {
        sellDaiTransaction(tokenToEthSwapInput, medianEthereumPrice, balances);
      }
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
    }
  }

  EthToTokenSwapInput getBuyDaiParameters(@NotNull Balances balances, Wad18 medianEthereumPrice)
      throws Exception {
    Wad18 wethBalance = balances.weth.getAccount().getBalance();
    Wad18 ethToSell =
        wethBalance.add(balances.ethereum.getBalanceWithoutMinimumEthereumReserveUpperLimit());
    Wad18 buyableDaiAmount;
    try {
      buyableDaiAmount =
          new Wad18(this.uniswapContract.getEthToTokenInputPrice(ethToSell.toBigInteger()).send());
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
      return null;
    }
    UniswapOffer offer =
        getProfitableBuyDaiOffer(
            buyableDaiAmount,
            ethToSell,
            balances,
            medianEthereumPrice,
            gasProvider.getPercentageOfProfitAsFee(
                gasProvider.getFailedTransactionsWithinTheLastTwelveHoursForGasPriceArrayList()));
    if (offer.buyableAmount.compareTo(Wad18.ZERO) != 0) {
      return calculateBuyDaiParameters(medianEthereumPrice, offer, balances);
    }
    return null;
  }

  TokenToEthSwapInput getSellDaiParameters(@NotNull Balances balances, Wad18 medianEthereumPrice)
      throws IOException {
    Wad18 daiToSell = balances.getMaxDaiToSell(); // TODO: test this line
    UniswapOffer offer =
        getProfitableSellDaiOffer(
            daiToSell,
            balances,
            medianEthereumPrice,
            gasProvider.getPercentageOfProfitAsFee(
                gasProvider.getFailedTransactionsWithinTheLastTwelveHoursForGasPriceArrayList()));
    if (offer.buyableAmount.compareTo(Wad18.ZERO) != 0) {
      return calculateSellDaiParameters(medianEthereumPrice, daiToSell, offer);
    }
    return null;
  }

  @NotNull
  private EthToTokenSwapInput calculateBuyDaiParameters(
      Wad18 medianEthereumPrice, UniswapOffer offer, @NotNull Balances balances)
      throws IOException {
    long currentUnixTimePlusFiveMinutes = System.currentTimeMillis() / 1000L + 300L;
    logger.info("UNISWAP BUY DAI PROFIT CALCULATION");
    logger.trace("ALTERNATIVE DEADLINE {}", currentUnixTimePlusFiveMinutes);

    Wad18 deadline =
        new Wad18(
            web3j
                .ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false)
                .send()
                .getBlock()
                .getTimestamp()
                .add(BigInteger.valueOf(300)));
    Wad18 wethBalance = balances.weth.getAccount().getBalance();
    if (wethBalance.compareTo(
            balances
                .ethereum
                .minimumEthereumReserveUpperLimit) // TODO: move this line up in the method
        // hierarchy
        // hierarchy
        > 0) { // TODO: check this line
      logger.trace(PROFIT, offer.profit);
      weth.weth2Eth(
          balances, offer.profit, medianEthereumPrice, balances.weth.getAccount().getBalance());
    }
    Wad18 profitWillingToGiveUp =
        offer.profit.multiply(new Wad18(getMachineReadable(buyProfitPercentage)));
    Wad18 actualProfitInUSD = offer.profit.subtract(profitWillingToGiveUp);
    Wad18 minTokens = offer.buyableAmount.subtract(profitWillingToGiveUp);
    Wad18 ethSold = balances.ethereum.getBalanceWithoutMinimumEthereumReserveUpperLimit();

    // https://stackoverflow.com/questions/39506891/why-is-zoneoffset-utc-zoneid-ofutc
    String timeZone = TimeZone.getDefault().getID();
    String formattedDeadline =
        Instant.ofEpochSecond(currentUnixTimePlusFiveMinutes)
            .atZone(ZoneId.of(timeZone))
            .format(dtf);

    logger.trace("PROFIT WILLING TO GIVE UP {}", profitWillingToGiveUp);
    logger.trace("ETH SOLD {}", ethSold);
    logger.trace("MIN TOKENS {}", minTokens);
    logger.trace("DEADLINE {}", formattedDeadline);
    logger.trace(PROFIT, actualProfitInUSD.toString(5));
    return new EthToTokenSwapInput(minTokens, deadline, ethSold, actualProfitInUSD);
  }

  @NotNull
  private TokenToEthSwapInput calculateSellDaiParameters(
      Wad18 medianEthereumPrice, Wad18 daiToSell, @NotNull UniswapOffer offer) throws IOException {
    long unixTime = System.currentTimeMillis() / 1000L + 300L;
    logger.trace("DEADLINE IN UNIX {}", unixTime);
    Wad18 deadline =
        new Wad18(
            web3j
                .ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false)
                .send()
                .getBlock()
                .getTimestamp()
                .add(BigInteger.valueOf(300)));
    Wad18 profitWillingToGiveUp =
        offer
            .profit
            .multiply(new Wad18(getMachineReadable(sellProfitPercentage)))
            .divide(daiToSell.divide(offer.buyableAmount));
    Wad18 actualProfitInUSD =
        offer.profit.subtract(profitWillingToGiveUp.multiply(medianEthereumPrice));
    Wad18 minEth = offer.buyableAmount.subtract(profitWillingToGiveUp);

    // https://stackoverflow.com/questions/39506891/why-is-zoneoffset-utc-zoneid-ofutc
    String timeZone = TimeZone.getDefault().getID();
    String formatedDeadline =
        Instant.ofEpochSecond(unixTime).atZone(ZoneId.of(timeZone)).format(dtf);

    logger.info("PROFIT WILLING TO GIVE UP {}", profitWillingToGiveUp);
    logger.info("TOKEN SOLD {}", minEth);
    logger.info("MIN ETH {}", minEth);
    logger.info("DEADLINE {}", formatedDeadline);
    logger.info(PROFIT, actualProfitInUSD.toString(5));
    return new TokenToEthSwapInput(minEth, deadline, daiToSell, actualProfitInUSD);
  }

  @NotNull
  UniswapOffer getProfitableBuyDaiOffer(
      Wad18 buyableDaiAmount,
      Wad18 ethToSell,
      @NotNull Balances balances,
      Wad18 medianEthereumPrice,
      double percentageOfProfitAsFee) {
    logger.trace("BUYABLE DAI AMOUNT {} DAI", buyableDaiAmount);

    Wad18 uniswapBuyDaiPrice = buyableDaiAmount.divide(ethToSell);
    logger.trace("DAI PER ETH {}{}", uniswapBuyDaiPrice.toString(5), " ETH/DAI");

    Wad18 bestOfferMedianRatio = medianEthereumPrice.divide(uniswapBuyDaiPrice);
    logger.trace("MEDIAN-OFFER RATIO {}", bestOfferMedianRatio);

    //        BigDecimal potentialProfit = new
    // BigDecimal(buyableDaiAmount).subtract(multiply(medianEthereumPrice, new
    // BigDecimal(ethToSell))); // without transaction costs
    Wad18 potentialProfit =
        getPotentialProfit(bestOfferMedianRatio, buyableDaiAmount, percentageOfProfitAsFee);

    if (potentialProfit.compareTo(balances.getMinimumTradeProfitBuyDai()) > 0)
      return new UniswapOffer(buyableDaiAmount, potentialProfit);
    return new UniswapOffer(new Wad18(), new Wad18());
  }

  @NotNull
  UniswapOffer getProfitableSellDaiOffer(
      Wad18 daiToSell,
      Balances balances,
      Wad18 medianEthereumPrice,
      double percentageOfProfitAsFee) {
    try {
      Wad18 buyableEthAmount =
          new Wad18(this.uniswapContract.getTokenToEthInputPrice(daiToSell.toBigInteger()).send());
      logger.info("BUYABLE ETH AMOUNT {} ETH", buyableEthAmount);

      Wad18 ethDaiRatio = daiToSell.divide(buyableEthAmount);
      logger.info("OFFER ETH PRICE {} ETH/DAI", ethDaiRatio.toString(5));

      Wad18 bestOfferMedianRatio = ethDaiRatio.divide(medianEthereumPrice);
      logger.info("OFFER-MEDIAN RATIO {}", bestOfferMedianRatio.toString(5));

      Wad18 potentialProfit =
          getPotentialProfit(bestOfferMedianRatio, daiToSell, percentageOfProfitAsFee);

      if (potentialProfit.compareTo(balances.getMinimumTradeProfitSellDai()) > 0)
        return new UniswapOffer(buyableEthAmount, potentialProfit);
      return new UniswapOffer(new Wad18(), potentialProfit);
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
    }
    logger.info("POTENTIAL PROFIT 0 DAI");
    return new UniswapOffer(new Wad18(), new Wad18());
  }

  private void buyDaiTransaction(
      EthToTokenSwapInput ethToTokenSwapInput, Wad18 medianEthereumPrice, Balances balances) {
    if (permissions.check("UNISWAP BUY DAI")) {
      try {
        this.gasProvider.updateFastGasPrice(
            medianEthereumPrice, ethToTokenSwapInput.potentialProfit);
        TransactionReceipt transferReceipt =
            this.uniswapContract
                .ethToTokenSwapInput(
                    ethToTokenSwapInput.minTokens.toBigInteger(),
                    ethToTokenSwapInput.deadline.toBigInteger(),
                    ethToTokenSwapInput.ethSold.toBigInteger())
                .send();
        TimeUnit.SECONDS.sleep(
            1); // for Balances to update, otherwise same (buy/sell) type of transaction happens,
        // although not enough balance weth/dai

        logger.trace(
            "Transaction complete, view it at https://etherscan.io/tx/{}",
            transferReceipt.getTransactionHash());
        balances.refreshLastSuccessfulTransaction();

        buyProfitPercentage = Math.max(0.05, buyProfitPercentage - 0.05);
        javaProperties.setValue(
            UNISWAP_BUY_PROFIT_PERCENTAGE, Double.toString(buyProfitPercentage));
        logger.trace("UNISWAP BUY PROFIT PERCENTAGE {}", buyProfitPercentage);

        balances.addToSumEstimatedProfits(ethToTokenSwapInput.potentialProfit);
      } catch (Exception e) {
        circuitBreaker.addTransactionFailedNow();

        buyProfitPercentage = Math.min(0.75, buyProfitPercentage + 0.05);
        javaProperties.setValue(
            UNISWAP_BUY_PROFIT_PERCENTAGE, Double.toString(buyProfitPercentage));
        logger.trace("UNISWAP BUY PROFIT PERCENTAGE {}", buyProfitPercentage);

        balances.addToSumEstimatedMissedProfits(ethToTokenSwapInput.potentialProfit);

        logger.error(EXCEPTION, e);
      }
      balances.updateBalanceInformation(medianEthereumPrice);
    }
  }

  private void sellDaiTransaction(
      TokenToEthSwapInput tokenToEthSwapInput, Wad18 medianEthereumPrice, Balances balances) {
    if (permissions.check("UNISWAP SELL DAI")) {
      try {
        this.gasProvider.updateFastGasPrice(
            medianEthereumPrice, tokenToEthSwapInput.potentialProfit);
        TransactionReceipt transferReceipt =
            this.uniswapContract
                .tokenToEthSwapInput(
                    tokenToEthSwapInput.tokenSold.toBigInteger(),
                    tokenToEthSwapInput.minEth.toBigInteger(),
                    tokenToEthSwapInput.deadline.toBigInteger())
                .send();
        TimeUnit.SECONDS.sleep(
            1); // for Balances to update, otherwise same (buy/sell) type of transaction happens,
        // although not enough balance weth/dai
        logger.info(
            "Transaction complete, view it at https://etherscan.io/tx/{}",
            transferReceipt.getTransactionHash());
        balances.refreshLastSuccessfulTransaction();

        sellProfitPercentage = Math.max(0.05, sellProfitPercentage - 0.05);
        javaProperties.setValue(
            UNISWAP_SELL_PROFIT_PERCENTAGE, Double.toString(sellProfitPercentage));
        logger.info("UNISWAP SELL PROFIT PERCENTAGE {}", sellProfitPercentage);

        balances.addToSumEstimatedProfits(tokenToEthSwapInput.potentialProfit);
      } catch (Exception e) {
        circuitBreaker.addTransactionFailedNow();

        sellProfitPercentage = Math.min(0.75, sellProfitPercentage + 0.05);
        javaProperties.setValue(
            UNISWAP_SELL_PROFIT_PERCENTAGE, Double.toString(sellProfitPercentage));
        logger.info("UNISWAP SELL PROFIT PERCENTAGE {}", sellProfitPercentage);

        balances.addToSumEstimatedMissedProfits(tokenToEthSwapInput.potentialProfit);

        logger.error(EXCEPTION, e);
      }
      balances.updateBalanceInformation(medianEthereumPrice);
    }
  }

  public String getAddress() {
    return ADDRESS;
  }
}
