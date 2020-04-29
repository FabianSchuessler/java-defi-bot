package de.fs92.defi.contractneedsprovider;

import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import java.lang.invoke.MethodHandles;

public class Wallet {
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

  private Credentials credentials;

  public Wallet(String password, String wallet) {
    if (wallet.equals("") || password.equals("")) {
      logger.error("WALLET OR PASSWORD IS EMPTY");
    }

    try {
      credentials = WalletUtils.loadJsonCredentials(password, wallet);
      logger.trace("Credentials loaded");
    } catch (Exception e) {
      logger.error("Exception", e);
    }
  }

  public Credentials getCredentials() {
    return credentials;
  }
}
