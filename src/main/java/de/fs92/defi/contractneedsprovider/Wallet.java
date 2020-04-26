package de.fs92.defi.contractneedsprovider;

import de.fs92.defi.util.DirectoryUtil;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import java.io.File;
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
      credentials = WalletUtils.loadCredentials(password, wallet);
      logger.trace("Credentials loaded");
    } catch (Exception e) {
      logger.error("Exception", e);
    }
  }

  public Wallet(String password, String givenEthereumAddress, boolean isDevelopmentEnvironment) {
    if (givenEthereumAddress.equals("")) {
      logger.error("ETHEREUM ADDRESS IS EMPTY {}", givenEthereumAddress);
    }

    String currentDirectory = DirectoryUtil.getCurrentDirectory(isDevelopmentEnvironment);

    try {
      File walletFile;
      if (isDevelopmentEnvironment) {
        logger.trace("TESTING TRUE"); // weird bug with caching testing = false
        walletFile = new File(currentDirectory + "\\wallets\\", givenEthereumAddress);
      } else {
        logger.trace("TESTING FALSE: AWS"); // weird bug with caching testing = false
        walletFile = new File(currentDirectory, givenEthereumAddress);
      }
      logger.trace("Wallet path {}", walletFile.getPath());
      credentials = WalletUtils.loadCredentials(password, walletFile);
      logger.trace("Credentials loaded");
    } catch (Exception e) {
      logger.error("Exception", e);
    }
  }

  public Credentials getCredentials() {
    return credentials;
  }
}
