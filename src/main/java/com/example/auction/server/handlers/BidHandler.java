package com.example.auction.server.handlers;

import com.example.auction.dao.AuctionDAO;
import com.example.auction.dao.BidDAO;
import com.example.auction.server.ClientManager;
import com.example.auction.server.ClientSession;
import com.example.auction.server.UserSession;
import com.example.auction.shared.dto.AuctionDTO;
import com.example.auction.shared.dto.BidDTO;
import com.example.auction.shared.dto.MessageProtocol;
import com.example.auction.shared.entity.Role;

/**
 * BidHandler - Validates and processes a BID request, then broadcasts the update.
 *
 * Validation order:
 * 1. User must be logged in
 * 2. User role must be MEMBER
 * 3. Bid amount must be > 0
 * 4. Bidder username must match the logged-in session
 * 5. Auction must exist in DB
 * 6. Auction status must be ACTIVE
 * 7. Bid amount must be > current highest bid
 * 8. Bidder must not be the seller
 */
public class BidHandler extends RequestHandler {
  @Override
  public void handle(ClientSession session, MessageProtocol message, UserSession userSession) {
    BidDTO bidDTO = (BidDTO) message.data();

    try {
      // 1. Login check
      if (userSession == null) {
        sendResponse(session, error("Phải login trước khi đấu giá"));
        return;
      }

      // 2. Role check
      if (userSession.getRole() != Role.MEMBER) {
        sendResponse(session, error("Chỉ MEMBER mới được đấu giá"));
        return;
      }

      // 3. Amount check
      if (bidDTO.amount() == null || bidDTO.amount() <= 0) {
        sendResponse(session, error("Số tiền phải > 0"));
        return;
      }

      // 4. Username consistency
      if (!bidDTO.bidderUsername().equals(userSession.getUserName())) {
        sendResponse(session, error("Username không trùng"));
        return;
      }

      // 5-8. DB-level validation
      AuctionDTO auction = AuctionDAO.getInstance().getAuctionDTOById(bidDTO.auctionId());

      if (auction == null) {
        sendResponse(session, error("Phiên đấu giá không tồn tại"));
        return;
      }

      if (!"ACTIVE".equals(auction.status())) {
        sendResponse(session, error("Phiên đấu giá không còn hoạt động"));
        return;
      }

      if (bidDTO.amount() <= auction.currentHighestBid()) {
        sendResponse(session, error("Giá đặt phải cao hơn giá hiện tại: " + auction.currentHighestBid()));
        return;
      }

      if (bidDTO.bidderUsername().equals(auction.sellerUsername())) {
        sendResponse(session, error("Người bán không thể tự đặt giá"));
        return;
      }

      // Save bid and update auction in DB
      BidDAO.getInstance().saveBid(bidDTO);
      AuctionDAO.getInstance().updateHighestBid(
        bidDTO.auctionId(),
        bidDTO.amount(),
        bidDTO.bidderUsername()
      );

      // Respond success to bidder
      sendResponse(session, new MessageProtocol(
        "BID", bidDTO, "SUCCESS", "Đặt giá thành công! Giá: " + bidDTO.amount()
      ));

      System.out.println("Bid accepted: " + bidDTO.bidderUsername() +
        " bid " + bidDTO.amount() + " for auction " + bidDTO.auctionId());

      // Broadcast update to all connected clients
      ClientManager.getInstance().broadcast(new MessageProtocol(
        "NEW_BID", bidDTO, "SUCCESS", null
      ));

    } catch (Exception e) {
      sendResponse(session, error("Lỗi xử lý bid: " + e.getMessage()));
      e.printStackTrace();
    }
  }

  private MessageProtocol error(String msg) {
    return new MessageProtocol("BID", null, "ERROR", msg);
  }
}
