package de.fs92.defi.gasprovider;

import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class ETHGasStation {
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

  private ETHGasStation() {
    throw new IllegalStateException("Utility class");
  }

  @NotNull
  static BigInteger getFastestGasPrice() throws GasPriceException {
    BigInteger gasPrice = getGasPrice(8);
    logger.trace(
        "ETHERGASSTATION SUGGESTS GP {}{}",
        Convert.fromWei(gasPrice.toString(), Convert.Unit.GWEI),
        " GWEI");
    return gasPrice;
  }

  @NotNull
  static BigInteger getAverageGasPrice() throws GasPriceException {
    BigInteger gasPrice = getGasPrice(4);
    logger.trace(
        "ETHERGASSTATION SUGGESTS GP {}{}",
        Convert.fromWei(gasPrice.toString(), Convert.Unit.GWEI),
        " GWEI");
    return gasPrice;
  }

  private static BigInteger getGasPrice(int i) throws GasPriceException {
    try {
      URL url = new URL("https://ethgasstation.info/json/ethgasAPI.json");
      URLConnection hc = url.openConnection();
      hc.setRequestProperty(
          "User-Agent",
          "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
      String out = new Scanner(hc.getInputStream(), "UTF-8").useDelimiter("\\A").next();
      String[] parts = out.split("\"");
      String clean = parts[i].replaceAll("[^\\d.]", "");
      return Convert.toWei(clean, Convert.Unit.GWEI).toBigInteger();
    } catch (IOException e) {
      logger.error("IOException ", e);
      throw new GasPriceException("ETHGasStationException");
    }
  }
}
