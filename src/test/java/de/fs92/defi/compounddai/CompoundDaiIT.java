package de.fs92.defi.compounddai;

import de.fs92.defi.contractneedsprovider.*;
import de.fs92.defi.gasprovider.GasProvider;
import de.fs92.defi.numberutil.Sth28;
import de.fs92.defi.numberutil.Wad18;
import de.fs92.defi.util.JavaProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CompoundDaiIT {
  private static final String TRAVIS_INFURA_PROJECT_ID = "TRAVIS_INFURA_PROJECT_ID";
  private static final String TRAVIS_WALLET = "TRAVIS_WALLET";
  private static final String TRAVIS_PASSWORD = "TRAVIS_PASSWORD";

  private static final Wad18 minimumGasPrice = new Wad18(1_000000000);
  private static final Wad18 maximumGasPrice = new Wad18(200_000000000L);
  CompoundDai compoundDai;

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

    CircuitBreaker circuitBreaker = new CircuitBreaker();
    Web3j web3j = new Web3jProvider(infuraProjectId).web3j;
    Credentials credentials = new Wallet(password, wallet).getCredentials();
    GasProvider gasProvider = new GasProvider(web3j, minimumGasPrice, maximumGasPrice);
    Permissions permissions = new Permissions(true, true);
    ContractNeedsProvider contractNeedsProvider =
            new ContractNeedsProvider(web3j, credentials, gasProvider, permissions, circuitBreaker);
    compoundDai = new CompoundDai(contractNeedsProvider);
  }

  @Test
  void getExchangeRate_isBiggerThanHistoricRate_true() {
    Sth28 actual = compoundDai.getExchangeRate();
    Sth28 expected = new Sth28("204721828221871910000000000");
    assertTrue(actual.compareTo(expected) > 0);
  }

  @Test
  void getSupplyRate_isBiggerThanHistoricRate_true() {
    Wad18 actual = compoundDai.getSupplyRate();
    Wad18 expected = new Wad18("4035852335128320");
    assertTrue(actual.compareTo(expected) > 0);
  }

  @Test
  void getDailyInterest_isBiggerThanHistoricRate_true() {
    Wad18 daiSupplied = new Wad18("5000000000000000000000");
    Wad18 actual = compoundDai.getDailyInterest(daiSupplied);
    System.out.println(actual);
    Wad18 expected = new Wad18("4035852335128320");
    assertTrue(actual.compareTo(expected) > 0);
  }
}
