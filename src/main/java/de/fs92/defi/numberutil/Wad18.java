package de.fs92.defi.numberutil;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class Wad18 extends NumberWrapper {
  public static final Wad18 ZERO = new Wad18(0);
  public static final Wad18 ONE = new Wad18(1);
  private static final int DECIMALS = 18;

  public Wad18() {
    super(DECIMALS);
  }

  public Wad18(int number) {
    super(number, DECIMALS);
  }

  public Wad18(String number) {
    super(number, DECIMALS);
  }

  public Wad18(long number) {
    super(number, DECIMALS);
  }

  public Wad18(BigInteger bigInteger) {
    super(bigInteger, DECIMALS);
  }

  public Wad18(BigDecimal bigDecimal) {
    super(bigDecimal, DECIMALS);
  }

  @Override
  public Wad18 divide(@NotNull BigInteger divisorWrapper) {
    return this.divide(new Wad18(divisorWrapper));
  }

  @Override
  public Wad18 divide(@NotNull NumberWrapper divisorWrapper) {
    BigDecimal divisor = divisorWrapper.toBigDecimal();
    if (divisor.compareTo(BigDecimal.ZERO) == 0)
      throw new IllegalArgumentException("Argument 'divisor' is 0");
    return new Wad18(
            BIG_DECIMAL
                    .multiply(BigDecimal.valueOf(Math.pow(10, DECIMALS)))
                    .divide(divisor, 0, RoundingMode.DOWN)
                    .stripTrailingZeros());
  }

  @Override
  public Wad18 multiply(@NotNull NumberWrapper multiplicand) {
    return new Wad18(
            BIG_DECIMAL
                    .multiply(multiplicand.toBigDecimal())
                    .divide(BigDecimal.valueOf(Math.pow(10, DECIMALS)), RoundingMode.DOWN));
  }

  @Override
  public Wad18 add(@NotNull NumberWrapper augend) {
    return new Wad18(BIG_DECIMAL.add(augend.toBigDecimal()));
  }

  @Override
  public Wad18 subtract(@NotNull NumberWrapper subtrahend) {
    return new Wad18(BIG_DECIMAL.subtract(subtrahend.toBigDecimal()));
  }

  @Override
  public Wad18 min(@NotNull NumberWrapper compareObject) {
    return new Wad18(BIG_DECIMAL.min(compareObject.toBigDecimal()));
  }

  @Override
  public Wad18 max(@NotNull NumberWrapper compareObject) {
    return new Wad18(BIG_DECIMAL.max(compareObject.toBigDecimal()));
  }
}
