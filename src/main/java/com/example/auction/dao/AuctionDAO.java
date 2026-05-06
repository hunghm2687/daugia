package com.example.auction.dao;

import com.example.auction.shared.dto.AuctionDTO;
import com.example.auction.shared.entity.Auction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuctionDAO extends BaseDAO {
  private static AuctionDAO instance;

  private AuctionDAO() {}

  public static AuctionDAO getInstance() {
    if (instance == null) {
      synchronized (AuctionDAO.class) {
        if (instance == null) {
          instance = new AuctionDAO();
        }
      }
    }
    return instance;
  }

  // Lấy AuctionDTO theo ID (dùng cho validation)
  public AuctionDTO getAuctionDTOById(Long auctionId) {
    String sql = "SELECT id, item_name, item_image, start_price, current_highest_bid, " +
      "current_highest_bidder_username, seller_username, start_time, end_time, status, bid_count " +
      "FROM auctions WHERE id = ?";

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setLong(1, auctionId);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return new AuctionDTO(
            rs.getLong("id"),
            rs.getString("seller_username"),
            rs.getString("item_name"),
            rs.getDouble("start_price"),
            rs.getDouble("current_highest_bid"),
            rs.getString("current_highest_bidder_username"),
            rs.getTimestamp("start_time").toInstant(),
            rs.getTimestamp("end_time").toInstant(),
            rs.getString("status"),
            rs.getInt("bid_count"),
            rs.getString("item_image")
          );
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return null;
  }

  // Lấy auction entity theo ID (placeholder)
  public Auction getAuctionById(Long auctionId) {
    String sql = "SELECT * FROM auctions WHERE id = ?";

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setLong(1, auctionId);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          // TODO: Map ResultSet → Auction entity
          System.out.println("Auction found: " + auctionId);
          return null; // placeholder
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return null;
  }

  // Update highest bid
  public void updateHighestBid(Long auctionId, Double amount, String bidderUsername) {
    String sql = "UPDATE auctions SET current_highest_bid = ?, " +
      "current_highest_bidder_username = ?, " +
      "bid_count = bid_count + 1, " +
      "updated_at = NOW() " +
      "WHERE id = ?";

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setDouble(1, amount);
      ps.setString(2, bidderUsername);
      ps.setLong(3, auctionId);

      int rowsAffected = ps.executeUpdate();
      if (rowsAffected > 0) {
        System.out.println("Auction updated: bid=" + amount + ", bidder=" + bidderUsername);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
