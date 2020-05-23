package de.fs92.defi.numberutil;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class NumberUtil {
  public static final BigInteger UINT_MAX =
      new BigInteger(
          "115792089237316195423570985008687907853269984665640564039457584007913129639935"); // 2^256-1
  /**
   * MINIMUM_APPROVAL_ALLOWANCE seems to be the maximal returned value instead of UINT_MAX TODO: ask
   * on github if this is necessary because of an web3j bug
   */
  public static final BigInteger MINIMUM_APPROVAL_ALLOWANCE =
      new BigInteger("115792089237316195423570985008687907853269");

  private NumberUtil() {}

  @NotNull
  public static BigInteger getMachineReadable(Double smallNumber) {
    if (smallNumber == 0.0) return BigInteger.ZERO;
    return BigDecimal.valueOf(smallNumber * Math.pow(10, 18)).toBigInteger();
  }
}
