package com.example.auction.server.handlers;

import com.example.auction.dao.UserDAO;
import com.example.auction.server.ClientManager;
import com.example.auction.server.ClientSession;
import com.example.auction.server.UserSession;
import com.example.auction.shared.dto.MessageProtocol;
import com.example.auction.shared.dto.UserDTO;

import java.io.IOException;

public class LoginHandler extends RequestHandler {
    @Override
    public void handle(ClientSession session , MessageProtocol message, UserSession tempSession) throws IOException {
        // ép kiểu
        UserDTO userDTO = (UserDTO) message.data();

        try {
            if (!UserDAO.getInstance().checkUserLogin(userDTO)) {
                // login thất bại
                sendResponse(session, new MessageProtocol(
                  "LOGIN",
                  null,
                  "ERROR",
                  "Email hoặc password kh đúng"
                ));
                return;
            }

            UserSession userSession = UserDAO.getInstance().createUserSession(userDTO.email());

            if (userSession == null) {
                sendResponse(session, new MessageProtocol(
                  "LOGIN",
                  null,
                  "ERROR",
                  "User không tìm thấy"
                ));
                return;
            }
            // set user cho session
            session.setCurrentUserSession(userSession);

            // Add client vào ClientManager
            // ClientManager sẽ:
            // - Thêm vào onlineClients map
            // - Broadcast "USER_ONLINE" notification
            ClientManager.getInstance().addClient(userSession.getId(), session); // đoạn này sau cần sửa sang Long vì đang để là String

            System.out.println("User logged in: " + userSession.getUserName());

            UserDTO responseDTO = new UserDTO(
              userSession.getUserName(),
              null,
              userSession.getEmail(),
              userSession.getRole().name(),
              "LOGIN"
            );

            sendResponse(session, new MessageProtocol(
              "LOGIN",
              responseDTO,
              "SUCCESS",
              "Đăng nhập thành công"
            ));
        }
        catch (Exception e) {
            sendResponse(session, new MessageProtocol(
              "LOGIN",
              null,
              "ERROR",
              "Lỗi: " + e.getMessage()
            ));
        }
    }
}
