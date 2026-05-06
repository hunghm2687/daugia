////package com.example.auction.server.handlers;
////
////import com.example.auction.server.exception.RequestTypeException;
////import java.util.HashMap;
////import java.util.Map;
////
/////**
//// * HandlerFactory - FACTORY PATTERN FOR HANDLERS
//// * CÁI NÀY ĐỂ LÀM GÌ?
//// * - Map message type → handler phù hợp
//// * - Route messages tới handlers
//// * - Centralized handler registry
//// * Dùng Factory:
//// * handlers.put("LOGIN", new LoginHandler());
//// * handlers.put("BID", new BidHandler());
//// * handlers.put("SIGNUP", new SignupHandler());
//// * kh cần if else dài dòng để tạo handler
//// *
//// * RequestHandler handler = HandlerFactory.getHandler(msg.type());
//// * handler.handle(...);
//// * muốn thêm handler mới thì chỉ cần extends
//// */
////public abstract class HandlerFactory {
////    private static final Map<String , RequestHandler> handlers = new HashMap<>();// registry map + factory method
////
////    // Static initializer - chạy 1 lần khi class được load
////    static {
////        // Đăng ký handlers
////        handlers.put("LOGIN" , new LoginHandler());
////        handlers.put("SIGNUP" , new SignupHandler());
////        handlers.put("BID", new BidHandler());
////        handlers.put("GET_PRODUCTS_LIST", new GetProductsHandler());
////        // sau này thêm delete account hoặc reset password thì thêm vào đây!
////    }
////
////    /**
////     * getHandler - Lấy handler dựa trên message type
////     *
////     * Input: String type (e.g., "LOGIN", "BID")
////     * Return: RequestHandler
////     * Vận dụng:
////     * RequestHandler handler = HandlerFactory.getHandler(msg.type());
////     * handler.handle(session, msg);
////     */
////    public static RequestHandler getHandler(String type){
////        RequestHandler handler = handlers.get(type);
////        if (handler == null){
////            throw new RequestTypeException("Request không được hỗ trợ!");
////        }
////        return handler;
////    }
////}
//package com.example.auction.server.handlers;
//
//import com.example.auction.server.exception.RequestTypeException;
//import java.util.HashMap;
//import java.util.Map;
//
//public abstract class HandlerFactory {
//    private static final Map<String, RequestHandler> handlers = new HashMap<>();
//
//    static {
//        // Register all handlers
//        handlers.put("LOGIN", new LoginHandler());
//        handlers.put("LOGOUT", new LogoutHandler());
//        handlers.put("SIGNUP", new SignupHandler());
//        handlers.put("BID", new BidHandler());
//        handlers.put("GET_PRODUCTS_LIST", new GetProductsHandler());
//        handlers.put("SEARCH", new SearchHandler());
//    }
//
//    public static RequestHandler getHandler(String type) {
//        RequestHandler handler = handlers.get(type);
//        if (handler == null) {
//            throw new RequestTypeException("Request type không được hỗ trợ: " + type);
//        }
//        return handler;
//    }
//}
package com.example.auction.server.handlers;

import com.example.auction.server.exception.RequestTypeException;
import com.example.auction.shared.util.LoggerUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * HandlerFactory - Route messages to appropriate handlers
 */
public abstract class HandlerFactory {
    private static final Map<String, RequestHandler> handlers = new HashMap<>();

    static {
        // Authentication Handlers
        handlers.put("LOGIN", new LoginHandler());
        handlers.put("LOGOUT", new LogoutHandler());
        handlers.put("SIGNUP", new SignupHandler());

        // Auction Handlers
        handlers.put("GET_PRODUCTS_LIST", new GetProductsHandler());
        handlers.put("SEARCH", new SearchHandler());
        handlers.put("CREATE_AUCTION", new CreateAuctionHandler());

        // Bidding Handlers
        handlers.put("BID", new BidHandler());
        handlers.put("GET_BID_HISTORY", new BidHistoryHandler());

        LoggerUtil.info("HandlerFactory initialized with " + handlers.size() + " handlers");
    }

    /**
     * Get handler for message type
     */
    public static RequestHandler getHandler(String type) {
        RequestHandler handler = handlers.get(type);
        if (handler == null) {
            String error = "Request type not supported: " + type;
            LoggerUtil.warning(error);
            throw new RequestTypeException(error);
        }
        return handler;
    }
}