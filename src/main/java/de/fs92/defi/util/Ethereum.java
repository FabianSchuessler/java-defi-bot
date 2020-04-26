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

public class Ethereum {
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

  private final Web3j web3j;
  private final Credentials credentials;
  private final Permissions permissions;

  public Ethereum(@NotNull ContractNeedsProvider contractNeedsProvider) {
    web3j = contractNeedsProvider.getWeb3j();
    permissions = contractNeedsProvider.getPermissions();
    credentials = contractNeedsProvider.getCredentials();
  }

  public void sendTransaction() throws Exception {
    if (permissions.check("SEND TRANSACTION")) {
      logger.trace(
          "Sending 1 Wei ({} Ether)", Convert.fromWei("1", Convert.Unit.ETHER).toPlainString());

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

  public BigDecimal getBalance() {
    try {
      return new BigDecimal(
          web3j.ethGetBalance(getAddress(), DefaultBlockParameterName.LATEST).send().getBalance());
    } catch (IOException e) {
      logger.error("IOException ", e);
    }
    return BigDecimal.ZERO;
  }

  public String getAddress() {
    return credentials.getAddress();
  }
}
