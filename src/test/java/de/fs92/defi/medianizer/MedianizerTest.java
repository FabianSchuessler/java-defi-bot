package de.fs92.defi.medianizer;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;

public class MedianizerTest {
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

  @Test
  public void getMedian_arrayWithRandomOrderAndZerosAndEvenLength_returnCalculation()
      throws MedianException {
    BigDecimal expected = BigDecimal.valueOf(11);
    BigDecimal actual =
        Medianizer.getMedian(
            new BigDecimal[] {
              BigDecimal.valueOf(80),
              BigDecimal.ONE,
              BigDecimal.ZERO,
              BigDecimal.TEN,
              BigDecimal.valueOf(2),
              BigDecimal.valueOf(12),
              BigDecimal.valueOf(100.6)
            });
    assertThat(actual, Matchers.comparesEqualTo(expected));
  }

  @Test
  public void getMedian_arrayWithUnevenLength_returnCalculation() throws MedianException {
    BigDecimal expected = BigDecimal.TEN;
    BigDecimal actual =
        Medianizer.getMedian(new BigDecimal[] {BigDecimal.TEN, BigDecimal.ONE, BigDecimal.TEN});
    assertThat(actual, Matchers.comparesEqualTo(expected));
  }

  @Test
  public void getMedian_arrayWithEvenLength_returnCalculation() throws MedianException {
    BigDecimal expected = BigDecimal.valueOf(5);
    BigDecimal actual =
        Medianizer.getMedian(
            new BigDecimal[] {BigDecimal.TEN, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.TEN});
    assertThat(actual, Matchers.comparesEqualTo(expected));
  }

  @Test
  public void getMedian_arrayWithAFewZeros_returnCalculation() throws MedianException {
    BigDecimal expected = BigDecimal.TEN;
    BigDecimal actual =
        Medianizer.getMedian(
            new BigDecimal[] {BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.TEN});
    assertThat(actual, Matchers.comparesEqualTo(expected));
  }

  @Test
  public void getMedian_arrayWithOnlyZeros_throwMedianException() {
    BigDecimal[] array = {BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO};
    Assertions.assertThrows(MedianException.class, () -> Medianizer.getMedian(array));
  }
}
