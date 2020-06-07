package de.fs92.defi.numberutil;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Sth28 extends NumberWrapper {
  public static final Sth28 ZERO = new Sth28(0);
  public static final Sth28 ONE = new Sth28(1);
  private static final int STH32_DECIMALS = 28;

  public Sth28() {
    super(STH32_DECIMALS);
  }

  public Sth28(int number) {
    super(number, STH32_DECIMALS);
  }

  public Sth28(String number) {
    super(number, STH32_DECIMALS);
  }

  public Sth28(long number) {
    super(number, STH32_DECIMALS);
  }

  public Sth28(BigInteger bigInteger) {
    super(bigInteger, STH32_DECIMALS);
  }

  public Sth28(BigDecimal bigDecimal) {
    super(bigDecimal, STH32_DECIMALS);
  }

  @Override
  public Sth28 add(@NotNull NumberWrapper augend) {
    return new Sth28(bigDecimal.add(augend.toBigDecimal()));
  }

  @Override
  public Sth28 subtract(@NotNull NumberWrapper subtrahend) {
    return new Sth28(bigDecimal.subtract(subtrahend.toBigDecimal()));
  }

  @Override
  public Sth28 min(@NotNull NumberWrapper compareObject) {
    return new Sth28(bigDecimal.min(compareObject.toBigDecimal()));
  }

  @Override
  public Sth28 max(@NotNull NumberWrapper compareObject) {
    return new Sth28(bigDecimal.max(compareObject.toBigDecimal()));
  }
}
