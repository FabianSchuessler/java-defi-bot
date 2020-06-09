package de.fs92.defi.weth;

import de.fs92.defi.contractneedsprovider.CircuitBreaker;
import de.fs92.defi.contractneedsprovider.ContractNeedsProvider;
import de.fs92.defi.contractneedsprovider.Permissions;
import de.fs92.defi.contractutil.Account;
import de.fs92.defi.contractutil.Approval;
import de.fs92.defi.contractutil.ContractValidationUtil;
import de.fs92.defi.gasprovider.GasProvider;
import de.fs92.defi.numberutil.Wad18;
import de.fs92.defi.util.Balances;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.TimeUnit;

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
    ContractValidationUtil.isContractValid(wethContract);
    account = new Account(wethContract, credentials, "WETH");
    approval = new Approval(wethContract, contractNeedsProvider);
  }

  public void weth2Eth(
      Balances balances,
      Wad18 potentialProfit,
      Wad18 medianEthereumPrice,
      Wad18 amountOfWethToUnwrap) {
    if (permissions.check("WETH2ETH")) {
      try {
        gasProvider.updateFastGasPrice(medianEthereumPrice, potentialProfit);
        Wad18 wethBalance = balances.weth.getAccount().getBalance();
        if (amountOfWethToUnwrap.compareTo(wethBalance) > 0) {
          amountOfWethToUnwrap = wethBalance;
          logger.warn("WETH AMOUNT TO UNWRAP WAS TOO BIG {}", amountOfWethToUnwrap);
        }

        logger.warn("CONVERT {} WETH TO ETH", amountOfWethToUnwrap);
        TransactionReceipt transferReceipt =
            wethContract.withdraw(amountOfWethToUnwrap.toBigInteger()).send();
        TimeUnit.SECONDS.sleep(
            1); // for balances to update, otherwise same (buy/sell) type of transaction happens,
        // although not enough balance weth/dai
        logger.trace(
            "Transaction complete, view it at https://etherscan.io/tx/{}",
            transferReceipt.getTransactionHash());

        balances.updateBalanceInformation(medianEthereumPrice); // not really necessary?
      } catch (Exception e) {
        logger.error(EXCEPTION, e);
        circuitBreaker.addTransactionFailedNow();
      }
    }
  }

  public void eth2Weth(
      Wad18 amountOfEthToWrap,
      Wad18 potentialProfit,
      Wad18 medianEthereumPrice,
      Balances balances) {
    if (permissions.check("ETH2WETH")) {
      try {
        gasProvider.updateFastGasPrice(medianEthereumPrice, potentialProfit);
        if (amountOfEthToWrap.compareTo(balances.ethereum.getBalance()) > 0) {
          amountOfEthToWrap = balances.ethereum.getBalanceWithoutMinimumEthereumReserveUpperLimit();
          logger.warn("ETH AMOUNT TO WRAP WAS TOO BIG {}", amountOfEthToWrap);
        }

        logger.warn("CONVERT {} ETH TO WETH", amountOfEthToWrap);
        TransactionReceipt transferReceipt =
            wethContract.deposit(amountOfEthToWrap.toBigInteger()).send();
        TimeUnit.SECONDS.sleep(1);
        logger.trace(
            "Transaction complete, view it at https://etherscan.io/tx/{}",
            transferReceipt.getTransactionHash());
        balances.updateBalanceInformation(medianEthereumPrice); // not really necessary?
      } catch (Exception e) {
        logger.error(EXCEPTION, e);
        circuitBreaker.addTransactionFailedNow();
      }
    }
  }

  public Approval getApproval() {
    return approval;
  }

  public Account getAccount() {
    return account;
  }

  public void checkIfWeth2EthConversionNecessaryThenDoIt(
      Wad18 requiredBalance, Balances balances, Wad18 potentialProfit, Wad18 medianEthereumPrice) {
    if (account.getBalance().compareTo(requiredBalance) < 0) {
      logger.trace("WETH 2 ETH CONVERSION NECESSARY");
      weth2Eth(
          balances,
          potentialProfit,
          medianEthereumPrice,
          account.getBalance().subtract(requiredBalance));
    }
  }
}
