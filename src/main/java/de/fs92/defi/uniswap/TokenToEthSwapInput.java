package de.fs92.defi.uniswap;

import de.fs92.defi.numberutil.Wad18;

public class TokenToEthSwapInput {
  public final Wad18 minEth;
  public final Wad18 deadline;
  public final Wad18 tokenSold;
  public final Wad18 potentialProfit;

  public TokenToEthSwapInput(Wad18 minEth, Wad18 deadline, Wad18 tokenSold, Wad18 potentialProfit) {
    this.minEth = minEth;
    this.deadline = deadline;
    this.tokenSold = tokenSold;
    this.potentialProfit = potentialProfit;
  }
}
