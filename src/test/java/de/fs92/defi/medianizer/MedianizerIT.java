package de.fs92.defi.medianizer;

import de.fs92.defi.contractneedsprovider.*;
import de.fs92.defi.gasprovider.GasProvider;
import de.fs92.defi.numberutil.Wad18;
import de.fs92.defi.util.JavaProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MedianizerIT {
  private static final String TRAVIS_INFURA_PROJECT_ID = "TRAVIS_INFURA_PROJECT_ID";
  private static final String TRAVIS_WALLET = "TRAVIS_WALLET";
  private static final String TRAVIS_PASSWORD = "TRAVIS_PASSWORD";

  private static final String TOO_HIGH = "Error, value is too high";
  private static final String TOO_LOW = "Error, value is too low";
  private static final Wad18 MINIMUM_ETH_PRICE = new Wad18("100000000000000000000");
  private static final Wad18 MAXIMUM_ETH_PRICE = new Wad18("500000000000000000000");
  static ContractNeedsProvider contractNeedsProvider;

  @BeforeEach
  void setUp() {
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
            new GasProvider(web3j, new Wad18(1_000000000), new Wad18(200_000000000L));
    Permissions permissions =
            new Permissions(
                    Boolean.parseBoolean(javaProperties.getValue("transactionRequiresConfirmation")),
                    Boolean.parseBoolean(javaProperties.getValue("playSoundOnTransaction")));
    CircuitBreaker circuitBreaker = new CircuitBreaker();
    contractNeedsProvider =
        new ContractNeedsProvider(web3j, credentials, gasProvider, permissions, circuitBreaker);
  }

  @Test
  void getCoinbaseProEthPrice_simpleGet_returnPrice() {
    Medianizer.setMedianizerContract(contractNeedsProvider);
    Wad18 coinbaseEthPrice = Medianizer.getCoinbaseProEthPrice();
    assertTrue(MINIMUM_ETH_PRICE.compareTo(coinbaseEthPrice) <= 0, TOO_LOW);
    assertTrue(MAXIMUM_ETH_PRICE.compareTo(coinbaseEthPrice) >= 0, TOO_HIGH);
  }

  @Test
  void getPrice_oneExecution_priceIsWithinReasonableBounds() throws MedianException {
    Medianizer.setMedianizerContract(contractNeedsProvider);
    Wad18 median = Medianizer.getPrice();
    assertTrue(MINIMUM_ETH_PRICE.compareTo(median) <= 0, TOO_LOW);
    assertTrue(MAXIMUM_ETH_PRICE.compareTo(median) >= 0, TOO_HIGH);
  }

  @Test
  void getPrice_twoExecutionsWithinPriceUpdateInterval_priceIsEqual()
      throws MedianException {
    Medianizer.setMedianizerContract(contractNeedsProvider);
    Wad18 firstMedian = Medianizer.getPrice();
    Wad18 secondMedian = Medianizer.getPrice();

    assertEquals(firstMedian, secondMedian);

    assertTrue(MINIMUM_ETH_PRICE.compareTo(firstMedian) <= 0, TOO_LOW);
    assertTrue(MAXIMUM_ETH_PRICE.compareTo(firstMedian) >= 0, TOO_HIGH);
    assertTrue(MINIMUM_ETH_PRICE.compareTo(secondMedian) <= 0, TOO_LOW);
    assertTrue(MAXIMUM_ETH_PRICE.compareTo(secondMedian) >= 0, TOO_HIGH);
  }
}
