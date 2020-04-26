package de.fs92.defi.dai;

import de.fs92.defi.contractneedsprovider.*;
import de.fs92.defi.gasprovider.GasProvider;
import de.fs92.defi.util.JavaProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

import java.math.BigInteger;

public class DaiIT {
  private static final BigInteger minimumGasPrice = BigInteger.valueOf(1_000000000);
  private static final BigInteger maximumGasPrice = BigInteger.valueOf(200_000000000L);
  Dai dai;

  @BeforeEach
  public void setUp() {
    JavaProperties javaProperties = new JavaProperties(true);
    String ethereumAddress = javaProperties.getValue("myEthereumAddress");
    String password = javaProperties.getValue("password");
    String infuraProjectId = javaProperties.getValue("infuraProjectId");
    CircuitBreaker circuitBreaker = new CircuitBreaker();
    Web3j web3j = new Web3jProvider(infuraProjectId).web3j;
    Credentials credentials = new Wallet(password, ethereumAddress, true).getCredentials();
    GasProvider gasProvider = new GasProvider(web3j, minimumGasPrice, maximumGasPrice);
    Permissions permissions = new Permissions(true, true);
    ContractNeedsProvider contractNeedsProvider =
        new ContractNeedsProvider(web3j, credentials, gasProvider, permissions, circuitBreaker);
    dai = new Dai(contractNeedsProvider);
  }

  @Test
  public void isContractValid_isValid_continueRunning() {
    dai.isContractValid();
  }

  @Test
  public void isContractValid_isNotValid_stopRunning() {}
}
