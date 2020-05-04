package de.fs92.defi.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static de.fs92.defi.util.BigNumberUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BigNumberUtilTest {
  private static final BigDecimal SOME_NUMBER = new BigDecimal("1454141938282760506");

  @Test
  public void makeBigNumberHumanReadableFullPrecision_numbersAsString_returnNumber() {
    String expected = "98.765432109876543210";
    String actual =
        makeBigNumberHumanReadableFullPrecision(new BigDecimal("098765432109876543210"));
    assertEquals(expected, actual);
  }

  @Test
  public void makeBigNumberHumanReadableFullPrecision_zeroAsString_returnZero() {
    String expected = "0.000000000000000000";
    String actual = makeBigNumberHumanReadableFullPrecision(new BigDecimal("000000000000000000"));
    assertEquals(expected, actual);
  }

  @Test
  public void makeBigNumberHumanReadableFullPrecision_bigNumberAsString_ReturnBigNumber() {
    String expected = "99999.000000000000000001";
    String actual =
        makeBigNumberHumanReadableFullPrecision(new BigDecimal("99999000000000000000001"));
    assertEquals(expected, actual);
  }

  @Test
  public void makeBigNumberHumanReadableFullPrecision_zeroAsStringWithDot_returnZero() {
    String expected = "0.000000000000000000";
    String actual = makeBigNumberHumanReadableFullPrecision(new BigDecimal("0.0000000000000000"));
    assertEquals(expected, actual);
  }

  @Test
  public void makeBigNumberHumanReadableFullPrecision_zeroAsStringWithDot_returnZero2() {
    String expected = "201.000000000000000000";
    String actual =
        makeBigNumberHumanReadableFullPrecision(new BigDecimal("201.0000000000000000000"));
    assertEquals(expected, actual);
  }

  @Test
  public void multiply_twoNumbers_returnMultiplication() {
    BigDecimal expected = new BigDecimal("145414193828276050");
    BigDecimal actual = multiply(new BigDecimal("100000000000000000"), SOME_NUMBER);
    assertEquals(expected, actual);
  }

  @Test
  public void multiply_subOneNumber_returnMultiplication() {
    BigDecimal expected = new BigDecimal("0.1");
    BigDecimal actual = multiply(new BigDecimal("0.1"), SOME_NUMBER);
    assertEquals(expected, actual);
  }

  @Test
  public void divide_twoNumbers_returnDivision() {
    BigDecimal expected = new BigDecimal("655846084377936");
    BigDecimal actual =
        divide(new BigDecimal("145414193828276050"), new BigDecimal("221720000000000000000"));
    assertEquals(expected, actual);
  }

  @Test
  public void multiply_divide_twoNumbers_returnResult() {
    BigDecimal expected = new BigDecimal("655846084377936");
    BigDecimal actual =
        divide(
            multiply(new BigDecimal("100000000000000000"), SOME_NUMBER),
            new BigDecimal("221720000000000000000"));
    assertEquals(expected, actual);
  }
}
