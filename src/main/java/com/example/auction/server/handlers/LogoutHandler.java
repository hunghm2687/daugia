package com.example.auction.server.handlers;

import com.example.auction.server.ClientManager;
import com.example.auction.server.ClientSession;
import com.example.auction.server.UserSession;
import com.example.auction.shared.dto.MessageProtocol;

/**
 * LogoutHandler - Removes the client from ClientManager when user logs out.
 */
public class LogoutHandler extends RequestHandler {
    @Override
    public void handle(ClientSession session, MessageProtocol message, UserSession userSession) {
        if (userSession != null) {
            ClientManager.getInstance().removeClient(userSession.getId());
            session.setCurrentUserSession(null);
            System.out.println("User logged out: " + userSession.getUserName());
        }
        sendResponse(session, new MessageProtocol("LOGOUT", null, "SUCCESS", "Đăng xuất thành công"));
    }
}
