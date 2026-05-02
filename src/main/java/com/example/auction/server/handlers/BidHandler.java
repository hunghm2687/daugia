package com.example.auction.server.handlers;

import com.example.auction.server.ClientManager;
import com.example.auction.server.ClientSession;
import com.example.auction.server.UserSession;
import com.example.auction.shared.dto.BidDTO;
import com.example.auction.shared.dto.MessageProtocol;
import com.example.auction.shared.entity.Role;

import java.lang.ref.Cleaner;

/**
 * ╔════════════════════════════════════════════════════════════╗
 * ║ BidHandler - REAL-TIME BIDDING HANDLER                      ║
 * ╚════════════════════════════════════════════════════════════╝
 *
 * CÁI NÀY ĐỂ LÀM GÌ?
 * - Handle BID message từ client
 * - Validate bid (amount > 0, user login, ...)
 * - Save bid tới database
 * - Update auction (currentBid, currentBidder)
 * - BROADCAST new bid tới tất cả clients
 * - REAL-TIME: Tất cả clients update UI đồng thời
 *
 * FLOW:
 * 1. Client gửi: MessageProtocol("BID", bidDTO, null, null)
 * 2. ClientSession.handleMessage() → BidHandler.handle()
 * 3. Validate:
 *    ├─ User login?
 *    ├─ Amount > 0?
 *    ├─ Bidder = current user?
 *    ├─ Amount > current bid?
 *    ├─ User có tiền?
 *    └─ Auction active?
 * 4. Nếu OK:
 *    ├─ TODO: Save database
 *    ├─ sendResponse(session, SUCCESS response)
 *    ├─ BROADCAST new bid
 *    └─ Tất cả clients nhận → update UI
 * 5. Nếu fail:
 *    └─ sendResponse(session, ERROR response)
 *
 * REAL-TIME MAGIC:
 * 1. User1 bid 500
 * 2. Server nhận → BidHandler xử lý
 * 3. broadcast(MessageProtocol("NEW_BID", bidDTO(500)))
 * 4. Server gửi tới:
 *    ├─ ClientSession1 (User1)
 *    ├─ ClientSession2 (User2)
 *    ├─ ClientSession3 (User3)
 *    └─ ...
 * 5. Tất cả clients nhận:
 *    ├─ readObject() → MessageProtocol("NEW_BID", bidDTO)
 *    ├─ Platform.runLater() → update UI
 *    └─ Show "Current bid: 500"
 * 6. Tất cả thấy update đồng thời REAL-TIME!
 *
 * VÌ SAO BROADCAST?
 * - Bid không phải private event
 * - Tất cả users bidding cùng phiên
 * - Tất cả cần biết current bid
 * - Broadcast → tất cả thấy update đồng thời
 */
public class BidHandler extends RequestHandler {
  @Override
  public void handle(ClientSession session, MessageProtocol message, UserSession userSession) {
    // ép kiểu sang BidDTO
    BidDTO bidDTO = (BidDTO) message.data();

    try {
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
          "Chỉ BIDDER hoặc SELLER mới được đấu giá"
        ));
        return;
      }

      if (bidDTO.amount() == null || bidDTO.amount() <= 0) {
        sendResponse(session, new MessageProtocol(
          "BID",
          null,
          "ERROR",
          "Số tiền phải > 0"
        ));
        return;
      }

      if (!bidDTO.bidderUsername().equals(userSession.getUserName())) {
        sendResponse(session, new MessageProtocol(
          "BID",
          null,
          "ERROR",
          "Username không trùng"
        ));
        return;
      }
      // ╔─────────────────────────────────────────────┐
      // ║ TODO: DATABASE VALIDATIONS                 │
      // ╔─────────────────────────────────────────────┐
      // TODO: AuctionDAO.getAuction(bidDTO.auctionId())
      //   ├─ Kiểm tra auction tồn tại
      //   ├─ Kiểm tra auction đang ACTIVE
      //   ├─ Kiểm tra bidDTO.amount() > current bid
      // TODO: UserDAO.getUser(bidDTO.bidderUsername())
      //   └─ Kiểm tra user có đủ tiền

      // ╔─────────────────────────────────────────────┐
      // ║ PROCESS: Save database (TODO)              │
      // ╔─────────────────────────────────────────────┐
      // TODO: BidDAO.saveBid(bidDTO)
      // TODO: AuctionDAO.updateHighestBid(
      //         bidDTO.auctionId(),
      //         bidDTO.amount(),
      //         bidDTO.bidderUsername()
      //       )

      // RESPONSE: Send success to bidder
      sendResponse(session, new MessageProtocol(
        "BID",
        bidDTO,
        "SUCCESS",
        "Đặt giá thành công! Giá: " + bidDTO.amount()
      ));

      System.out.println("Bid accepted: " + bidDTO.bidderUsername() +
        " bid " + bidDTO.amount() +
        " for auction " + bidDTO.auctionId());

      // BROADCAST: Tất cả clients nhận update
      MessageProtocol broadcastMsg = new MessageProtocol(
        "NEW_BID",  // type: Broadcast new bid
        bidDTO,   // data: BidDTO
        "SUCCESS",   // data: BidDTO
        null   // data: BidDTO
      );

      // broadcast tơ tất cả connected clients
      // ClientManager sẽ gửi message tới tất cả ClientSession
      ClientManager.getInstance().broadcast(broadcastMsg);
      System.out.println("Broadcast to " +
        ClientManager.getInstance().getOnlineCount() +
        " clients");
    }
    catch (Exception e) {
      sendResponse(session, new MessageProtocol(
        "BID",
        null,
        "ERROR",
        "Lỗi xử lý bid: " + e.getMessage()
      ));
      e.printStackTrace();
    }
  }


}
