package com.example.auction.server.handlers;

import com.example.auction.dao.UserDAO;
import com.example.auction.server.ClientSession;
import com.example.auction.server.UserSession;
import com.example.auction.shared.dto.MessageProtocol;
import com.example.auction.shared.dto.UserDTO;

import java.io.IOException;

public class SignupHandler extends RequestHandler {
    @Override
    public void handle(ClientSession session, MessageProtocol message, UserSession userSession) throws IOException {

        UserDTO userDTO = (UserDTO) message.data();

        try {
            System.out.println("\n🔍 Step 1: Checking if email exists...");
            boolean emailExists = UserDAO.getInstance().emailIsExit(userDTO.email());
            System.out.println("  Email exists? " + emailExists);
            if (UserDAO.getInstance().emailIsExit(userDTO.email())) {
                // Email đã tồn tại
                // GỌI sendResponse()
                sendResponse(session, new MessageProtocol(
                  "SIGNUP",
                  null,
                  "ERROR",
                  "Email đã tồn tại"
                ));
                return;
            }
            System.out.println("User registered: " + userDTO.username());
            UserDAO.getInstance().addUser(userDTO);

            // RESPONSE: Send success
            // GỌI sendResponse()
            sendResponse(session, new MessageProtocol(
              "SIGNUP",
              userDTO,
              "SUCCESS",
              "Đăng ký thành công"
            ));

        } catch (Exception e) {
            // GỌI sendResponse()
            System.out.println("\n❌ EXCEPTION in SignupHandler:");
            System.out.println("  Exception class: " + e.getClass().getName());
            System.out.println("  Message: " + e.getMessage());
            System.out.println("  Stack trace:");
            e.printStackTrace();
            sendResponse(session, new MessageProtocol(
              "SIGNUP",
              null,
              "ERROR",
              "Lỗi: " + e.getMessage()
            ));
        }
    }
}
