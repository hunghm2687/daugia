package com.example.auction.shared.dto;

import java.io.Serializable;
import java.time.Instant;

/**
 * CreateAuctionDTO - Dữ liệu để tạo phiên đấu giá mới
 */
public record CreateAuctionDTO(
  String itemName,
  String itemDescription,
  String itemImage,
  String category,
  Double startPrice,
  Instant startTime,
  Instant endTime,
  String conditionType  // NEW, LIKE_NEW, GOOD, FAIR
) implements Serializable {
  private static final long serialVersionUID = 1L;
}