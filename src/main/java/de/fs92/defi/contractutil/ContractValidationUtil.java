package de.fs92.defi.contractutil;

import org.slf4j.LoggerFactory;
import org.web3j.tx.Contract;

import java.lang.invoke.MethodHandles;

import static de.fs92.defi.Main.shutdown;

public class ContractValidationUtil {
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
  private static final String EXCEPTION = "Exception";

  private ContractValidationUtil() {
    throw new IllegalStateException("Utility class");
  }

  public static void isContractValid(Contract contract) {
    try {
      if (contract.isValid()) {
        logger.trace("{} CONTRACT IS VALID", contract.getClass());
      } else {
        logger.trace("{} CONTRACT IS INVALID", contract.getClass());
        shutdown();
      }
    } catch (Exception e) {
      shutdown();
      logger.error(EXCEPTION, e);
    }
  }
}
