package de.fs92.defi.flipper;

import de.fs92.defi.numberutil.Wad18;
import org.junit.jupiter.api.Test;
import org.web3j.tuples.generated.Tuple8;

import java.math.BigInteger;

import static de.fs92.defi.numberutil.NumberUtil.getMachineReadable;
import static org.junit.jupiter.api.Assertions.*;

class AuctionTest {

  @Test
  void isEmpty_emptyParameters_True() {
    Tuple8<BigInteger, BigInteger, String, BigInteger, BigInteger, String, String, BigInteger>
        auctionTuple =
            new Tuple8<>(
                BigInteger.ZERO,
                BigInteger.ZERO,
                "0x0000000000000000000000000000000000000000",
                BigInteger.ZERO,
                BigInteger.ZERO,
                "0x0000000000000000000000000000000000000000",
                "0x0000000000000000000000000000000000000000",
                BigInteger.ZERO);
    Auction auction = new Auction(BigInteger.ONE, auctionTuple);
    assertTrue(auction.isEmpty());
  }

  @Test
  void isEmpty_notEmptyParameters_False() {
    Tuple8<BigInteger, BigInteger, String, BigInteger, BigInteger, String, String, BigInteger>
        auctionTuple =
            new Tuple8<>(
                new BigInteger("37299123089429162514476831876850683361693243730"),
                new BigInteger("175927491330994700"),
                "0x04bB161C4e7583CDAaDEe93A8b8E6125FD661E57",
                new BigInteger("1588287896"),
                new BigInteger("1588266341"),
                "0x42A142cc082255CaEE58E3f30dc6d4Fc3056b6A7",
                "0xA950524441892A31ebddF91d3cEEFa04Bf454466",
                new BigInteger("37299123089429162514476831876850683361693243730"));
    Auction auction = new Auction(BigInteger.ONE, auctionTuple);
    assertFalse(auction.isEmpty());
  }

  @Test
  void isCompleted_completedAuction_True() {
    Tuple8<BigInteger, BigInteger, String, BigInteger, BigInteger, String, String, BigInteger>
        auctionTuple =
            new Tuple8<>(
                new BigInteger("37299123089429162514476831876850683361693243730"),
                new BigInteger("175927491330994700"),
                "0x04bB161C4e7583CDAaDEe93A8b8E6125FD661E57",
                new BigInteger("1588287896"),
                new BigInteger("1588266341"),
                "0x42A142cc082255CaEE58E3f30dc6d4Fc3056b6A7",
                "0xA950524441892A31ebddF91d3cEEFa04Bf454466",
                new BigInteger("37299123089429162514476831876850683361693243730"));
    Auction auction = new Auction(BigInteger.ONE, auctionTuple);
    assertTrue(auction.isCompleted());
  }

  @Test
  void isCompleted_runningAuction_False() {
    long currentUnixTimePlusFiveMinutes = System.currentTimeMillis() / 1000L + 300L;
    Tuple8<BigInteger, BigInteger, String, BigInteger, BigInteger, String, String, BigInteger>
        auctionTuple =
            new Tuple8<>(
                new BigInteger("37299123089429162514476831876850683361693243730"),
                new BigInteger("175927491330994700"),
                "0x04bB161C4e7583CDAaDEe93A8b8E6125FD661E57",
                new BigInteger("1588287896"),
                new BigInteger(String.valueOf(currentUnixTimePlusFiveMinutes)),
                "0x42A142cc082255CaEE58E3f30dc6d4Fc3056b6A7",
                "0xA950524441892A31ebddF91d3cEEFa04Bf454466",
                new BigInteger("37299123089429162514476831876850683361693243730"));
    Auction auction = new Auction(BigInteger.ONE, auctionTuple);
    assertFalse(auction.isCompleted());
  }

  @Test
  void getPotentialProfit_someParameters_correctResult() {
    Tuple8<BigInteger, BigInteger, String, BigInteger, BigInteger, String, String, BigInteger>
            auctionTuple =
            new Tuple8<>(
                    new BigInteger("2000000000000000000000000000000000000000000000000"),
                    new BigInteger("10000000000000000000"),
                    "0x04bB161C4e7583CDAaDEe93A8b8E6125FD661E57",
                    new BigInteger("1588287896"),
                    new BigInteger("1588266341"),
                    "0x42A142cc082255CaEE58E3f30dc6d4Fc3056b6A7",
                    "0xA950524441892A31ebddF91d3cEEFa04Bf454466",
                    new BigInteger("37299123089429162514476831876850683361693243730"));
    Auction auction = new Auction(BigInteger.ONE, auctionTuple);
    Wad18 minimumBidIncrease = new Wad18(getMachineReadable(1.03));
    Wad18 median = new Wad18(getMachineReadable(250.0));
    Wad18 actualValue = auction.getPotentialProfit(minimumBidIncrease, median);
    assertEquals(0, getMachineReadable(440.0).compareTo(actualValue.toBigInteger()));
  }

  @Test
  void isActive_NotCompletedNotEmpty_True() {
    long currentUnixTimePlusFiveMinutes = System.currentTimeMillis() / 1000L + 300L;
    Tuple8<BigInteger, BigInteger, String, BigInteger, BigInteger, String, String, BigInteger>
        auctionTuple =
            new Tuple8<>(
                new BigInteger("37299123089429162514476831876850683361693243730"),
                new BigInteger("175927491330994700"),
                "0x04bB161C4e7583CDAaDEe93A8b8E6125FD661E57",
                new BigInteger("1588287896"),
                new BigInteger(String.valueOf(currentUnixTimePlusFiveMinutes)),
                "0x42A142cc082255CaEE58E3f30dc6d4Fc3056b6A7",
                "0xA950524441892A31ebddF91d3cEEFa04Bf454466",
                new BigInteger("37299123089429162514476831876850683361693243730"));
    Auction auction = new Auction(BigInteger.ONE, auctionTuple);
    assertTrue(auction.isActive());
  }

  @Test
  void isActive_Empty_False() {
    Tuple8<BigInteger, BigInteger, String, BigInteger, BigInteger, String, String, BigInteger>
        auctionTuple =
            new Tuple8<>(
                BigInteger.ZERO,
                BigInteger.ZERO,
                "0x0000000000000000000000000000000000000000",
                BigInteger.ZERO,
                BigInteger.ZERO,
                "0x0000000000000000000000000000000000000000",
                "0x0000000000000000000000000000000000000000",
                BigInteger.ZERO);
    Auction auction = new Auction(BigInteger.ONE, auctionTuple);
    assertFalse(auction.isActive());
  }

  @Test
  void isActive_Completed_False() {
    Tuple8<BigInteger, BigInteger, String, BigInteger, BigInteger, String, String, BigInteger>
        auctionTuple =
            new Tuple8<>(
                new BigInteger("37299123089429162514476831876850683361693243730"),
                new BigInteger("175927491330994700"),
                "0x04bB161C4e7583CDAaDEe93A8b8E6125FD661E57",
                new BigInteger("1588287896"),
                new BigInteger("1588266341"),
                "0x42A142cc082255CaEE58E3f30dc6d4Fc3056b6A7",
                "0xA950524441892A31ebddF91d3cEEFa04Bf454466",
                new BigInteger("37299123089429162514476831876850683361693243730"));
    Auction auction = new Auction(BigInteger.ONE, auctionTuple);
    assertFalse(auction.isActive());
  }

  @Test
  void isDent_bidAmountInDai_Equal_totalDaiWanted_true() {
    Tuple8<BigInteger, BigInteger, String, BigInteger, BigInteger, String, String, BigInteger>
        auctionTuple =
            new Tuple8<>(
                new BigInteger("37299123089429162514476831876850683361693243730"),
                new BigInteger("175927491330994700"),
                "0x04bB161C4e7583CDAaDEe93A8b8E6125FD661E57",
                new BigInteger("1588287896"),
                new BigInteger("1588266341"),
                "0x42A142cc082255CaEE58E3f30dc6d4Fc3056b6A7",
                "0xA950524441892A31ebddF91d3cEEFa04Bf454466",
                new BigInteger("37299123089429162514476831876850683361693243730"));
    Auction auction = new Auction(BigInteger.ONE, auctionTuple);
    assertTrue(auction.isDent(new Wad18(1030000000000000000L)));
  }

  @Test
  void isDent_bidAmountInDai_Less_totalDaiWanted_false() {
    Tuple8<BigInteger, BigInteger, String, BigInteger, BigInteger, String, String, BigInteger>
        auctionTuple =
            new Tuple8<>(
                new BigInteger("36212740863523458751000000000000000000000000000"),
                new BigInteger("175927491330994700"),
                "0x04bB161C4e7583CDAaDEe93A8b8E6125FD661E57",
                new BigInteger("1588287896"),
                new BigInteger("1588266341"),
                "0x42A142cc082255CaEE58E3f30dc6d4Fc3056b6A7",
                "0xA950524441892A31ebddF91d3cEEFa04Bf454466",
                new BigInteger("37299123089429162514476831876850683361693243730"));
    Auction auction = new Auction(BigInteger.ONE, auctionTuple);
    assertFalse(auction.isDent(new Wad18(1030000000000000000L)));
  }

  @Test
  void isDent_bidAmountInDai_multiplied_minimumBidIncrease_equals_totalDaiWanted_false() {
    Tuple8<BigInteger, BigInteger, String, BigInteger, BigInteger, String, String, BigInteger>
        auctionTuple =
            new Tuple8<>(
                new BigInteger("36212740863523458752000000000000000000000000000"),
                new BigInteger("175927491330994700"),
                "0x04bB161C4e7583CDAaDEe93A8b8E6125FD661E57",
                new BigInteger("1588287896"),
                new BigInteger("1588266341"),
                "0x42A142cc082255CaEE58E3f30dc6d4Fc3056b6A7",
                "0xA950524441892A31ebddF91d3cEEFa04Bf454466",
                new BigInteger("37299123089429162514000000000000000000000000000"));
    // INFO: the actual value is 37.29912308942916251456 but due to 18 decimals the 56 gets lost.
    Auction auction = new Auction(BigInteger.ONE, auctionTuple);
    assertTrue(auction.isDent(new Wad18(1030000000000000000L)));
  }

  @Test
  void isInDefinedBiddingPhase_completed_false() {
    Tuple8<BigInteger, BigInteger, String, BigInteger, BigInteger, String, String, BigInteger>
            auctionTuple =
            new Tuple8<>(
                    new BigInteger("37299123089429162514476831876850683361693243730"),
                    new BigInteger("175927491330994700"),
                    "0x04bB161C4e7583CDAaDEe93A8b8E6125FD661E57",
                    new BigInteger("1588287896"),
                    new BigInteger("1588266341"),
                    "0x42A142cc082255CaEE58E3f30dc6d4Fc3056b6A7",
                    "0xA950524441892A31ebddF91d3cEEFa04Bf454466",
                    new BigInteger("37299123089429162514476831876850683361693243730"));
    Auction auction = new Auction(BigInteger.ONE, auctionTuple);
    assertFalse(auction.isInDefinedBiddingPhase(BigInteger.valueOf(300)));
  }

  @Test
  void isInDefinedBiddingPhase_beforeBiddingPhase_currentTimeLowerThanBothBidExpiryAndMaxAuctionLength_false() {
    long currentUnixTimePlusMoreThanBiddingPeriod = System.currentTimeMillis() / 1000L + 310L;
    Tuple8<BigInteger, BigInteger, String, BigInteger, BigInteger, String, String, BigInteger>
            auctionTuple =
            new Tuple8<>(
                    new BigInteger("37299123089429162514476831876850683361693243730"),
                    new BigInteger("175927491330994700"),
                    "0x04bB161C4e7583CDAaDEe93A8b8E6125FD661E57",
                    new BigInteger(String.valueOf(currentUnixTimePlusMoreThanBiddingPeriod)),
                    new BigInteger(String.valueOf(currentUnixTimePlusMoreThanBiddingPeriod)),
                    "0x42A142cc082255CaEE58E3f30dc6d4Fc3056b6A7",
                    "0xA950524441892A31ebddF91d3cEEFa04Bf454466",
                    new BigInteger("37299123089429162514476831876850683361693243730"));
    Auction auction = new Auction(BigInteger.ONE, auctionTuple);
    assertFalse(auction.isInDefinedBiddingPhase(BigInteger.valueOf(300)));
  }

  @Test
  void isInDefinedBiddingPhase_biddingPhase_BidExpiry_true() {
    long currentUnixTimePlusMoreThanBiddingPeriod = System.currentTimeMillis() / 1000L + 310L;
    long currentUnixTime = System.currentTimeMillis() / 1000L;
    Tuple8<BigInteger, BigInteger, String, BigInteger, BigInteger, String, String, BigInteger>
            auctionTuple =
            new Tuple8<>(
                    new BigInteger("37299123089429162514476831876850683361693243730"),
                    new BigInteger("175927491330994700"),
                    "0x04bB161C4e7583CDAaDEe93A8b8E6125FD661E57",
                    new BigInteger(String.valueOf(currentUnixTimePlusMoreThanBiddingPeriod)),
                    new BigInteger(String.valueOf(currentUnixTime)),
                    "0x42A142cc082255CaEE58E3f30dc6d4Fc3056b6A7",
                    "0xA950524441892A31ebddF91d3cEEFa04Bf454466",
                    new BigInteger("37299123089429162514476831876850683361693243730"));
    Auction auction = new Auction(BigInteger.ONE, auctionTuple);
    assertTrue(auction.isInDefinedBiddingPhase(BigInteger.valueOf(300)));
  }

  @Test
  void isInDefinedBiddingPhase_biddingPhase_MaxAuctionLength_true() {
    long currentUnixTimePlusMoreThanBiddingPeriod = System.currentTimeMillis() / 1000L + 310L;
    long currentUnixTime = System.currentTimeMillis() / 1000L;
    Tuple8<BigInteger, BigInteger, String, BigInteger, BigInteger, String, String, BigInteger>
            auctionTuple =
            new Tuple8<>(
                    new BigInteger("37299123089429162514476831876850683361693243730"),
                    new BigInteger("175927491330994700"),
                    "0x04bB161C4e7583CDAaDEe93A8b8E6125FD661E57",
                    new BigInteger(String.valueOf(currentUnixTime)),
                    new BigInteger(String.valueOf(currentUnixTimePlusMoreThanBiddingPeriod)),
                    "0x42A142cc082255CaEE58E3f30dc6d4Fc3056b6A7",
                    "0xA950524441892A31ebddF91d3cEEFa04Bf454466",
                    new BigInteger("37299123089429162514476831876850683361693243730"));
    Auction auction = new Auction(BigInteger.ONE, auctionTuple);
    assertTrue(auction.isInDefinedBiddingPhase(BigInteger.valueOf(300)));
  }
}
