package de.fs92.defi.uniswap;

import de.fs92.defi.numberutil.Wad18;

public class UniswapOffer {
  public final Wad18 buyableAmount;
  public final Wad18 profit;

  public UniswapOffer(Wad18 buyableAmount, Wad18 profit) {
    this.buyableAmount = buyableAmount;
    this.profit = profit;
  }
}
