package com.example.auction.server.handlers;

import com.example.auction.dao.BidDAO;
import com.example.auction.server.ClientSession;
import com.example.auction.server.UserSession;
import com.example.auction.shared.dto.BidDTO;
import com.example.auction.shared.dto.MessageProtocol;
import com.example.auction.shared.util.LoggerUtil;

import java.util.List;

/**
 * BidHistoryHandler - Get bid history for user
 */
public class BidHistoryHandler extends RequestHandler {

  @Override
  public void handle(ClientSession session, MessageProtocol message, UserSession userSession) throws Exception {
    try {
      if (userSession == null) {
        sendResponse(session, new MessageProtocol(
          "GET_BID_HISTORY",
          null,
          "ERROR",
          "Phải login trước"
        ));
        return;
      }

      List<BidDTO> bids = BidDAO.getInstance().getBidsByBidder(userSession.getUserName());

      sendResponse(session, new MessageProtocol(
        "GET_BID_HISTORY",
        bids,
        "SUCCESS",
        "Tìm thấy " + bids.size() + " bids"
      ));

      LoggerUtil.info("Sent " + bids.size() + " bids to " + userSession.getUserName());

    } catch (Exception e) {
      LoggerUtil.error("BidHistory error", e);
      sendResponse(session, new MessageProtocol(
        "GET_BID_HISTORY",
        null,
        "ERROR",
        "Lỗi: " + e.getMessage()
      ));
    }
  }
}