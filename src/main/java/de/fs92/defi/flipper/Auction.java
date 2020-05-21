package de.fs92.defi.flipper;

import de.fs92.defi.numberutil.Rad45;
import de.fs92.defi.numberutil.Wad18;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.tuples.generated.Tuple8;

import java.lang.invoke.MethodHandles;
import java.math.BigInteger;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class Auction {
  public static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd HH_mm_ss");
  private static final org.slf4j.Logger logger =
          LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
  public final Rad45 bidAmountInDai; // bid
  public final Wad18 collateralForSale; // lot
  public final String highestBidder; // guy
  public final Wad18 bidExpiry; // tic
  public final Wad18 maxAuctionDuration; // end
  public final String addressOfAuctionedVault; // usr
  public final String recipientOfAuctionIncome; // gal
  public final Rad45 totalDaiWanted; // tab
  public final BigInteger id; // id

  Auction(
          BigInteger id,
          @NotNull
                  Tuple8<BigInteger, BigInteger, String, BigInteger, BigInteger, String, String, BigInteger>
                  auctionTuple) {
    this.id = id;
    this.bidAmountInDai = new Rad45(auctionTuple.component1());
    this.collateralForSale = new Wad18(auctionTuple.component2());
    this.highestBidder = auctionTuple.component3();
    this.bidExpiry = new Wad18(auctionTuple.component4());
    this.maxAuctionDuration = new Wad18(auctionTuple.component5());
    this.addressOfAuctionedVault = auctionTuple.component6();
    this.recipientOfAuctionIncome = auctionTuple.component7();
    this.totalDaiWanted = new Rad45(auctionTuple.component8());
    logger.trace("AUCTION CREATED {}", this);
  }

  boolean isDent(Wad18 minimumBidIncrease) {
    System.out.println(totalDaiWanted);
    System.out.println(bidAmountInDai.multiply(minimumBidIncrease));
    return totalDaiWanted.compareTo(bidAmountInDai.multiply(minimumBidIncrease)) <= 0;
  }

  boolean isInDefinedBiddingPhase(BigInteger biddingPeriod) {
    long currentUnixTime = System.currentTimeMillis() / 1000L;
    return !isCompleted()
            && (currentUnixTime + biddingPeriod.longValue())
            >= bidExpiry.min(maxAuctionDuration).longValue();
  }

  boolean amIHighestBidder(@NotNull Credentials credentials) {
    return highestBidder.equalsIgnoreCase(credentials.getAddress());
  }

  Wad18 getPotentialProfit(Wad18 minimumBidIncrease, @NotNull Wad18 median) {
    Wad18 marketPrice = median.multiply(collateralForSale); // todo: test this properly
    Wad18 auctionPrice = bidAmountInDai.multiply(minimumBidIncrease);
    Wad18 potentialProfit = marketPrice.subtract(auctionPrice);
    logger.trace("AUCTION {} HAS POTENTIAL PROFIT {} DAI", id, potentialProfit);
    return potentialProfit;
  }

  boolean isAffordable(Wad18 minimumBidIncrease, @NotNull Wad18 maxDaiToSell) {
    Wad18 auctionPrice = bidAmountInDai.multiply(minimumBidIncrease);
    boolean isAffordable = auctionPrice.compareTo(maxDaiToSell) < 0;
    logger.trace("AUCTION IS AFFORDABLE {}", isAffordable);
    return isAffordable;
  }

  boolean isActive() {
    return !isEmpty() && !isCompleted();
  }

  boolean isEmpty() {
    String burnAddress = "0x0000000000000000000000000000000000000000";
    boolean isEmpty =
        bidAmountInDai.compareTo(BigInteger.ZERO) == 0
            && collateralForSale.compareTo(BigInteger.ZERO) == 0
            && highestBidder.equalsIgnoreCase(burnAddress)
            && bidExpiry.compareTo(BigInteger.ZERO) == 0
            && maxAuctionDuration.compareTo(BigInteger.ZERO) == 0
            && addressOfAuctionedVault.equalsIgnoreCase(burnAddress)
            && recipientOfAuctionIncome.equalsIgnoreCase(burnAddress)
            && totalDaiWanted.compareTo(BigInteger.ZERO) == 0;
    logger.trace("AUCTION IS EMPTY {}", isEmpty);
    return isEmpty;
  }

  boolean isCompleted() {
    String timeZone = TimeZone.getDefault().getID();
    String formattedBidExpiry =
        Instant.ofEpochSecond(maxAuctionDuration.longValue())
            .atZone(ZoneId.of(timeZone))
            .format(dtf);
    long currentUnixTime = System.currentTimeMillis() / 1000L;
    logger.trace("END OF AUCTION {}", formattedBidExpiry);
    boolean isCompleted = maxAuctionDuration.longValue() < currentUnixTime;
    logger.trace("AUCTION IS COMPLETED {}", isCompleted);
    return isCompleted;
  }

  @Override
  public String toString() {
    String timeZone = TimeZone.getDefault().getID();
    return "Auction{"
            + "id="
            + id
            + ", bidAmountInDai="
            + bidAmountInDai
            + ", collateralForSale="
            + collateralForSale
            + ", highestBidder='"
            + highestBidder
            + ", bidExpiry="
            + Instant.ofEpochSecond(bidExpiry.longValue()).atZone(ZoneId.of(timeZone)).format(dtf)
            + ", maxAuctionDuration="
            + Instant.ofEpochSecond(maxAuctionDuration.longValue())
            .atZone(ZoneId.of(timeZone))
            .format(dtf)
            + ", addressOfAuctionedVault='"
            + addressOfAuctionedVault
            + ", recipientOfAuctionIncome='"
            + recipientOfAuctionIncome
            + ", totalDaiWanted="
            + totalDaiWanted
            + '}';
  }
}
