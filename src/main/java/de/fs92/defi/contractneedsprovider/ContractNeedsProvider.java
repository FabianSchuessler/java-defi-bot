package de.fs92.defi.contractneedsprovider;

import de.fs92.defi.gasprovider.GasProvider;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

public class ContractNeedsProvider {
  private final Web3j web3j;
  private final Credentials credentials;
  private final GasProvider gasProvider;
  private final Permissions permissions;
  private final CircuitBreaker circuitBreaker;

  public ContractNeedsProvider(
      Web3j web3j,
      Credentials credentials,
      GasProvider gasProvider,
      Permissions permissions,
      CircuitBreaker circuitBreaker) {
    this.web3j = web3j;
    this.credentials = credentials;
    this.gasProvider = gasProvider;
    this.permissions = permissions;
    this.circuitBreaker = circuitBreaker;
  }

  public Web3j getWeb3j() {
    return web3j;
  }

  public Credentials getCredentials() {
    return credentials;
  }

  public GasProvider getGasProvider() {
    return gasProvider;
  }

  public Permissions getPermissions() {
    return permissions;
  }

  public CircuitBreaker getCircuitBreaker() {
    return circuitBreaker;
  }
}
