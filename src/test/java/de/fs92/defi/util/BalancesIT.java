package de.fs92.defi.util;

import de.fs92.defi.compounddai.CompoundDai;
import de.fs92.defi.contractneedsprovider.*;
import de.fs92.defi.dai.Dai;
import de.fs92.defi.gasprovider.GasProvider;
import de.fs92.defi.medianizer.Medianizer;
import de.fs92.defi.numberutil.Wad18;
import de.fs92.defi.weth.Weth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BalancesIT {
  private static final String TRAVIS_INFURA_PROJECT_ID = "TRAVIS_INFURA_PROJECT_ID";
  private static final String TRAVIS_WALLET = "TRAVIS_WALLET";
  private static final String TRAVIS_PASSWORD = "TRAVIS_PASSWORD";

  private static final String TOO_HIGH = "Error, value is too high";
  private static final String TOO_LOW = "Error, value is too low";

  private static final Wad18 MINIMUM_GAS_PRICE = new Wad18(1_000000000);
  private static final Wad18 MAXIMUM_GAS_PRICE = new Wad18(200_000000000L);

  Balances balances;

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
    GasProvider gasProvider = new GasProvider(web3j, MINIMUM_GAS_PRICE, MAXIMUM_GAS_PRICE);
    Permissions permissions = new Permissions(true, true);
    ContractNeedsProvider contractNeedsProvider =
            new ContractNeedsProvider(web3j, credentials, gasProvider, permissions, circuitBreaker);

    Medianizer.setMedianizerContract(contractNeedsProvider);
    Dai dai =
            new Dai(
                    contractNeedsProvider,
                    Double.parseDouble(javaProperties.getValue("minimumDaiNecessaryForSaleAndLending")));
    Weth weth = new Weth(contractNeedsProvider);
    CompoundDai compoundDai = new CompoundDai(contractNeedsProvider);
    Ethereum ethereum =
            new Ethereum(
                    contractNeedsProvider,
                    Double.parseDouble(javaProperties.getValue("minimumEthereumReserveUpperLimit")),
                    Double.parseDouble(javaProperties.getValue("minimumEthereumReserveLowerLimit")),
                    Double.parseDouble(javaProperties.getValue("minimumEthereumNecessaryForSale")));

    balances = new Balances(dai, weth, compoundDai, ethereum);
  }

  @Test
  void currentOwnershipRatio_zeroDai_zero() throws InterruptedException {
    Wad18 medianEthereumPrice = new Wad18("200000000000000000000");
    Wad18 ethBalance = new Wad18("10000000000000000000");
    Wad18 daiBalance = Wad18.ZERO;
    Wad18 wethBalance = Wad18.ZERO;
    Wad18 cdaiBalance = Wad18.ZERO;
    balances.usd =
            ethBalance
                    .multiply(medianEthereumPrice)
                    .add(wethBalance.multiply(medianEthereumPrice))
                    .add(daiBalance)
                    .add(cdaiBalance);
    balances.currentOwnershipRatio(medianEthereumPrice, ethBalance, daiBalance, wethBalance);
    TimeUnit.MILLISECONDS.sleep(1000);
    double currentOwnershipRatio =
            balances.currentOwnershipRatio(medianEthereumPrice, ethBalance, daiBalance, wethBalance);
    assertTrue(currentOwnershipRatio == 0.0);
  }

  @Test
  void currentOwnershipRatio_zeroEth_one() throws InterruptedException {
    Wad18 medianEthereumPrice = new Wad18("200000000000000000000");
    Wad18 ethBalance = Wad18.ZERO;
    Wad18 daiBalance = new Wad18("10000000000000000000");
    Wad18 wethBalance = Wad18.ZERO;
    Wad18 cdaiBalance = Wad18.ZERO;
    balances.usd =
            ethBalance
                    .multiply(medianEthereumPrice)
                    .add(wethBalance.multiply(medianEthereumPrice))
                    .add(daiBalance)
                    .add(cdaiBalance);
    balances.currentOwnershipRatio(medianEthereumPrice, ethBalance, daiBalance, wethBalance);
    TimeUnit.MILLISECONDS.sleep(1000);
    double currentOwnershipRatio =
            balances.currentOwnershipRatio(medianEthereumPrice, ethBalance, daiBalance, wethBalance);
    System.out.println(currentOwnershipRatio);
    assertTrue(currentOwnershipRatio == 100.0);
  }

  @Test
  void currentOwnershipRatio_halfEth_zeroPointFive() throws InterruptedException {
    Wad18 medianEthereumPrice = new Wad18("200000000000000000000"); // 200 USD
    Wad18 ethBalance = new Wad18("10000000000000000000"); // 10 ETH
    Wad18 daiBalance = new Wad18("2000000000000000000000"); // 2000 DAI
    Wad18 wethBalance = Wad18.ZERO;
    Wad18 cdaiBalance = Wad18.ZERO;
    balances.usd =
            ethBalance
                    .multiply(medianEthereumPrice)
                    .add(wethBalance.multiply(medianEthereumPrice))
                    .add(daiBalance)
                    .add(cdaiBalance);
    balances.currentOwnershipRatio(medianEthereumPrice, ethBalance, daiBalance, wethBalance);
    TimeUnit.MILLISECONDS.sleep(1000);
    double currentOwnershipRatio =
            balances.currentOwnershipRatio(medianEthereumPrice, ethBalance, daiBalance, wethBalance);
    System.out.println(currentOwnershipRatio);
    assertTrue(currentOwnershipRatio == 50.0);
  }
}
