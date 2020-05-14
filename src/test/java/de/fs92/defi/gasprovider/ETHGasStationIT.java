package de.fs92.defi.gasprovider;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ETHGasStationIT {
  private static final String TOO_HIGH = "Error, value is too high";
  private static final String TOO_LOW = "Error, value is too low";
  private static final BigInteger MINIMUM_GAS_PRICE = BigInteger.valueOf(1_000000000);
  private static final BigInteger MAXIMUM_GAS_PRICE = BigInteger.valueOf(100_000000000L);

  @Test
  public void getFastestGasPrice_currentGasPrice_withinReasonableBoundaries()
      throws GasPriceException {
    BigInteger result = ETHGasStation.getFastestGasPrice();
    assertTrue(MINIMUM_GAS_PRICE.compareTo(result) < 0, TOO_LOW);
    assertTrue(MAXIMUM_GAS_PRICE.compareTo(result) > 0, TOO_HIGH);
  }

  @Test
  public void getSafeLowGasPrice_currentGasPrice_withinReasonableBoundaries()
      throws GasPriceException {
    BigInteger result = ETHGasStation.getSafeLowGasPrice();
    assertTrue(MINIMUM_GAS_PRICE.compareTo(result) < 0, TOO_LOW);
    assertTrue(MAXIMUM_GAS_PRICE.compareTo(result) > 0, TOO_HIGH);
  }
}
