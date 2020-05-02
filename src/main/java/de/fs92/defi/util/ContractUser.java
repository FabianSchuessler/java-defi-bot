package de.fs92.defi.util;

import de.fs92.defi.contractneedsprovider.CircuitBreaker;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.web3j.tx.Contract;

import java.lang.invoke.MethodHandles;

public class ContractUser {
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
  private static final String EXCEPTION = "Exception";

  protected static void isContractValid(@NotNull Contract contract, CircuitBreaker circuitBreaker) {
    try {
      boolean isValid = contract.isValid();
      if (isValid) {
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
