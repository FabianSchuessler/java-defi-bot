package de.fs92.defi.oasisdex;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

public class OasisDexOffer {
  public final BigInteger offerId;
  public final Map<String, BigDecimal> offerValues;
  public final BigDecimal bestOfferDaiPerEth;
  public final BigDecimal profit;

  public OasisDexOffer(
      BigInteger offerId,
      Map<String, BigDecimal> offerValues,
      BigDecimal bestOfferDaiPerEth,
      BigDecimal profit) {
    this.offerId = offerId;
    this.offerValues = offerValues;
    this.bestOfferDaiPerEth = bestOfferDaiPerEth;
    this.profit = profit;
  }
}
