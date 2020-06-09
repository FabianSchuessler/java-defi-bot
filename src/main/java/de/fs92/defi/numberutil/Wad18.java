package de.fs92.defi.numberutil;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * Immutable
 */
public class Wad18 extends NumberWrapper {
  public static final Wad18 ZERO = new Wad18(0);
  public static final Wad18 ONE = new Wad18(1);
  private static final int WAD18_DECIMALS = 18;

  public Wad18() {
    super(WAD18_DECIMALS);
  }

  public Wad18(int number) {
    super(number, WAD18_DECIMALS);
  }

  public Wad18(String number) {
    super(number, WAD18_DECIMALS);
  }

  public Wad18(long number) {
    super(number, WAD18_DECIMALS);
  }

  public Wad18(BigInteger bigInteger) {
    super(bigInteger, WAD18_DECIMALS);
  }

  public Wad18(BigDecimal bigDecimal) {
    super(bigDecimal, WAD18_DECIMALS);
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
            bigDecimal
                    .multiply(BigDecimal.valueOf(Math.pow(10, WAD18_DECIMALS)))
                    .divide(divisor, 0, RoundingMode.DOWN)
                    .stripTrailingZeros());
  }

  @Override
  public Wad18 multiply(@NotNull NumberWrapper multiplicandNumberWrapper) {
    Wad18 multiplicand = new Wad18(multiplicandNumberWrapper.toBigDecimal());
    int decimalsDifference = multiplicandNumberWrapper.decimals - decimals;
    if (decimalsDifference > 0) {
      multiplicand =
              new Wad18(
                      multiplicandNumberWrapper
                              .toBigDecimal()
                              .divide(
                                      BigDecimal.valueOf(Math.pow(10, decimalsDifference)),
                                      RoundingMode.HALF_EVEN));
    } else if (decimalsDifference < 0) {
      throw new IllegalArgumentException("Not yet implemented");
    }
    return multiply(multiplicand);
  }

  public Wad18 multiply(@NotNull Wad18 multiplicand) {
    return new Wad18(
            bigDecimal
                    .multiply(multiplicand.toBigDecimal())
                    .divide(BigDecimal.valueOf(Math.pow(10, WAD18_DECIMALS)), 0, RoundingMode.DOWN));
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
  public Wad18 min(@NotNull NumberWrapper compareObject) {
    return new Wad18(bigDecimal.min(compareObject.toBigDecimal()));
  }

  @Override
  public Wad18 max(@NotNull NumberWrapper compareObject) {
    return new Wad18(bigDecimal.max(compareObject.toBigDecimal()));
  }
}
