package de.fs92.defi.gasprovider;

import de.fs92.defi.numberutil.Wad18;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EtherchainIT {
  private static final String TOO_HIGH = "Error, value is too high";
  private static final String TOO_LOW = "Error, value is too low";
  private static final Wad18 MINIMUM_GAS_PRICE = new Wad18(1_000000000);
  private static final Wad18 MAXIMUM_GAS_PRICE = new Wad18(100_000000000L);

  @Test
  public void getFastestGasPrice_currentGasPrice_GasPriceWithinBoundaries()
          throws GasPriceException {
    Wad18 result = Etherchain.getFastestGasPrice();
    assertTrue(MINIMUM_GAS_PRICE.compareTo(result) < 0, TOO_LOW);
    assertTrue(MAXIMUM_GAS_PRICE.compareTo(result) > 0, TOO_HIGH);
  }

  @Test // todo: this is a bad test
  public void getFastestGasPrice_currentGasPriceDifference_GasPriceDifferenceIsReasonable()
      throws GasPriceException {
    Wad18 etherchainResult = Etherchain.getFastestGasPrice();
    Wad18 ethGasStationResult = ETHGasStation.getFastestGasPrice();
    Wad18 difference = ethGasStationResult.subtract(etherchainResult);
    assertThat(difference.toBigInteger(), is(lessThan(BigInteger.valueOf(20_000000000L))));
  }
}
