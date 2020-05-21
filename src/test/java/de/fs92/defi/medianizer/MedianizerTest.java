package de.fs92.defi.medianizer;

import de.fs92.defi.numberutil.Wad18;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.math.BigInteger;

import static org.hamcrest.MatcherAssert.assertThat;

class MedianizerTest {
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

  @Test
  void getMedian_arrayWithRandomOrderAndZerosAndEvenLength_returnCalculation()
      throws MedianException {
    BigInteger expected = BigInteger.valueOf((11));
    BigInteger actual =
            Medianizer.getMedian(
                    new Wad18[]{
                            new Wad18(80),
                            new Wad18(1),
                            new Wad18(0),
                            new Wad18(10),
                            new Wad18(2),
                            new Wad18(12),
                            new Wad18(100)
                    }).toBigInteger();
    assertThat(actual, Matchers.comparesEqualTo(expected));
  }

  @Test
  void getMedian_arrayWithUnevenLength_returnCalculation() throws MedianException {
    BigInteger expected = BigInteger.TEN;
    BigInteger actual =
            Medianizer.getMedian(new Wad18[]{new Wad18(10), new Wad18(1), new Wad18(10)}).toBigInteger();
    assertThat(actual, Matchers.comparesEqualTo(expected));
  }

  @Test
  void getMedian_arrayWithEvenLength_returnCalculation() throws MedianException {
    BigInteger expected = BigInteger.valueOf(5);
    BigInteger actual =
            Medianizer.getMedian(
                    new Wad18[]{new Wad18(10), new Wad18(1), new Wad18(1), new Wad18(10)}).toBigInteger();
    assertThat(actual, Matchers.comparesEqualTo(expected));
  }

  @Test
  void getMedian_arrayWithAFewZeros_returnCalculation() throws MedianException {
    BigInteger expected = BigInteger.TEN;
    BigInteger actual =
            Medianizer.getMedian(
                    new Wad18[]{new Wad18(10), new Wad18(0), new Wad18(0), new Wad18(10)}).toBigInteger();
    assertThat(actual, Matchers.comparesEqualTo(expected));
  }

  @Test
  void getMedian_arrayWithOnlyZeros_throwMedianException() {
    Wad18[] array = {new Wad18(0), new Wad18(0), new Wad18(0), new Wad18(0)};
    Assertions.assertThrows(MedianException.class, () -> Medianizer.getMedian(array));
  }
}
