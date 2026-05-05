package com.example.auction.shared.dto;

import java.io.Serializable;

/**
 * ProductDTO - Chỉ chứa các thông tin cần thiết để hiện thị Card sản phẩm ở màn hình chính
 * Giúp giảm tải băng thông network so với AuctionDTO đầy đủ.
 */
public record ProductDTO(
  Long id,
  String itemName,
  Double currentPrice,
  String sellerName,
  String imageUrl // Sau này thêm ảnh sẽ dùng
) implements Serializable {
  private static final long serialVersionUID = 1L;
}
