package de.fs92.defi.uniswap;

import de.fs92.defi.compounddai.CompoundDai;
import de.fs92.defi.contractneedsprovider.*;
import de.fs92.defi.dai.Dai;
import de.fs92.defi.gasprovider.GasProvider;
import de.fs92.defi.util.Balances;
import de.fs92.defi.util.Ethereum;
import de.fs92.defi.util.JavaProperties;
import de.fs92.defi.weth.Weth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import static de.fs92.defi.util.BigNumberUtil.makeDoubleMachineReadable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UniswapIT {
  public Uniswap uniswap;
  public Balances balances;
  public ContractNeedsProvider contractNeedsProvider;
  public JavaProperties javaProperties;
  public Weth weth;
  public Ethereum ethereum;
  public CompoundDai compoundDai;
  public Dai dai;

  @BeforeEach
  public void setUp() {
    javaProperties = new JavaProperties(true);
    Permissions permissions =
        new Permissions(
            Boolean.parseBoolean(javaProperties.getValue("transactionsRequireConfirmation")),
            Boolean.parseBoolean(javaProperties.getValue("soundOnTransaction")));
    Web3j web3j = new Web3jProvider(javaProperties.getValue("infuraProjectId")).web3j;
    CircuitBreaker circuitBreaker = new CircuitBreaker();
    GasProvider gasProvider =
        new GasProvider(
            web3j, BigInteger.valueOf(1_000000000), BigInteger.valueOf(1000_000000000L));
    Wallet wallet =
        new Wallet(
            javaProperties.getValue("password"),
            javaProperties.getValue("myEthereumAddress"),
            true);

    Credentials credentials = wallet.getCredentials();

    contractNeedsProvider =
        new ContractNeedsProvider(web3j, credentials, gasProvider, permissions, circuitBreaker);

    dai = new Dai(contractNeedsProvider);
    compoundDai = new CompoundDai(contractNeedsProvider);
    weth = new Weth(contractNeedsProvider);
    ethereum = new Ethereum(contractNeedsProvider);

    balances = new Balances(dai, weth, compoundDai, ethereum);
    uniswap = new Uniswap(contractNeedsProvider, javaProperties, compoundDai, weth);
  }

  @Test
  public void getProfitableBuyDaiOffer_someRealNumbers_returnExpectedCalculation() {
    BigInteger buyableDaiAmount =
        new BigInteger("4533813969247998520957"); // 4533.813969247998520957
    BigDecimal medianEthereumPrice =
        new BigDecimal("231690000000000000000"); // 231.690000000000000000
    BigInteger ethToSell = new BigInteger("19439031735500000000"); // 19.439031735500000000
    UniswapOffer offer =
        uniswap.getProfitableBuyDaiOffer(
            buyableDaiAmount, ethToSell, balances, medianEthereumPrice, 0.35);
    assertEquals(new BigDecimal("19494706450003521351"), offer.profit);
  }

  @Test
  public void getBuyDaiParameters_buyableAmountIsZero_Null() throws Exception {
    BigDecimal medianEthereumPrice = new BigDecimal("231690000000000000000");
    Balances balances =
        new Balances(
            makeDoubleMachineReadable(0.0),
            makeDoubleMachineReadable(0.0),
            makeDoubleMachineReadable(0.0),
            dai,
            weth,
            compoundDai,
            ethereum);
    EthToTokenSwapInput ethToTokenSwapInput =
        uniswap.getBuyDaiParameters(balances, medianEthereumPrice);
    assertNull(ethToTokenSwapInput);
  }

  @Test
  public void
      getBuyDaiParameters_buyableAmountIsBiggerThanZero_allEthToTokenSwapInputAttributesNonZero()
          throws Exception {
    Balances balances =
        new Balances(
            makeDoubleMachineReadable(1.0),
            makeDoubleMachineReadable(0.0),
            makeDoubleMachineReadable(1.0),
            dai,
            weth,
            compoundDai,
            ethereum);
    BigDecimal medianEthereumPrice = new BigDecimal("231690000000000000000");
    EthToTokenSwapInput ethToTokenSwapInput =
        uniswap.getBuyDaiParameters(balances, medianEthereumPrice);
    assertNull(ethToTokenSwapInput);
  }

  @Test
  public void getSellDaiParameters_buyableAmountIsZero_Null() throws IOException {
    BigDecimal medianEthereumPrice = new BigDecimal("231690000000000000000");
    TokenToEthSwapInput tokenToEthSwapInput =
        uniswap.getSellDaiParameters(balances, medianEthereumPrice);
    assertNull(tokenToEthSwapInput);
  }

  @Test
  public void
      getSellDaiParameters_buyableAmountIsBiggerThanZero_allTokenToEthSwapInputAttributesNonZero() {
    // TODO: use mockito set eth and weth balances to non zero
  }

  @Test
  public void getProfitableBuyDaiOffer_triggerException_uniswapOfferZeroZero() {
    //    uniswap.getProfitableBuyDaiOffer();
  }

  @Test
  public void getProfitableBuyDaiOffer_lowerThanMinimumProfit_uniswapOfferZeroNonZero() {}

  @Test
  public void getProfitableBuyDaiOffer_higherThanMinimumProfit_uniswapOfferNonZeroNonZero() {}

  @Test
  public void getProfitableSellDaiOffer_triggerException_uniswapOfferZeroZero() {}

  @Test
  public void getProfitableSellDaiOffer_lowerThanMinimumProfit_uniswapOfferZeroNonZero() {}

  @Test
  public void getProfitableSellDaiOffer_higherThanMinimumProfit_uniswapOfferNonZeroNonZero() {}
}
