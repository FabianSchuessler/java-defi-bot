package de.fs92.defi.gasprovider;

import de.fs92.defi.contractneedsprovider.Web3jProvider;
import de.fs92.defi.oasis.OasisContract;
import de.fs92.defi.util.JavaProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.web3j.protocol.Web3j;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class GasProviderIT {
  private static final String TOO_HIGH = "Error, value is too high";
  private static final String TOO_LOW = "Error, value is too low";
  private static final String EXCEPTION = "calculateGasPriceAsAPercentageOfProfit Exception";
  private static final BigInteger MINIMUM_GAS_PRICE = BigInteger.valueOf(1_000000000);
  private static final BigInteger MAXIMUM_GAS_PRICE = BigInteger.valueOf(400_000000000L);
  private static Web3j web3j;

  @BeforeEach
  public void setUp() {
    JavaProperties javaProperties = new JavaProperties(true);
    String infuraProjectId = javaProperties.getValue("infuraProjectId");
    web3j = new Web3jProvider(infuraProjectId).web3j;
  }

  @Test
  public void updateSlowGasPrice() {
    GasProvider gasProvider = new GasProvider(web3j, MINIMUM_GAS_PRICE, MAXIMUM_GAS_PRICE);
    gasProvider.updateSlowGasPrice();
    assertTrue(MINIMUM_GAS_PRICE.compareTo(gasProvider.gasPrice) <= 0, TOO_LOW);
    assertTrue(MAXIMUM_GAS_PRICE.compareTo(gasProvider.gasPrice) >= 0, TOO_HIGH);
  }

  @Test
  public void updateFastGasPrice_2() {
    GasProvider gasProvider = new GasProvider(web3j, MINIMUM_GAS_PRICE, MAXIMUM_GAS_PRICE);
    gasProvider.updateFastGasPrice(BigDecimal.ZERO, BigDecimal.ZERO);
    assertTrue(MINIMUM_GAS_PRICE.compareTo(gasProvider.gasPrice) <= 0, TOO_LOW);
    assertTrue(MAXIMUM_GAS_PRICE.compareTo(gasProvider.gasPrice) >= 0, TOO_HIGH);
  }

  @Test
  public void updateFastGasPrice_1() {
    GasProvider gasProvider = new GasProvider(web3j, MINIMUM_GAS_PRICE, MAXIMUM_GAS_PRICE);
    gasProvider.updateFastGasPrice(
        new BigDecimal("200000000000000000000"), new BigDecimal("10000000000000000000"));
    assertTrue(MINIMUM_GAS_PRICE.compareTo(gasProvider.gasPrice) <= 0, TOO_LOW);
    assertTrue(MAXIMUM_GAS_PRICE.compareTo(gasProvider.gasPrice) >= 0, TOO_HIGH);
  }

  @Test
  public void getGasLimit_someFunction_returnInRealisticBounds() {
    GasProvider gasProvider = new GasProvider(web3j, MINIMUM_GAS_PRICE, MAXIMUM_GAS_PRICE);
    assertEquals(BigInteger.valueOf(300_000), gasProvider.getGasLimit(OasisContract.FUNC_BUY));
  }

  @Test
  public void getGasPrice_someFunction_returnInRealisticBounds() {
    GasProvider gasProvider = new GasProvider(web3j, MINIMUM_GAS_PRICE, MAXIMUM_GAS_PRICE);
    BigInteger actual = gasProvider.getGasPrice(OasisContract.FUNC_BUY);
    assertTrue(MINIMUM_GAS_PRICE.compareTo(actual) <= 0, TOO_LOW);
    assertTrue(MAXIMUM_GAS_PRICE.compareTo(actual) >= 0, TOO_HIGH);
  }

  @Test
  public void getPercentageOfProfitAsFee_zeroTransactions_returnCalculation() {
    assertThat(2, allOf(greaterThanOrEqualTo(1), lessThanOrEqualTo(2)));

    GasProvider gasProvider = new GasProvider(web3j, MINIMUM_GAS_PRICE, MAXIMUM_GAS_PRICE);
    assertEquals(0.1, gasProvider.getPercentageOfProfitAsFee(0));
  }

  @Test
  public void getPercentageOfProfitAsFee_tenTransactions_returnCalculation() {
    GasProvider gasProvider = new GasProvider(web3j, MINIMUM_GAS_PRICE, MAXIMUM_GAS_PRICE);
    assertEquals(0.35, gasProvider.getPercentageOfProfitAsFee(10));
  }

  @Test
  public void calculateGasPriceAsAPercentageOfProfit_zeroMedian_throwException() {
    GasProvider gasProvider = new GasProvider(web3j, MINIMUM_GAS_PRICE, MAXIMUM_GAS_PRICE);
    Exception exception =
        assertThrows(
            GasPriceException.class,
            () ->
                gasProvider.calculateGasPriceAsAPercentageOfProfit(
                    BigDecimal.ZERO, BigDecimal.ONE, 20.0, 0.1));
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(EXCEPTION));
  }

  @Test
  public void calculateGasPriceAsAPercentageOfProfit_zeroProfit_throwException() {
    GasProvider gasProvider = new GasProvider(web3j, MINIMUM_GAS_PRICE, MAXIMUM_GAS_PRICE);
    Exception exception =
        assertThrows(
            GasPriceException.class,
            () ->
                gasProvider.calculateGasPriceAsAPercentageOfProfit(
                    BigDecimal.ONE, BigDecimal.ZERO, 20.0, 0.1));
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(EXCEPTION));
  }

  @Test
  public void calculateGasPriceAsAPercentageOfProfit_zeroGasLimit_throwException() {
    GasProvider gasProvider = new GasProvider(web3j, MINIMUM_GAS_PRICE, MAXIMUM_GAS_PRICE);
    Exception exception =
        assertThrows(
            GasPriceException.class,
            () ->
                gasProvider.calculateGasPriceAsAPercentageOfProfit(
                    BigDecimal.ONE, BigDecimal.ONE, 0.0, 0.1));
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(EXCEPTION));
  }

  @Test
  public void calculateGasPriceAsAPercentageOfProfit_someNumbers_returnCalculation()
      throws GasPriceException {
    GasProvider gasProvider = new GasProvider(web3j, MINIMUM_GAS_PRICE, MAXIMUM_GAS_PRICE);
    BigInteger gasPrice =
        gasProvider.calculateGasPriceAsAPercentageOfProfit(
            new BigDecimal("200000000000000000000"),
            new BigDecimal("10000000000000000000"),
            300000.0,
            0.1);
    assertEquals(BigInteger.valueOf(16_666666666L), gasPrice);
  }

  @Test
  public void calculateGasPriceAsAPercentageOfProfit_someRealNumbers_returnCalculation()
      throws GasPriceException {
    GasProvider gasProvider = new GasProvider(web3j, MINIMUM_GAS_PRICE, MAXIMUM_GAS_PRICE);
    BigInteger gasPrice =
        gasProvider.calculateGasPriceAsAPercentageOfProfit(
            new BigDecimal("124730000000000000000"),
            new BigDecimal("1772900000000000000"),
            5300000,
            0.35);
    assertEquals(BigInteger.valueOf(938653907), gasPrice);
  }
}
