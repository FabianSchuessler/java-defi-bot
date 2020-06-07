package de.fs92.defi.numberutil;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class Wad18Test {

  @Test
  void divide_TwoDividedByOne_Two() {
    Wad18 two = new Wad18(new BigInteger("2000000000000000000"));
    Wad18 one = new Wad18(new BigInteger("1000000000000000000"));
    BigDecimal expected = new BigDecimal("2000000000000000000");
    BigDecimal actual = two.divide(one).toBigDecimal();
    assertEquals(0, expected.compareTo(actual));
  }

  @Test
  void divide_TwoWeiDividedByOneWei_Two() {
    Wad18 two = new Wad18(new BigInteger("2"));
    Wad18 one = new Wad18(new BigInteger("1"));
    BigDecimal expected = new BigDecimal("2000000000000000000");
    BigDecimal actual = two.divide(one).toBigDecimal();
    assertEquals(0, expected.compareTo(actual));
  }

  @Test
  void divide_TwoDividedByTwoWei_One() {
    Wad18 two = new Wad18(new BigInteger("2000000000000000000"));
    Wad18 twoWei = new Wad18(new BigInteger("2"));
    BigDecimal expected = new BigDecimal("1000000000000000000000000000000000000");
    BigDecimal actual = two.divide(twoWei).toBigDecimal();
    assertEquals(0, expected.compareTo(actual));
  }

  @Test
  void divide_MedianDividedByMedian_AboutOne() {
    Wad18 biggerMedian = new Wad18(new BigInteger("204120000000000000000"));
    Wad18 smallerMedian = new Wad18(new BigInteger("201120000000000000000"));
    BigDecimal expected = new BigDecimal("1014916467780429594");
    BigDecimal actual = biggerMedian.divide(smallerMedian).toBigDecimal();
    assertEquals(0, expected.compareTo(actual));
  }

  @Test
  void divide_DivideByZero_IllegalArgumentException() {
    Wad18 wad18 = new Wad18(BigDecimal.ONE);
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              wad18.divide(Wad18.ZERO);
            });
    String expectedMessage = "Argument 'divisor' is 0";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  void multiply_TwoMultipliedByOne_Two() {
    Wad18 two = new Wad18(new BigInteger("2000000000000000000"));
    Wad18 one = new Wad18(new BigInteger("1000000000000000000"));
    BigDecimal expected = new BigDecimal("2000000000000000000");
    BigDecimal actual = two.multiply(one).toBigDecimal();
    assertEquals(0, expected.compareTo(actual));
  }

  @Test
  void multiply_TwoWeiMultipliedByOneWei_Zero() {
    Wad18 two = new Wad18(new BigInteger("2"));
    Wad18 one = new Wad18(new BigInteger("1"));
    BigDecimal expected = new BigDecimal("0");
    BigDecimal actual = two.multiply(one).toBigDecimal();
    assertEquals(0, expected.compareTo(actual));
  }

  @Test
  void multiply_TwoMultipliedByTwoWei_FourWei() {
    Wad18 bigTwo = new Wad18(new BigInteger("2000000000000000000"));
    Wad18 smallTwo = new Wad18(new BigInteger("2"));
    BigDecimal expected = new BigDecimal("4");
    BigDecimal actual = bigTwo.multiply(smallTwo).toBigDecimal();
    assertEquals(0, expected.compareTo(actual));
  }

  @Test
  void multiply_MedianMultipliedByMedian_AboutOne() {
    Wad18 biggerMedian = new Wad18(new BigInteger("204120000000000000000"));
    Wad18 smallerMedian = new Wad18(new BigInteger("201120000000000000000"));
    BigDecimal expected = new BigDecimal("41052614400000000000000");
    BigDecimal actual = biggerMedian.multiply(smallerMedian).toBigDecimal();
    assertEquals(0, expected.compareTo(actual));
  }

  @Test
  void multiply_Wad18MultpliedBySth28_Result() {
    Wad18 wad18 = new Wad18(new BigInteger("10000000000000000000")); // 10.000000000000000000
    Sth28 sth28 = new Sth28(new BigInteger("204721618847438310000000000")); // 0.0204721618847438310000000000
    BigDecimal expected = new BigDecimal("204721618847438310");
    BigDecimal actual = wad18.multiply(sth28).toBigDecimal();
    assertEquals(0, expected.compareTo(actual));
  }

  @Test
  void toBigInteger() {
    BigInteger expected = new BigInteger("204120000000000000000");
    BigInteger actual = new Wad18(expected).toBigInteger();
    assertEquals(expected, actual);
  }

  @Test
  void toBigDecimal() {
    BigDecimal expected = new BigDecimal("204120000000000000000");
    BigDecimal actual = new Wad18(expected).toBigDecimal();
    assertEquals(expected, actual);
  }

  @Test
  void toString_median_expectedString() {
    BigDecimal number = new BigDecimal("204120000000000000000");
    String actual = new Wad18(number).toString();
    assertEquals("204.120000000000000000", actual);
  }

  @Test
  void toString_zero_expectedString() {
    BigDecimal number = new BigDecimal("0");
    String actual = new Wad18(number).toString();
    assertEquals("0.000000000000000000", actual);
  }

  @Test
  void toString_oneWei_expectedString() {
    BigDecimal number = new BigDecimal("1");
    String actual = new Wad18(number).toString();
    assertEquals("0.000000000000000001", actual);
  }

  @Test
  void toString_oneWeiAndDecimal_expectedString() {
    BigDecimal number = new BigDecimal("1.1");
    String actual = new Wad18(number).toString();
    assertEquals("0.000000000000000001", actual);
  }
}
