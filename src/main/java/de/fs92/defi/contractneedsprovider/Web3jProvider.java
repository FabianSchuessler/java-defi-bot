package de.fs92.defi.contractneedsprovider;

import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.exceptions.ClientConnectionException;
import org.web3j.protocol.http.HttpService;

import java.lang.invoke.MethodHandles;

public class Web3jProvider {
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

  public final Web3j web3j;

  public Web3jProvider(String infuraProjectId) {
    this.web3j = Web3j.build(new HttpService("https://mainnet.infura.io/v3/" + infuraProjectId));
    try {
      logger.trace(
          "Connected to Ethereum client version: {}",
          web3j.web3ClientVersion().send().getWeb3ClientVersion());
    } catch (Exception e) {
      if (e instanceof ClientConnectionException) {
        logger.error("Check your infuraProjectId in the config.properties file.");
      }
      logger.error("Exception", e);
      System.exit(0);
    }
  }
}
