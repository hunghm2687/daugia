package com.example.auction.server.handlers;

import com.example.auction.dao.ProductDAO;
import com.example.auction.server.ClientSession;
import com.example.auction.server.UserSession;
import com.example.auction.shared.dto.AuctionDTO;
import com.example.auction.shared.dto.MessageProtocol;

import java.util.List;

/**
 * GetProductsHandler - Lấy danh sách sản phẩm
 * - Mỗi product = 1 AuctionDTO
 * - GUEST cũng xem được
 */

public class GetProductsHandler extends RequestHandler {
  @Override
  public void handle(ClientSession session, MessageProtocol message, UserSession userSession ) {
    try {
      // lấy dữ liệu thật từ DB
      List<AuctionDTO> products = ProductDAO.getInstance().getAllActiveAuctions();

      // gửi phản hồi về cho Client
      sendResponse(session, new MessageProtocol(
        "GET_PRODUCTS_LIST",
        products,
        "SUCCESS",
        null
      ));
    }
    catch (Exception e) {
      sendResponse(session, new MessageProtocol(
        "GET_PRODUCTS_LIST",
        null,
        "ERROR",
        "Lỗi lấy sản phẩm: " + e.getMessage()
      ));
      e.printStackTrace();
    }
  }
}
