package de.fs92.defi.uniswap;

import java.math.BigInteger;

public class UniswapOffer {
  public final BigInteger buyableAmount;
  public final BigInteger profit;

  public UniswapOffer(BigInteger buyableAmount, BigInteger profit) {
    this.buyableAmount = buyableAmount;
    this.profit = profit;
  }
}
