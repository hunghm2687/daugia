package com.example.auction.shared.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

public class BidTest {

    private Bid bid;

    @BeforeEach
    void setUp() {
        bid = new Bid(1L, "bidder1", 1_200_000);
    }
    // test create
    @Test
    void testCreateBid() {
        assertEquals(1L, bid.getAuctionId());
        assertEquals("bidder1", bid.getBidderUsername());
        assertEquals(1_200_000, bid.getAmount());
        assertEquals(BidStatus.SUCCESS, bid.getStatus());
        // bidTime được set tự động = Instant.now()
        assertNotNull(bid.getBidTime());
    }

    @Test
    void testCreateBidWithCustomTime() {
        Instant customTime = Instant.parse("2026-04-11T08:30:00Z");
        // Constructor thứ 2 với Instant
        Bid bidWithTime = new Bid(1L, "bidder1", 1_200_000, customTime);
        assertEquals(customTime, bidWithTime.getBidTime());
    }

    @Test
    void testCreateBidThrowsExceptionForInvalidAmount() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Bid(1L, "bidder1", 0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Bid(1L, "bidder1", -100_000);
        });
    }

    @Test
    void testCreateBidThrowsExceptionForEmptyUsername() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Bid(1L, "", 1_200_000);
        });
    }

    // test status changes
    @Test
    void testMarkAsOutbid() {
        assertEquals(BidStatus.SUCCESS, bid.getStatus());
        bid.markOutbid();
        assertEquals(BidStatus.OUTBID, bid.getStatus());
    }

    @Test
    void testMarkAsOutbidThrowsExceptionIfNotSuccess() {
        bid.markAsFailed("Not enough money");
        assertThrows(IllegalStateException.class, () -> {
            bid.markOutbid();
        });
    }

    @Test
    void testMarkAsFailed() {
        bid.markAsFailed("Not enough money");
        assertEquals(BidStatus.FAILED, bid.getStatus());
    }

    @Test
    void testCancel() {
        bid.markcancel();
        assertEquals(BidStatus.CANCELLED, bid.getStatus());
    }
    // test truy vấn
    @Test
    void testIsActive() {
        assertTrue(bid.isActive());

        bid.markOutbid();
        assertFalse(bid.isActive());
    }

    @Test
    void testIsSuccessful() {
        assertTrue(bid.isSuccessful());

        bid.markAsFailed("Not enough money");
        assertFalse(bid.isSuccessful());
    }
    // test copy constructor
    @Test
    void testCopyConstructor() {
        bid.markOutbid();

        Bid copy = new Bid(bid);

        assertEquals(bid.getAuctionId(), copy.getAuctionId());
        assertEquals(bid.getBidderUsername(), copy.getBidderUsername());
        assertEquals(bid.getAmount(), copy.getAmount());
        assertEquals(bid.getStatus(), copy.getStatus());
    }

    @Test
    void testCopyConstructorIsIndependent() {
        Bid copy = new Bid(bid);

        copy.markOutbid();

        assertEquals(BidStatus.SUCCESS, bid.getStatus());
        assertEquals(BidStatus.OUTBID, copy.getStatus());
    }
    // test equals
    @Test
    void testEqualsWithSameBidderAndTime() {
        Bid bid2 = new Bid(1L, "bidder1", 1_200_000, bid.getBidTime());

        assertEquals(bid, bid2);
    }

    @Test
    void testNotEqualsWithDifferentBidder() {
        Bid bid2 = new Bid(1L, "bidder2", 1_200_000, bid.getBidTime());

        assertNotEquals(bid, bid2);
    }
}