package com.example.auction.server.handlers;

import com.example.auction.dao.AuctionDAO;
import com.example.auction.server.ClientSession;
import com.example.auction.server.UserSession;
import com.example.auction.shared.dto.CreateAuctionDTO;
import com.example.auction.shared.dto.MessageProtocol;
import com.example.auction.shared.entity.Role;
import com.example.auction.shared.util.LoggerUtil;

import java.time.Instant;

/**
 * CreateAuctionHandler - Create new auction
 */
public class CreateAuctionHandler extends RequestHandler {

  @Override
  public void handle(ClientSession session, MessageProtocol message, UserSession userSession) throws Exception {
    try {
      if (userSession == null) {
        sendResponse(session, new MessageProtocol(
          "CREATE_AUCTION",
          null,
          "ERROR",
          "Phải login trước"
        ));
        return;
      }

      if (userSession.getRole() != Role.MEMBER) {
        sendResponse(session, new MessageProtocol(
          "CREATE_AUCTION",
          null,
          "ERROR",
          "Phải là member để tạo phiên"
        ));
        return;
      }

      CreateAuctionDTO auctionData = (CreateAuctionDTO) message.data();

      // ============ VALIDATION ============

      if (auctionData.itemName() == null || auctionData.itemName().trim().isEmpty()) {
        sendResponse(session, new MessageProtocol(
          "CREATE_AUCTION",
          null,
          "ERROR",
          "Tên sản phẩm không được để trống"
        ));
        return;
      }

      if (auctionData.startPrice() == null || auctionData.startPrice() <= 0) {
        sendResponse(session, new MessageProtocol(
          "CREATE_AUCTION",
          null,
          "ERROR",
          "Giá khởi điểm phải > 0"
        ));
        return;
      }

      if (auctionData.startTime().isBefore(Instant.now())) {
        sendResponse(session, new MessageProtocol(
          "CREATE_AUCTION",
          null,
          "ERROR",
          "Thời gian bắt đầu phải trong tương lai"
        ));
        return;
      }

      if (auctionData.endTime().isBefore(auctionData.startTime())) {
        sendResponse(session, new MessageProtocol(
          "CREATE_AUCTION",
          null,
          "ERROR",
          "Thời gian kết thúc phải sau thời gian bắt đầu"
        ));
        return;
      }

      // ============ CREATE AUCTION ============

      Long auctionId = AuctionDAO.getInstance().createAuction(
        userSession.getUserName(),
        auctionData.itemName(),
        auctionData.itemDescription(),
        auctionData.itemImage(),
        auctionData.category(),
        auctionData.startPrice(),
        auctionData.startTime(),
        auctionData.endTime()
      );

      if (auctionId != null && auctionId > 0) {
        LoggerUtil.info("Auction created: " + auctionId);
        sendResponse(session, new MessageProtocol(
          "CREATE_AUCTION",
          auctionId,
          "SUCCESS",
          "Tạo phiên đấu giá thành công. ID: " + auctionId
        ));
      } else {
        sendResponse(session, new MessageProtocol(
          "CREATE_AUCTION",
          null,
          "ERROR",
          "Lỗi tạo phiên"
        ));
      }

    } catch (Exception e) {
      LoggerUtil.error("CreateAuction error", e);
      sendResponse(session, new MessageProtocol(
        "CREATE_AUCTION",
        null,
        "ERROR",
        "Lỗi: " + e.getMessage()
      ));
    }
  }
}