package de.fs92.defi.medianizer;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.math.BigInteger;

import static org.hamcrest.MatcherAssert.assertThat;

public class MedianizerTest {
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

  @Test
  public void getMedian_arrayWithRandomOrderAndZerosAndEvenLength_returnCalculation()
      throws MedianException {
    BigInteger expected = BigInteger.valueOf(11);
    BigInteger actual =
        Medianizer.getMedian(
            new BigInteger[] {
              BigInteger.valueOf(80),
              BigInteger.ONE,
              BigInteger.ZERO,
              BigInteger.TEN,
              BigInteger.valueOf(2),
              BigInteger.valueOf(12),
              BigInteger.valueOf(100)
            });
    assertThat(actual, Matchers.comparesEqualTo(expected));
  }

  @Test
  public void getMedian_arrayWithUnevenLength_returnCalculation() throws MedianException {
    BigInteger expected = BigInteger.TEN;
    BigInteger actual =
        Medianizer.getMedian(new BigInteger[] {BigInteger.TEN, BigInteger.ONE, BigInteger.TEN});
    assertThat(actual, Matchers.comparesEqualTo(expected));
  }

  @Test
  public void getMedian_arrayWithEvenLength_returnCalculation() throws MedianException {
    BigInteger expected = BigInteger.valueOf(5);
    BigInteger actual =
        Medianizer.getMedian(
            new BigInteger[] {BigInteger.TEN, BigInteger.ONE, BigInteger.ONE, BigInteger.TEN});
    assertThat(actual, Matchers.comparesEqualTo(expected));
  }

  @Test
  public void getMedian_arrayWithAFewZeros_returnCalculation() throws MedianException {
    BigInteger expected = BigInteger.TEN;
    BigInteger actual =
        Medianizer.getMedian(
            new BigInteger[] {BigInteger.TEN, BigInteger.ZERO, BigInteger.ZERO, BigInteger.TEN});
    assertThat(actual, Matchers.comparesEqualTo(expected));
  }

  @Test
  public void getMedian_arrayWithOnlyZeros_throwMedianException() {
    BigInteger[] array = {BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO};
    Assertions.assertThrows(MedianException.class, () -> Medianizer.getMedian(array));
  }
}
