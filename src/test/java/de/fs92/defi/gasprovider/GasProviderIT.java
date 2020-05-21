package de.fs92.defi.gasprovider;

import de.fs92.defi.contractneedsprovider.Web3jProvider;
import de.fs92.defi.numberutil.Wad18;
import de.fs92.defi.oasis.OasisContract;
import de.fs92.defi.util.JavaProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.web3j.protocol.Web3j;

import java.math.BigInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class GasProviderIT {
  private static final String TRAVIS_INFURA_PROJECT_ID = "TRAVIS_INFURA_PROJECT_ID";

  private static final String TOO_HIGH = "Error, value is too high";
  private static final String TOO_LOW = "Error, value is too low";
  private static final String EXCEPTION = "calculateGasPriceAsAPercentageOfProfit Exception";
  private static final Wad18 MINIMUM_GAS_PRICE = new Wad18(1_000000000);
  private static final Wad18 MAXIMUM_GAS_PRICE = new Wad18(100_000000000L);
  private static Web3j web3j;

  @BeforeEach
  void setUp() {
    JavaProperties javaProperties = new JavaProperties(true);

    String infuraProjectId;

    if ("true".equals(System.getenv().get("TRAVIS"))) {
      infuraProjectId = System.getenv().get(TRAVIS_INFURA_PROJECT_ID);
    } else {
      infuraProjectId = javaProperties.getValue("infuraProjectId");
    }

    web3j = new Web3jProvider(infuraProjectId).web3j;
  }

  @Test
  void updateSlowGasPrice_currentGasPrice_GasPriceWithinBoundaries() {
    GasProvider gasProvider = new GasProvider(web3j, MINIMUM_GAS_PRICE, MAXIMUM_GAS_PRICE);
    gasProvider.updateSlowGasPrice();
    assertTrue(MINIMUM_GAS_PRICE.compareTo(gasProvider.gasPrice) <= 0, TOO_LOW);
    assertTrue(MAXIMUM_GAS_PRICE.compareTo(gasProvider.gasPrice) >= 0, TOO_HIGH);
  }

  @Test
  void updateFastGasPrice_ZeroMedianZeroProfit_GasPriceWithinBoundaries() {
    GasProvider gasProvider = new GasProvider(web3j, MINIMUM_GAS_PRICE, MAXIMUM_GAS_PRICE);
    gasProvider.updateFastGasPrice(Wad18.ZERO, Wad18.ZERO);
    assertTrue(MINIMUM_GAS_PRICE.compareTo(gasProvider.gasPrice) <= 0, TOO_LOW);
    assertTrue(MAXIMUM_GAS_PRICE.compareTo(gasProvider.gasPrice) >= 0, TOO_HIGH);
  }

  @Test
  void updateFastGasPrice_RealMedianAndProfit_GasPriceWithinBoundaries() {
    GasProvider gasProvider = new GasProvider(web3j, MINIMUM_GAS_PRICE, MAXIMUM_GAS_PRICE);
    gasProvider.updateFastGasPrice(
        new Wad18("200000000000000000000"), new Wad18("10000000000000000000"));
    assertTrue(MINIMUM_GAS_PRICE.compareTo(gasProvider.gasPrice) <= 0, TOO_LOW);
    assertTrue(MAXIMUM_GAS_PRICE.compareTo(gasProvider.gasPrice) >= 0, TOO_HIGH);
  }

  @Test
  void getGasLimit_someFunction_returnInRealisticBounds() {
    GasProvider gasProvider = new GasProvider(web3j, MINIMUM_GAS_PRICE, MAXIMUM_GAS_PRICE);
    assertEquals(BigInteger.valueOf(300_000), gasProvider.getGasLimit(OasisContract.FUNC_BUY));
  }

  @Test
  void getGasPrice_someFunction_returnInRealisticBounds() {
    GasProvider gasProvider = new GasProvider(web3j, MINIMUM_GAS_PRICE, MAXIMUM_GAS_PRICE);
    BigInteger actual = gasProvider.getGasPrice(OasisContract.FUNC_BUY);
    assertTrue(MINIMUM_GAS_PRICE.toBigInteger().compareTo(actual) <= 0, TOO_LOW);
    assertTrue(MAXIMUM_GAS_PRICE.toBigInteger().compareTo(actual) >= 0, TOO_HIGH);
  }

  @Test
  void getPercentageOfProfitAsFee_zeroTransactions_returnCalculation() {
    assertThat(2, allOf(greaterThanOrEqualTo(1), lessThanOrEqualTo(2)));

    GasProvider gasProvider = new GasProvider(web3j, MINIMUM_GAS_PRICE, MAXIMUM_GAS_PRICE);
    assertEquals(0.1, gasProvider.getPercentageOfProfitAsFee(0));
  }

  @Test
  void getPercentageOfProfitAsFee_tenTransactions_returnCalculation() {
    GasProvider gasProvider = new GasProvider(web3j, MINIMUM_GAS_PRICE, MAXIMUM_GAS_PRICE);
    assertEquals(0.35, gasProvider.getPercentageOfProfitAsFee(10));
  }

  @Test
  void calculateGasPriceAsAPercentageOfProfit_zeroMedian_throwException() {
    GasProvider gasProvider = new GasProvider(web3j, MINIMUM_GAS_PRICE, MAXIMUM_GAS_PRICE);
    Exception exception =
        assertThrows(
            GasPriceException.class,
            () ->
                gasProvider.calculateGasPriceAsAPercentageOfProfit(
                    Wad18.ZERO, Wad18.ONE, 20.0, 0.1));
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(EXCEPTION));
  }

  @Test
  void calculateGasPriceAsAPercentageOfProfit_zeroProfit_throwException() {
    GasProvider gasProvider = new GasProvider(web3j, MINIMUM_GAS_PRICE, MAXIMUM_GAS_PRICE);
    Exception exception =
        assertThrows(
            GasPriceException.class,
            () ->
                gasProvider.calculateGasPriceAsAPercentageOfProfit(
                    Wad18.ONE, Wad18.ZERO, 20.0, 0.1));
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(EXCEPTION));
  }

  @Test
  void calculateGasPriceAsAPercentageOfProfit_zeroGasLimit_throwException() {
    GasProvider gasProvider = new GasProvider(web3j, MINIMUM_GAS_PRICE, MAXIMUM_GAS_PRICE);
    Exception exception =
        assertThrows(
            GasPriceException.class,
            () ->
                gasProvider.calculateGasPriceAsAPercentageOfProfit(Wad18.ONE, Wad18.ONE, 0.0, 0.1));
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(EXCEPTION));
  }

  @Test
  void calculateGasPriceAsAPercentageOfProfit_someNumbers_returnCalculation()
      throws GasPriceException {
    GasProvider gasProvider = new GasProvider(web3j, MINIMUM_GAS_PRICE, MAXIMUM_GAS_PRICE);
    Wad18 gasPrice =
        gasProvider.calculateGasPriceAsAPercentageOfProfit(
            new Wad18("200000000000000000000"), new Wad18("10000000000000000000"), 300000.0, 0.1);
    assertEquals("0.000000016666666666", gasPrice.toString());
  }

  @Test
  void calculateGasPriceAsAPercentageOfProfit_someRealNumbers_returnCalculation()
      throws GasPriceException {
    GasProvider gasProvider = new GasProvider(web3j, MINIMUM_GAS_PRICE, MAXIMUM_GAS_PRICE);
    Wad18 gasPrice =
        gasProvider.calculateGasPriceAsAPercentageOfProfit(
            new Wad18("124730000000000000000"), new Wad18("1772900000000000000"), 5300000, 0.35);
    assertEquals("0.000000000938653907", gasPrice.toString());
  }

  // TODO: have another look at this test
  @Test
  void calculateGasPriceAsAPercentageOfProfit_someRealNumbers2_returnCalculation()
      throws GasPriceException {
    GasProvider gasProvider = new GasProvider(web3j, MINIMUM_GAS_PRICE, MAXIMUM_GAS_PRICE);
    Wad18 gasPrice =
        gasProvider.calculateGasPriceAsAPercentageOfProfit(
            new Wad18("190775000000000000000"), new Wad18("39335200000000000000"), 300000, 0.15);
    assertEquals("0.000000103093172585", gasPrice.toString());
  }
}
