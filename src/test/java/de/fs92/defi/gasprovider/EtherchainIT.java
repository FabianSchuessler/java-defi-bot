package de.fs92.defi.gasprovider;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EtherchainIT {
  private static final String TOO_HIGH = "Error, value is too high";
  private static final String TOO_LOW = "Error, value is too low";
  private static final BigInteger MINIMUM_GAS_PRICE = BigInteger.valueOf(1_000000000);
  private static final BigInteger MAXIMUM_GAS_PRICE = BigInteger.valueOf(400_000000000L);

  @Test
  public void testGetFastestGasPriceBigInteger() throws GasPriceException {
    BigInteger result = Etherchain.getFastestGasPrice();
    assertTrue(BigInteger.valueOf(250_000000000L).compareTo(result) > 0, TOO_HIGH);
    assertTrue(BigInteger.valueOf(1_000000000).compareTo(result) < 0, TOO_LOW);
  }

  @Test
  public void testGetFastestGasPriceWithGasProvider() throws GasPriceException {
    BigInteger result = Etherchain.getFastestGasPrice();
    assertTrue(MINIMUM_GAS_PRICE.compareTo(result) < 0, TOO_LOW);
    assertTrue(MAXIMUM_GAS_PRICE.compareTo(result) > 0, TOO_HIGH);
  }

  @Test // this is a bad test
  public void gasPriceDifference() throws GasPriceException {
    BigInteger etherchainResult = Etherchain.getFastestGasPrice();
    BigInteger ethGasStationResult = ETHGasStation.getAverageGasPrice();
    BigInteger difference = ethGasStationResult.subtract(etherchainResult);
    assertThat(difference, is(lessThan(BigInteger.valueOf(300_000000000L))));
  }
}
