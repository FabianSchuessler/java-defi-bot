package de.fs92.defi.oasis;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

public class OasisOffer {
  public final BigInteger offerId;
  public final Map<String, BigDecimal> offerValues;
  public final BigDecimal bestOfferDaiPerEth;
  public final BigDecimal profit;

  public OasisOffer(
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
