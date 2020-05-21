package de.fs92.defi.numberutil;

import org.jetbrains.annotations.NotNull;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class NumberWrapper implements Comparable<NumberWrapper> {
    final BigDecimal BIG_DECIMAL;
    final int DECIMALS;

    public NumberWrapper(String number, int DECIMALS) {
        this.BIG_DECIMAL = new BigDecimal(number);
        this.DECIMALS = DECIMALS;
    }

    public NumberWrapper(int number, int DECIMALS) {
        this.BIG_DECIMAL = BigDecimal.valueOf(number);
        this.DECIMALS = DECIMALS;
    }

    public NumberWrapper(long number, int DECIMALS) {
        this.BIG_DECIMAL = BigDecimal.valueOf(number);
        this.DECIMALS = DECIMALS;
    }

    public NumberWrapper(int DECIMALS) {
        this.BIG_DECIMAL = new BigDecimal(BigInteger.ZERO);
        this.DECIMALS = DECIMALS;
    }

    public NumberWrapper(BigInteger bigInteger, int DECIMALS) {
        this.BIG_DECIMAL = new BigDecimal(bigInteger);
        this.DECIMALS = DECIMALS;
    }

    public NumberWrapper(BigDecimal BIG_DECIMAL, int DECIMALS) {
        this.BIG_DECIMAL = BIG_DECIMAL;
        this.DECIMALS = DECIMALS;
    }

    public NumberWrapper divide(@NotNull BigInteger divisorWrapper) {
        return this.divide(new Wad18(divisorWrapper));
    }

    public NumberWrapper divide(@NotNull NumberWrapper divisorWrapper) {
        BigDecimal divisor = divisorWrapper.toBigDecimal();
        if (divisor.compareTo(BigDecimal.ZERO) == 0)
            throw new IllegalArgumentException("Argument 'divisor' is 0");
        return new NumberWrapper(
                BIG_DECIMAL
                        .multiply(BigDecimal.valueOf(Math.pow(10, DECIMALS)))
                        .divide(divisor, 0, RoundingMode.DOWN)
                        .stripTrailingZeros(),
                DECIMALS);
    }

    public NumberWrapper multiply(@NotNull NumberWrapper multiplicand) {
        return new NumberWrapper(
                BIG_DECIMAL
                        .multiply(multiplicand.toBigDecimal())
                        .divide(BigDecimal.valueOf(Math.pow(10, DECIMALS)), RoundingMode.DOWN),
                DECIMALS);
    }

    public BigInteger toBigInteger() {
        return BIG_DECIMAL.toBigInteger();
    }

    public BigDecimal toBigDecimal() {
        return BIG_DECIMAL;
    }

    @Override
    public String toString() {
        if (DECIMALS == 18) {
            return Convert.fromWei(BIG_DECIMAL, Convert.Unit.ETHER)
                    .setScale(DECIMALS, RoundingMode.DOWN)
                    .toPlainString();
        } else if (DECIMALS > 18) {
            return Convert.fromWei(
                    BIG_DECIMAL.divide(BigDecimal.valueOf(Math.pow(10, DECIMALS - 18.0)), RoundingMode.DOWN),
                    Convert.Unit.ETHER)
                    .setScale(DECIMALS, RoundingMode.DOWN)
                    .toPlainString();
        }
        throw new IllegalArgumentException("todo"); //todo
    }

    public String toString(int decimals) {
        // todo: only works for decimal == 18
        return Convert.fromWei(BIG_DECIMAL, Convert.Unit.ETHER)
                .setScale(decimals, RoundingMode.DOWN)
                .toPlainString();
    }

    public NumberWrapper add(@NotNull NumberWrapper augend) {
        return new Wad18(BIG_DECIMAL.add(augend.toBigDecimal()));
    }

    public NumberWrapper subtract(@NotNull NumberWrapper subtrahend) {
        return new Wad18(BIG_DECIMAL.subtract(subtrahend.toBigDecimal()));
    }

    @Override
    public int compareTo(@NotNull NumberWrapper compareObject) {
        // TODO: check if this can be better
        BigDecimal bigDecimal1 = BIG_DECIMAL.multiply(BigDecimal.valueOf(Math.pow(10, compareObject.DECIMALS)));
        BigDecimal bigDecimal2 = compareObject.toBigDecimal().multiply(BigDecimal.valueOf(Math.pow(10, DECIMALS)));
        return bigDecimal1.compareTo(bigDecimal2);
    }

    @Override
    public int hashCode() {
        return BIG_DECIMAL.intValue(); // INFO: check if this is a good idea
    }

    @Override
    public boolean equals(Object compareObject) {
        if (compareObject instanceof NumberWrapper) {
            NumberWrapper numberWrapper = (NumberWrapper) compareObject;
            return BIG_DECIMAL.compareTo(numberWrapper.toBigDecimal()) == 0;
        }
        return false;
    } // todo: hashcode, null?

    public NumberWrapper min(@NotNull NumberWrapper compareObject) {
        return new NumberWrapper(BIG_DECIMAL.min(compareObject.toBigDecimal()), this.DECIMALS);
    }

    public NumberWrapper max(@NotNull NumberWrapper compareObject) {
        return new NumberWrapper(BIG_DECIMAL.max(compareObject.toBigDecimal()), this.DECIMALS);
    }

    public long longValue() {
        return BIG_DECIMAL.longValue();
    }
}
