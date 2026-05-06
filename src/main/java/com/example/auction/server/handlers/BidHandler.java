////package com.example.auction.server.handlers;
////
////import com.example.auction.dao.AuctionDAO;
////import com.example.auction.server.ClientManager;
////import com.example.auction.server.ClientSession;
////import com.example.auction.server.UserSession;
////import com.example.auction.shared.dto.BidDTO;
////import com.example.auction.shared.dto.MessageProtocol;
////import com.example.auction.shared.entity.Role;
////
////import java.lang.ref.Cleaner;
////
/////**
//// * ╔════════════════════════════════════════════════════════════╗
//// * ║ BidHandler - REAL-TIME BIDDING HANDLER                      ║
//// * ╚════════════════════════════════════════════════════════════╝
//// *
//// * CÁI NÀY ĐỂ LÀM GÌ?
//// * - Handle BID message từ client
//// * - Validate bid (amount > 0, user login, ...)
//// * - Save bid tới database
//// * - Update auction (currentBid, currentBidder)
//// * - BROADCAST new bid tới tất cả clients
//// * - REAL-TIME: Tất cả clients update UI đồng thời
//// *
//// * FLOW:
//// * 1. Client gửi: MessageProtocol("BID", bidDTO, null, null)
//// * 2. ClientSession.handleMessage() → BidHandler.handle()
//// * 3. Validate:
//// *    ├─ User login?
//// *    ├─ Amount > 0?
//// *    ├─ Bidder = current user?
//// *    ├─ Amount > current bid?
//// *    ├─ User có tiền?
//// *    └─ Auction active?
//// * 4. Nếu OK:
//// *    ├─ TODO: Save database
//// *    ├─ sendResponse(session, SUCCESS response)
//// *    ├─ BROADCAST new bid
//// *    └─ Tất cả clients nhận → update UI
//// * 5. Nếu fail:
//// *    └─ sendResponse(session, ERROR response)
//// *
//// * REAL-TIME MAGIC:
//// * 1. User1 bid 500
//// * 2. Server nhận → BidHandler xử lý
//// * 3. broadcast(MessageProtocol("NEW_BID", bidDTO(500)))
//// * 4. Server gửi tới:
//// *    ├─ ClientSession1 (User1)
//// *    ├─ ClientSession2 (User2)
//// *    ├─ ClientSession3 (User3)
//// *    └─ ...
//// * 5. Tất cả clients nhận:
//// *    ├─ readObject() → MessageProtocol("NEW_BID", bidDTO)
//// *    ├─ Platform.runLater() → update UI
//// *    └─ Show "Current bid: 500"
//// * 6. Tất cả thấy update đồng thời REAL-TIME!
//// *
//// * VÌ SAO BROADCAST?
//// * - Bid không phải private event
//// * - Tất cả users bidding cùng phiên
//// * - Tất cả cần biết current bid
//// * - Broadcast → tất cả thấy update đồng thời
//// */
////public class BidHandler extends RequestHandler {
////  @Override
////  public void handle(ClientSession session, MessageProtocol message, UserSession userSession) {
////    // ép kiểu sang BidDTO
////    BidDTO bidDTO = (BidDTO) message.data();
////
////    try {
////      if (userSession == null) {
////        sendResponse(session, new MessageProtocol(
////          "BID",
////          null,
////          "ERROR",
////          "Phải login trước khi đấu giá"
////        ));
////        return;
////      }
////
////      if (userSession.getRole() != Role.MEMBER) {
////        sendResponse(session, new MessageProtocol(
////          "BID",
////          null,
////          "ERROR",
////          "Chỉ BIDDER hoặc SELLER mới được đấu giá"
////        ));
////        return;
////      }
////
////      if (bidDTO.amount() == null || bidDTO.amount() <= 0) {
////        sendResponse(session, new MessageProtocol(
////          "BID",
////          null,
////          "ERROR",
////          "Số tiền phải > 0"
////        ));
////        return;
////      }
////
////      if (!bidDTO.bidderUsername().equals(userSession.getUserName())) {
////        sendResponse(session, new MessageProtocol(
////          "BID",
////          null,
////          "ERROR",
////          "Username không trùng"
////        ));
////        return;
////      }
////      // ╔─────────────────────────────────────────────┐
////      // ║ TODO: DATABASE VALIDATIONS                 │
////      // ╔─────────────────────────────────────────────┐
////      // TODO: AuctionDAO.getAuction(bidDTO.auctionId())
////      //   ├─ Kiểm tra auction tồn tại
////      //   ├─ Kiểm tra auction đang ACTIVE
////      //   ├─ Kiểm tra bidDTO.amount() > current bid
////      // TODO: UserDAO.getUser(bidDTO.bidderUsername())
////      //   └─ Kiểm tra user có đủ tiền
////
////      // ╔─────────────────────────────────────────────┐
////      // ║ PROCESS: Save database (TODO)              │
////      // ╔─────────────────────────────────────────────┐
////      // TODO: BidDAO.saveBid(bidDTO)
////      // TODO: AuctionDAO.updateHighestBid(
////      //         bidDTO.auctionId(),
////      //         bidDTO.amount(),
////      //         bidDTO.bidderUsername()
////      //       )
////
////      // RESPONSE: Send success to bidder
////      sendResponse(session, new MessageProtocol(
////        "BID",
////        bidDTO,
////        "SUCCESS",
////        "Đặt giá thành công! Giá: " + bidDTO.amount()
////      ));
////      AuctionDAO.getInstance().updateHighestBid(bidDTO.auctionId(), bidDTO.amount(), bidDTO.bidderUsername());
////      System.out.println("Bid accepted: " + bidDTO.bidderUsername() +
////        " bid " + bidDTO.amount() +
////        " for auction " + bidDTO.auctionId());
////
////      // BROADCAST: Tất cả clients nhận update
////      MessageProtocol broadcastMsg = new MessageProtocol(
////        "NEW_BID",  // type: Broadcast new bid
////        bidDTO,   // data: BidDTO
////        "SUCCESS",   // data: BidDTO
////        null   // data: BidDTO
////      );
////
////      // broadcast tơ tất cả connected clients
////      // ClientManager sẽ gửi message tới tất cả ClientSession
////      ClientManager.getInstance().broadcast(broadcastMsg);
////      System.out.println("Broadcast to " +
////        ClientManager.getInstance().getOnlineCount() +
////        " clients");
////    }
////    catch (Exception e) {
////      sendResponse(session, new MessageProtocol(
////        "BID",
////        null,
////        "ERROR",
////        "Lỗi xử lý bid: " + e.getMessage()
////      ));
////      e.printStackTrace();
////    }
////  }
////}
//package com.example.auction.server.handlers;
//
//import com.example.auction.dao.AuctionDAO;
//import com.example.auction.dao.BidDAO;
//import com.example.auction.dao.UserDAO;
//import com.example.auction.server.ClientManager;
//import com.example.auction.server.ClientSession;
//import com.example.auction.server.UserSession;
//import com.example.auction.shared.dto.BidDTO;
//import com.example.auction.shared.dto.MessageProtocol;
//import com.example.auction.shared.entity.Role;
//
//import java.time.Instant;
//
//public class BidHandler extends RequestHandler {
//  @Override
//  public void handle(ClientSession session, MessageProtocol message, UserSession userSession) {
//    BidDTO bidDTO = (BidDTO) message.data();
//
//    try {
//      // Validate user login
//      if (userSession == null) {
//        sendResponse(session, new MessageProtocol(
//          "BID",
//          null,
//          "ERROR",
//          "Phải login trước khi đấu giá"
//        ));
//        return;
//      }
//
//      // Validate role
//      if (userSession.getRole() != Role.MEMBER) {
//        sendResponse(session, new MessageProtocol(
//          "BID",
//          null,
//          "ERROR",
//          "Chỉ MEMBER mới được đấu giá"
//        ));
//        return;
//      }
//
//      // Validate amount
//      if (bidDTO.amount() == null || bidDTO.amount() <= 0) {
//        sendResponse(session, new MessageProtocol(
//          "BID",
//          null,
//          "ERROR",
//          "Số tiền phải > 0"
//        ));
//        return;
//      }
//
//      // Validate username match
//      if (!bidDTO.bidderUsername().equals(userSession.getUserName())) {
//        sendResponse(session, new MessageProtocol(
//          "BID",
//          null,
//          "ERROR",
//          "Username không trùng"
//        ));
//        return;
//      }
//
//      // Check if auction exists and is active
//      com.example.auction.shared.dto.AuctionDTO auction =
//        new com.example.auction.dao.ProductDAO().getAuctionById(bidDTO.auctionId());
//
//      if (auction == null) {
//        sendResponse(session, new MessageProtocol(
//          "BID",
//          null,
//          "ERROR",
//          "Phiên không tồn tại"
//        ));
//        return;
//      }
//
//      if (!"ACTIVE".equals(auction.status())) {
//        sendResponse(session, new MessageProtocol(
//          "BID",
//          null,
//          "ERROR",
//          "Phiên không hoạt động"
//        ));
//        return;
//      }
//
//      // Validate bid amount
//      if (bidDTO.amount() <= auction.currentHighestBid()) {
//        sendResponse(session, new MessageProtocol(
//          "BID",
//          null,
//          "ERROR",
//          "Giá phải cao hơn giá hiện tại: " + auction.currentHighestBid()
//        ));
//        return;
//      }
//
//      // Save bid to database
//      BidDTO bidToSave = new BidDTO(
//        bidDTO.auctionId(),
//        bidDTO.bidderUsername(),
//        bidDTO.amount(),
//        Instant.now(),
//        "SUCCESS"
//      );
//
//      boolean saved = BidDAO.getInstance().saveBid(bidToSave);
//      if (!saved) {
//        sendResponse(session, new MessageProtocol(
//          "BID",
//          null,
//          "ERROR",
//          "Lỗi lưu bid"
//        ));
//        return;
//      }
//
//      // Update auction
//      AuctionDAO.getInstance().updateHighestBid(
//        bidDTO.auctionId(),
//        bidDTO.amount(),
//        bidDTO.bidderUsername()
//      );
//
//      // Send success response to bidder
//      sendResponse(session, new MessageProtocol(
//        "BID",
//        bidToSave,
//        "SUCCESS",
//        "Đặt giá thành công! Giá: " + bidDTO.amount()
//      ));
//
//      System.out.println("Bid accepted: " + bidDTO.bidderUsername() +
//        " bid " + bidDTO.amount() +
//        " for auction " + bidDTO.auctionId());
//
//      // Broadcast to all clients
//      MessageProtocol broadcastMsg = new MessageProtocol(
//        "NEW_BID",
//        bidToSave,
//        "SUCCESS",
//        "Có bid mới từ " + bidDTO.bidderUsername()
//      );
//
//      ClientManager.getInstance().broadcast(broadcastMsg);
//      System.out.println("Broadcast to " +
//        ClientManager.getInstance().getOnlineCount() +
//        " clients");
//    }
//    catch (Exception e) {
//      sendResponse(session, new MessageProtocol(
//        "BID",
//        null,
//        "ERROR",
//        "Lỗi xử lý bid: " + e.getMessage()
//      ));
//      e.printStackTrace();
//    }
//  }
//}
package com.example.auction.server.handlers;

import com.example.auction.dao.AuctionDAO;
import com.example.auction.dao.BidDAO;
import com.example.auction.dao.ProductDAO;
import com.example.auction.server.ClientManager;
import com.example.auction.server.ClientSession;
import com.example.auction.server.UserSession;
import com.example.auction.shared.dto.AuctionDTO;
import com.example.auction.shared.dto.BidDTO;
import com.example.auction.shared.dto.MessageProtocol;
import com.example.auction.shared.entity.Role;
import com.example.auction.shared.util.LoggerUtil;

import java.time.Instant;

/**
 * BidHandler - Process bid requests with real-time broadcasting
 */
public class BidHandler extends RequestHandler {

  @Override
  public void handle(ClientSession session, MessageProtocol message, UserSession userSession) throws Exception {
    BidDTO bidDTO = (BidDTO) message.data();

    try {
      // ============ VALIDATION ============

      if (userSession == null) {
        sendResponse(session, new MessageProtocol(
          "BID",
          null,
          "ERROR",
          "Phải login trước khi đấu giá"
        ));
        return;
      }

      if (userSession.getRole() != Role.MEMBER) {
        sendResponse(session, new MessageProtocol(
          "BID",
          null,
          "ERROR",
          "Chỉ MEMBER mới được đấu giá"
        ));
        return;
      }

      // Validate bid amount
      if (bidDTO.amount() == null || bidDTO.amount() <= 0) {
        sendResponse(session, new MessageProtocol(
          "BID",
          null,
          "ERROR",
          "Số tiền phải > 0"
        ));
        return;
      }

      // Validate username match
      if (!bidDTO.bidderUsername().equals(userSession.getUserName())) {
        sendResponse(session, new MessageProtocol(
          "BID",
          null,
          "ERROR",
          "Username không trùng"
        ));
        return;
      }

      // ============ BUSINESS LOGIC ============

      // Get auction from database
      AuctionDTO auction = ProductDAO.getInstance().getAuctionById(bidDTO.auctionId());

      if (auction == null) {
        sendResponse(session, new MessageProtocol(
          "BID",
          null,
          "ERROR",
          "Phiên không tồn tại"
        ));
        return;
      }

      if (!"ACTIVE".equals(auction.status())) {
        sendResponse(session, new MessageProtocol(
          "BID",
          null,
          "ERROR",
          "Phiên không hoạt động"
        ));
        return;
      }

      // Validate bid amount is higher than current highest
      if (bidDTO.amount() <= auction.currentHighestBid()) {
        sendResponse(session, new MessageProtocol(
          "BID",
          null,
          "ERROR",
          "Giá phải cao hơn giá hiện tại: " + String.format("%.0f", auction.currentHighestBid())
        ));
        return;
      }

      // ============ SAVE TO DATABASE ============

      BidDTO bidToSave = new BidDTO(
        bidDTO.auctionId(),
        bidDTO.bidderUsername(),
        bidDTO.amount(),
        Instant.now(),
        "SUCCESS"
      );

      boolean saved = BidDAO.getInstance().saveBid(bidToSave);
      if (!saved) {
        sendResponse(session, new MessageProtocol(
          "BID",
          null,
          "ERROR",
          "Lỗi lưu bid"
        ));
        return;
      }

      // Update auction with new highest bid
      AuctionDAO.getInstance().updateHighestBid(
        bidDTO.auctionId(),
        bidDTO.amount(),
        bidDTO.bidderUsername()
      );

      // ============ SEND RESPONSE TO BIDDER ============

      sendResponse(session, new MessageProtocol(
        "BID",
        bidToSave,
        "SUCCESS",
        "Đặt giá thành công! Giá: " + String.format("%.0f", bidDTO.amount())
      ));

      LoggerUtil.info("Bid accepted: " + bidDTO.bidderUsername() + " bid " + bidDTO.amount());

      // ============ BROADCAST TO ALL CLIENTS ============

      MessageProtocol broadcastMsg = new MessageProtocol(
        "NEW_BID",
        bidToSave,
        "SUCCESS",
        "Có bid mới từ " + bidDTO.bidderUsername() + ": " + String.format("%.0f", bidDTO.amount())
      );

      ClientManager.getInstance().broadcast(broadcastMsg);
      LoggerUtil.info("Broadcast to " + ClientManager.getInstance().getOnlineCount() + " clients");

    } catch (Exception e) {
      LoggerUtil.error("Bid handler error", e);
      sendResponse(session, new MessageProtocol(
        "BID",
        null,
        "ERROR",
        "Lỗi xử lý bid: " + e.getMessage()
      ));
    }
  }
}