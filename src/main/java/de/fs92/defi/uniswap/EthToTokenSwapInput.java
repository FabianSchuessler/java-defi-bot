package de.fs92.defi.uniswap;

import de.fs92.defi.numberutil.Wad18;

public class EthToTokenSwapInput {
  public final Wad18 minTokens;
  public final Wad18 deadline;
  public final Wad18 ethSold;
  public final Wad18 potentialProfit;

  public EthToTokenSwapInput(
          Wad18 minTokens, Wad18 deadline, Wad18 ethSold, Wad18 potentialProfit) {
    this.minTokens = minTokens;
    this.deadline = deadline;
    this.ethSold = ethSold;
    this.potentialProfit = potentialProfit;
  }
}
