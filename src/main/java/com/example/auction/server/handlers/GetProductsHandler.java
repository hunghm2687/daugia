//package com.example.auction.server.handlers;
//
//import com.example.auction.dao.ProductDAO;
//import com.example.auction.server.ClientSession;
//import com.example.auction.server.UserSession;
//import com.example.auction.shared.dto.AuctionDTO;
//import com.example.auction.shared.dto.MessageProtocol;
//import com.example.auction.shared.dto.ProductDTO;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * GetProductsHandler - Lấy danh sách sản phẩm
// * - Mỗi product = 1 AuctionDTO
// * - GUEST cũng xem được
// */
//
//public class GetProductsHandler extends RequestHandler {
//  @Override
//  public void handle(ClientSession session, MessageProtocol message, UserSession userSession ) {
//    try {
//      // lấy dữ liệu thật từ DB
//      List<AuctionDTO> products = ProductDAO.getInstance().getAllActiveAuctions();
//
//      // gửi phản hồi về cho Client
//      sendResponse(session, new MessageProtocol(
//        "GET_PRODUCTS_LIST",
//        products,
//        "SUCCESS",
//        null
//      ));
//    }
//    catch (Exception e) {
//      sendResponse(session, new MessageProtocol(
//        "GET_PRODUCTS_LIST",
//        null,
//        "ERROR",
//        "Lỗi lấy sản phẩm: " + e.getMessage()
//      ));
//      e.printStackTrace();
//    }
//  }
//}
package com.example.auction.server.handlers;

import com.example.auction.dao.ProductDAO;
import com.example.auction.server.ClientSession;
import com.example.auction.server.UserSession;
import com.example.auction.shared.dto.AuctionDTO;
import com.example.auction.shared.dto.MessageProtocol;
import com.example.auction.shared.util.LoggerUtil;

import java.util.List;

/**
 * GetProductsHandler - Get list of active auctions
 */
public class GetProductsHandler extends RequestHandler {

  @Override
  public void handle(ClientSession session, MessageProtocol message, UserSession userSession) throws Exception {
    try {
      List<AuctionDTO> products = ProductDAO.getInstance().getAllActiveAuctions();

      sendResponse(session, new MessageProtocol(
        "GET_PRODUCTS_LIST",
        products,
        "SUCCESS",
        "Tìm thấy " + products.size() + " sản phẩm"
      ));

      LoggerUtil.info("Sent " + products.size() + " products to client");

    } catch (Exception e) {
      LoggerUtil.error("GetProducts error", e);
      sendResponse(session, new MessageProtocol(
        "GET_PRODUCTS_LIST",
        null,
        "ERROR",
        "Lỗi lấy sản phẩm: " + e.getMessage()
      ));
    }
  }
}