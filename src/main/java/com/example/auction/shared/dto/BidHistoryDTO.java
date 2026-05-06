package com.example.auction.shared.dto;

import java.io.Serializable;
import java.time.Instant;

/**
 * BidHistoryDTO - Lịch sử đặt giá của user
 */
public record BidHistoryDTO(
  Long auctionId,
  String itemName,
  Double bidAmount,
  Double currentHighestBid,
  String status,  // SUCCESS, OUTBID, WON, LOST
  Instant bidTime
) implements Serializable {
  private static final long serialVersionUID = 1L;
}