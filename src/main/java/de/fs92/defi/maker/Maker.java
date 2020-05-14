package de.fs92.defi.maker;

import de.fs92.defi.contractneedsprovider.ContractNeedsProvider;
import de.fs92.defi.gasprovider.GasProvider;
import de.fs92.defi.util.Balances;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.utils.Numeric;

import java.lang.invoke.MethodHandles;
import java.math.BigInteger;

import static de.fs92.defi.util.NumberUtil.*;

/**
 * @deprecated This class is currently unused. It was created for SCD and has to be updated to MCD.
 */
@Deprecated
public class Maker {
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
  private static final String ADDRESS = "0x448a5065aeBB8E423F0896E6c5D525C040f59af3";
  private static final BigInteger liquidationRatio = getMachineReadable(1.5);
  private static final String CDP_ID =
      "0x0000000000000000000000000000000000000000000000000000000000000000"; // TODO: Get own cdp ID
  private static final BigInteger MAX_LIQUIDATION_PRICE = getMachineReadable(100.0);
  private final MakerContract makerContract;
  private BigInteger drawableDai;

  public Maker(ContractNeedsProvider contractNeedsProvider) {
    drawableDai = BigInteger.ZERO;
    Web3j web3j = contractNeedsProvider.getWeb3j();
    Credentials credentials = contractNeedsProvider.getCredentials();
    GasProvider gasProvider = contractNeedsProvider.getGasProvider();
    makerContract = MakerContract.load(ADDRESS, web3j, credentials, gasProvider);
  }

  private void updateCDPInformation(Balances balances) throws Exception {
    Tuple4<String, BigInteger, BigInteger, BigInteger> cupsResult =
        makerContract.cups(Numeric.hexStringToByteArray(CDP_ID)).send();

    BigInteger lockedEth = cupsResult.component2(); // ink is the locked ETH
    BigInteger outstandingDaiDebt = cupsResult.component3(); // art is outstanding dai debt
    BigInteger collateralLessFees = cupsResult.component4(); // ire is collateral - fees

    // (Stability Debt * Liquidation Ratio) / Collateral = Liquidation Price
    BigInteger currentLiquidationPrice =
        multiply(outstandingDaiDebt.multiply(liquidationRatio), lockedEth);

    // INFO: drawable dai does not include owned eth
    if (MAX_LIQUIDATION_PRICE.compareTo(currentLiquidationPrice) > 0) {
      drawableDai = multiply(MAX_LIQUIDATION_PRICE, lockedEth);
      drawableDai = divide(drawableDai, liquidationRatio);
      drawableDai = drawableDai.subtract(outstandingDaiDebt);
    } else {
      drawableDai = BigInteger.valueOf(0);
    }
    logger.trace("CDP INFORMATION");
    logger.trace("LOCKED ETH (INK) {}", getFullPrecision(lockedEth));
    logger.trace("OUTSTANDING DAI DEBT (ART) {}", getFullPrecision(outstandingDaiDebt));
    logger.trace("COLLATERAL-FEES (IRE) {}", getFullPrecision(collateralLessFees));
    logger.trace("LIQUIDATION PRICE {}", getHumanReadable(currentLiquidationPrice));
    logger.trace("DRAWABLE DAI {}", getFullPrecision(drawableDai));
    BigInteger potentialDai = drawableDai.add(balances.dai.getAccount().getBalance());
    logger.trace("POTENTIAL DAI {}", getFullPrecision(potentialDai));
  }
}
