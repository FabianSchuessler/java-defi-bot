package de.fs92.defi.contractutil;

import de.fs92.defi.contractneedsprovider.ContractNeedsProvider;
import de.fs92.defi.contractuserutil.AddressMethod;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.math.BigInteger;

import static de.fs92.defi.util.NumberUtil.*;

public class Approval {
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
  private static final String EXCEPTION = "Exception";
  private final ApprovalMethod contract;
  private final ContractNeedsProvider contractNeedsProvider;

  public Approval(ApprovalMethod contract, ContractNeedsProvider contractNeedsProvider) {
    this.contract = contract;
    this.contractNeedsProvider = contractNeedsProvider;
  }

  private void approve(String address, String name) {
    if (contractNeedsProvider.getPermissions().check("DAI UNLOCK " + name)) {
      try {
        contractNeedsProvider.getGasProvider().updateSlowGasPrice();
        contract.approve(address, UINT_MAX).send();
        logger.debug("{} UNLOCK DAI", name);
      } catch (Exception e) {
        logger.error(EXCEPTION, e);
        contractNeedsProvider.getCircuitBreaker().add(System.currentTimeMillis());
      }
    }
  }

  public void check(AddressMethod toAllowContract) {
    try {
      String address = toAllowContract.getAddress();
      BigInteger allowance =
          contract.allowance(contractNeedsProvider.getCredentials().getAddress(), address).send();
      if (allowance.compareTo(MINIMUM_APPROVAL_ALLOWANCE) < 0) {
        logger.warn("DAI ALLOWANCE IS TOO LOW {}", getFullPrecision(allowance));
        approve(address, toAllowContract.getClass().getName());
      }
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
    }
  }
}
