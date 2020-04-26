package de.fs92.defi.util;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class BigNumberUtil {
  public static final BigInteger BIGGEST_NUMBER =
      new BigInteger("115792089237316195423570985008687907853269984665640564039457");

  private BigNumberUtil() {
    throw new IllegalStateException("Utility class");
  }

  @NotNull
  public static BigDecimal makeDoubleMachineReadable(Double smallNumber) {
    return BigDecimal.valueOf(smallNumber * Math.pow(10, 18));
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
    return dividend.multiply(BigInteger.valueOf(1000000000000000000L)).divide(divisor);
  }

  public static BigDecimal divide(@NotNull BigDecimal dividend, BigDecimal divisor) {
    return dividend
        .multiply(BigDecimal.valueOf(Math.pow(10, 18)))
        .divide(divisor, 0, RoundingMode.DOWN)
        .stripTrailingZeros();
  }

  public static double makeBigNumberCurrencyHumanReadable(@NotNull BigDecimal bigNumber) {
    return Math.round((bigNumber.floatValue() / Math.pow(10, 18)) * 100.0) / 100.0;
  }

  public static double makeBigNumberCurrencyHumanReadable(@NotNull BigInteger bigNumber) {
    return Math.round((bigNumber.floatValue() / Math.pow(10, 18)) * 100.0) / 100.0;
  }

  public static double makeBigNumberHumanReadable(@NotNull BigDecimal bigNumber) {
    return Math.round((bigNumber.floatValue() / Math.pow(10, 18)) * 10000.0) / 10000.0;
  }

  public static double makeBigNumberHumanReadable(@NotNull BigInteger bigNumber) {
    return Math.round((bigNumber.floatValue() / Math.pow(10, 18)) * 10000.0) / 10000.0;
  }

  @NotNull
  public static String makeBigNumberHumanReadableFullPrecision(@NotNull BigDecimal bigNumber) {
    String number = StringUtils.leftPad(bigNumber.toString(), 18, "0");
    if (number.length() == 18) {
      if (new BigDecimal(number).compareTo(BigDecimal.ZERO) == 0) return "0.000000000000000000";
      return "0." + number;
    } else {
      return number.substring(0, number.length() - 18)
          + "."
          + number.substring(number.length() - 18);
    }
  }

  @NotNull
  public static String makeBigNumberHumanReadableFullPrecision(@NotNull BigInteger bigNumber) {
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
  public static String makeBigNumberHumanReadableFullPrecision(
      @NotNull BigInteger bigNumber, int decimals) {
    String number = StringUtils.leftPad(bigNumber.toString(), decimals, "0");
    if (number.length() == decimals) {
      return "0." + number;
    } else {
      return number.substring(0, number.length() - decimals)
          + "."
          + number.substring(number.length() - decimals);
    }
  }
}
