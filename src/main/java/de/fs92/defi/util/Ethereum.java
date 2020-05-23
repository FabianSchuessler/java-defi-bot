package de.fs92.defi.util;

import de.fs92.defi.contractneedsprovider.ContractNeedsProvider;
import de.fs92.defi.contractneedsprovider.Permissions;
import de.fs92.defi.medianizer.MedianException;
import de.fs92.defi.numberutil.Wad18;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.BigInteger;

import static de.fs92.defi.numberutil.NumberUtil.getMachineReadable;

public class Ethereum {
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

  public final Wad18 minimumEthereumReserveUpperLimit;
  public final Wad18 minimumEthereumReserveLowerLimit;
  public final Wad18 minimumEthereumNecessaryForSale;

  private final Web3j web3j;
  private final Credentials credentials;
  private final Permissions permissions;

  private Wad18 balance;

  public Ethereum(
      @NotNull ContractNeedsProvider contractNeedsProvider,
      double minimumEthereumReserveUpperLimit,
      double minimumEthereumReserveLowerLimit,
      double minimumEthereumNecessaryForSale) {
    web3j = contractNeedsProvider.getWeb3j();
    permissions = contractNeedsProvider.getPermissions();
    credentials = contractNeedsProvider.getCredentials();
    this.minimumEthereumReserveUpperLimit =
        new Wad18(getMachineReadable(minimumEthereumReserveUpperLimit));
    this.minimumEthereumReserveLowerLimit =
        new Wad18(getMachineReadable(minimumEthereumReserveLowerLimit));
    this.minimumEthereumNecessaryForSale =
        new Wad18(getMachineReadable(minimumEthereumNecessaryForSale));
    balance = Wad18.ZERO;
  }

  public void sendTransaction() throws Exception {
    if (permissions.check("SEND TRANSACTION")) {
      logger.trace("Sending 1 Wei ({} Ether)", Convert.fromWei("1", Convert.Unit.ETHER));

      TransactionReceipt transferReceipt =
          Transfer.sendFunds(
                  web3j,
                  credentials,
                  credentials.getAddress(), // you can put any address here
                  BigDecimal.ONE,
                  Convert.Unit.WEI) // 1 wei = 10^-18 Ether, this is the amount sent
              .send();

      logger.trace(
          "Transaction complete, view it at https://etherscan.io/tx/{}",
          transferReceipt.getTransactionHash());
      logger.trace("end run()");
    }
  }

  public void updateBalance() {
    Wad18 oldBalance = balance; // todo: use account
    try {
      balance =
          new Wad18(
              web3j
                  .ethGetBalance(getAddress(), DefaultBlockParameterName.LATEST)
                  .send()
                  .getBalance());
    } catch (IOException e) {
      logger.error("IOException", e);
      balance = new Wad18();
    }
    if (oldBalance != null && oldBalance.compareTo(balance) != 0) {
      logger.trace("OLD BALANCE {} ETH", oldBalance);
      logger.trace("UPDATED BALANCE {} ETH", balance);
    } else if (oldBalance == null) {
      logger.trace("ETH BALANCE {} ETH", balance);
    }
  }

  public String getAddress() {
    return credentials.getAddress();
  }

  public Wad18 getBalance() {
    if (balance.compareTo(Wad18.ZERO) != 0) logger.trace("ETH BALANCE {}{}", balance, " ETH");
    return balance;
  }

  public Wad18 getBalanceWithoutMinimumEthereumReserveUpperLimit() {
    Wad18 balanceWithoutMinimumEthereumReserveUpperLimit =
        Wad18.ZERO.max(balance.subtract(minimumEthereumReserveUpperLimit));
    if (balanceWithoutMinimumEthereumReserveUpperLimit.compareTo(Wad18.ZERO) != 0)
      logger.trace(
          "ETH BALANCE WITHOUT MINIMUM ETHEREUM RESERVER UPPER LIMIT {}{}",
          balanceWithoutMinimumEthereumReserveUpperLimit,
          " ETH");
    return balanceWithoutMinimumEthereumReserveUpperLimit;
  }

  public BigInteger getCurrentBlock() throws MedianException {
    try {
      return web3j.ethBlockNumber().send().getBlockNumber();
    } catch (Exception e) {
      logger.error("Exception", e);
      throw new MedianException("CAN'T GET CURRENT BLOCK");
    }
  }
}
