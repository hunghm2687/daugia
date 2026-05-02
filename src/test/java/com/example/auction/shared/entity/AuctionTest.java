package com.example.auction.shared.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.function.Supplier;
import static org.junit.jupiter.api.Assertions.*;
// Auction Tests với timeProvider
// Test deterministic - control time hoàn toàn
// Không phụ thuộc real time

public class AuctionTest {

    private static final Instant NOW = Instant.parse("2026-04-11T10:00:00Z");
    // START & END: Tính toán dựa trên NOW
    // START = NOW + 60 giây (phiên bắt đầu sau 1 phút)
    // END = NOW + 3600 giây (phiên kết thúc sau 1 giờ)
    private static final Instant START = NOW.plusSeconds(60);
    private static final Instant END = NOW.plusSeconds(3600);

    private Auction auction;
    private ElectronicsItem item;

    // timeProvider: Supplier injection cho Auction
    // Luôn trả về NOW cố định
    private Supplier<Instant> fixedTime;

    @BeforeEach
    void setUp() {
        item = new ElectronicsItem(
                "MacBook Pro",
                "15-inch 2023",
                1_000_000,
                "seller123",
                "Apple",
                24
        );
        // timeProvider = () -> NOW (luôn trả về NOW cố định)
        fixedTime = () -> NOW;  // nghĩa là: khi gọi fixedTime.get() luôn trả về NOW

        // Tạo auction VỚI timeProvider
        // Nếu không truyền timeProvider → test sẽ fail!
        auction = new Auction(item, "seller123", START, END, fixedTime);
    }

    // test create
    // Test: Tạo Auction với dữ liệu hợp lệ
    // Lý do test:
    // Verify constructor khởi tạo tất cả fields đúng
    // Verify initial values là cái mong đợi

    @Test
    void testCreateAuction() {
        assertEquals(item, auction.getItem());
        assertEquals("seller123", auction.getSellerUsername());
        assertEquals(AuctionStatus.PENDING, auction.getStatus());
        assertEquals(1_000_000, auction.getCurrentHighestBid());
        assertEquals(0, auction.getBidCount());
    }

    @Test
    void testCreateAuctionThrowsForNullItem() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Auction(null, "seller123", START, END);
        });
    }

    @Test
    void testCreateAuctionThrowsForEmptySeller() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Auction(item, "", START, END);
        });
    }

    @Test
    void testCreateAuctionThrowsForEndBeforeStart() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Auction(item, "seller123", END, START);
        });
    }
    // test time

    @Test
    void testIsOngoingReturnsFalseBeforeStart() {
        assertFalse(auction.isOngoing());
    }

    @Test
    void testIsOngoingReturnsTrueWhenBetweenStartAndEnd() {
        // NOW = START + 30s (giữa START và END)
        Supplier<Instant> between = () -> START.plusSeconds(30);
        Auction ongoingAuction = new Auction(item, "seller123", START, END, between);
        assertTrue(ongoingAuction.isOngoing());
    }

    @Test
    void testHasEndedReturnsFalseBeforeEnd() {
        assertFalse(auction.hasEnded());
    }

    @Test
    void testHasEndedReturnsTrueAtOrAfterEnd() {
        Supplier<Instant> afterEnd = () -> END.plusSeconds(1);
        Auction endedAuction = new Auction(item, "seller123", START, END, afterEnd);
        assertTrue(endedAuction.hasEnded());
    }

    @Test
    void testGetTimeRemainingSeconds() {
        // NOW = 10:00, END = 11:00 → 3600 giây
        long timeRemaining = auction.getTimeRemainingSeconds();
        assertEquals(3600, timeRemaining);
    }

    @Test
    void testGetTimeRemainingSecondsReturnsZeroWhenEnded() {
        Supplier<Instant> afterEnd = () -> END.plusSeconds(1);
        Auction endedAuction = new Auction(item, "seller123", START, END, afterEnd);
        assertEquals(0, endedAuction.getTimeRemainingSeconds());
    }
    // test status

    @Test
    void testStartAuction() {
        Supplier<Instant> atStart = () -> START;
        Auction startingAuction = new Auction(item, "seller123", START, END, atStart);

        startingAuction.startAuction();
        assertEquals(AuctionStatus.ACTIVE, startingAuction.getStatus());
    }

    @Test
    void testCloseAuction() {
        Supplier<Instant> afterEnd = () -> END.plusSeconds(1);
        Auction closingAuction = new Auction(item, "seller123", START, END, afterEnd);
        closingAuction.startAuction();

        Bid bid = new Bid(closingAuction.getId(), "bidder1", 1_200_000);
        closingAuction.addBid(bid);

        closingAuction.closeAuction();
        assertEquals(AuctionStatus.CLOSED, closingAuction.getStatus());
        assertEquals("bidder1", closingAuction.getWinnerUsername());
        assertEquals(1_200_000, closingAuction.getFinalPrice());
    }

    @Test
    void testCompleteAuction() {
        Supplier<Instant> afterEnd = () -> END.plusSeconds(1);
        Auction completingAuction = new Auction(item, "seller123", START, END, afterEnd);
        completingAuction.startAuction();
        completingAuction.closeAuction();

        completingAuction.completeAuction();
        assertEquals(AuctionStatus.COMPLETED, completingAuction.getStatus());
    }

    @Test
    void testCancelAuction() {
        auction.cancelAuction();
        assertEquals(AuctionStatus.CANCELLED, auction.getStatus());
    }

    // ============ BID MANAGEMENT TESTS ============

    @Test
    void testAddBidSuccessfully() {
        Supplier<Instant> between = () -> START.plusSeconds(1);
        Auction activeAuction = new Auction(item, "seller123", START, END, between);
        activeAuction.startAuction();

        Bid bid = new Bid(activeAuction.getId(), "bidder1", 1_200_000);
        assertTrue(activeAuction.addBid(bid));
        assertEquals(1_200_000, activeAuction.getCurrentHighestBid());
        assertEquals("bidder1", activeAuction.getCurrentHighestBidderUsername());
        assertEquals(1, activeAuction.getBidCount());
    }

    @Test
    void testRejectBidWhenNotActive() {
        Bid bid = new Bid(auction.getId(), "bidder1", 1_200_000);
        assertFalse(auction.addBid(bid));
    }

    @Test
    void testRejectBidWithLowerAmount() {
        Supplier<Instant> between = () -> START.plusSeconds(1);
        Auction activeAuction = new Auction(item, "seller123", START, END, between);
        activeAuction.startAuction();

        Bid bid1 = new Bid(activeAuction.getId(), "bidder1", 1_200_000);
        activeAuction.addBid(bid1);

        Bid bid2 = new Bid(activeAuction.getId(), "bidder2", 1_100_000);
        assertFalse(activeAuction.addBid(bid2));
    }

    @Test
    void testRejectBidFromSeller() {
        Supplier<Instant> between = () -> START.plusSeconds(1);
        Auction activeAuction = new Auction(item, "seller123", START, END, between);
        activeAuction.startAuction();

        Bid bid = new Bid(activeAuction.getId(), "seller123", 2_000_000);
        assertFalse(activeAuction.addBid(bid));
    }

    @Test
    void testMultipleBids() {
        Supplier<Instant> between = () -> START.plusSeconds(1);
        Auction activeAuction = new Auction(item, "seller123", START, END, between);
        activeAuction.startAuction();

        Bid bid1 = new Bid(activeAuction.getId(), "bidder1", 1_200_000);
        activeAuction.addBid(bid1);
        assertEquals(1_200_000, activeAuction.getCurrentHighestBid());

        Bid bid2 = new Bid(activeAuction.getId(), "bidder2", 1_500_000);
        activeAuction.addBid(bid2);
        assertEquals(1_500_000, activeAuction.getCurrentHighestBid());
        assertEquals("bidder2", activeAuction.getCurrentHighestBidderUsername());

        assertEquals(2, activeAuction.getBidCount());
    }

    // ============ OTHER TESTS ============

    @Test
    void testGetBidHistoryReturnsCopy() {
        Supplier<Instant> between = () -> START.plusSeconds(1);
        Auction activeAuction = new Auction(item, "seller123", START, END, between);
        activeAuction.startAuction();

        Bid bid = new Bid(activeAuction.getId(), "bidder1", 1_200_000);
        activeAuction.addBid(bid);

        java.util.List<Bid> copy = activeAuction.getBidHistory();
        copy.add(new Bid(activeAuction.getId(), "fake", 9_999_999));

        assertEquals(1, activeAuction.getBidCount());
    }

    @Test
    void testIsSellerOf() {
        assertTrue(auction.isSellerOf("seller123"));
        assertFalse(auction.isSellerOf("other_seller"));
    }

    @Test
    void testHasWinner() {
        assertFalse(auction.hasWinner());

        Supplier<Instant> afterEnd = () -> END.plusSeconds(1);
        Auction closedAuction = new Auction(item, "seller123", START, END, afterEnd);
        closedAuction.startAuction();

        Bid bid = new Bid(closedAuction.getId(), "bidder1", 1_200_000);
        closedAuction.addBid(bid);
        closedAuction.closeAuction();

        assertTrue(closedAuction.hasWinner());
    }
}