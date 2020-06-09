package de.fs92.defi.util;

import de.fs92.defi.numberutil.Wad18;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static de.fs92.defi.numberutil.NumberUtil.getMachineReadable;
import static de.fs92.defi.util.TransactionUtil.getTransactionCosts;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionUtilTest {

  @Test
  void getTransactionCosts_realNumbers_true() {
    // 2 * 222.53 * 300,000 * 0.00000001 = 1.33518
    Wad18 gasPrice = new Wad18("10000000000"); // 10 GWEI
    Wad18 medianEthereumPrice = new Wad18(getMachineReadable(222.53));
    BigInteger gasLimit = BigInteger.valueOf(300000);
    Wad18 wad18 = getTransactionCosts(gasPrice, medianEthereumPrice, gasLimit, 2);
    System.out.println(wad18);
    assertTrue(wad18.compareTo(new Wad18(getMachineReadable(1.33518))) == 0);
  }
}
