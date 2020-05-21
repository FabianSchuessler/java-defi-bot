package de.fs92.defi.numberutil;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static de.fs92.defi.numberutil.NumberUtil.getMachineReadable;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NumberWrapperTest {

  @Test
  void divide() {
    Wad18 wad18 = new Wad18(BigInteger.ONE);
    Rad45 rad45 = new Rad45(BigInteger.ONE);
    Wad18 actual = wad18.divide(rad45);
    Wad18 expected = new Wad18(getMachineReadable(1.0));
    assertEquals(0, expected.compareTo(actual));
  }
}
