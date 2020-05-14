package de.fs92.defi.util;

import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.math.BigInteger;

import static de.fs92.defi.util.NumberUtil.*;

public class ProfitCalculator {
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

  private ProfitCalculator() {
    throw new IllegalStateException("Utility class");
  }

  @NotNull // todo: think about moving these methods into the classes uniswapoffer,
           // ethtokenswapinput and
  // tokentoethswapinput
  public static BigInteger getPotentialProfit(
      BigInteger bestOfferMedianRatio, BigInteger toSellInDAI, double percentageOfProfitAsFee) {
    BigInteger potentialProfitBeforeCosts =
        multiply(getMachineReadable(1.0).subtract(bestOfferMedianRatio), toSellInDAI);
    logger.trace(
        "POTENTIAL PROFIT BEFORE COSTS {}{}", getHumanReadable(potentialProfitBeforeCosts), " DAI");
    BigInteger maxTransactionCosts =
        getMachineReadable(0.50)
            .max(multiply(potentialProfitBeforeCosts, getMachineReadable(percentageOfProfitAsFee)));
    BigInteger potentialProfitAfterCosts = potentialProfitBeforeCosts.subtract(maxTransactionCosts);

    if (potentialProfitAfterCosts.compareTo(BigInteger.ZERO) > 0) {
      logger.trace("POTENTIAL PROFIT +{}{}", getCurrency(potentialProfitAfterCosts), " DAI");
    } else {
      logger.trace("POTENTIAL PROFIT {}{}", getCurrency(potentialProfitAfterCosts), " DAI");
    }

    return potentialProfitAfterCosts;
  }
}
