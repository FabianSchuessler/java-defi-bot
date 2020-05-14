package de.fs92.defi.uniswap;

import java.math.BigInteger;

public class EthToTokenSwapInput {
  public final BigInteger minTokens;
  public final BigInteger deadline;
  public final BigInteger ethSold;
  public final BigInteger potentialProfit;

  public EthToTokenSwapInput(
      BigInteger minTokens, BigInteger deadline, BigInteger ethSold, BigInteger potentialProfit) {
    this.minTokens = minTokens;
    this.deadline = deadline;
    this.ethSold = ethSold;
    this.potentialProfit = potentialProfit;
  }
}
