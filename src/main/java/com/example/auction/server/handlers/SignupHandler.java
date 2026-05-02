package com.example.auction.server.handlers;

import com.example.auction.dao.UserDAO;
import com.example.auction.server.ClientManager;
import com.example.auction.server.ClientSession;
import com.example.auction.server.UserSession;
import com.example.auction.shared.dto.MessageProtocol;
import com.example.auction.shared.dto.UserDTO;
import com.example.auction.shared.entity.Role;
import com.example.auction.shared.entity.User;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class SignupHandler extends RequestHandler {
    @Override
    public void handle(ClientSession session, MessageProtocol message, UserSession userSession) throws IOException {

        UserDTO userDTO = (UserDTO) message.data();

        try {
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
            UserDAO.getInstance().addUser(userDTO);

            System.out.println("User registered: " + userDTO.username());

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
            sendResponse(session, new MessageProtocol(
              "SIGNUP",
              null,
              "ERROR",
              "Lỗi: " + e.getMessage()
            ));
        }
    }
}
