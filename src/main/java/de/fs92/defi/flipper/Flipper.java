package de.fs92.defi.flipper;

import de.fs92.defi.contractneedsprovider.CircuitBreaker;
import de.fs92.defi.contractneedsprovider.ContractNeedsProvider;
import de.fs92.defi.contractneedsprovider.Permissions;
import de.fs92.defi.gasprovider.ArrayListUtil;
import de.fs92.defi.gasprovider.GasProvider;
import de.fs92.defi.medianizer.MedianException;
import de.fs92.defi.medianizer.Medianizer;
import de.fs92.defi.util.Balances;
import de.fs92.defi.util.ContractUser;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tuples.generated.Tuple8;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

public class Flipper extends ContractUser {
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
  public static final String ADDRESS = "0xd8a04F5412223F513DC55F839574430f5EC15531";
  private static final String EXCEPTION = "Exception";
  private final FlipperContract contract;
  private final GasProvider gasProvider;
  private final Permissions permissions;
  private final Credentials credentials;
  private final CircuitBreaker circuitBreaker;
  private BigInteger pastTotalAuctionCount;
  private BigInteger minimumBidIncrease; // beg
  private BigInteger bidDuration; // ttl
  private BigInteger auctionLength; // tau

  private final ArrayList<Auction> activeAuctionList;

  public Flipper(@NotNull ContractNeedsProvider contractNeedsProvider) {
    Web3j web3j = contractNeedsProvider.getWeb3j();
    credentials = contractNeedsProvider.getCredentials();
    gasProvider = contractNeedsProvider.getGasProvider();
    permissions = contractNeedsProvider.getPermissions();
    circuitBreaker = contractNeedsProvider.getCircuitBreaker();
    contract = FlipperContract.load(ADDRESS, web3j, credentials, gasProvider);
    pastTotalAuctionCount = BigInteger.ZERO;
    minimumBidIncrease = getMinimumBidIncrease();
    bidDuration = getBidDuration();
    auctionLength = getAuctionLength();
    activeAuctionList = new ArrayList<>();
  }

  public ArrayList<Auction> checkIfThereAreProfitableFlipAuctions(Balances balances) {
    BigInteger totalAuctionCount = getTotalAuctionCount();
    if (totalAuctionCount.compareTo(BigInteger.ZERO) == 0) return new ArrayList<>();
    ArrayList<Auction> auctionList = getActiveAffordableAuctionList(totalAuctionCount, balances);
    BigDecimal median;
    try {
      median = Medianizer.getPrice();
    } catch (MedianException e) {
      logger.error(EXCEPTION, e);
      return auctionList;
    }
    for (Auction auction : auctionList) {
      auction.getPotentialProfit(minimumBidIncrease, median);
    }
    return auctionList;
  }

  BigInteger getAuctionLength() {
    BigInteger currentAuctionLength = BigInteger.valueOf(21600L);
    try {
      currentAuctionLength = contract.tau().send();
      logger.trace("UPDATED AUCTION LENGTH {}", currentAuctionLength);
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
    }
    return currentAuctionLength;
  }

  BigInteger getBidDuration() {
    BigInteger currentBidDuration = BigInteger.valueOf(21600L);
    try {
      currentBidDuration = contract.ttl().send();
      logger.trace("UPDATED BID DURATION {}", currentBidDuration);
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
    }
    return currentBidDuration;
  }

  BigInteger getMinimumBidIncrease() {
    BigInteger currentMinimumBidIncrease = BigInteger.valueOf(1030000000000000000L);
    try {
      currentMinimumBidIncrease = contract.beg().send();
      logger.trace("UPDATED MINIMUM BID INCREASE {}", currentMinimumBidIncrease);
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
    }
    return currentMinimumBidIncrease;
  }

  ArrayList<Auction> getActiveAffordableAuctionList(
      BigInteger totalAuctionCount, Balances balances) {
    logger.trace("PAST TOTAL AUCTION COUNT {}", pastTotalAuctionCount);

    boolean auctionIsCompleted = false;
    if (pastTotalAuctionCount.compareTo(BigInteger.ZERO) == 0) {
      while (!auctionIsCompleted) {
        Auction auction = getAuction(totalAuctionCount);
        if (auction != null && auction.isCompleted()) auctionIsCompleted = true;
        if (auction != null
            && auction.isActive()
            && auction.isAffordable(minimumBidIncrease, balances)) activeAuctionList.add(auction);
        totalAuctionCount = totalAuctionCount.subtract(BigInteger.ONE);
      }
      logger.trace("ACTIVE AUCTION LIST {}", ArrayListUtil.toString(activeAuctionList));
      pastTotalAuctionCount = totalAuctionCount;
      return activeAuctionList;
    }

    for (BigInteger auctionCount = pastTotalAuctionCount;
        auctionCount.compareTo(totalAuctionCount) < 0;
        auctionCount = auctionCount.add(BigInteger.ONE)) {
      Auction auction = getAuction(totalAuctionCount);
      if (auction != null && !auction.isEmpty()) activeAuctionList.add(auction);
    }
    logger.trace("ACTIVE AUCTION LIST {}", ArrayListUtil.toString(activeAuctionList));
    pastTotalAuctionCount = totalAuctionCount;
    return activeAuctionList;
  }

  Auction getAuction(BigInteger totalAuctionCount) {
    Tuple8<BigInteger, BigInteger, String, BigInteger, BigInteger, String, String, BigInteger>
        auctionTuple = null;
    try {
      auctionTuple = contract.bids(totalAuctionCount).send();
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
    }
    if (auctionTuple == null) return null;
    return new Auction(auctionTuple);
  }

  BigInteger getTotalAuctionCount() {
    BigInteger totalAuctionCount = BigInteger.ZERO;
    try {
      totalAuctionCount = contract.kicks().send();
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
    }
    return totalAuctionCount;
  }
}
