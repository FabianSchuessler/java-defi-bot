package de.fs92.defi.maker;

import de.fs92.defi.contractneedsprovider.CircuitBreaker;
import de.fs92.defi.contractneedsprovider.ContractNeedsProvider;
import de.fs92.defi.gasprovider.GasProvider;
import de.fs92.defi.util.Balances;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.BigInteger;

import static de.fs92.defi.util.BigNumberUtil.*;

// TODO: this currently unused class has only been tested for SCD, update it to MCD
// TODO: somehow get own cdp id
public class Maker {
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
  private static final String ADDRESS = "0x448a5065aeBB8E423F0896E6c5D525C040f59af3";
  private static final BigDecimal liquidationRatio = makeDoubleMachineReadable(1.5);
  private static final String CDP_ID =
      "0x0000000000000000000000000000000000000000000000000000000000000000";
  private static final BigDecimal MAX_LIQUIDATION_PRICE = makeDoubleMachineReadable(100.0);
  private final MakerContract contract;
  private BigDecimal drawableDai;

  public Maker(ContractNeedsProvider contractNeedsProvider) {
    drawableDai = BigDecimal.ZERO;
    Web3j web3j = contractNeedsProvider.getWeb3j();
    Credentials credentials = contractNeedsProvider.getCredentials();
    GasProvider gasProvider = contractNeedsProvider.getGasProvider();
    contract = MakerContract.load(ADDRESS, web3j, credentials, gasProvider);
    isContractValid();
  }

  void isContractValid() {
    try {
      contract.isValid();
      logger.trace("MAKER CONTRACT IS VALID");
    } catch (IOException e) {
      CircuitBreaker.stopRunning();
      logger.error("IOException", e);
    }
  }

  private void updateCDPInformation(Balances balances) throws Exception {
    Tuple4<String, BigInteger, BigInteger, BigInteger> cupsResult =
        contract.cups(Numeric.hexStringToByteArray(CDP_ID)).send();

    BigDecimal lockedEth = new BigDecimal(cupsResult.component2()); // ink is the locked ETH
    BigDecimal outstandingDaiDebt =
        new BigDecimal(cupsResult.component3()); // art is outstanding dai debt
    BigDecimal collateralLessFees =
        new BigDecimal(cupsResult.component4()); // ire is collateral - fees

    // (Stability Debt * Liquidation Ratio) / Collateral = Liquidation Price
    BigDecimal currentLiquidationPrice =
        multiply(outstandingDaiDebt.multiply(liquidationRatio), lockedEth);

    // INFO: drawable dai does not include owned eth
    if (MAX_LIQUIDATION_PRICE.compareTo(currentLiquidationPrice) > 0) {
      drawableDai = multiply(MAX_LIQUIDATION_PRICE, lockedEth);
      drawableDai = divide(drawableDai, liquidationRatio);
      drawableDai = drawableDai.subtract(outstandingDaiDebt);
    } else {
      drawableDai = BigDecimal.valueOf(0);
    }
    logger.trace("CDP INFORMATION");
    logger.trace("LOCKED ETH (INK) {}", makeBigNumberHumanReadableFullPrecision(lockedEth));
    logger.trace(
        "OUTSTANDING DAI DEBT (ART) {}",
        makeBigNumberHumanReadableFullPrecision(outstandingDaiDebt));
    logger.trace(
        "COLLATERAL-FEES (IRE) {}", makeBigNumberHumanReadableFullPrecision(collateralLessFees));
    logger.trace("LIQUIDATION PRICE {}", makeBigNumberHumanReadable(currentLiquidationPrice));
    logger.trace("DRAWABLE DAI {}", makeBigNumberHumanReadableFullPrecision(drawableDai));
    BigDecimal potentialDai = drawableDai.add(balances.getDaiBalance());
    logger.trace("POTENTIAL DAI {}", makeBigNumberHumanReadableFullPrecision(potentialDai));
  }
}
