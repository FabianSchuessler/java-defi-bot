package de.fs92.defi.uniswap;

import java.math.BigDecimal;
import java.math.BigInteger;

public class TokenToEthSwapInput {
  public final BigInteger minEth;
  public final BigInteger deadline;
  public final BigInteger tokenSold;
  public final BigDecimal potentialProfit;

  public TokenToEthSwapInput(
      BigInteger minEth, BigInteger deadline, BigInteger tokenSold, BigDecimal potentialProfit) {
    this.minEth = minEth;
    this.deadline = deadline;
    this.tokenSold = tokenSold;
    this.potentialProfit = potentialProfit;
  }
}
