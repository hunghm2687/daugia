//package com.example.auction.server.service;
//
///**
// * BidService - Business logic cho bids
// */
//
//import com.example.auction.shared.dto.BidDTO;
//
//public class BidService {
//  public boolean validateBid(BidDTO bidDTO, Double currentBid, String sellerUsername) {
//    // Validation logic
//    if (bidDTO.amount() <= currentBid) {
//      return false; // Amount quá thấp
//    }
//
//    if (bidDTO.bidderUsername().equals(sellerUsername)) {
//      return false; // Seller không thể bid
//    }
//
//    // TODO: Kiểm tra user có đủ tiền
//
//    return true;
//  }
//
//}
package com.example.auction.server.service;

import com.example.auction.shared.dto.BidDTO;
import com.example.auction.shared.util.LoggerUtil;

/**
 * BidService - Validate and process bid operations
 */
public class BidService {

  /**
   * Validate bid
   */
  public boolean validateBid(BidDTO bidDTO, Double currentBid, String sellerUsername) {
    try {
      // Check amount
      if (bidDTO.amount() == null || bidDTO.amount() <= 0) {
        LoggerUtil.warning("Invalid bid amount: " + bidDTO.amount());
        return false;
      }

      // Check amount is higher than current
      if (bidDTO.amount() <= currentBid) {
        LoggerUtil.warning("Bid too low: " + bidDTO.amount() + " <= " + currentBid);
        return false;
      }

      // Check bidder is not seller
      if (bidDTO.bidderUsername().equals(sellerUsername)) {
        LoggerUtil.warning("Seller cannot bid on own auction");
        return false;
      }

      return true;

    } catch (Exception e) {
      LoggerUtil.error("Error validating bid", e);
    }
    return false;
  }

  /**
   * Calculate minimum next bid
   */
  public Double getMinimumNextBid(Double currentBid) {
    // Increase by 10% or minimum 1000
    double increase = Math.max(currentBid * 0.1, 1000);
    return currentBid + increase;
  }

  /**
   * Get increment percentage based on current bid
   */
  public double getIncrementPercentage(Double currentBid) {
    if (currentBid < 100_000) {
      return 0.05; // 5%
    } else if (currentBid < 1_000_000) {
      return 0.03; // 3%
    } else {
      return 0.01; // 1%
    }
  }
}