package de.fs92.defi.contractutil;

import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

public interface ApprovalMethod {
  RemoteFunctionCall<TransactionReceipt> approve(String guy, BigInteger wad);

  RemoteFunctionCall<BigInteger> allowance(String param0, String param1);
}
