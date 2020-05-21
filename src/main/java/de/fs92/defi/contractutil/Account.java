package de.fs92.defi.contractutil;

import de.fs92.defi.numberutil.Wad18;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;

import java.lang.invoke.MethodHandles;
import java.math.BigInteger;

public class Account {
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
  private static final String EXCEPTION = "Exception";
  private final AccountMethod contract;
  private final Credentials credentials;
  private final String name;

  private Wad18 balance;

  public Account(AccountMethod contract, Credentials credentials, String name) {
    this.contract = contract;
    this.credentials = credentials;
    this.name = name;
    update();
  }

  public Wad18 getBalance() {
    if (balance.compareTo(Wad18.ZERO) != 0)
      logger.trace("{} BALANCE {} {}", name, balance, name);
    return balance;
  }

  public void update() {
    Wad18 oldBalance = balance;
    try {
      balance = new Wad18(contract.balanceOf(credentials.getAddress()).send());
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
      balance = new Wad18();
    }
    if (oldBalance != null && oldBalance.compareTo(balance) != 0) {
      logger.trace("OLD BALANCE {} {}", oldBalance, name);
      logger.trace("UPDATED BALANCE {} {}", balance, name);
    } else if (oldBalance == null) {
      logger.trace("{} BALANCE {} {}", name, balance, name);
    }
  }
}
