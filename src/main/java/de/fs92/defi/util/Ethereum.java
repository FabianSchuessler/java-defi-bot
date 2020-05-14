package de.fs92.defi.util;

import de.fs92.defi.contractneedsprovider.ContractNeedsProvider;
import de.fs92.defi.contractneedsprovider.Permissions;
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

import static de.fs92.defi.util.NumberUtil.getFullPrecision;
import static de.fs92.defi.util.NumberUtil.getMachineReadable;

public class Ethereum {
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

  public final BigInteger minimumEthereumReserveUpperLimit;
  public final BigInteger minimumEthereumReserveLowerLimit;
  public final BigInteger minimumEthereumNecessaryForSale;

  private final Web3j web3j;
  private final Credentials credentials;
  private final Permissions permissions;

  private BigInteger balance;

  public Ethereum(
      @NotNull ContractNeedsProvider contractNeedsProvider,
      double minimumEthereumReserveUpperLimit,
      double minimumEthereumReserveLowerLimit,
      double minimumEthereumNecessaryForSale) {
    web3j = contractNeedsProvider.getWeb3j();
    permissions = contractNeedsProvider.getPermissions();
    credentials = contractNeedsProvider.getCredentials();
    this.minimumEthereumReserveUpperLimit = getMachineReadable(minimumEthereumReserveUpperLimit);
    this.minimumEthereumReserveLowerLimit = getMachineReadable(minimumEthereumReserveLowerLimit);
    this.minimumEthereumNecessaryForSale = getMachineReadable(minimumEthereumNecessaryForSale);
    balance = BigInteger.ZERO;
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
    BigInteger oldBalance = balance;
    try {
      balance =
          web3j.ethGetBalance(getAddress(), DefaultBlockParameterName.LATEST).send().getBalance();
    } catch (IOException e) {
      logger.error("IOException ", e);
      balance = BigInteger.ZERO;
    }
    if (oldBalance.compareTo(balance) != 0) {
      logger.trace("OLD BALANCE {} ETH", getFullPrecision(oldBalance));
      logger.trace("UPDATED BALANCE {} ETH", getFullPrecision(balance));
    }
  }

  public String getAddress() {
    return credentials.getAddress();
  }

  public BigInteger getBalance() {
    if (balance.compareTo(BigInteger.ZERO) != 0)
      logger.trace("ETH BALANCE {}{}", getFullPrecision(balance), " ETH");
    return balance;
  }

  public BigInteger getBalanceWithoutMinimumEthereumReserveUpperLimit() {
    BigInteger balanceWithoutMinimumEthereumReserveUpperLimit =
        BigInteger.ZERO.max(balance.subtract(minimumEthereumReserveUpperLimit));
    if (balanceWithoutMinimumEthereumReserveUpperLimit.compareTo(BigInteger.ZERO) != 0)
      logger.trace(
          "ETH BALANCE WITHOUT MINIMUM ETHEREUM RESERVER UPPER LIMIT {}{}",
          getFullPrecision(balanceWithoutMinimumEthereumReserveUpperLimit),
          " ETH");
    return balanceWithoutMinimumEthereumReserveUpperLimit;
  }
}
