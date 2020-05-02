package de.fs92.defi.medianizer;

import de.fs92.defi.contractneedsprovider.ContractNeedsProvider;
import de.fs92.defi.gasprovider.GasProvider;
import de.fs92.defi.util.BigNumberUtil;
import de.fs92.defi.util.ContractUser;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;

public class Medianizer extends ContractUser {
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
  private static final String ETH_USD = " ETH/USD";
  private static final String ETH_DAI = " ETH/DAI";
  private static final String UTF8 = "UTF-8";
  private static final String ADDRESS = "0x729D19f657BD0614b4985Cf1D82531c67569197B";
  static final int PRICE_UPDATE_INTERVAL = 8 * 1000;
  private static final String EXCEPTION = "Exception";
  private static MedianizerContract contract;
  private static BigDecimal median;
  private static long pastTimeMedian;
  //  private static BigInteger pastBlock;
  private static Web3j web3j;

  private Medianizer() {
    throw new IllegalStateException("Utility class");
  }

  public static void setContract(@NotNull ContractNeedsProvider contractNeedsProvider) {
    Medianizer.web3j = contractNeedsProvider.getWeb3j();
    Credentials credentials = contractNeedsProvider.getCredentials();
    GasProvider gasProvider = contractNeedsProvider.getGasProvider();
    Medianizer.contract = MedianizerContract.load(ADDRESS, web3j, credentials, gasProvider);
  }

  public static BigDecimal getPrice() throws MedianException {
    long currentTime = System.currentTimeMillis();
    //    BigInteger currentBlock = getCurrentBlock();

    if (currentTime >= pastTimeMedian + PRICE_UPDATE_INTERVAL) {
      logger.trace("MEDIAN: TIME");
      pastTimeMedian = currentTime;
      updateMedian();
    }
    //    if (!pastBlock.equals(currentBlock)) {
    //      logger.trace("MEDIAN: BLOCK");
    //      pastBlock = currentBlock;
    //      updateMedian();
    //    }
    logger.trace("MEDIAN {}{}", BigNumberUtil.makeBigNumberHumanReadable(median), ETH_DAI);
    return median;
  }

  private static void updateMedian() throws MedianException {
    BigDecimal newMedian =
        getMedian(
            new BigDecimal[] {
              getKrakenEthPrice(), getMakerDAOEthPrice(), getCoinbaseProEthPrice()
            });
    if (newMedian.equals(BigDecimal.ZERO)) throw new MedianException("MEDIAN IS ZERO EXCEPTION");
    median = newMedian;
  }

  private static BigInteger getCurrentBlock() throws MedianException {
    try {
      return web3j.ethBlockNumber().send().getBlockNumber();
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
      throw new MedianException("CAN'T GET CURRENT BLOCK");
    }
  }

  static BigDecimal getMedian(BigDecimal[] array) throws MedianException {
    array =
        Arrays.stream(array)
            .filter(n -> n.compareTo(BigDecimal.ZERO) > 0)
            .sorted()
            .toArray(BigDecimal[]::new);

    logger.trace("MEDIAN {}", Arrays.toString(array));
    if (array.length == 0) throw new MedianException("ARRAY IS EMPTY");
    if (array.length == 1) throw new MedianException("TOO FEW PRICE FEEDS");
    if (array.length % 2 == 0)
      return array[array.length / 2]
          .add(array[array.length / 2 - 1])
          .divide(BigDecimal.valueOf(2), RoundingMode.DOWN);
    else return array[array.length / 2];
  }

  private static BigDecimal getMakerDAOEthPrice() {
    BigDecimal ethPrice = BigDecimal.ZERO;
    try {
      byte[] result = contract.read().send();
      ethPrice = new BigDecimal(new BigInteger(result));
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
    }
    logger.trace("MAKERDAO {}{}", BigNumberUtil.makeBigNumberHumanReadable(ethPrice), ETH_DAI);
    return ethPrice;
  }

  private static BigDecimal getCryptocompareEthPrice() {
    // maybe make this work with rate limit
    /*
     {"Response":"Error","Message":"You are over your rate limit please upgrade your
     account!","HasWarning":false,"Type":99,"RateLimit":{"calls_made":{"second":1,"minute":1,"hour":76,"day":20003,"month":57172,"total_calls":57172},"max_calls":{"second":20,"minute":300,"hour":3000,"day":20000,"month":180000}},"Data":{}}
    */
    BigDecimal ethPrice = BigDecimal.ZERO;
    try (Scanner scanner =
        new Scanner(
            new URL("https://min-api.cryptocompare.com/data/price?fsym=ETH&tsyms=USD").openStream(),
            UTF8)) {
      String out = scanner.useDelimiter("\\A").next();
      // {"USD":268.35}
      String[] parts = out.split("\"");
      ethPrice =
          BigNumberUtil.makeDoubleMachineReadable(
              Double.valueOf(parts[2].substring(1, parts[2].length() - 1)));
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
    }
    logger.trace("CRYPTOCOMPARE {}{}", BigNumberUtil.makeBigNumberHumanReadable(ethPrice), ETH_USD);
    return ethPrice;
  }

  private static BigDecimal getKrakenEthPrice() {
    BigDecimal ethPrice = BigDecimal.ZERO;
    try (Scanner scanner =
        new Scanner(
            new URL("https://api.kraken.com/0/public/Ticker?pair=ETHUSD").openStream(), UTF8)) {
      String out = scanner.useDelimiter("\\A").next();
      // {"error":[],"result":{"XETHZUSD":{"a":["251.29000","4","4.000"],"b":["250.99000","6","6.000"],"c":["251.04000","2.00000000"],"v":["25489.49597316","35276.40048677"],"p":["249.36880","248.99603"],"t":[3542,4754],"l":["245.85000","245.85000"],"h":["252.58000","253.35000"],"o":"252.05000"}}}
      String[] parts = out.split("\"");
      BigDecimal bid = BigNumberUtil.makeDoubleMachineReadable(Double.valueOf(parts[9]));
      BigDecimal ask = BigNumberUtil.makeDoubleMachineReadable(Double.valueOf(parts[17]));
      ethPrice = bid.add(ask).divide(BigDecimal.valueOf(2), RoundingMode.DOWN);
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
    }
    logger.trace("KRAKEN {}{}", BigNumberUtil.makeBigNumberHumanReadable(ethPrice), ETH_USD);
    return ethPrice;
  }

  static BigDecimal getCoinbaseProEthPrice() {
    BigDecimal ethPrice = BigDecimal.ZERO;
    try (Scanner scanner =
        new Scanner(
            new URL("https://api.coinbase.com/v2/prices/ETH-USD/spot").openStream(), UTF8)) {
      String out = scanner.useDelimiter("\\A").next();
      // {"sequence":6998278453,"bids":[["237.49","3.61796708",1]],"asks":[["237.64","48.303",2]]}
      String[] parts = out.split("\"");
      ethPrice = BigNumberUtil.makeDoubleMachineReadable(Double.valueOf(parts[13]));
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
    }
    logger.trace("COINBASE {}{}", BigNumberUtil.makeBigNumberHumanReadable(ethPrice), ETH_USD);
    return ethPrice;
  }
}
