package de.fs92.defi.util;

import de.fs92.defi.Main;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.net.URISyntaxException;

public class DirectoryUtil {
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

  private DirectoryUtil() {
    throw new IllegalStateException("Utility class");
  }

  public static String getCurrentDirectory(boolean isDevelopmentEnvironment) {
    String currentDirectory = "";
    try {
      File currentFile =
          new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI())
              .getParentFile();
      if (isDevelopmentEnvironment) {
        logger.trace("TESTING TRUE"); // weird bug with caching testing = false
        currentDirectory = currentFile.getParentFile().getPath();
      } else {
        logger.trace("TESTING FALSE"); // weird bug with caching testing = false
        currentDirectory = currentFile.getPath();
      }
    } catch (URISyntaxException e) {
      logger.error("URISyntaxException", e);
    }
    return currentDirectory;
  }
}
