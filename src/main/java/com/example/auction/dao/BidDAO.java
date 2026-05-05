package com.example.auction.dao;

import com.example.auction.shared.dto.BidDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BidDAO extends BaseDAO {
  private static BidDAO instance;

  private BidDAO() {}

  public static BidDAO getInstance() {
    if (instance == null) {
      synchronized (BidDAO.class) {
        if (instance == null) {
          instance = new BidDAO();
        }
      }
    }
    return instance;
  }

  // Save bid vào database
  public void saveBid(BidDTO bidDTO) {
    String sql = "INSERT INTO bids (auction_id, bidder_username, amount, bid_time, status) " +
      "VALUES (?, ?, ?, ?, ?)";

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setLong(1, bidDTO.auctionId());
      ps.setString(2, bidDTO.bidderUsername());
      ps.setDouble(3, bidDTO.amount());
      ps.setTimestamp(4, java.sql.Timestamp.from(bidDTO.bidtime()));
      ps.setString(5, bidDTO.status());

      ps.executeUpdate();
      System.out.println("Bid saved: " + bidDTO.bidderUsername() + " bid " + bidDTO.amount());

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

}
