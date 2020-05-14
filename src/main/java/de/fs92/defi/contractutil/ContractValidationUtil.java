package de.fs92.defi.contractutil;

import de.fs92.defi.contractneedsprovider.CircuitBreaker;
import org.slf4j.LoggerFactory;
import org.web3j.tx.Contract;

import java.lang.invoke.MethodHandles;

public class ContractValidationUtil {
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
  private static final String EXCEPTION = "Exception";

  private ContractValidationUtil() {
    throw new IllegalStateException("Utility class");
  }

  public static void isContractValid(Contract contract, CircuitBreaker circuitBreaker) {
    try {
      if (contract.isValid()) {
        logger.trace("{} CONTRACT IS VALID", contract.getClass());
      } else {
        logger.trace("{} CONTRACT IS INVALID", contract.getClass());
        circuitBreaker.stopRunning();
      }
    } catch (Exception e) {
      circuitBreaker.stopRunning();
      logger.error(EXCEPTION, e);
    }
  }
}
