package de.fs92.defi.flipper;

import de.fs92.defi.contractneedsprovider.CircuitBreaker;
import de.fs92.defi.contractneedsprovider.ContractNeedsProvider;
import de.fs92.defi.contractneedsprovider.Permissions;
import de.fs92.defi.gasprovider.ArrayListUtil;
import de.fs92.defi.gasprovider.GasProvider;
import de.fs92.defi.medianizer.MedianException;
import de.fs92.defi.medianizer.Medianizer;
import de.fs92.defi.util.Balances;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tuples.generated.Tuple8;

import java.lang.invoke.MethodHandles;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Flipper {
  public static final String ADDRESS = "0xd8a04F5412223F513DC55F839574430f5EC15531";
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
  private static final String EXCEPTION = "Exception";
  private final FlipperContract flipperContract;
  private final GasProvider gasProvider;
  private final Permissions permissions;
  private final Credentials credentials;
  private final CircuitBreaker circuitBreaker;
  private final BigInteger minimumBidIncrease; // beg
  private final BigInteger bidDuration; // ttl
  private final BigInteger auctionLength; // tau
  private ArrayList<Auction> activeAuctionList;
  private BigInteger pastTotalAuctionCount;

  public Flipper(@NotNull ContractNeedsProvider contractNeedsProvider) {
    Web3j web3j = contractNeedsProvider.getWeb3j();
    credentials = contractNeedsProvider.getCredentials();
    gasProvider = contractNeedsProvider.getGasProvider();
    permissions = contractNeedsProvider.getPermissions();
    circuitBreaker = contractNeedsProvider.getCircuitBreaker();
    flipperContract = FlipperContract.load(ADDRESS, web3j, credentials, gasProvider);
    pastTotalAuctionCount = BigInteger.ZERO;
    minimumBidIncrease = getMinimumBidIncrease();
    bidDuration = getBidDuration();
    auctionLength = getAuctionLength();
    activeAuctionList = new ArrayList<>();
  }

  public List<Auction> checkIfThereAreProfitableFlipAuctions(Balances balances) {
    BigInteger totalAuctionCount = getTotalAuctionCount();
    if (totalAuctionCount.compareTo(BigInteger.ZERO) == 0) return new ArrayList<>();
    ArrayList<Auction> auctionList = getActiveAffordableAuctionList(totalAuctionCount, balances);
    BigInteger median;
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
      currentAuctionLength = flipperContract.tau().send();
      logger.trace("UPDATED AUCTION LENGTH {}", currentAuctionLength);
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
    }
    return currentAuctionLength;
  }

  BigInteger getBidDuration() {
    BigInteger currentBidDuration = BigInteger.valueOf(21600L);
    try {
      currentBidDuration = flipperContract.ttl().send();
      logger.trace("UPDATED BID DURATION {}", currentBidDuration);
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
    }
    return currentBidDuration;
  }

  BigInteger getMinimumBidIncrease() {
    BigInteger currentMinimumBidIncrease = BigInteger.valueOf(1030000000000000000L);
    try {
      currentMinimumBidIncrease = flipperContract.beg().send();
      logger.trace("UPDATED MINIMUM BID INCREASE {}", currentMinimumBidIncrease);
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
    }
    return currentMinimumBidIncrease;
  }

  ArrayList<Auction> getActiveAffordableAuctionList(
      BigInteger totalAuctionCount, Balances balances) {

    logger.trace("PAST TOTAL AUCTION COUNT {}", pastTotalAuctionCount);

    updateAlreadyFoundAuctions(balances);

    boolean auctionIsCompleted = false;
    if (pastTotalAuctionCount.compareTo(BigInteger.ZERO) == 0) {
      while (!auctionIsCompleted && pastTotalAuctionCount.compareTo(totalAuctionCount) != 0) {
        Auction auction = getAuction(totalAuctionCount);
        if (auction != null && auction.isCompleted()) auctionIsCompleted = true;
        if (auction != null
            && auction.isActive()
            && auction.isAffordable(minimumBidIncrease, balances.getMaxDaiToSell()))
          activeAuctionList.add(auction);
        totalAuctionCount = totalAuctionCount.subtract(BigInteger.ONE);
      }
      logger.trace("ACTIVE AUCTION LIST SIZE: {}", activeAuctionList.size());
      if (!activeAuctionList.isEmpty())
        logger.trace("ACTIVE AUCTION LIST: {}", ArrayListUtil.toString(activeAuctionList));
      pastTotalAuctionCount = totalAuctionCount;
    }
    return activeAuctionList;
  }

  private void updateAlreadyFoundAuctions(Balances balances) {
    ArrayList<Auction> newActiveAuctionList = new ArrayList<>();
    for (Auction value : activeAuctionList) {
      Auction auction = getAuction(value.id);
      if (auction != null
          && auction.isActive()
          && auction.isAffordable(minimumBidIncrease, balances.getMaxDaiToSell()))
        newActiveAuctionList.add(auction);
    }
    this.activeAuctionList = newActiveAuctionList;
  }

  Auction getAuction(BigInteger auctionId) {
    Tuple8<BigInteger, BigInteger, String, BigInteger, BigInteger, String, String, BigInteger>
        auctionTuple = null;
    try {
      auctionTuple = flipperContract.bids(auctionId).send();
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
    }
    if (auctionTuple == null) return null;
    return new Auction(auctionId, auctionTuple);
  }

  BigInteger getTotalAuctionCount() {
    BigInteger totalAuctionCount = BigInteger.ZERO;
    try {
      totalAuctionCount = flipperContract.kicks().send();
    } catch (Exception e) {
      logger.error(EXCEPTION, e);
    }
    return totalAuctionCount;
  }
}
