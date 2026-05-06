package com.example.auction.server.handlers;

import com.example.auction.dao.UserDAO;
import com.example.auction.server.ClientSession;
import com.example.auction.server.UserSession;
import com.example.auction.shared.dto.MessageProtocol;
import com.example.auction.shared.dto.UserProfileDTO;

/**
 * AccountHandler - Xử lý các yêu cầu liên quan đến account
 * - GET_PROFILE: Lấy thông tin profile
 * - UPDATE_PROFILE: Cập nhật thông tin profile
 * - CHANGE_PASSWORD: Đổi mật khẩu
 * - TOP_UP_BALANCE: Nạp tiền
 */
public class AccountHandler extends RequestHandler {
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

      if ("GET_PROFILE".equals(message.type())) {
        handleGetProfile(session, userSession);
      } else if ("UPDATE_PROFILE".equals(message.type())) {
        handleUpdateProfile(session, message, userSession);
      } else if ("TOP_UP_BALANCE".equals(message.type())) {
        handleTopUpBalance(session, message, userSession);
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

  private void handleGetProfile(ClientSession session, UserSession userSession) {
    UserProfileDTO profile = UserDAO.getInstance().getUserProfile(userSession.getUserName());

    if (profile != null) {
      sendResponse(session, new MessageProtocol(
        "GET_PROFILE",
        profile,
        "SUCCESS",
        null
      ));
    } else {
      sendResponse(session, new MessageProtocol(
        "GET_PROFILE",
        null,
        "ERROR",
        "Không tìm thấy profile"
      ));
    }
  }

  private void handleUpdateProfile(ClientSession session, MessageProtocol message, UserSession userSession) {
    java.util.Map<String, String> data = (java.util.Map<String, String>) message.data();

    String phone = data.get("phone");
    String address = data.get("address");
    String fullName = data.get("fullName");
    String bio = data.get("bio");
    String avatar = data.get("avatar");

    UserDAO.getInstance().updateUserProfile(
      userSession.getUserName(),
      phone, address, fullName, bio, avatar
    );

    sendResponse(session, new MessageProtocol(
      "UPDATE_PROFILE",
      null,
      "SUCCESS",
      "Cập nhật thành công"
    ));
  }

  private void handleTopUpBalance(ClientSession session, MessageProtocol message, UserSession userSession) {
    Double amount = (Double) message.data();

    if (amount == null || amount <= 0) {
      sendResponse(session, new MessageProtocol(
        "TOP_UP_BALANCE",
        null,
        "ERROR",
        "Số tiền phải > 0"
      ));
      return;
    }

    // TODO: Integration with payment gateway
    // For now, just update balance
    UserProfileDTO currentProfile = UserDAO.getInstance().getUserProfile(userSession.getUserName());
    double newBalance = currentProfile.balance() + amount;
    UserDAO.getInstance().updateBalance(userSession.getUserName(), newBalance);

    sendResponse(session, new MessageProtocol(
      "TOP_UP_BALANCE",
      newBalance,
      "SUCCESS",
      "Nạp tiền thành công. Số dư: " + newBalance
    ));
  }
}