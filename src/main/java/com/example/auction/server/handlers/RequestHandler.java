package com.example.auction.server.handlers;

import com.example.auction.server.ClientSession;
import com.example.auction.server.UserSession;
import com.example.auction.shared.dto.MessageProtocol;

import java.io.ObjectOutputStream;

//- handle() nhận UserSession (thay vì ClientSession trực tiếp)
//- Tách biệt: network message xử lý từ logic

// interface định nghĩa hành động xử lý request
public abstract class RequestHandler {
    public abstract void handle(ClientSession session , MessageProtocol message, UserSession userSession) throws Exception;

    protected void sendResponse(ClientSession session, MessageProtocol response) {
        session.sendMessage(response);
    }
}