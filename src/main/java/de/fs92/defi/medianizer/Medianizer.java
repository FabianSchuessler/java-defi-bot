package de.fs92.defi.medianizer;

import de.fs92.defi.contractneedsprovider.ContractNeedsProvider;
import de.fs92.defi.gasprovider.GasProvider;
import de.fs92.defi.numberutil.Wad18;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

import java.lang.invoke.MethodHandles;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

import static de.fs92.defi.numberutil.NumberUtil.getMachineReadable;

public class Medianizer {
  static final int PRICE_UPDATE_INTERVAL = 8 * 1000;
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
  private static final String ETH_USD = " ETH/USD";
  private static final String ETH_DAI = " ETH/DAI";
  private static final String ADDRESS = "0x729D19f657BD0614b4985Cf1D82531c67569197B";
  private static final String EXCEPTION = "Exception";
  private static MedianizerContract medianizerContract;
  private static Wad18 median;
  private static long pastTimeMedian;

  private Medianizer() {
    throw new IllegalStateException("Utility class");
  }

  public static void setMedianizerContract(@NotNull ContractNeedsProvider contractNeedsProvider) {
    Web3j web3j = contractNeedsProvider.getWeb3j();
    Credentials credentials = contractNeedsProvider.getCredentials();
    GasProvider gasProvider = contractNeedsProvider.getGasProvider();
    Medianizer.medianizerContract =
        MedianizerContract.load(ADDRESS, web3j, credentials, gasProvider);
  }

  public static Wad18 getPrice() throws MedianException {
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
    logger.trace("MEDIAN {}{}", median, ETH_DAI);
    return median;
  }

  private static void updateMedian() throws MedianException {
    Wad18 newMedian =
        getMedian(
            new Wad18[] {getKrakenEthPrice(), getMakerDAOEthPrice(), getCoinbaseProEthPrice()});
    if (newMedian.equals(Wad18.ZERO)) throw new MedianException("MEDIAN IS ZERO EXCEPTION");
    median = newMedian;
  }

  static Wad18 getMedian(Wad18[] array) throws MedianException {
    array =
        Arrays.stream(array)
            .filter(n -> n.compareTo(Wad18.ZERO) > 0)
            .sorted()
            .toArray(Wad18[]::new);

    logger.trace("MEDIAN {}", Arrays.toString(array));
    if (array.length == 0) throw new MedianException("ARRAY IS EMPTY");
    if (array.length == 1) throw new MedianException("TOO FEW PRICE FEEDS");
    if (array.length % 2 == 0)
      return array[array.length / 2]
          .add(array[array.length / 2 - 1])
          .divide(getMachineReadable(2.0));
    else return array[array.length / 2];
  }

  private static Wad18 getMakerDAOEthPrice() {
    Wad18 ethPrice = new Wad18();
    try {
      byte[] result = medianizerContract.read().send();
      ethPrice = new Wad18(new BigInteger(result));
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
    }
    logger.trace("MAKERDAO {}{}", ethPrice, ETH_DAI);
    return ethPrice;
  }

  /**
   * successful input example: {"USD":268.35}
   *
   * <p>input rate limit error example: {"Response":"Error","Message":"You are over your rate limit
   * please upgrade your
   * account!","HasWarning":false,"Type":99,"RateLimit":{"calls_made":{"second":1,"minute":1,"hour":76,"day":20003,"month":57172,"total_calls":57172},"max_calls":{"second":20,"minute":300,"hour":3000,"day":20000,"month":180000}},"Data":{}}
   *
   * @return BigInteger
   */
  private static Wad18 getCryptocompareEthPrice() {
    Wad18 ethPrice = new Wad18();
    try (Scanner scanner =
        new Scanner(
            new URL("https://min-api.cryptocompare.com/data/price?fsym=ETH&tsyms=USD").openStream(),
            StandardCharsets.UTF_8)) {
      String input = scanner.useDelimiter("\\A").next();
      String[] parts = input.split("\"");
      ethPrice =
          new Wad18(
              getMachineReadable(Double.valueOf(parts[2].substring(1, parts[2].length() - 1))));
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
    }
    logger.trace("CRYPTOCOMPARE {}{}", ethPrice, ETH_USD);
    return ethPrice;
  }

  /**
   * input example:
   * {"error":[],"result":{"XETHZUSD":{"a":["251.29000","4","4.000"],"b":["250.99000","6","6.000"],"c":["251.04000","2.00000000"],"v":["25489.49597316","35276.40048677"],"p":["249.36880","248.99603"],"t":[3542,4754],"l":["245.85000","245.85000"],"h":["252.58000","253.35000"],"o":"252.05000"}}}
   *
   * @return BigInteger
   */
  private static Wad18 getKrakenEthPrice() {
    Wad18 ethPrice = new Wad18();
    try (Scanner scanner =
        new Scanner(
            new URL("https://api.kraken.com/0/public/Ticker?pair=ETHUSD").openStream(),
            StandardCharsets.UTF_8)) {
      String input = scanner.useDelimiter("\\A").next();
      String[] parts = input.split("\"");
      BigInteger bid = getMachineReadable(Double.valueOf(parts[9]));
      BigInteger ask = getMachineReadable(Double.valueOf(parts[17]));
      ethPrice = new Wad18(bid.add(ask).divide(BigInteger.valueOf(2)));
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
    }
    logger.trace("KRAKEN {}{}", ethPrice, ETH_USD);
    return ethPrice;
  }

  /**
   * input example:
   * {"sequence":6998278453,"bids":[["237.49","3.61796708",1]],"asks":[["237.64","48.303",2]]}
   *
   * @return BigInteger
   */
  static Wad18 getCoinbaseProEthPrice() {
    Wad18 ethPrice = new Wad18();
    try (Scanner scanner =
        new Scanner(
            new URL("https://api.coinbase.com/v2/prices/ETH-USD/spot").openStream(),
            StandardCharsets.UTF_8)) {
      String input = scanner.useDelimiter("\\A").next();
      String[] parts = input.split("\"");
      ethPrice = new Wad18(getMachineReadable(Double.valueOf(parts[13])));
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
    }
    logger.trace("COINBASE {}{}", ethPrice, ETH_USD);
    return ethPrice;
  }
}
