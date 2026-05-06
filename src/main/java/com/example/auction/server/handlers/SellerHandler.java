package com.example.auction.server.handlers;

import com.example.auction.dao.UserDAO;
import com.example.auction.dao.AuctionDAO;
import com.example.auction.server.ClientSession;
import com.example.auction.server.UserSession;
import com.example.auction.shared.dto.MessageProtocol;
import com.example.auction.shared.dto.SellerShopDTO;
import com.example.auction.shared.entity.Role;

/**
 * SellerHandler - Xử lý các yêu cầu từ seller
 * - REGISTER_SELLER: Đăng ký bán hàng
 * - GET_SHOP_INFO: Lấy thông tin cửa hàng
 * - UPDATE_SHOP_INFO: Cập nhật thông tin cửa hàng
 * - GET_MY_AUCTIONS: Lấy danh sách phiên của seller
 */
public class SellerHandler extends RequestHandler {
  @Override
  public void handle(ClientSession session, MessageProtocol message, UserSession userSession) throws Exception {
    try {
      if (userSession == null) {
        sendResponse(session, new MessageProtocol(
          message.type(),
          null,
          "ERROR",
          "Phải login trước"
        ));
        return;
      }

      if (userSession.getRole() != Role.MEMBER) {
        sendResponse(session, new MessageProtocol(
          message.type(),
          null,
          "ERROR",
          "Chỉ member mới có thể đăng kí bán"
        ));
        return;
      }

      if ("REGISTER_SELLER".equals(message.type())) {
        handleRegisterSeller(session, message, userSession);
      } else if ("GET_SHOP_INFO".equals(message.type())) {
        handleGetShopInfo(session, userSession);
      } else if ("UPDATE_SHOP_INFO".equals(message.type())) {
        handleUpdateShopInfo(session, message, userSession);
      } else if ("GET_MY_AUCTIONS".equals(message.type())) {
        handleGetMyAuctions(session, userSession);
      }
    } catch (Exception e) {
      sendResponse(session, new MessageProtocol(
        message.type(),
        null,
        "ERROR",
        "Lỗi: " + e.getMessage()
      ));
    }
  }

  private void handleRegisterSeller(ClientSession session, MessageProtocol message, UserSession userSession) {
    java.util.Map<String, String> data = (java.util.Map<String, String>) message.data();

    String shopName = data.get("shopName");
    String shopDesc = data.get("shopDesc");
    String shopImage = data.get("shopImage");

    if (shopName == null || shopName.trim().isEmpty()) {
      sendResponse(session, new MessageProtocol(
        "REGISTER_SELLER",
        null,
        "ERROR",
        "Tên cửa hàng không được để trống"
      ));
      return;
    }

    UserDAO.getInstance().registerAsSeller(
      userSession.getUserName(),
      shopName, shopDesc, shopImage
    );

    sendResponse(session, new MessageProtocol(
      "REGISTER_SELLER",
      null,
      "SUCCESS",
      "Đăng ký bán hàng thành công"
    ));
  }

  private void handleGetShopInfo(ClientSession session, UserSession userSession) {
    // TODO: Get shop info from database
    sendResponse(session, new MessageProtocol(
      "GET_SHOP_INFO",
      null,
      "SUCCESS",
      null
    ));
  }

  private void handleUpdateShopInfo(ClientSession session, MessageProtocol message, UserSession userSession) {
    // TODO: Update shop info
    sendResponse(session, new MessageProtocol(
      "UPDATE_SHOP_INFO",
      null,
      "SUCCESS",
      "Cập nhật thành công"
    ));
  }

  private void handleGetMyAuctions(ClientSession session, UserSession userSession) {
    // TODO: Get auctions by seller
    sendResponse(session, new MessageProtocol(
      "GET_MY_AUCTIONS",
      null,
      "SUCCESS",
      null
    ));
  }
}