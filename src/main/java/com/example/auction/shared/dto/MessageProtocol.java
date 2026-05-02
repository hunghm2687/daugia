package com.example.auction.shared.dto;

import java.io.Serializable;

/**
 * MessageProtocol - WRAPPER CHO MESSAGES
 * Để làm gì:
 * Vấn đề: Server không biết message từ client là gì
 * - Client gửi UserDTO → Server không biết là LOGIN hay SIGNUP
 * - Phải guess type → error prone
 * <p>
 * Giải pháp: Wrapper với "type" field
 * - new MessageProtocol("LOGIN", userDTO, null, null)
 * - Server biết ngay: type = "LOGIN"
 * - Route tới LoginHandler
 * Vì sao cần:
 * 1. Unified format:
 * - Request: MessageProtocol("LOGIN", userDTO, null, null)
 * - Response: MessageProtocol("LOGIN", userData, "SUCCESS", "OK")
 * - Error: MessageProtocol("LOGIN", null, "ERROR", "Sai email")
 * → Tất cả cùng format → dễ handle
 * <p>
 * 2. Routing:
 * switch (msg.type()) {
 * case "LOGIN" → LoginHandler
 * case "BID" → BidHandler
 * }
 * → Không cần instanceof check
 * <p>
 * 3. Scale:
 * - Thêm message type mới? Chỉ thêm case trong switch
 * - Không phải thay đổi protocol
 * <p>
 * 4. Debug:
 * - Biết message type → dễ debug
 * - Log: " Message from user1: LOGIN"
 * Thiết kế:
 * Record: Tại sao không class?
 * - Record = immutable data class (Java 16+)
 * - Tự generate: constructor, getter, toString, equals, hashCode
 * - Lightweight → perfect cho data transfer
 * - Implement Serializable → gửi qua socket
 */
public record MessageProtocol(
  String type, // Ví dụ: "LOGIN", "SIGNUP", "BID", "CREATE_AUCTION"
  // data: Dữ liệu của message
  // - LOGIN: data = UserDTO(username, password, email, ...)
  // - BID: data = BidDTO(auctionId, amount, ...)
  // - Server: Object obj = (UserDTO) msg.data()
  Object data, // Ví dụ: UserDTO, BidDTO, AuctionDTO
  // Object vì mỗi message type cần data khác, Object chứa tất cả, xog ép kiểu ví dụ (UserDTO) msg.data

  // status: Trạng thái của message
  // Ví dụ: "SUCCESS", "ERROR", null
  //
  // Vận dụng:
  // REQUEST (client gửi tới server):
  //   status = null
  //   new MessageProtocol("LOGIN", userDTO, null, null)
  //
  // RESPONSE (server trả về client):
  //   status = "SUCCESS" hoặc "ERROR"
  //   new MessageProtocol("LOGIN", userData, "SUCCESS", "OK")
  //
  // BROADCAST (server gửi tất cả):
  //   status = "SUCCESS"
  //   new MessageProtocol("NEW_BID", bidDTO, "SUCCESS", null)

  String status,
  // message: Thông báo chi tiết
  // Ví dụ: "Đăng nhập OK", "Email sai", "Không đủ tiền"

  // Vận dụng:
  // SUCCESS: message = "Đăng nhập OK" (tùy chọn)
  // ERROR: message = "Email sai" (bắt buộc)

  // Server code:
  //   if ("ERROR".equals(msg.status())) {
  //     System.out.println("Lỗi: " + msg.message());
  //   }

  // Tại sao?
  // - ERROR cần message để user biết lỗi gì
  // - SUCCESS không cần message (có data đã đủ)
  String message

) implements Serializable {
  // serialVersionUID: Version của class
  // Bắt buộc khi implement Serializable
  private static final long serialVersionUID = 1L;
}
