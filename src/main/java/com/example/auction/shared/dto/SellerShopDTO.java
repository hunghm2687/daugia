package com.example.auction.shared.dto;

import java.io.Serializable;
import java.time.Instant;

/**
 * SellerShopDTO - Thông tin shop bán hàng
 */
public record SellerShopDTO(
  String shopName,
  String shopDescription,
  String shopImage,
  double shopRating,
  int totalSold,
  int totalReviews,
  double totalCommission,
  int activeAuctions,
  int totalAuctions,
  Instant createdAt
) implements Serializable {
  private static final long serialVersionUID = 1L;
}