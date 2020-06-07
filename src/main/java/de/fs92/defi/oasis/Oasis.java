package de.fs92.defi.oasis;

import de.fs92.defi.compounddai.CompoundDai;
import de.fs92.defi.contractneedsprovider.CircuitBreaker;
import de.fs92.defi.contractneedsprovider.ContractNeedsProvider;
import de.fs92.defi.contractneedsprovider.Permissions;
import de.fs92.defi.contractuserutil.AddressMethod;
import de.fs92.defi.dai.Dai;
import de.fs92.defi.gasprovider.GasProvider;
import de.fs92.defi.medianizer.MedianException;
import de.fs92.defi.medianizer.Medianizer;
import de.fs92.defi.numberutil.Wad18;
import de.fs92.defi.util.Balances;
import de.fs92.defi.weth.Weth;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple4;

import java.lang.invoke.MethodHandles;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static de.fs92.defi.util.ProfitCalculator.getPotentialProfit;

public class Oasis implements AddressMethod {
  public static final String ADDRESS = "0x794e6e91555438aFc3ccF1c5076A74F42133d08D";
  private static final String EXCEPTION = "Exception";
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
  private final Weth weth;
  private final CompoundDai compoundDai;
  private final GasProvider gasProvider;
  private final Permissions permissions;
  private final CircuitBreaker circuitBreaker;
  private final OasisContract uniswapContract;

  public Oasis(
      @NotNull ContractNeedsProvider contractNeedsProvider, CompoundDai compoundDai, Weth weth) {
    Web3j web3j = contractNeedsProvider.getWeb3j();
    Credentials credentials = contractNeedsProvider.getCredentials();
    permissions = contractNeedsProvider.getPermissions();
    gasProvider = contractNeedsProvider.getGasProvider();
    circuitBreaker = contractNeedsProvider.getCircuitBreaker();
    uniswapContract = OasisContract.load(ADDRESS, web3j, credentials, gasProvider);
    this.compoundDai = compoundDai;
    this.weth = weth;
  }

  /**
   * TODO: test this method
   *
   * @param bestOffer current best offer
   * @return offerValues can be empty
   * @throws Exception calling uniSwap Contract can cause Exception
   */
  @NotNull
  Map<String, Wad18> getOffer(BigInteger bestOffer) throws Exception {
    Map<String, Wad18> offerValues = new HashMap<>();
    Tuple4<BigInteger, String, BigInteger, String> getOffer =
        uniswapContract.getOffer(bestOffer).send();

    offerValues.put(getOffer.component2(), new Wad18(getOffer.component1()));
    offerValues.put(getOffer.component4(), new Wad18(getOffer.component3()));

    if (!(getOffer.component2().equalsIgnoreCase(Weth.ADDRESS)
            && getOffer.component4().equalsIgnoreCase(Dai.ADDRESS))
        && (!(getOffer.component2().equalsIgnoreCase(Dai.ADDRESS)
            && getOffer.component4().equalsIgnoreCase(Weth.ADDRESS)))) {
      logger.info("BIG INTEGER 1 {}", getOffer.component1());
      logger.info("CONTRACT ADDRESS 1 {}", getOffer.component2());
      logger.info("BIG INTEGER 2 {}", getOffer.component3());
      logger.info("CONTRACT ADDRESS 2 {}", getOffer.component4());
      throw new DaiOrWethMissingException("BOTH DAI AND WETH NEED TO BE PRESENT ONCE.");
    }
    return offerValues;
  }

  // TODO: methods calling this method should check for BigInteger.ZERO to improve their performance
  BigInteger getBestOffer(String buyAddress, String sellAddress) {
    try {
      return uniswapContract.getBestOffer(buyAddress, sellAddress).send();
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
      return BigInteger.ZERO;
    }
  }

  public void checkIfBuyDaiIsProfitableThenDoIt(@NotNull Balances balances) {
    logger.trace("");
    if (balances.isThereTooFewEthAndWethForSaleAndLending(balances.ethereum)) {
      logger.info("NOT ENOUGH WETH AND ETH TO BUY DAI ON OASIS");
      return;
    }
    try {
      logger.info("OASIS BUY DAI PROFIT CALCULATION");
      Wad18 medianEthereumPrice = Medianizer.getPrice();
      OasisOffer bestOffer =
          buyDaiSellWethIsProfitable(
              medianEthereumPrice,
              balances,
              gasProvider.getPercentageOfProfitAsFee(
                  gasProvider.getFailedTransactionsWithinTheLastTwelveHoursForGasPriceArrayList()));
      if (bestOffer.offerId.compareTo(BigInteger.ZERO) != 0) {
        String weiValue = "100000000"; // INFO: seems to be necessary due to rounding error
        Wad18 wethBalance = balances.weth.getAccount().getBalance();
        Wad18 ethBalance = balances.ethereum.getBalanceWithoutMinimumEthereumReserveUpperLimit();
        Wad18 ownConstraint =
            wethBalance
                .add(ethBalance)
                .multiply(bestOffer.bestOfferDaiPerEth)
                .subtract(new Wad18(new BigInteger(weiValue)));
        Wad18 offerConstraint = bestOffer.offerValues.get(Dai.ADDRESS);
        logger.debug("OWN CONSTRAINT {}", ownConstraint);
        logger.debug("OFFER CONSTRAINT {}", offerConstraint);
        if (balances
                .ethereum
                .getBalance()
                .compareTo(balances.ethereum.minimumEthereumReserveUpperLimit)
            > 0) {
          weth.eth2Weth(
              balances.ethereum.getBalanceWithoutMinimumEthereumReserveUpperLimit(),
              bestOffer.profit,
              medianEthereumPrice,
              balances);
        }
        takeOrder(
            bestOffer.offerId,
            ownConstraint.min(offerConstraint),
            bestOffer.profit,
            medianEthereumPrice,
            balances);
      }
    } catch (MedianException e) {
      logger.error(EXCEPTION, e);
    }
  }

  public void checkIfSellDaiIsProfitableThenDoIt(@NotNull Balances balances) {
    logger.trace("");
    if (balances.isThereTooFewDaiAndDaiInCompoundForSale()) {
      logger.info("NOT ENOUGH DAI TO SELL DAI ON OASIS");
      return;
    }
    try {
      logger.trace("OASIS SELL DAI PROFIT CALCULATION");
      Wad18 medianEthereumPrice = Medianizer.getPrice();
      Wad18 maxDaiToSell = balances.getMaxDaiToSell();
      OasisOffer bestOffer =
          sellDaiBuyWethIsProfitable(
              medianEthereumPrice,
              maxDaiToSell,
              balances,
              gasProvider.getPercentageOfProfitAsFee(
                  gasProvider.getFailedTransactionsWithinTheLastTwelveHoursForGasPriceArrayList()));
      if (bestOffer.offerId.compareTo(BigInteger.ZERO) != 0) {
        Wad18 ownConstraint = balances.getMaxDaiToSell().divide(bestOffer.bestOfferDaiPerEth);
        Wad18 offerConstraint = bestOffer.offerValues.get(Weth.ADDRESS);
        logger.debug("OWN CONSTRAINT {}", ownConstraint);
        logger.debug("OFFER CONSTRAINT {}", offerConstraint);
        if (compoundDai.canOtherProfitMethodsWorkWithoutCDaiConversion(
            balances, bestOffer.profit, medianEthereumPrice)) {
          takeOrder(
              bestOffer.offerId,
              ownConstraint.min(offerConstraint),
              bestOffer.profit,
              medianEthereumPrice,
              balances);
        }
      }
    } catch (MedianException e) {
      logger.error(EXCEPTION, e);
    }
  }

  @NotNull
  private OasisOffer buyDaiSellWethIsProfitable(
      Wad18 medianEthereumPrice, Balances balances, double percentageOfProfitAsFee) {
    BigInteger bestOffer = getBestOffer(Dai.ADDRESS, Weth.ADDRESS);
    logger.trace("BEST BUY-DAI-OFFER ID {}", bestOffer);
    Map<String, Wad18> offerValues;
    try {
      offerValues = getOffer(bestOffer);
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
      return new OasisOffer(BigInteger.ZERO, null, null, Wad18.ZERO);
    }
    logger.trace("BUYABLE DAI AMOUNT {}{}", offerValues.get(Dai.ADDRESS), " DAI");
    logger.trace("SELLABLE WETH AMOUNT {}{}", offerValues.get(Weth.ADDRESS), " WETH");
    if (!offerValues.get(Weth.ADDRESS).toBigInteger().equals(BigInteger.ZERO)) {
      Wad18 bestOfferEthDaiRatioBuyDai =
          offerValues.get(Dai.ADDRESS).divide(offerValues.get(Weth.ADDRESS));
      Wad18 bestOfferMedianRatio = medianEthereumPrice.divide(bestOfferEthDaiRatioBuyDai);
      logger.trace("DAI PER WETH {}{}", bestOfferEthDaiRatioBuyDai.toString(5), " WETH/DAI");
      logger.trace("MEDIAN-OFFER RATIO {}", bestOfferMedianRatio);
      Wad18 wethBalance = balances.weth.getAccount().getBalance();
      Wad18 ethBalance = balances.ethereum.getBalanceWithoutMinimumEthereumReserveUpperLimit();
      Wad18 potentialProfit =
          getPotentialProfit(
              bestOfferMedianRatio,
              ((wethBalance.add(ethBalance)).min(offerValues.get(Weth.ADDRESS)))
                  .multiply(bestOfferEthDaiRatioBuyDai),
              percentageOfProfitAsFee);
      if (potentialProfit.compareTo(balances.getMinimumTradeProfitBuyDai()) > 0) {
        return new OasisOffer(bestOffer, offerValues, bestOfferEthDaiRatioBuyDai, potentialProfit);
      }
    } else {
      logger.trace("OFFER TAKEN DURING PROCESSING!");
    }
    return new OasisOffer(BigInteger.ZERO, null, null, Wad18.ZERO);
  }

  @NotNull
  private OasisOffer sellDaiBuyWethIsProfitable(
      Wad18 medianEthereumPrice,
      Wad18 maxDaiToSell,
      Balances balances,
      double percentageOfProfitAsFee) {
    BigInteger bestOffer = getBestOffer(Weth.ADDRESS, Dai.ADDRESS);
    logger.trace("BEST SELL-DAI-OFFER ID {}", bestOffer);
    Map<String, Wad18> offerValues;
    try {
      offerValues = getOffer(bestOffer);
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
      return new OasisOffer(BigInteger.ZERO, null, null, Wad18.ZERO);
    }
    logger.trace("BUYABLE WETH AMOUNT {}{}", offerValues.get(Weth.ADDRESS), " WETH");
    logger.trace("SELLABLE DAI AMOUNT {}{}", offerValues.get(Dai.ADDRESS), " DAI");

    if (!offerValues.get(Weth.ADDRESS).equals(Wad18.ZERO)) {
      Wad18 bestOfferEthDaiRatioSellDai =
          offerValues.get(Dai.ADDRESS).divide(offerValues.get(Weth.ADDRESS));
      Wad18 bestOfferMedianRatio = bestOfferEthDaiRatioSellDai.divide(medianEthereumPrice);
      logger.trace("OFFER ETH PRICE {}{}", bestOfferEthDaiRatioSellDai.toString(5), " WETH/DAI");
      logger.trace("OFFER-MEDIAN RATIO {}", bestOfferMedianRatio.toString(5));
      Wad18 potentialProfit =
          getPotentialProfit(
              bestOfferMedianRatio,
              maxDaiToSell.min(offerValues.get(Dai.ADDRESS)),
              percentageOfProfitAsFee);
      // TODO: do constraints already here

      if (potentialProfit.compareTo(balances.getMinimumTradeProfitSellDai()) > 0) {
        return new OasisOffer(bestOffer, offerValues, bestOfferEthDaiRatioSellDai, potentialProfit);
      }
    } else {
      logger.trace("OFFER TAKEN DURING PROCESSING!");
    }
    return new OasisOffer(BigInteger.ZERO, null, null, Wad18.ZERO);
  }

  private void takeOrder(
      BigInteger offerId,
      Wad18 amountToBuy,
      Wad18 potentialProfit,
      Wad18 medianEthereumPrice,
      Balances balances) {
    if (permissions.check("OASIS BUY ORDER")) {
      try {
        gasProvider.updateFastGasPrice(medianEthereumPrice, potentialProfit);
        TransactionReceipt transferReceipt =
            uniswapContract.buy(offerId, amountToBuy.toBigInteger()).send();
        logger.info(
            "Transaction complete, view it at https://etherscan.io/tx/{}",
            transferReceipt.getTransactionHash());
        TimeUnit.SECONDS.sleep(
            1); // for Balances to update, otherwise same (buy/sell) type of transaction happens,
        // although not enough balance weth/dai
        balances.refreshLastSuccessfulTransaction();
        balances.addToSumEstimatedProfits(potentialProfit);
      } catch (Exception e) {
        circuitBreaker.addTransactionFailedNow();
        balances.addToSumEstimatedMissedProfits(potentialProfit);
        logger.error(EXCEPTION, e);
      }
      balances.updateBalanceInformation(medianEthereumPrice);
    }
  }

  public String getAddress() {
    return ADDRESS;
  }
}
