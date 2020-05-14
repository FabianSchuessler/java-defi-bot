package de.fs92.defi.dai;

import de.fs92.defi.contractneedsprovider.*;
import de.fs92.defi.gasprovider.GasProvider;
import de.fs92.defi.util.JavaProperties;
import org.junit.jupiter.api.BeforeEach;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

import java.math.BigInteger;

public class DaiIT {
  private static final String TRAVIS_INFURA_PROJECT_ID = "TRAVIS_INFURA_PROJECT_ID";
  private static final String TRAVIS_WALLET = "TRAVIS_WALLET";
  private static final String TRAVIS_PASSWORD = "TRAVIS_PASSWORD";

  private static final BigInteger minimumGasPrice = BigInteger.valueOf(1_000000000);
  private static final BigInteger maximumGasPrice = BigInteger.valueOf(200_000000000L);
  Dai dai;
  JavaProperties javaProperties;
  ContractNeedsProvider contractNeedsProvider;

  @BeforeEach
  public void setUp() {
    String infuraProjectId;
    String password;
    String wallet;

    javaProperties = new JavaProperties(true);

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
    contractNeedsProvider =
        new ContractNeedsProvider(web3j, credentials, gasProvider, permissions, circuitBreaker);
    dai =
        new Dai(
            contractNeedsProvider,
            Double.parseDouble(javaProperties.getValue("minimumDaiNecessaryForSale")));
  }
}
