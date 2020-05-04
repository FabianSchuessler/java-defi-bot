package de.fs92.defi.util;

import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;

import static de.fs92.defi.util.BigNumberUtil.*;

public class ProfitCalculator {
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

  private ProfitCalculator() {
    throw new IllegalStateException("Utility class");
  }

  @NotNull // todo: think about moving these methods into uniswapoffer, ethtokenswapinput and
  // tokentoethswapinput
  public static BigDecimal getPotentialProfit(
      BigDecimal bestOfferMedianRatio, BigDecimal toSellInDAI, double percentageOfProfitAsFee) {
    BigDecimal potentialProfitBeforeCosts =
        multiply(makeDoubleMachineReadable(1.0).subtract(bestOfferMedianRatio), toSellInDAI);
    logger.trace(
        "POTENTIAL PROFIT BEFORE COSTS {}{}",
        makeBigNumberHumanReadable(potentialProfitBeforeCosts),
        " DAI");
    BigDecimal maxTransactionCosts =
        makeDoubleMachineReadable(0.50)
            .max(
                multiply(
                    potentialProfitBeforeCosts,
                    BigNumberUtil.makeDoubleMachineReadable(percentageOfProfitAsFee)));
    BigDecimal potentialProfitAfterCosts = potentialProfitBeforeCosts.subtract(maxTransactionCosts);

    if (potentialProfitAfterCosts.compareTo(BigDecimal.ZERO) > 0) {
      logger.trace(
          "POTENTIAL PROFIT +{}{}",
          BigNumberUtil.makeBigNumberCurrencyHumanReadable(potentialProfitAfterCosts),
          " DAI");
    } else {
      logger.trace(
          "POTENTIAL PROFIT {}{}",
          BigNumberUtil.makeBigNumberCurrencyHumanReadable(potentialProfitAfterCosts),
          " DAI");
    }

    return potentialProfitAfterCosts;
  }
}
