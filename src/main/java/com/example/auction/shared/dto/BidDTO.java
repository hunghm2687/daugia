package com.example.auction.shared.dto;

import java.io.Serializable;
import java.time.Instant;

/**
 * BidDTO - DATA TRANSFER OBJECT FOR BIDS
 * CÁI NÀY ĐỂ LÀM GÌ?
 * - Transfer bid info qua network (client <-> server)
 * - Broadcast new bids tới tất cả clients
 * - Track bid info: auctionId, bidder, amount, time, status
 * <p>
 * VÌ SAO DÙNG RECORD?
 * - Record = immutable data class
 * - Tự generate constructor, getter, toString, equals, hashCode
 * - Perfect cho DTO
 * - Implement Serializable → gửi qua socket
 * <p>
 * FIELDS GIẢI THÍCH:
 * <p>
 * auctionId (Long):
 * - Phiên đấu giá nào
 * - Ví dụ: 1L = Auction #1
 * <p>
 * bidderUsername (String):
 * - Ai đặt giá
 * - Ví dụ: "john", "jane"
 * <p>
 * amount (Double):
 * - Số tiền đặt
 * - Ví dụ: 500.0, 1000.0
 * <p>
 * bidTime (Instant):
 * - Thời điểm đặt
 * - Ví dụ: Instant.now()
 * - Instant = UTC time (phù hợp network + database)
 * <p>
 * status (String):
 * - SUCCESS: Bid được chấp nhận (là highest bid)
 * - FAILED: Bid bị từ chối (amount quá thấp, hết tiền, ...)
 * - OUTBID: Bid bị vượt qua (bị bid cao hơn)
 * <p>
 * VẬN DỤNG:
 * <p>
 * 1. Client gửi bid request:
 * new BidDTO(1L, "john", 500.0, Instant.now(), "SUCCESS")
 * MessageProtocol("BID", bidDTO, null, null)
 * <p>
 * 2. Server nhận, xử lý, broadcast:
 * if (bid valid) {
 * save database
 * broadcast(MessageProtocol("NEW_BID", bidDTO, "SUCCESS", null))
 * }
 * 3. Tất cả clients nhận:
 * MessageProtocol msg = readObject()
 * if ("NEW_BID".equals(msg.type())) {
 * BidDTO bid = (BidDTO) msg.data()
 * updateUI(bid.amount())  // Update current bid
 * }
 */
public record BidDTO(
  Long auctionId,  // Auction ID
  String bidderUsername,  // Bidder username
  Double amount,  // Bid amount
  Instant bidtime,  // Bid time
  String status  // SUCCESS, FAILED, OUTBID
) implements Serializable {
  private static final long serialVersionUID = 1L;
}
