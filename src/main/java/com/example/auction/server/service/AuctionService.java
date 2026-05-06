//package com.example.auction.server.service;
//
//import com.example.auction.shared.entity.Auction;
//import com.example.auction.shared.entity.AuctionStatus;
//import com.example.auction.shared.entity.Item;
//import java.time.Instant;
//
//// lớp này để kiểm tra logic hệ thống, xác thực thời gian thực, chuyển đổi status, luật của hệ thống
//
//public class AuctionService {
//
//    // Tạo auction mới
//    public Auction createAuction(Item item, String sellerUsername, Instant startTime, Instant endTime) {
//        // Check 1: Real-time validation (không thể test nếu ở constructor)
//        Instant now = Instant.now();
//        if (startTime.isBefore(now)) {
//            throw new IllegalArgumentException("Start time phải trong tương lai");
//        }
//
//        // Check 2: Tạo auction ( constructor sẽ xác thực logic )
//        Auction auction = new Auction(item, sellerUsername, startTime, endTime);
//
//        // add more logic (save to database, etc)
//        // auctionRepository.save(auction)
//        // thêm sau
//        // TODO: auctionRepository.save(auction);
//
//        return auction;
//    }
//
//    // Bắt đầu phiên
//    public void startAuction(Auction auction) {
//        if (auction.getStatus() != AuctionStatus.PENDING) {
//            throw new IllegalStateException("Chỉ có thể start phiên khi đang là PENDING");
//        }
//        if (!auction.hasStarted()) {
//            throw new IllegalStateException("Thời gian bắt đầu chưa đến");
//        }
//        auction.startAuction();
//        // TODO: auctionRepository.save(auction);
//    }
//
//    // Đóng phiên
//    public void closeAuction(Auction auction) {
//        if (!auction.hasEnded()) {
//            throw new IllegalStateException("Thời gian kết thúc chưa đến");
//        }
//        auction.closeAuction();
//        // TODO: auctionRepository.save(auction);
//     }
//
//     // Hoàn tất phiên
//    public void completedAuction(Auction auction) {
//        if (auction.getStatus() != AuctionStatus.CLOSED) {
//            throw new IllegalStateException("Phiên phải CLOSED mới có thể complete");
//        }
//
//        auction.completeAuction();
//        // TODO: auctionRepository.save(auction);
//
//        // TODO: sau này thêm: chuyển tiền, update user ratings, etc
//    }
//
//    // Hủy phiên
//    public void cancelAuction(Auction auction) {
//        if (auction.getStatus() == AuctionStatus.COMPLETED) {
//            throw new IllegalStateException("Không thể hủy phiên đã COMPLETED");
//        }
//
//        auction.cancelAuction();
//        // TODO: auctionRepository.save(auction);
//    }
//}
package com.example.auction.server.service;

import com.example.auction.dao.AuctionDAO;
import com.example.auction.dao.BidDAO;
import com.example.auction.dao.ProductDAO;
import com.example.auction.shared.dto.AuctionDTO;
import com.example.auction.shared.dto.BidDTO;
import com.example.auction.shared.util.LoggerUtil;

import java.time.Instant;
import java.util.List;

/**
 * AuctionService - Business logic for auction operations
 * Responsibilities:
 * - Validate auction business rules
 * - Process bid operations
 * - Create and manage auctions
 * - Handle auction state transitions
 */
public class AuctionService {

    /**
     * Validate and process a bid
     */
    public boolean processBid(Long auctionId, String bidderUsername, Double amount) {
        try {
            // Get auction
            AuctionDTO auction = ProductDAO.getInstance().getAuctionById(auctionId);
            if (auction == null) {
                LoggerUtil.warning("Auction not found: " + auctionId);
                return false;
            }

            // Check if auction is active
            if (!"ACTIVE".equals(auction.status())) {
                LoggerUtil.warning("Auction not active: " + auctionId);
                return false;
            }

            // Check bid amount
            if (amount <= auction.currentHighestBid()) {
                LoggerUtil.warning("Bid amount too low: " + amount);
                return false;
            }

            // Check if bidder is seller
            if (bidderUsername.equals(auction.sellerUsername())) {
                LoggerUtil.warning("Seller cannot bid on own auction");
                return false;
            }

            // Process bid
            BidDTO bidDTO = new BidDTO(
              auctionId,
              bidderUsername,
              amount,
              Instant.now(),
              "SUCCESS"
            );

            boolean saved = BidDAO.getInstance().saveBid(bidDTO);
            if (saved) {
                // Update auction
                AuctionDAO.getInstance().updateHighestBid(auctionId, amount, bidderUsername);
                LoggerUtil.info("✅ Bid processed: " + bidderUsername + " bid " + amount);
                return true;
            }

        } catch (Exception e) {
            LoggerUtil.error("Error processing bid", e);
        }
        return false;
    }

    /**
     * Create new auction
     */
    public Long createAuction(String sellerUsername, String itemName, String itemDescription,
                              String itemImage, String category, Double startPrice,
                              Instant startTime, Instant endTime) {
        try {
            // Validate inputs
            if (startPrice <= 0) {
                LoggerUtil.warning("Invalid start price: " + startPrice);
                return null;
            }

            if (startTime.isAfter(endTime)) {
                LoggerUtil.warning("Invalid time range");
                return null;
            }

            if (startTime.isBefore(Instant.now())) {
                LoggerUtil.warning("Start time must be in future");
                return null;
            }

            // Create auction
            Long auctionId = AuctionDAO.getInstance().createAuction(
              sellerUsername, itemName, itemDescription, itemImage, category,
              startPrice, startTime, endTime
            );

            if (auctionId != null) {
                LoggerUtil.info("✅ Auction created: " + auctionId);
            }
            return auctionId;

        } catch (Exception e) {
            LoggerUtil.error("Error creating auction", e);
        }
        return null;
    }

    /**
     * Get auctions by status
     */
    public List<AuctionDTO> getAuctionsByStatus(String status) {
        try {
            return ProductDAO.getInstance().getAuctionsByStatus(status);
        } catch (Exception e) {
            LoggerUtil.error("Error getting auctions by status", e);
        }
        return List.of();
    }

    /**
     * Search auctions
     */
    public List<AuctionDTO> searchAuctions(String keyword) {
        try {
            return ProductDAO.getInstance().searchAuctions(keyword);
        } catch (Exception e) {
            LoggerUtil.error("Error searching auctions", e);
        }
        return List.of();
    }

    /**
     * Get all active auctions
     */
    public List<AuctionDTO> getAllActiveAuctions() {
        try {
            return ProductDAO.getInstance().getAllActiveAuctions();
        } catch (Exception e) {
            LoggerUtil.error("Error getting active auctions", e);
        }
        return List.of();
    }

    /**
     * Close auction when time ends
     */
    public void closeAuction(Long auctionId) {
        try {
            AuctionDTO auction = ProductDAO.getInstance().getAuctionById(auctionId);
            if (auction != null && "ACTIVE".equals(auction.status())) {
                AuctionDAO.getInstance().updateStatus(auctionId, "CLOSED");

                if (auction.currentHighestBidderUsername() != null) {
                    AuctionDAO.getInstance().updateWinner(
                      auctionId,
                      auction.currentHighestBidderUsername(),
                      auction.currentHighestBid()
                    );
                }

                LoggerUtil.info("✅ Auction closed: " + auctionId);
            }
        } catch (Exception e) {
            LoggerUtil.error("Error closing auction", e);
        }
    }

    /**
     * Get bid history for auction
     */
    public List<BidDTO> getBidHistory(Long auctionId) {
        try {
            return BidDAO.getInstance().getAllBidsForAuction(auctionId);
        } catch (Exception e) {
            LoggerUtil.error("Error getting bid history", e);
        }
        return List.of();
    }
}