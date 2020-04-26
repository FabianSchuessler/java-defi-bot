package de.fs92.defi.contractneedsprovider;

import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;

public class Permissions {
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

  private final boolean soundOnTransaction;
  private final boolean transactionRequiresConfirmation;

  public Permissions(boolean transactionRequiresConfirmation, boolean soundOnTransaction) {
    this.transactionRequiresConfirmation = transactionRequiresConfirmation;
    this.soundOnTransaction = soundOnTransaction;
  }

  public boolean check(String transactionInformation) {
    if (soundOnTransaction) {
      final Runnable runnable =
          (Runnable) Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.exclamation");
      if (runnable != null) runnable.run();
    }

    if (transactionRequiresConfirmation) {
      logger.warn(transactionInformation);

      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      logger.warn("PLEASE CONFIRM {}{}", transactionInformation, " TRANSACTION WITH [confirm] ");

      String isConfirmed = null;
      try {
        isConfirmed = reader.readLine();
      } catch (IOException e) {
        logger.error("IOException", e);
      }
      assert isConfirmed != null;
      if (isConfirmed.equals("confirm")) {
        logger.info("TRANSACTION PERMITTED.");
        return true;
      } else {
        logger.info("TRANSACTION DENIED.");
        return false;
      }
    }
    return true;
  }
}
