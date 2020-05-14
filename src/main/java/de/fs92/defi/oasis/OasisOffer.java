package de.fs92.defi.oasis;

import java.math.BigInteger;
import java.util.Map;

public class OasisOffer {
  public final BigInteger offerId;
  public final Map<String, BigInteger> offerValues;
  public final BigInteger bestOfferDaiPerEth;
  public final BigInteger profit;

  public OasisOffer(
      BigInteger offerId,
      Map<String, BigInteger> offerValues,
      BigInteger bestOfferDaiPerEth,
      BigInteger profit) {
    this.offerId = offerId;
    this.offerValues = offerValues;
    this.bestOfferDaiPerEth = bestOfferDaiPerEth;
    this.profit = profit;
  }
}
