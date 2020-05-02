package de.fs92.defi.flipper;

import de.fs92.defi.contractneedsprovider.CircuitBreaker;
import de.fs92.defi.contractneedsprovider.ContractNeedsProvider;
import de.fs92.defi.contractneedsprovider.Permissions;
import de.fs92.defi.gasprovider.GasProvider;
import de.fs92.defi.util.Balances;
import de.fs92.defi.util.ContractUser;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

import java.lang.invoke.MethodHandles;

public class Flipper extends ContractUser {
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
  public static final String ADDRESS = "0xd8a04F5412223F513DC55F839574430f5EC15531";
  private final FlipperContract contract;
  private final GasProvider gasProvider;
  private final Permissions permissions;
  private final Credentials credentials;
  private final CircuitBreaker circuitBreaker;

  public Flipper(@NotNull ContractNeedsProvider contractNeedsProvider) {
    Web3j web3j = contractNeedsProvider.getWeb3j();
    credentials = contractNeedsProvider.getCredentials();
    gasProvider = contractNeedsProvider.getGasProvider();
    permissions = contractNeedsProvider.getPermissions();
    circuitBreaker = contractNeedsProvider.getCircuitBreaker();
    contract = FlipperContract.load(ADDRESS, web3j, credentials, gasProvider);
  }

  public void checkIfBuyEthIsProfitableThenDoIt(Balances balances) {}
}
