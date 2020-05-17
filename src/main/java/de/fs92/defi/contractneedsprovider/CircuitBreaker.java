package de.fs92.defi.contractneedsprovider;

import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

public class CircuitBreaker {
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
  final ArrayList<Long> failedTransactionsWithinTheLastTenMinutesForErrorBlockingArrayList;
  private boolean continueRunning = true;

  public CircuitBreaker() {
    failedTransactionsWithinTheLastTenMinutesForErrorBlockingArrayList = new ArrayList<>();
  }

  public boolean getContinueRunning() {
    return continueRunning;
  }

  public void stopRunning() {
    continueRunning = false;
  }

  public boolean isAllowingOperations(int number) {
    return failedTransactionsWithinTheLastTenMinutesForErrorBlockingArrayList.size() < number;
  }

  public void addTransactionFailedNow() {
    logger.trace("ADD A FAILED TRANSACTION");
    failedTransactionsWithinTheLastTenMinutesForErrorBlockingArrayList.add(
        System.currentTimeMillis());
  }

  public void update() {
    if (System.currentTimeMillis()
        >= failedTransactionsWithinTheLastTenMinutesForErrorBlockingArrayList.get(0)
            + 10 * 60 * 1000) {
      logger.trace("REMOVE FAILED TRANSACTION: 10 MINUTES");
      failedTransactionsWithinTheLastTenMinutesForErrorBlockingArrayList.remove(0);
    }
  }

  public List<Long> getFailedTransactions() {
    return failedTransactionsWithinTheLastTenMinutesForErrorBlockingArrayList;
  }
}
