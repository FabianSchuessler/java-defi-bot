package de.fs92.defi.numberutil;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static de.fs92.defi.numberutil.NumberUtil.UINT_MAX;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NumberUtilTest {
  private static final BigDecimal SOME_NUMBER = new BigDecimal("1454141938282760506");

  @Test
  public void UINTMAX_getUINTMAX_equals() {
    BigInteger expected = new BigInteger("2").pow(256).subtract(BigInteger.ONE);
    assertEquals(0, expected.compareTo(UINT_MAX));
  }
}
