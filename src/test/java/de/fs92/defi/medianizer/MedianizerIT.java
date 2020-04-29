package de.fs92.defi.medianizer;

import de.fs92.defi.contractneedsprovider.*;
import de.fs92.defi.gasprovider.GasProvider;
import de.fs92.defi.util.JavaProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.RepeatFailedTest;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

public class MedianizerIT {
  private static final String TRAVIS_INFURA_PROJECT_ID = "TRAVIS_INFURA_PROJECT_ID";
  private static final String TRAVIS_WALLET = "TRAVIS_WALLET";
  private static final String TRAVIS_PASSWORD = "TRAVIS_PASSWORD";

  private static final String TOO_HIGH = "Error, value is too high";
  private static final String TOO_LOW = "Error, value is too low";
  private static final BigDecimal MINIMUM_ETH_PRICE = new BigDecimal("100000000000000000000");
  private static final BigDecimal MAXIMUM_ETH_PRICE = new BigDecimal("500000000000000000000");
  public static ContractNeedsProvider contractNeedsProvider;

  @BeforeEach
  public void setUp() {
    String infuraProjectId;
    String password;
    String wallet;

    JavaProperties javaProperties = new JavaProperties(true);

    if ("true".equals(System.getenv().get("TRAVIS"))) {
      infuraProjectId = System.getenv().get(TRAVIS_INFURA_PROJECT_ID);
      wallet = System.getenv().get(TRAVIS_WALLET);
      password = System.getenv().get(TRAVIS_PASSWORD);
    } else {
      infuraProjectId = javaProperties.getValue("infuraProjectId");
      wallet = javaProperties.getValue("wallet");
      password = javaProperties.getValue("password");
    }

    Web3j web3j = new Web3jProvider(infuraProjectId).web3j;
    Credentials credentials = new Wallet(password, wallet).getCredentials();
    GasProvider gasProvider =
        new GasProvider(web3j, BigInteger.valueOf(1_000000000), BigInteger.valueOf(200_000000000L));
    Permissions permissions =
        new Permissions(
            Boolean.parseBoolean(javaProperties.getValue("transactionRequiresConfirmation")),
            Boolean.parseBoolean(javaProperties.getValue("soundOnTransaction")));
    CircuitBreaker circuitBreaker = new CircuitBreaker();
    contractNeedsProvider =
        new ContractNeedsProvider(web3j, credentials, gasProvider, permissions, circuitBreaker);
  }

  @Test
  public void isContractValid_isValid_continueRunning() {
    Medianizer.setContract(contractNeedsProvider);
    assertDoesNotThrow(Medianizer::isContractValid);
  }

  @Test
  public void isContractValid_isNotValid_stopRunning() {}

  @Test
  public void getCoinbaseProEthPrice_simpleGet_returnPrice() {
    Medianizer.setContract(contractNeedsProvider);
    BigDecimal coinbaseEthPrice = Medianizer.getCoinbaseProEthPrice();
    assertTrue(MINIMUM_ETH_PRICE.compareTo(coinbaseEthPrice) <= 0, TOO_LOW);
    assertTrue(MAXIMUM_ETH_PRICE.compareTo(coinbaseEthPrice) >= 0, TOO_HIGH);
  }

  @Test
  public void getPrice_oneExecution_priceIsWithinReasonableBounds() throws MedianException {
    Medianizer.setContract(contractNeedsProvider);
    BigDecimal median = Medianizer.getPrice();
    assertTrue(MINIMUM_ETH_PRICE.compareTo(median) <= 0, TOO_LOW);
    assertTrue(MAXIMUM_ETH_PRICE.compareTo(median) >= 0, TOO_HIGH);
  }

  @Test
  public void getPrice_twoExecutionsWithinPriceUpdateInterval_priceIsEqual()
      throws MedianException {
    Medianizer.setContract(contractNeedsProvider);
    BigDecimal firstMedian = Medianizer.getPrice();
    BigDecimal secondMedian = Medianizer.getPrice();

    assertEquals(firstMedian, secondMedian);

    assertTrue(MINIMUM_ETH_PRICE.compareTo(firstMedian) <= 0, TOO_LOW);
    assertTrue(MAXIMUM_ETH_PRICE.compareTo(firstMedian) >= 0, TOO_HIGH);
    assertTrue(MINIMUM_ETH_PRICE.compareTo(secondMedian) <= 0, TOO_LOW);
    assertTrue(MAXIMUM_ETH_PRICE.compareTo(secondMedian) >= 0, TOO_HIGH);
  }

  @RepeatFailedTest(10)
  public void getTwoExecutionsAfterPriceUpdateInterval_priceMightBeDifferent() throws Exception {
    Medianizer.setContract(contractNeedsProvider);
    BigDecimal firstMedian = Medianizer.getPrice();
    Thread.sleep(Medianizer.PRICE_UPDATE_INTERVAL);
    BigDecimal secondMedian = Medianizer.getPrice();
    assertNotEquals(firstMedian, secondMedian);
  }
}
