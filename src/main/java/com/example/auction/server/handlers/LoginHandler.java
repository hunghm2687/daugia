//package com.example.auction.server.handlers;
//
//import com.example.auction.dao.UserDAO;
//import com.example.auction.server.ClientManager;
//import com.example.auction.server.ClientSession;
//import com.example.auction.server.UserSession;
//import com.example.auction.server.exception.RequestTypeException;
//import com.example.auction.shared.dto.MessageProtocol;
//import com.example.auction.shared.dto.UserDTO;
//import com.example.auction.shared.entity.Role;
//import com.example.auction.shared.entity.User;
//
//import java.io.IOException;
//import java.io.ObjectOutputStream;
//
//public class LoginHandler extends RequestHandler {
//    @Override
//    public void handle(ClientSession session , MessageProtocol message, UserSession tempSession) throws IOException {
//        // ép kiểu
//        UserDTO userDTO = (UserDTO) message.data();
//
//        try {
//            if (!UserDAO.getInstance().checkUserLogin(userDTO)) {
//                // login thất bại
//                sendResponse(session, new MessageProtocol(
//                  "LOGIN",
//                  null,
//                  "ERROR",
//                  "Email hoặc password kh đúng"
//                ));
//                return;
//            }
//
//            UserSession userSession = UserDAO.getInstance().createUserSession(userDTO.email());
//
//            if (userSession == null) {
//                sendResponse(session, new MessageProtocol(
//                  "LOGIN",
//                  null,
//                  "ERROR",
//                  "User không tìm thấy"
//                ));
//                return;
//            }
//            // set user cho session
//            session.setCurrentUserSession(userSession);
//
//            // Add client vào ClientManager
//            // ClientManager sẽ:
//            // - Thêm vào onlineClients map
//            // - Broadcast "USER_ONLINE" notification
//
//
//            System.out.println("User logged in: " + userSession.getUserName());
//
//            UserDTO responseDTO = new UserDTO(
//              userSession.getUserName(),
//              null,
//              userSession.getEmail(),
//              userSession.getRole().name()
//            );
//
//            sendResponse(session, new MessageProtocol(
//              "LOGIN",
//              responseDTO,
//              "SUCCESS",
//              "Đăng nhập thành công"
//            ));
//        }
//        catch (Exception e) {
//            sendResponse(session, new MessageProtocol(
//              "LOGIN",
//              null,
//              "ERROR",
//              "Lỗi: " + e.getMessage()
//            ));
//        }
//    }
//}
package com.example.auction.server.handlers;

import com.example.auction.dao.UserDAO;
import com.example.auction.server.ClientManager;
import com.example.auction.server.ClientSession;
import com.example.auction.server.UserSession;
import com.example.auction.shared.dto.MessageProtocol;
import com.example.auction.shared.dto.UserDTO;
import com.example.auction.shared.util.LoggerUtil;

/**
 * LoginHandler - Handle login requests
 */
public class LoginHandler extends RequestHandler {

    @Override
    public void handle(ClientSession session, MessageProtocol message, UserSession tempSession) throws Exception {
        UserDTO userDTO = (UserDTO) message.data();

        try {
            // Validate credentials
            boolean isValid = UserDAO.getInstance().checkUserLogin(userDTO.email(), userDTO.password());

            if (!isValid) {
                sendResponse(session, new MessageProtocol(
                  "LOGIN",
                  null,
                  "ERROR",
                  "Email hoặc password không đúng"
                ));
                return;
            }

            // Create user session
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

            // Set user for this session
            session.setCurrentUserSession(userSession);

            // Add to online clients
            ClientManager.getInstance().addClient(userSession.getId(), session);

            LoggerUtil.info("✅ User logged in: " + userSession.getUserName());

            // Create response
            UserDTO responseDTO = new UserDTO(
              userSession.getUserName(),
              null,
              userSession.getEmail(),
              userSession.getRole().name()
            );

            sendResponse(session, new MessageProtocol(
              "LOGIN",
              responseDTO,
              "SUCCESS",
              "Đăng nhập thành công"
            ));

        } catch (Exception e) {
            LoggerUtil.error("LoginHandler error", e);
            sendResponse(session, new MessageProtocol(
              "LOGIN",
              null,
              "ERROR",
              "Lỗi: " + e.getMessage()
            ));
        }
    }
}