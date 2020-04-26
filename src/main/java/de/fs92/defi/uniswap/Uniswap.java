package de.fs92.defi.uniswap;

import de.fs92.defi.compounddai.CompoundDai;
import de.fs92.defi.contractneedsprovider.CircuitBreaker;
import de.fs92.defi.contractneedsprovider.ContractNeedsProvider;
import de.fs92.defi.contractneedsprovider.Permissions;
import de.fs92.defi.gasprovider.GasProvider;
import de.fs92.defi.medianizer.Medianizer;
import de.fs92.defi.util.Balances;
import de.fs92.defi.util.IContract;
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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import static de.fs92.defi.util.BigNumberUtil.*;
import static de.fs92.defi.util.ProfitCalculator.getPotentialProfit;

public class Uniswap implements IContract {
  public static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd HH_mm_ss");
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
  private static final String ADDRESS = "0x2a1530C4C41db0B0b2bB646CB5Eb1A67b7158667";
  private static final String UNISWAP_BUY_PROFIT_PERCENTAGE = "uniswapBuyProfitPercentage";
  private static final String UNISWAP_SELL_PROFIT_PERCENTAGE = "uniswapSellProfitPercentage";

  private final UniswapContract contract;
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
    contract = UniswapContract.load(ADDRESS, web3j, credentials, gasProvider);
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
    if (balances.checkTooFewEthOrWeth()) {
      logger.trace("NOT ENOUGH WETH AND ETH TO BUY DAI ON UNISWAP");
      return;
    }
    try {
      logger.trace("UNISWAP BUY DAI PROFIT CALCULATION");
      BigDecimal medianEthereumPrice = Medianizer.getPrice();
      EthToTokenSwapInput ethToTokenSwapInput = getBuyDaiParameters(balances, medianEthereumPrice);

      if (ethToTokenSwapInput != null
          && compoundDai.canOtherProfitMethodsWorkWithoutCDaiConversion(
              balances, ethToTokenSwapInput.potentialProfit, medianEthereumPrice)) {
        buyDaiTransaction(ethToTokenSwapInput, medianEthereumPrice, balances);
      }
    } catch (Exception e) {
      logger.error("Exception", e);
    }
  }

  public void checkIfSellDaiIsProfitableThenDoIt(@NotNull Balances balances) {
    if (balances.checkTooFewDaiAndDaiInCompound()) {
      logger.trace("NOT ENOUGH DAI TO SELL DAI ON UNISWAP");
      return;
    }

    try {
      logger.trace("UNISWAP SELL DAI PROFIT CALCULATION");
      BigDecimal medianEthereumPrice = Medianizer.getPrice();
      TokenToEthSwapInput tokenToEthSwapInput = getSellDaiParameters(balances, medianEthereumPrice);
      if (tokenToEthSwapInput != null
          && compoundDai.canOtherProfitMethodsWorkWithoutCDaiConversion(
              balances, tokenToEthSwapInput.potentialProfit, medianEthereumPrice)) {
        sellDaiTransaction(tokenToEthSwapInput, medianEthereumPrice, balances);
      }
    } catch (Exception e) {
      logger.error("Exception", e);
    }
  }

  EthToTokenSwapInput getBuyDaiParameters(
      @NotNull Balances balances, BigDecimal medianEthereumPrice) throws Exception {
    BigInteger ethToSell =
        balances
            .getWethBalance()
            .add(
                BigDecimal.ZERO.max(
                    balances
                        .getEthBalance()
                        .subtract(Balances.MINIMUM_ETHEREUM_RESERVE_UPPER_LIMIT)))
            .toBigInteger();
    BigInteger buyableDaiAmount;
    try {
      buyableDaiAmount = this.contract.getEthToTokenInputPrice(ethToSell).send();
    } catch (Exception e) {
      logger.error("Exception", e);
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
    if (offer.buyableAmount.compareTo(BigInteger.ZERO) != 0) {
      return calculateBuyDaiParameters(medianEthereumPrice, offer, balances);
    }
    return null;
  }

  TokenToEthSwapInput getSellDaiParameters(
      @NotNull Balances balances, BigDecimal medianEthereumPrice) throws IOException {
    BigInteger daiToSell = balances.getMaxDaiToSell().toBigInteger(); // TODO: test this line
    UniswapOffer offer =
        getProfitableSellDaiOffer(
            daiToSell,
            balances,
            medianEthereumPrice,
            gasProvider.getPercentageOfProfitAsFee(
                gasProvider.getFailedTransactionsWithinTheLastTwelveHoursForGasPriceArrayList()));
    if (offer.buyableAmount.compareTo(BigInteger.ZERO) != 0) {
      return calculateSellDaiParameters(medianEthereumPrice, daiToSell, offer);
    }
    return null;
  }

  @NotNull
  private EthToTokenSwapInput calculateBuyDaiParameters(
      BigDecimal medianEthereumPrice, UniswapOffer offer, @NotNull Balances balances)
      throws IOException {
    long unixTime = System.currentTimeMillis() / 1000L + 300L;
    logger.info("UNISWAP BUY DAI PROFIT CALCULATION");
    logger.trace("ALTERNATIVE DEADLINE {}", unixTime);

    BigInteger deadline =
        web3j
            .ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false)
            .send()
            .getBlock()
            .getTimestamp()
            .add(BigInteger.valueOf(300));
    if (balances
            .getWethBalance()
            .compareTo(
                Balances
                    .MINIMUM_ETHEREUM_RESERVE_UPPER_LIMIT) // TODO: move this line up in the method
                                                           // hierarchy
        > 0) { // TODO: check this line
      logger.trace("PROFIT {}", makeBigNumberHumanReadableFullPrecision(offer.profit));
      weth.weth2Eth(
          balances,
          offer.profit,
          medianEthereumPrice,
          balances.getWethBalance().toBigInteger()); // TODO: check if enough stuff left
    }
    BigInteger profitWillingToGiveUp =
        multiply(offer.profit, makeDoubleMachineReadable(buyProfitPercentage)).toBigInteger();
    BigDecimal actualProfitInUSD = offer.profit.subtract(new BigDecimal(profitWillingToGiveUp));
    BigInteger minTokens = offer.buyableAmount.subtract(profitWillingToGiveUp);
    BigInteger ethSold =
        balances
            .getEthBalance()
            .subtract(Balances.MINIMUM_ETHEREUM_RESERVE_UPPER_LIMIT)
            .toBigInteger();

    // https://stackoverflow.com/questions/39506891/why-is-zoneoffset-utc-zoneid-ofutc
    String formattedDeadline = Instant.ofEpochSecond(unixTime).atZone(ZoneId.of("UTC")).format(dtf);

    logger.trace(
        "PROFIT WILLING TO GIVE UP {}",
        makeBigNumberHumanReadableFullPrecision(profitWillingToGiveUp));
    logger.trace("ETH SOLD {}", makeBigNumberHumanReadableFullPrecision(ethSold));
    logger.trace("MIN TOKENS {}", makeBigNumberHumanReadableFullPrecision(minTokens));
    logger.trace("DEADLINE {}", formattedDeadline);
    logger.trace("PROFIT {}", makeBigNumberHumanReadable(actualProfitInUSD));
    return new EthToTokenSwapInput(minTokens, deadline, ethSold, actualProfitInUSD);
  }

  @NotNull
  private TokenToEthSwapInput calculateSellDaiParameters(
      BigDecimal medianEthereumPrice, BigInteger daiToSell, @NotNull UniswapOffer offer)
      throws IOException {
    long unixTime = System.currentTimeMillis() / 1000L + 300L;
    logger.trace("DEADLINE IN UNIX {}", unixTime);
    BigInteger deadline =
        web3j
            .ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false)
            .send()
            .getBlock()
            .getTimestamp()
            .add(BigInteger.valueOf(300));
    BigInteger profitWillingToGiveUp =
        divide(
            multiply(offer.profit, makeDoubleMachineReadable(sellProfitPercentage)).toBigInteger(),
            divide(daiToSell, offer.buyableAmount));
    BigDecimal actualProfitInUSD =
        offer.profit.subtract(multiply(new BigDecimal(profitWillingToGiveUp), medianEthereumPrice));
    BigInteger minEth = offer.buyableAmount.subtract(profitWillingToGiveUp);

    // https://stackoverflow.com/questions/39506891/why-is-zoneoffset-utc-zoneid-ofutc
    String formatedDeadline = Instant.ofEpochSecond(unixTime).atZone(ZoneId.of("UTC")).format(dtf);

    logger.info(
        "PROFIT WILLING TO GIVE UP {}",
        makeBigNumberHumanReadableFullPrecision(profitWillingToGiveUp));
    logger.info("TOKEN SOLD {}", makeBigNumberHumanReadableFullPrecision(minEth));
    logger.info("MIN ETH {}", makeBigNumberHumanReadableFullPrecision(minEth));
    logger.info("DEADLINE {}", formatedDeadline);
    logger.info("PROFIT {}", makeBigNumberHumanReadable(actualProfitInUSD));
    return new TokenToEthSwapInput(minEth, deadline, daiToSell, actualProfitInUSD);
  }

  @NotNull
  UniswapOffer getProfitableBuyDaiOffer(
      BigInteger buyableDaiAmount,
      BigInteger ethToSell,
      @NotNull Balances balances,
      BigDecimal medianEthereumPrice,
      double percentageOfProfitAsFee) {
    logger.trace(
        "BUYABLE DAI AMOUNT {} DAI", makeBigNumberHumanReadableFullPrecision(buyableDaiAmount));

    BigInteger uniswapBuyDaiPrice = divide(buyableDaiAmount, ethToSell);
    logger.trace("DAI PER ETH {}{}", makeBigNumberHumanReadable(uniswapBuyDaiPrice), " ETH/DAI");

    BigDecimal bestOfferMedianRatio =
        divide(medianEthereumPrice, new BigDecimal(uniswapBuyDaiPrice));
    logger.trace(
        "MEDIAN-OFFER RATIO {}", makeBigNumberHumanReadableFullPrecision(bestOfferMedianRatio));

    //        BigDecimal potentialProfit = new
    // BigDecimal(buyableDaiAmount).subtract(multiply(medianEthereumPrice, new
    // BigDecimal(ethToSell))); // without transaction costs
    BigDecimal potentialProfit =
        getPotentialProfit(
            bestOfferMedianRatio, new BigDecimal(buyableDaiAmount), percentageOfProfitAsFee);

    if (potentialProfit.compareTo(balances.getMinimumTradeProfitBuyDai()) > 0)
      return new UniswapOffer(buyableDaiAmount, potentialProfit);
    return new UniswapOffer(BigInteger.ZERO, BigDecimal.ZERO);
  }

  @NotNull
  UniswapOffer getProfitableSellDaiOffer(
      BigInteger daiToSell,
      Balances balances,
      BigDecimal medianEthereumPrice,
      double percentageOfProfitAsFee) {
    try {
      BigInteger buyableEthAmount = this.contract.getTokenToEthInputPrice(daiToSell).send();
      logger.info(
          "BUYABLE ETH AMOUNT {} ETH", makeBigNumberHumanReadableFullPrecision(buyableEthAmount));

      BigInteger ethDaiRatio = divide(daiToSell, buyableEthAmount);
      logger.info("OFFER ETH PRICE {} ETH/DAI", makeBigNumberHumanReadable(ethDaiRatio));

      BigDecimal bestOfferMedianRatio = divide(new BigDecimal(ethDaiRatio), medianEthereumPrice);
      logger.info("OFFER-MEDIAN RATIO {}", makeBigNumberHumanReadable(bestOfferMedianRatio));

      BigDecimal potentialProfit =
          getPotentialProfit(
              bestOfferMedianRatio, new BigDecimal(daiToSell), percentageOfProfitAsFee);

      if (potentialProfit.compareTo(balances.getMinimumTradeProfitSellDai()) > 0)
        return new UniswapOffer(buyableEthAmount, potentialProfit);
      return new UniswapOffer(BigInteger.ZERO, potentialProfit);
    } catch (Exception e) {
      logger.error("Exception", e);
    }
    logger.info("POTENTIAL PROFIT 0 DAI");
    return new UniswapOffer(BigInteger.ZERO, BigDecimal.ZERO);
  }

  private void buyDaiTransaction(
      EthToTokenSwapInput ethToTokenSwapInput, BigDecimal medianEthereumPrice, Balances balances) {
    if (permissions.check("UNISWAP BUY DAI")) {
      try {
        this.gasProvider.updateFastGasPrice(
            ethToTokenSwapInput.potentialProfit, medianEthereumPrice);
        TransactionReceipt transferReceipt =
            this.contract
                .ethToTokenSwapInput(
                    ethToTokenSwapInput.minTokens,
                    ethToTokenSwapInput.deadline,
                    ethToTokenSwapInput.ethSold)
                .send();
        TimeUnit.SECONDS.sleep(
            1); // for Balances to update, otherwise same (buy/sell) type of transaction happens,
        // although not enough balance weth/dai

        logger.trace(
            "Transaction complete, view it at https://etherscan.io/tx/{}",
            transferReceipt.getTransactionHash());
        balances.refreshLastSuccessfulTransaction();

        buyProfitPercentage = Math.max(0.05, buyProfitPercentage - 0.05);
        javaProperties.updateValue(
            UNISWAP_BUY_PROFIT_PERCENTAGE, Double.toString(buyProfitPercentage));
        logger.trace("UNISWAP BUY PROFIT PERCENTAGE {}", buyProfitPercentage);

        balances.addToSumEstimatedProfits(ethToTokenSwapInput.potentialProfit);
      } catch (Exception e) {
        circuitBreaker.add(System.currentTimeMillis());

        buyProfitPercentage = Math.min(0.75, buyProfitPercentage + 0.05);
        javaProperties.updateValue(
            UNISWAP_BUY_PROFIT_PERCENTAGE, Double.toString(buyProfitPercentage));
        logger.trace("UNISWAP BUY PROFIT PERCENTAGE {}", buyProfitPercentage);

        balances.addToSumEstimatedMissedProfits(ethToTokenSwapInput.potentialProfit);

        logger.error("Exception", e);
      }
      balances.updateBalanceInformation(medianEthereumPrice);
    }
  }

  private void sellDaiTransaction(
      TokenToEthSwapInput tokenToEthSwapInput, BigDecimal medianEthereumPrice, Balances balances) {
    if (permissions.check("UNISWAP SELL DAI")) {
      try {
        this.gasProvider.updateFastGasPrice(
            tokenToEthSwapInput.potentialProfit, medianEthereumPrice);
        TransactionReceipt transferReceipt =
            this.contract
                .tokenToEthSwapInput(
                    tokenToEthSwapInput.tokenSold,
                    tokenToEthSwapInput.minEth,
                    tokenToEthSwapInput.deadline)
                .send();
        TimeUnit.SECONDS.sleep(
            1); // for Balances to update, otherwise same (buy/sell) type of transaction happens,
        // although not enough balance weth/dai
        logger.info(
            "Transaction complete, view it at https://etherscan.io/tx/{}",
            transferReceipt.getTransactionHash());
        balances.refreshLastSuccessfulTransaction();

        sellProfitPercentage = Math.max(0.05, sellProfitPercentage - 0.05);
        javaProperties.updateValue(
            UNISWAP_SELL_PROFIT_PERCENTAGE, Double.toString(sellProfitPercentage));
        logger.info("UNISWAP SELL PROFIT PERCENTAGE {}", sellProfitPercentage);

        balances.addToSumEstimatedProfits(tokenToEthSwapInput.potentialProfit);
      } catch (Exception e) {
        circuitBreaker.add(System.currentTimeMillis());

        sellProfitPercentage = Math.min(0.75, sellProfitPercentage + 0.05);
        javaProperties.updateValue(
            UNISWAP_SELL_PROFIT_PERCENTAGE, Double.toString(sellProfitPercentage));
        logger.info("UNISWAP SELL PROFIT PERCENTAGE {}", sellProfitPercentage);

        balances.addToSumEstimatedMissedProfits(tokenToEthSwapInput.potentialProfit);

        logger.error("Exception", e);
      }
      balances.updateBalanceInformation(medianEthereumPrice);
    }
  }

  public String getAddress() {
    return ADDRESS;
  }
}
