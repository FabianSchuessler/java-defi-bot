package de.fs92.defi.util;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

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

  public static BigInteger multiply(@NotNull BigInteger multiplicand1, BigInteger multiplicand2) {
    return multiplicand1.multiply(multiplicand2).divide(BigInteger.valueOf(1000000000000000000L));
  }

  public static BigDecimal multiply(@NotNull BigDecimal multiplicand1, BigDecimal multiplicand2) {
    return multiplicand1
        .multiply(multiplicand2)
        .divide(BigDecimal.valueOf(Math.pow(10, 18)), RoundingMode.DOWN);
  }

  public static BigInteger divide(@NotNull BigInteger dividend, BigInteger divisor) {
    if (divisor.compareTo(BigInteger.ZERO) == 0) throw new IllegalArgumentException("Argument 'divisor' is 0");
    return dividend.multiply(BigInteger.valueOf(1000000000000000000L)).divide(divisor);
  }

  public static BigDecimal divide(@NotNull BigDecimal dividend, BigDecimal divisor) {
    if (divisor.compareTo(BigDecimal.ZERO) == 0) throw new IllegalArgumentException("Argument 'divisor' is 0");
    return dividend
        .multiply(BigDecimal.valueOf(Math.pow(10, 18)))
        .divide(divisor, 0, RoundingMode.DOWN)
        .stripTrailingZeros();
  }

  public static double getCurrency(@NotNull BigDecimal bigNumber) {
    return Math.round((bigNumber.floatValue() / Math.pow(10, 18)) * 100.0) / 100.0;
  }

  public static @NotNull String getCurrency(@NotNull BigInteger bigNumber) {
    return getPrecision(bigNumber, 2);
  }

  public static double getHumanReadable(@NotNull BigDecimal bigNumber) {
    return Math.round((bigNumber.floatValue() / Math.pow(10, 18)) * 10000.0) / 10000.0;
  }

  public static @NotNull String getHumanReadable(@NotNull BigInteger bigNumber) {
    return getPrecision(bigNumber, 4);
  }

  private static @NotNull String getPrecision(@NotNull BigInteger bigNumber, int decimals) {
    return String.valueOf(
        Math.round((bigNumber.floatValue() / Math.pow(10, 18)) * Math.pow(10, decimals))
            / Math.pow(10, decimals));
  }

  // INFO: have a look at the method and its different usages, maybe refactor this method
  @NotNull
  public static String getFullPrecision(@NotNull BigDecimal bigNumber) {
    String number = StringUtils.leftPad(bigNumber.toPlainString(), 18, "0");
    if (number.length() == 18) {
      if (new BigDecimal(number).compareTo(BigDecimal.ZERO) == 0) return "0.000000000000000000";
      return "0." + number;
    } else {
      String[] splitString = number.split("\\.");
      if (splitString.length == 2) {
        String decimal = StringUtils.rightPad(splitString[1], 18, "0");
        decimal = decimal.substring(0, 18);
        return splitString[0] + "." + decimal;
      }
      return number.substring(0, number.length() - 18)
          + "."
          + number.substring(number.length() - 18);
    }
  }

  // TODO: test this method
  @NotNull
  public static String getFullPrecision(@NotNull BigInteger bigNumber) {
    String number = StringUtils.leftPad(bigNumber.toString(), 18, "0");
    if (number.length() == 18) {
      return "0." + number;
    } else {
      return number.substring(0, number.length() - 18)
          + "."
          + number.substring(number.length() - 18);
    }
  }

  @NotNull
  public static String getFullPrecision(@NotNull BigInteger bigNumber, int decimals) {
    String number = StringUtils.leftPad(bigNumber.toString(), decimals, "0");
    if (number.length() == decimals) {
      return "0." + number;
    } else {
      // TODO: test this method with following input 15401073595382400000000.
      return number.substring(0, number.length() - decimals)
          + "."
          + number.substring(number.length() - decimals);
    }
  }

  // TODO: test this method
  @NotNull
  public static BigInteger convertUint256toBigInteger(@NotNull BigInteger bigNumber) {
    return bigNumber.divide(BigDecimal.valueOf(Math.pow(10, 27)).toBigInteger());
  }
}
