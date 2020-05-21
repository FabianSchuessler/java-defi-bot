package de.fs92.defi.numberutil;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static de.fs92.defi.numberutil.NumberUtil.UINT_MAX;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NumberUtilTest {
  @Test
  void UINTMAX_getUINTMAX_equals() {
    BigInteger expected = new BigInteger("2").pow(256).subtract(BigInteger.ONE);
    assertEquals(0, expected.compareTo(UINT_MAX));
  }
}
