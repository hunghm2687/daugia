package com.example.auction.shared.dto;

import java.io.Serializable;
import java.time.Instant;

/**
 * UserProfileDTO - Dữ liệu profile user hiển thị trên UI
 */
public record UserProfileDTO(
  String username,
  String email,
  String phone,
  String address,
  String fullName,
  String avatar,
  String bio,
  String role,
  String status,
  double balance,
  double totalSpent,
  double totalEarned,
  int totalBids,
  int totalAuctions,
  double sellerRating,
  int sellerReviews,
  Instant createdAt,
  Instant updatedAt
) implements Serializable {
  private static final long serialVersionUID = 1L;
}