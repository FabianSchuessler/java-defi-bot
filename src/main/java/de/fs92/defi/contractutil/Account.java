package de.fs92.defi.contractutil;

import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;

import java.lang.invoke.MethodHandles;
import java.math.BigInteger;

import static de.fs92.defi.util.NumberUtil.getFullPrecision;

public class Account {
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
  private static final String EXCEPTION = "Exception";
  private final AccountMethod contract;
  private final Credentials credentials;
  private final String name;

  private BigInteger balance;

  public Account(AccountMethod contract, Credentials credentials, String name) {
    this.contract = contract;
    this.credentials = credentials;
    this.name = name;
    update();
  }

  public BigInteger getBalance() {
    if (balance.compareTo(BigInteger.ZERO) != 0)
      logger.trace("{} BALANCE {} {}", name, getFullPrecision(balance), name);
    return balance;
  }

  public void update() {
    BigInteger oldBalance = balance;
    try {
      balance = contract.balanceOf(credentials.getAddress()).send();
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
      balance = BigInteger.ZERO;
    }
    if (oldBalance != null && oldBalance.compareTo(balance) != 0) {
      logger.trace("OLD BALANCE {} {}", getFullPrecision(oldBalance), name);
      logger.trace("UPDATED BALANCE {} {}", getFullPrecision(balance), name);
    } else if (oldBalance == null) {
      logger.trace("{} BALANCE {} {}", name, getFullPrecision(balance), name);
    }
  }
}
