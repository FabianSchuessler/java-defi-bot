package de.fs92.defi.weth;

import de.fs92.defi.contractneedsprovider.CircuitBreaker;
import de.fs92.defi.contractneedsprovider.ContractNeedsProvider;
import de.fs92.defi.contractneedsprovider.Permissions;
import de.fs92.defi.contractutil.Account;
import de.fs92.defi.contractutil.Approval;
import de.fs92.defi.contractutil.ContractValidationUtil;
import de.fs92.defi.gasprovider.GasProvider;
import de.fs92.defi.util.Balances;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.lang.invoke.MethodHandles;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import static de.fs92.defi.util.NumberUtil.getFullPrecision;

public class Weth {
  public static final String ADDRESS = "0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2";
  private static final String EXCEPTION = "Exception";
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
  private final WethContract wethContract;
  private final GasProvider gasProvider;
  private final Permissions permissions;
  private final CircuitBreaker circuitBreaker;

  private final Account account;
  private final Approval approval;

  public Weth(@NotNull ContractNeedsProvider contractNeedsProvider) {
    Web3j web3j = contractNeedsProvider.getWeb3j();
    gasProvider = contractNeedsProvider.getGasProvider();
    permissions = contractNeedsProvider.getPermissions();
    Credentials credentials = contractNeedsProvider.getCredentials();
    circuitBreaker = contractNeedsProvider.getCircuitBreaker();
    wethContract = WethContract.load(ADDRESS, web3j, credentials, gasProvider);
    ContractValidationUtil.isContractValid(wethContract, circuitBreaker);
    account = new Account(wethContract, credentials, "WETH");
    approval = new Approval(wethContract, contractNeedsProvider);
  }

  public void weth2Eth(
      Balances balances,
      BigInteger potentialProfit,
      BigInteger medianEthereumPrice,
      BigInteger amountOfWethToUnwrap) {
    if (permissions.check("WETH2ETH")) {
      try {
        gasProvider.updateFastGasPrice(medianEthereumPrice, potentialProfit);
        BigInteger wethBalance = balances.weth.getAccount().getBalance();
        if (amountOfWethToUnwrap.compareTo(wethBalance) > 0) {
          amountOfWethToUnwrap = wethBalance;
          logger.warn(
              "WETH AMOUNT TO UNWRAP WAS TOO BIG {}", getFullPrecision(amountOfWethToUnwrap));
        }

        logger.warn("CONVERT {} WETH TO ETH", getFullPrecision(amountOfWethToUnwrap));
        TransactionReceipt transferReceipt = wethContract.withdraw(amountOfWethToUnwrap).send();
        TimeUnit.SECONDS.sleep(
            1); // for balances to update, otherwise same (buy/sell) type of transaction happens,
        // although not enough balance weth/dai
        logger.trace(
            "Transaction complete, view it at https://etherscan.io/tx/{}",
            transferReceipt.getTransactionHash());

        balances.updateBalanceInformation(medianEthereumPrice); // not really necessary?
      } catch (Exception e) {
        logger.error(EXCEPTION, e);
        circuitBreaker.add(System.currentTimeMillis());
      }
    }
  }

  public void eth2Weth(
      BigInteger amountOfEthToWrap,
      BigInteger potentialProfit,
      BigInteger medianEthereumPrice,
      Balances balances) {
    if (permissions.check("ETH2WETH")) {
      try {
        gasProvider.updateFastGasPrice(medianEthereumPrice, potentialProfit);
        if (amountOfEthToWrap.compareTo(balances.ethereum.getBalance()) > 0) {
          amountOfEthToWrap = balances.ethereum.getBalanceWithoutMinimumEthereumReserveUpperLimit();
          logger.warn("ETH AMOUNT TO WRAP WAS TOO BIG {}", getFullPrecision(amountOfEthToWrap));
        }

        logger.warn("CONVERT {} ETH TO WETH", getFullPrecision(amountOfEthToWrap));
        TransactionReceipt transferReceipt = wethContract.deposit(amountOfEthToWrap).send();
        TimeUnit.SECONDS.sleep(1);
        logger.trace(
            "Transaction complete, view it at https://etherscan.io/tx/{}",
            transferReceipt.getTransactionHash());
        balances.updateBalanceInformation(medianEthereumPrice); // not really necessary?
      } catch (Exception e) {
        logger.error(EXCEPTION, e);
        circuitBreaker.add(System.currentTimeMillis());
      }
    }
  }

  public Approval getApproval() {
    return approval;
  }

  public Account getAccount() {
    return account;
  }
}
