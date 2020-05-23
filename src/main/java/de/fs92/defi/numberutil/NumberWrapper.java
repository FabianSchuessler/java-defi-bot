package de.fs92.defi.numberutil;

import org.jetbrains.annotations.NotNull;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class NumberWrapper implements Comparable<NumberWrapper> {
  final BigDecimal bigDecimal;
  final int decimals;

  public NumberWrapper(String number, int decimals) {
    this.bigDecimal = new BigDecimal(number);
    this.decimals = decimals;
  }

  public NumberWrapper(int number, int decimals) {
    this.bigDecimal = BigDecimal.valueOf(number);
    this.decimals = decimals;
  }

  public NumberWrapper(long number, int decimals) {
    this.bigDecimal = BigDecimal.valueOf(number);
    this.decimals = decimals;
  }

  public NumberWrapper(int decimals) {
    this.bigDecimal = new BigDecimal(BigInteger.ZERO);
    this.decimals = decimals;
  }

  public NumberWrapper(BigInteger bigInteger, int decimals) {
    this.bigDecimal = new BigDecimal(bigInteger);
    this.decimals = decimals;
  }

  public NumberWrapper(BigDecimal bigDecimal, int decimals) {
    this.bigDecimal = bigDecimal;
    this.decimals = decimals;
  }

  public NumberWrapper divide(@NotNull BigInteger divisorWrapper) {
    return this.divide(new Wad18(divisorWrapper));
  }

  public NumberWrapper divide(@NotNull NumberWrapper divisorWrapper) {
    BigDecimal divisor = divisorWrapper.toBigDecimal();
    if (divisor.compareTo(BigDecimal.ZERO) == 0)
      throw new IllegalArgumentException("Argument 'divisor' is 0");
    return new NumberWrapper(
        bigDecimal
            .multiply(BigDecimal.valueOf(Math.pow(10, decimals)))
            .divide(divisor, 0, RoundingMode.DOWN)
            .stripTrailingZeros(),
            decimals);
  }

  public NumberWrapper multiply(@NotNull NumberWrapper multiplicand) {
    return new NumberWrapper(
        bigDecimal
            .multiply(multiplicand.toBigDecimal())
            .divide(BigDecimal.valueOf(Math.pow(10, decimals)), RoundingMode.DOWN),
            decimals);
  }

  public BigInteger toBigInteger() {
    return bigDecimal.toBigInteger();
  }

  public BigDecimal toBigDecimal() {
    return bigDecimal;
  }

  @Override
  public String toString() {
    if (decimals == 18) {
      return Convert.fromWei(bigDecimal, Convert.Unit.ETHER)
          .setScale(decimals, RoundingMode.DOWN)
          .toPlainString();
    } else if (decimals > 18) {
      return Convert.fromWei(
              bigDecimal.divide(
                  BigDecimal.valueOf(Math.pow(10, decimals - 18.0)), RoundingMode.DOWN),
              Convert.Unit.ETHER)
          .setScale(decimals, RoundingMode.DOWN)
          .toPlainString();
    }
    throw new IllegalArgumentException("todo"); // todo
  }

  public String toString(int decimals) {
    // todo: only works for decimal == 18
    return Convert.fromWei(bigDecimal, Convert.Unit.ETHER)
        .setScale(decimals, RoundingMode.DOWN)
        .toPlainString();
  }

  public NumberWrapper add(@NotNull NumberWrapper augend) {
    return new Wad18(bigDecimal.add(augend.toBigDecimal()));
  }

  public NumberWrapper subtract(@NotNull NumberWrapper subtrahend) {
    return new Wad18(bigDecimal.subtract(subtrahend.toBigDecimal()));
  }

  @Override
  public int compareTo(@NotNull NumberWrapper compareObject) {
    // TODO: check if this can be better
    BigDecimal bigDecimal1 =
        bigDecimal.multiply(BigDecimal.valueOf(Math.pow(10, compareObject.decimals)));
    BigDecimal bigDecimal2 =
        compareObject.toBigDecimal().multiply(BigDecimal.valueOf(Math.pow(10, decimals)));
    return bigDecimal1.compareTo(bigDecimal2);
  }

  @Override
  public int hashCode() {
    return bigDecimal.intValue(); // INFO: check if this is a good idea
  }

  @Override
  public boolean equals(Object compareObject) {
    if (compareObject instanceof NumberWrapper) {
      NumberWrapper numberWrapper = (NumberWrapper) compareObject;
      return bigDecimal.compareTo(numberWrapper.toBigDecimal()) == 0;
    }
    return false;
  } // todo: hashcode, null?

  public NumberWrapper min(@NotNull NumberWrapper compareObject) {
    return new NumberWrapper(bigDecimal.min(compareObject.toBigDecimal()), this.decimals);
  }

  public NumberWrapper max(@NotNull NumberWrapper compareObject) {
    return new NumberWrapper(bigDecimal.max(compareObject.toBigDecimal()), this.decimals);
  }

  public long longValue() {
    return bigDecimal.longValue();
  }
}
