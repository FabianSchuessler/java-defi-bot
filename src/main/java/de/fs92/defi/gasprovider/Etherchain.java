package de.fs92.defi.gasprovider;

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

public class Etherchain {
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

  private Etherchain() {
    throw new IllegalStateException("Utility class");
  }

  @NotNull
  static BigInteger getFastestGasPrice() throws GasPriceException {
    try {
      URL url = new URL("https://www.etherchain.org/api/gasPriceOracle");
      URLConnection hc = url.openConnection();
      hc.setRequestProperty(
          "User-Agent",
          "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
      String out =
          new Scanner(hc.getInputStream(), StandardCharsets.UTF_8).useDelimiter("\\A").next();
      String[] parts = out.split("\"");
      BigInteger gasPrice = Convert.toWei(parts[15], Convert.Unit.GWEI).toBigInteger();

      logger.trace(
          "ETHERCHAIN SUGGESTS GP {}{}",
          Convert.fromWei(gasPrice.toString(), Convert.Unit.GWEI),
          " GWEI");
      return gasPrice;
    } catch (IOException e) {
      logger.error("IOException", e);
      throw new GasPriceException("EtherchainException");
    }
  }
}
