package de.fs92.defi.gasprovider;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ETHGasStationIT {
  private static final String TOO_HIGH = "Error, value is too high";
  private static final String TOO_LOW = "Error, value is too low";
  private static final BigInteger MINIMUM_GAS_PRICE = BigInteger.valueOf(1_000000000);
  private static final BigInteger MAXIMUM_GAS_PRICE = BigInteger.valueOf(400_000000000L);

  @Test
  public void testGetFastestGasPriceWithBigInteger() throws GasPriceException {
    BigInteger result = ETHGasStation.getFastestGasPrice();
    assertTrue(BigInteger.valueOf(250_000000000L).compareTo(result) > 0, TOO_HIGH);
    assertTrue(BigInteger.valueOf(1_000000000).compareTo(result) < 0, TOO_LOW);
  }

  @Test
  public void testGetFastestGasPriceWithGasProvider() throws GasPriceException {
    BigInteger result = ETHGasStation.getFastestGasPrice();
    assertTrue(MINIMUM_GAS_PRICE.compareTo(result) < 0, TOO_LOW);
    assertTrue(MAXIMUM_GAS_PRICE.compareTo(result) > 0, TOO_HIGH);
  }

  @Test
  public void testGetAverageGasPriceWithBigInteger() throws GasPriceException {
    BigInteger result = ETHGasStation.getAverageGasPrice();
    assertTrue(BigInteger.valueOf(250_000000000L).compareTo(result) > 0, TOO_HIGH);
    assertTrue(BigInteger.valueOf(1_000000000).compareTo(result) < 0, TOO_LOW);
  }

  @Test
  public void testGetAverageGasPriceWithGasProvider() throws GasPriceException {
    BigInteger result = ETHGasStation.getAverageGasPrice();
    assertTrue(MINIMUM_GAS_PRICE.compareTo(result) < 0, TOO_LOW);
    assertTrue(MAXIMUM_GAS_PRICE.compareTo(result) > 0, TOO_HIGH);
  }
}
