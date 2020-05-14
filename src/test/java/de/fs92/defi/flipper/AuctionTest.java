package de.fs92.defi.flipper;

import org.junit.jupiter.api.Test;
import org.web3j.tuples.generated.Tuple8;

import java.math.BigInteger;

import static de.fs92.defi.util.NumberUtil.getMachineReadable;
import static org.junit.jupiter.api.Assertions.*;

class AuctionTest {

  @Test
  public void isEmpty_emptyParameters_True() {
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
  public void isEmpty_notEmptyParameters_False() {
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
  public void isCompleted_completedAuction_True() {
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
  public void isCompleted_runningAuction_False() {
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
  public void getPotentialProfit_someParameters_correctResult() {
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
    BigInteger minimumBidIncrease = getMachineReadable(1.03);
    BigInteger median = getMachineReadable(250.0);
    BigInteger actualValue = auction.getPotentialProfit(minimumBidIncrease, median);
    assertEquals(0, getMachineReadable(440.0).compareTo(actualValue));
  }

  @Test
  public void isActive_NotCompletedNotEmpty_True() {
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
  public void isActive_Empty_False() {
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
  public void isActive_Completed_False() {
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
}
