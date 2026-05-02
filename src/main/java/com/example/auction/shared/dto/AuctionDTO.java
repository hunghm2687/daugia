package com.example.auction.shared.dto;

import java.io.Serializable;
import java.time.Instant;

/**
 * AuctionDTO - DATA TRANSFER OBJECT FOR AUCTIONS
 * CÁI NÀY ĐỂ LÀM GÌ?
 * - Transfer auction info qua network
 * - Display auction list
 * - Broadcast auction updates (current bid changed)
 * - Track auction state
 *
 * VÌ SAO DTO?
 * - Auction entity có 20+ fields + methods
 * - Network: gửi all → overhead lớn
 * - DTO: chỉ 10 fields cần thiết
 * - Network efficient
 *
 * FIELDS:
 *
 * id (Long):
 * - Auction ID
 * - Ví dụ: 1L, 2L, ...
 *
 * sellerUsername (String):
 * - Người bán
 * - Ví dụ: "seller1", "seller2"
 *
 * itemName (String):
 * - Tên sản phẩm
 * - Ví dụ: "iPhone 15", "Laptop HP"
 *
 * startPrice (Double):
 * - Giá khởi điểm
 * - Ví dụ: 100.0, 500.0
 *
 * currentHighestBid (Double):
 * - Giá cao nhất hiện tại
 * - Ví dụ: 500.0, 750.0
 * - Update mỗi khi có bid mới
 *
 * currentHighestBidderUsername (String):
 * - Ai đang giữ giá cao nhất
 * - Ví dụ: "john", "jane"
 * - Update mỗi khi có bid mới cao hơn
 *
 * startTime (Instant):
 * - Thời bắt đầu
 * - Ví dụ: Instant.parse("2026-05-02T10:00:00Z")
 *
 * endTime (Instant):
 * - Thời kết thúc
 * - Ví dụ: Instant.parse("2026-05-02T12:00:00Z")
 *
 * status (String):
 * - PENDING: Chờ bắt đầu
 * - ACTIVE: Đang diễn ra (có thể bid)
 * - CLOSED: Kết thúc (xác định người thắng)
 * - COMPLETED: Hoàn tất (thanh toán xong)
 * - CANCELLED: Bị hủy
 *
 * bidCount (Integer):
 * - Số lần đấu giá
 * - Ví dụ: 5, 10 (5 bids, 10 bids)
 *
 * VẬN DỤNG:
 *
 * 1. Broadcast auction update (bid mới):
 *    new AuctionDTO(..., currentHighestBid = 500, ...)
 *    broadcast(MessageProtocol("AUCTION_UPDATE", auctionDTO, ...))
 *
 * 2. Display auction list:
 *    GET_AUCTIONS request
 *    response: MessageProtocol("GET_AUCTIONS", List<AuctionDTO>, ...)
 *
 * 3. Client update UI:
 *    Current bid: auctionDTO.currentHighestBid()
 *    Bidder: auctionDTO.currentHighestBidderUsername()
 *    Time left: auctionDTO.endTime() - now
 */
public record AuctionDTO(
  Long id,
  String sellerUsername,
  String itemName,
  Double startPrice,
  Double currentHighestBid,
  String currentHighestBidderUsername,
  Instant startTime,
  Instant endTime,
  String status,
  Integer bidCount
) implements Serializable {
  private static final long serialVersionUID = 1L;
}
