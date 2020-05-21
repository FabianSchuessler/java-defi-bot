package de.fs92.defi.gasprovider;

import de.fs92.defi.numberutil.Wad18;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ETHGasStationIT {
  private static final String TOO_HIGH = "Error, value is too high";
  private static final String TOO_LOW = "Error, value is too low";
  private static final Wad18 MINIMUM_GAS_PRICE = new Wad18(1_000000000);
  private static final Wad18 MAXIMUM_GAS_PRICE = new Wad18(100_000000000L);

  @Test
  void getFastestGasPrice_currentGasPrice_withinReasonableBoundaries() throws GasPriceException {
    Wad18 result = ETHGasStation.getFastestGasPrice();
    assertTrue(MINIMUM_GAS_PRICE.compareTo(result) < 0, TOO_LOW);
    assertTrue(MAXIMUM_GAS_PRICE.compareTo(result) > 0, TOO_HIGH);
  }

  @Test
  void getSafeLowGasPrice_currentGasPrice_withinReasonableBoundaries() throws GasPriceException {
    Wad18 result = ETHGasStation.getSafeLowGasPrice();
    assertTrue(MINIMUM_GAS_PRICE.compareTo(result) < 0, TOO_LOW);
    assertTrue(MAXIMUM_GAS_PRICE.compareTo(result) > 0, TOO_HIGH);
  }
}
