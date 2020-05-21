package de.fs92.defi.util;

import de.fs92.defi.numberutil.Wad18;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.math.BigInteger;

import static de.fs92.defi.numberutil.NumberUtil.getMachineReadable;

public class ProfitCalculator {
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

  private ProfitCalculator() {
    throw new IllegalStateException("Utility class");
  }

  @NotNull // todo: think about moving these methods into the classes uniswapoffer,
  // ethtokenswapinput and
  // tokentoethswapinput
  public static Wad18 getPotentialProfit(
          Wad18 bestOfferMedianRatio, Wad18 toSellInDAI, double percentageOfProfitAsFee) {
    Wad18 potentialProfitBeforeCosts =
            new Wad18(getMachineReadable(1.0)).subtract(bestOfferMedianRatio).multiply(toSellInDAI);
    logger.trace(
            "POTENTIAL PROFIT BEFORE COSTS {}{}", potentialProfitBeforeCosts.toString(5), " DAI");
    Wad18 maxTransactionCosts =
            new Wad18(getMachineReadable(0.50))
                    .max(potentialProfitBeforeCosts.multiply(new Wad18(getMachineReadable(percentageOfProfitAsFee))));
    Wad18 potentialProfitAfterCosts = potentialProfitBeforeCosts.subtract(maxTransactionCosts);

    if (potentialProfitAfterCosts.compareTo(BigInteger.ZERO) > 0) {
      logger.trace("POTENTIAL PROFIT +{}{}", potentialProfitAfterCosts.toString(2), " DAI");
    } else {
      logger.trace("POTENTIAL PROFIT {}{}", potentialProfitAfterCosts.toString(2), " DAI");
    }

    return potentialProfitAfterCosts;
  }
}
