package de.fs92.defi.oasis;

import de.fs92.defi.compounddai.CompoundDai;
import de.fs92.defi.contractneedsprovider.*;
import de.fs92.defi.dai.Dai;
import de.fs92.defi.gasprovider.GasProvider;
import de.fs92.defi.util.JavaProperties;
import de.fs92.defi.weth.Weth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

public class OasisIT {
  private static final String TRAVIS_INFURA_PROJECT_ID = "TRAVIS_INFURA_PROJECT_ID";
  private static final String TRAVIS_WALLET = "TRAVIS_WALLET";
  private static final String TRAVIS_PASSWORD = "TRAVIS_PASSWORD";

  private static final BigInteger minimumGasPrice = BigInteger.valueOf(1_000000000);
  private static final BigInteger maximumGasPrice = BigInteger.valueOf(200_000000000L);

  Oasis oasis;

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

    Credentials credentials = new Wallet(password, wallet).getCredentials();
    Web3j web3j = new Web3jProvider(infuraProjectId).web3j;
    GasProvider gasProvider = new GasProvider(web3j, minimumGasPrice, maximumGasPrice);
    Permissions permissions = new Permissions(true, true);
    CircuitBreaker circuitBreaker = new CircuitBreaker();
    ContractNeedsProvider contractNeedsProvider =
        new ContractNeedsProvider(web3j, credentials, gasProvider, permissions, circuitBreaker);
    Weth weth = new Weth(contractNeedsProvider);
    CompoundDai compoundDai = new CompoundDai(contractNeedsProvider);
    oasis = new Oasis(contractNeedsProvider, compoundDai, weth);
  }

  @Test
  public void isContractValid_isValid_continueRunning() {
    assertDoesNotThrow(() -> oasis.isContractValid());
  }

  @Test
  public void isContractValid_isNotValid_stopRunning() {}

  @Test
  public void getOffer_nonExistingOffer_DaiOrWethMissingException() {
    Exception exception =
        assertThrows(DaiOrWethMissingException.class, () -> oasis.getOffer(BigInteger.ZERO));
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains("BOTH DAI AND WETH NEED TO BE PRESENT ONCE."));
  }

  @Test
  public void getBestOffer_buyDai_returnOffer() {
    BigInteger actual = oasis.getBestOffer(Dai.ADDRESS, Weth.ADDRESS);
    assertNotEquals(BigInteger.ZERO, actual);
    assertNotNull(actual);
  }

  @Test
  public void getBestOffer_sellDai_returnOffer() {
    BigInteger actual = oasis.getBestOffer(Weth.ADDRESS, Dai.ADDRESS);
    assertNotEquals(BigInteger.ZERO, actual);
    assertNotNull(actual);
  }

  @Test
  public void buyDaiSellWethIsProfitable_triggerException_emptyOasisDexOffer() {
    //    oasisDex.buyDaiSellWethIsProfitable();
  }

  @Test
  public void buyDaiSellWethIsProfitable_realValues_OasisDexOffer() {}

  @Test
  public void sellDaiBuyWethIsProfitable_triggerException_emptyOasisDexOffer() {}

  @Test
  public void sellDaiBuyWethIsProfitable_realValues_OasisDexOffer() {}
}
