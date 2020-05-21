package de.fs92.defi.gasprovider;

import de.fs92.defi.numberutil.Wad18;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ETHGasStation {
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

  private ETHGasStation() {
    throw new IllegalStateException("Utility class");
  }

  @NotNull
  static Wad18 getFastestGasPrice() throws GasPriceException {
    Wad18 gasPrice = getGasPrice(4);
    logger.trace(
            "ETHERGASSTATION SUGGESTS GP {}{}",
            Convert.fromWei(gasPrice.toBigInteger().toString(), Convert.Unit.GWEI),
            " GWEI");
    return gasPrice;
  }

  @NotNull
  static Wad18 getSafeLowGasPrice() throws GasPriceException {
    Wad18 gasPrice = getGasPrice(6);
    logger.trace(
            "ETHERGASSTATION SUGGESTS GP {}{}",
            Convert.fromWei(gasPrice.toBigInteger().toString(), Convert.Unit.GWEI),
            " GWEI");
    return gasPrice;
  }

  private static Wad18 getGasPrice(int i) throws GasPriceException {
    try {
      URL url = new URL("https://ethgasstation.info/json/ethgasAPI.json");
      URLConnection hc = url.openConnection();
      hc.setRequestProperty(
              "User-Agent",
              "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
      String out =
              new Scanner(hc.getInputStream(), StandardCharsets.UTF_8).useDelimiter("\\A").next();
      String[] parts = out.split("\"");
      String clean = parts[i].replaceAll("[^\\d.]", "");
      return new Wad18(Convert.toWei(clean, Convert.Unit.GWEI).toBigInteger().divide(BigInteger.TEN));
    } catch (IOException e) {
      logger.error("IOException ", e);
      throw new GasPriceException("ETHGasStationException");
    }
  }
}
