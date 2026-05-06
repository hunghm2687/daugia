package com.example.auction.server.handlers;

import com.example.auction.server.ClientManager;
import com.example.auction.server.ClientSession;
import com.example.auction.server.UserSession;
import com.example.auction.shared.dto.MessageProtocol;
import com.example.auction.shared.util.LoggerUtil;

/**
 * LogoutHandler - Process logout requests
 */
public class LogoutHandler extends RequestHandler {

  @Override
  public void handle(ClientSession session, MessageProtocol message, UserSession userSession) throws Exception {
    try {
      if (userSession != null) {
        // Remove from online clients
        ClientManager.getInstance().removeClient(userSession.getId());
        LoggerUtil.info("User logged out: " + userSession.getUserName());
      }

      // Send response
      sendResponse(session, new MessageProtocol(
        "LOGOUT",
        null,
        "SUCCESS",
        "Đã đăng xuất"
      ));

      // Close connection after logout
      session.disconnect();

    } catch (Exception e) {
      LoggerUtil.error("Logout error", e);
      sendResponse(session, new MessageProtocol(
        "LOGOUT",
        null,
        "ERROR",
        "Lỗi đăng xuất"
      ));
    }
  }
}