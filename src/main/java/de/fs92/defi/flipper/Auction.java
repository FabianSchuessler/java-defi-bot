package de.fs92.defi.flipper;

import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.web3j.tuples.generated.Tuple8;

import java.lang.invoke.MethodHandles;
import java.math.BigInteger;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import static de.fs92.defi.util.NumberUtil.*;

public class Auction {
  public static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd HH_mm_ss");
  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
  public final BigInteger bidAmountInDai; // bid
  public final BigInteger collateralForSale; // lot
  public final String highestBidder; // guy
  public final BigInteger bidExpiry; // tic
  public final BigInteger maxAuctionDuration; // end
  public final String addressOfAuctionedVault; // usr
  public final String recipientOfAuctionIncome; // gal
  public final BigInteger totalDaiWanted; // tab
  public final BigInteger id; // id

  public Auction(
      BigInteger id,
      @NotNull
          Tuple8<BigInteger, BigInteger, String, BigInteger, BigInteger, String, String, BigInteger>
              auctionTuple) {
    this.id = id;
    this.bidAmountInDai = convertUint256toBigInteger(auctionTuple.component1());
    this.collateralForSale = auctionTuple.component2();
    this.highestBidder = auctionTuple.component3();
    this.bidExpiry = auctionTuple.component4();
    this.maxAuctionDuration = auctionTuple.component5();
    this.addressOfAuctionedVault = auctionTuple.component6();
    this.recipientOfAuctionIncome = auctionTuple.component7();
    this.totalDaiWanted = convertUint256toBigInteger(auctionTuple.component8());
    logger.trace("AUCTION CREATED {}", this);
  }

  public BigInteger getPotentialProfit(BigInteger minimumBidIncrease, BigInteger median) {
    BigInteger marketPrice = multiply(median, collateralForSale);
    BigInteger auctionPrice = multiply(bidAmountInDai, minimumBidIncrease);
    BigInteger potentialProfit = marketPrice.subtract(auctionPrice);
    logger.trace("POTENTIAL PROFIT {}{}", getCurrency(potentialProfit), " DAI");
    return potentialProfit;
  }

  public boolean isAffordable(BigInteger minimumBidIncrease, @NotNull BigInteger maxDaiToSell) {
    BigInteger auctionPrice = multiply(bidAmountInDai, minimumBidIncrease);
    boolean isAffordable = auctionPrice.compareTo(maxDaiToSell) < 0;
    logger.trace("AUCTION IS AFFORDABLE {}", isAffordable);
    return isAffordable;
  }

  public boolean isActive() {
    return !isEmpty() && !isCompleted();
  }

  //  once the bid hits the debt amount + 13 liquidation penalty threshold the auction switches to
  // the dent (reverse auction) phase
  //  in this phase everyone is bidding the debt amount + 13%, however they are underbidding each
  // other on the amount of collateral they are willing to accept

  public boolean isEmpty() {
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

  public boolean isCompleted() {
    String timeZone = TimeZone.getDefault().getID();
    String formattedBidExpiry =
        Instant.ofEpochSecond(maxAuctionDuration.longValue())
            .atZone(ZoneId.of(timeZone))
            .format(dtf);
    long unixTime = System.currentTimeMillis() / 1000L;
    logger.trace("END OF AUCTION {}", formattedBidExpiry);
    boolean isCompleted = maxAuctionDuration.longValue() < unixTime;
    logger.trace("AUCTION IS COMPLETED {}", isCompleted);
    return isCompleted;
  }

  @Override
  public String toString() {
    String timeZone = TimeZone.getDefault().getID();
    return "Auction{"
        + "bidAmountInDai="
        + getHumanReadable(bidAmountInDai)
        + ", collateralForSale="
        + getHumanReadable(collateralForSale)
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
        + getHumanReadable(totalDaiWanted)
        + '}';
  }
}
