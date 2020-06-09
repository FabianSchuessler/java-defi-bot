package de.fs92.defi.numberutil;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class Rad45 extends NumberWrapper {
  public static final Rad45 ZERO = new Rad45(0);
  public static final Rad45 ONE = new Rad45(1);
  private static final int RAD45_DECIMALS = 45;

  public Rad45() {
    super(RAD45_DECIMALS);
  }

  public Rad45(int number) {
    super(number, RAD45_DECIMALS);
  }

  public Rad45(String number) {
    super(number, RAD45_DECIMALS);
  }

  public Rad45(long number) {
    super(number, RAD45_DECIMALS);
  }

  public Rad45(BigInteger bigInteger) {
    super(bigInteger, RAD45_DECIMALS);
  }

  public Rad45(BigDecimal bigDecimal) {
    super(bigDecimal, RAD45_DECIMALS);
  }

  @Override
  public Wad18 divide(@NotNull BigInteger divisorWrapper) {
    return this.divide(new Rad45(divisorWrapper));
  }

  @Override
  public Wad18 divide(@NotNull NumberWrapper divisorWrapper) {
    BigDecimal divisor = divisorWrapper.toBigDecimal();
    if (divisor.compareTo(BigDecimal.ZERO) == 0)
      throw new IllegalArgumentException("Argument 'divisor' is 0");
    return new Wad18(
            bigDecimal
                    .multiply(BigDecimal.valueOf(Math.pow(10, RAD45_DECIMALS)))
                    .divide(divisor, 0, RoundingMode.DOWN)
                    .stripTrailingZeros());
  }

  @Override
  public Wad18 multiply(@NotNull NumberWrapper multiplicand) {
      return new Wad18(
              bigDecimal
                      .multiply(multiplicand.toBigDecimal())
                      .divide(BigDecimal.valueOf(Math.pow(10, RAD45_DECIMALS)), 0, RoundingMode.DOWN));
  }

  @Override
  public Wad18 add(@NotNull NumberWrapper augend) {
    return new Wad18(bigDecimal.add(augend.toBigDecimal()));
  }

  @Override
  public Wad18 subtract(@NotNull NumberWrapper subtrahend) {
    return new Wad18(bigDecimal.subtract(subtrahend.toBigDecimal()));
  }

  @Override
  public Rad45 min(@NotNull NumberWrapper compareObject) {
    return new Rad45(bigDecimal.min(compareObject.toBigDecimal()));
  }

  @Override
  public Rad45 max(@NotNull NumberWrapper compareObject) {
    return new Rad45(bigDecimal.max(compareObject.toBigDecimal()));
  }
}
