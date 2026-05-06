package com.example.auction.server.handlers;

import com.example.auction.dao.ProductDAO;
import com.example.auction.server.ClientSession;
import com.example.auction.server.UserSession;
import com.example.auction.shared.dto.AuctionDTO;
import com.example.auction.shared.dto.MessageProtocol;
import com.example.auction.shared.util.LoggerUtil;

import java.util.List;

/**
 * SearchHandler - Search auctions by keyword
 */
public class SearchHandler extends RequestHandler {

  @Override
  public void handle(ClientSession session, MessageProtocol message, UserSession userSession) throws Exception {
    try {
      String keyword = (String) message.data();

      if (keyword == null || keyword.trim().isEmpty()) {
        List<AuctionDTO> allProducts = ProductDAO.getInstance().getAllActiveAuctions();
        sendResponse(session, new MessageProtocol(
          "SEARCH",
          allProducts,
          "SUCCESS",
          "Tìm thấy " + allProducts.size() + " kết quả"
        ));
        return;
      }

      List<AuctionDTO> results = ProductDAO.getInstance().searchAuctions(keyword);

      sendResponse(session, new MessageProtocol(
        "SEARCH",
        results,
        "SUCCESS",
        "Tìm thấy " + results.size() + " kết quả cho: " + keyword
      ));

      LoggerUtil.info("Search results: " + results.size() + " for keyword: " + keyword);

    } catch (Exception e) {
      LoggerUtil.error("Search error", e);
      sendResponse(session, new MessageProtocol(
        "SEARCH",
        null,
        "ERROR",
        "Lỗi tìm kiếm: " + e.getMessage()
      ));
    }
  }
}