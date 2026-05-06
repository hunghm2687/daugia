//package com.example.auction.server.handlers;
//
//import com.example.auction.dao.UserDAO;
//import com.example.auction.server.ClientManager;
//import com.example.auction.server.ClientSession;
//import com.example.auction.server.UserSession;
//import com.example.auction.shared.dto.MessageProtocol;
//import com.example.auction.shared.dto.UserDTO;
//import com.example.auction.shared.entity.Role;
//import com.example.auction.shared.entity.User;
//
//import java.io.IOException;
//import java.io.ObjectOutputStream;
//
//public class SignupHandler extends RequestHandler {
//    @Override
//    public void handle(ClientSession session, MessageProtocol message, UserSession userSession) throws IOException {
//
//        UserDTO userDTO = (UserDTO) message.data();
//
//        try {
//            System.out.println("\n🔍 Step 1: Checking if email exists...");
//            boolean emailExists = UserDAO.getInstance().emailIsExit(userDTO.email());
//            System.out.println("  Email exists? " + emailExists);
//            if (UserDAO.getInstance().emailIsExit(userDTO.email())) {
//                // Email đã tồn tại
//                // GỌI sendResponse()
//                sendResponse(session, new MessageProtocol(
//                  "SIGNUP",
//                  null,
//                  "ERROR",
//                  "Email đã tồn tại"
//                ));
//                return;
//            }
//            System.out.println("User registered: " + userDTO.username());
//            UserDAO.getInstance().addUser(userDTO);
//
//            // RESPONSE: Send success
//            // GỌI sendResponse()
//            sendResponse(session, new MessageProtocol(
//              "SIGNUP",
//              userDTO,
//              "SUCCESS",
//              "Đăng ký thành công"
//            ));
//
//        } catch (Exception e) {
//            // GỌI sendResponse()
//            System.out.println("\n❌ EXCEPTION in SignupHandler:");
//            System.out.println("  Exception class: " + e.getClass().getName());
//            System.out.println("  Message: " + e.getMessage());
//            System.out.println("  Stack trace:");
//            e.printStackTrace();
//            sendResponse(session, new MessageProtocol(
//              "SIGNUP",
//              null,
//              "ERROR",
//              "Lỗi: " + e.getMessage()
//            ));
//        }
//    }
//}
package com.example.auction.server.handlers;

import com.example.auction.dao.UserDAO;
import com.example.auction.shared.dto.MessageProtocol;
import com.example.auction.shared.dto.UserDTO;
import com.example.auction.shared.entity.Role;
import com.example.auction.server.ClientSession;
import com.example.auction.server.UserSession;
import com.example.auction.shared.util.LoggerUtil;

/**
 * SignupHandler - Process signup requests
 */
public class SignupHandler extends RequestHandler {

    @Override
    public void handle(ClientSession session, MessageProtocol message, UserSession tempSession) throws Exception {
        UserDTO userDTO = (UserDTO) message.data();

        try {
            // Validate inputs
            if (userDTO.username() == null || userDTO.username().trim().isEmpty()) {
                sendResponse(session, new MessageProtocol(
                  "SIGNUP",
                  null,
                  "ERROR",
                  "Username không được để trống"
                ));
                return;
            }

            if (userDTO.password() == null || userDTO.password().length() < 6) {
                sendResponse(session, new MessageProtocol(
                  "SIGNUP",
                  null,
                  "ERROR",
                  "Password phải có ít nhất 6 ký tự"
                ));
                return;
            }

            if (userDTO.email() == null || !userDTO.email().contains("@")) {
                sendResponse(session, new MessageProtocol(
                  "SIGNUP",
                  null,
                  "ERROR",
                  "Email không hợp lệ"
                ));
                return;
            }

            // Check if email exists
            if (UserDAO.getInstance().emailExists(userDTO.email())) {
                sendResponse(session, new MessageProtocol(
                  "SIGNUP",
                  null,
                  "ERROR",
                  "Email đã tồn tại"
                ));
                return;
            }

            // Check if username exists
            if (UserDAO.getInstance().usernameExists(userDTO.username())) {
                sendResponse(session, new MessageProtocol(
                  "SIGNUP",
                  null,
                  "ERROR",
                  "Username đã tồn tại"
                ));
                return;
            }

            // Create user
            boolean success = UserDAO.getInstance().addUser(
              userDTO.username(),
              userDTO.password(),
              userDTO.email(),
              Role.MEMBER
            );

            if (success) {
                LoggerUtil.info("User registered: " + userDTO.username());
                sendResponse(session, new MessageProtocol(
                  "SIGNUP",
                  userDTO,
                  "SUCCESS",
                  "Đăng kí thành công! Vui lòng đăng nhập."
                ));
            } else {
                sendResponse(session, new MessageProtocol(
                  "SIGNUP",
                  null,
                  "ERROR",
                  "Lỗi tạo tài khoản"
                ));
            }

        } catch (Exception e) {
            LoggerUtil.error("Signup error", e);
            sendResponse(session, new MessageProtocol(
              "SIGNUP",
              null,
              "ERROR",
              "Lỗi: " + e.getMessage()
            ));
        }
    }
}