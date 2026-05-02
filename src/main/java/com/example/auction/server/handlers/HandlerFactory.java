package com.example.auction.server.handlers;

import com.example.auction.server.exception.RequestTypeException;
import java.util.HashMap;
import java.util.Map;

/**
 * HandlerFactory - FACTORY PATTERN FOR HANDLERS
 * CÁI NÀY ĐỂ LÀM GÌ?
 * - Map message type → handler phù hợp
 * - Route messages tới handlers
 * - Centralized handler registry
 * Dùng Factory:
 * handlers.put("LOGIN", new LoginHandler());
 * handlers.put("BID", new BidHandler());
 * handlers.put("SIGNUP", new SignupHandler());
 * kh cần if else dài dòng để tạo handler
 *
 * RequestHandler handler = HandlerFactory.getHandler(msg.type());
 * handler.handle(...);
 * muốn thêm handler mới thì chỉ cần extends
 */
public abstract class HandlerFactory {
    private static final Map<String , RequestHandler> handlers = new HashMap<>();// registry map + factory method

    // Static initializer - chạy 1 lần khi class được load
    static {
        // Đăng ký handlers
        handlers.put("LOGIN" , new LoginHandler());
        handlers.put("SIGNUP" , new SignupHandler());
        handlers.put("BID", new BidHandler());
        // sau này thêm delete account hoặc reset password thì thêm vào đây!
    }

    /**
     * getHandler - Lấy handler dựa trên message type
     *
     * Input: String type (e.g., "LOGIN", "BID")
     * Return: RequestHandler
     * Vận dụng:
     * RequestHandler handler = HandlerFactory.getHandler(msg.type());
     * handler.handle(session, msg);
     */
    public static RequestHandler getHandler(String type){
        RequestHandler handler = handlers.get(type);
        if (handler == null){
            throw new RequestTypeException("Request không được hỗ trợ!");
        }
        return handler;
    }
}
