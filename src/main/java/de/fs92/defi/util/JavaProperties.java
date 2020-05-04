package de.fs92.defi.util;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.Properties;

public class JavaProperties {
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
  private static final String CONFIG_FILE_NAME = "/config.properties";
  private final String currentDirectory;

  public JavaProperties(boolean isDevelopmentEnvironment) {
    this.currentDirectory = DirectoryUtil.getCurrentDirectory(isDevelopmentEnvironment);
    logger.trace("{}{}", currentDirectory, CONFIG_FILE_NAME);
  }

  public String getValue(String key) {
    try (InputStream propertiesFile = new FileInputStream(currentDirectory + CONFIG_FILE_NAME)) {
      Properties properties = new Properties();
      properties.load(propertiesFile);
      String propertiesValue = properties.getProperty(key);
      logger.trace("READ KEY {}", key);

      String loggedValue;
      switch (key) {
        case "password":
          loggedValue = "*password is not logged*";
          break;
        case "infuraProjectId":
          loggedValue = "*infuraProjectId is not logged*";
          break;
        case "wallet":
          loggedValue = "*wallet is not logged*";
          break;
        default:
          loggedValue = propertiesValue;
      }

      logger.trace("READ VALUE {}", loggedValue);

      return propertiesValue;
    } catch (IOException e) {
      logger.error("IOException ", e);
    }
    logger.warn("READ VALUE EXCEPTION");
    return "";
  }

  public void updateValue(String key, String value) {
    try {
      PropertiesConfiguration config =
          new PropertiesConfiguration(currentDirectory + CONFIG_FILE_NAME);
      logger.trace("CURRENT CONFIG {}", config);
      config.setProperty(key, value);
      logger.trace("WROTE KEY VALUE {} {}", key, value);
      config.save();
    } catch (Exception e) {
      logger.error("Exception", e);
    }
  }
}
