//package com.example.auction.dao;
//
//import com.example.auction.shared.entity.Auction;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//public class AuctionDAO extends BaseDAO {
//  private static AuctionDAO instance;
//
//  private AuctionDAO() {}
//
//  public static AuctionDAO getInstance() {
//    if (instance == null) {
//      synchronized (AuctionDAO.class) {
//        if (instance == null) {
//          instance = new AuctionDAO();
//        }
//      }
//    }
//    return instance;
//  }
//
//  // Lấy auction theo ID
//  public Auction getAuctionById(Long auctionId) {
//    String sql = "SELECT * FROM auctions WHERE id = ?";
//
//    try (Connection conn = getConnection();
//         PreparedStatement ps = conn.prepareStatement(sql)) {
//
//      ps.setLong(1, auctionId);
//
//      try (ResultSet rs = ps.executeQuery()) {
//        if (rs.next()) {
//          // TODO: Map ResultSet → Auction entity
//          System.out.println("Auction found: " + auctionId);
//          return null; // placeholder
//        }
//      }
//    } catch (SQLException e) {
//      e.printStackTrace();
//    }
//
//    return null;
//  }
//
//  // Update highest bid
//  public void updateHighestBid(Long auctionId, Double amount, String bidderUsername) {
//    String sql = "UPDATE auctions SET current_highest_bid = ?, " +  // Thêm dấu phẩy
//        "current_highest_bidder_username = ?, " +          // Thêm dấu phẩy
//        "bid_count = bid_count + 1 " +                   // Thêm dấu phẩy
//        "WHERE id = ?;";
//
//    try (Connection conn = getConnection();
//         PreparedStatement ps = conn.prepareStatement(sql)) {
//
//      ps.setDouble(1, amount);
//      ps.setString(2, bidderUsername);
//      ps.setLong(3, auctionId);
//
//      int rowsAffected = ps.executeUpdate();
//      if (rowsAffected > 0) {
//        System.out.println("Auction updated: bid=" + amount + ", bidder=" + bidderUsername);
//      }
//
//    } catch (SQLException e) {
//      e.printStackTrace();
//    }
//  }
//}
package com.example.auction.dao;

import com.example.auction.shared.util.LoggerUtil;
import java.sql.*;
import java.time.Instant;

/**
 * AuctionDAO - Create and manage auctions
 */
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

  // ============ CREATE ============

  /**
   * Create new auction in database
   */
  public Long createAuction(String sellerUsername, String itemName, String itemDescription,
                            String itemImage, String category, Double startPrice,
                            Instant startTime, Instant endTime) {
    String sql = "INSERT INTO auctions (seller_username, item_name, item_description, item_image, " +
      "category, start_price, current_highest_bid, start_time, end_time, status, bid_count, created_at, updated_at) " +
      "VALUES (?,?,?,?,?,?,?,?,?,?,?,NOW(),NOW())";

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, sellerUsername);
      ps.setString(2, itemName);
      ps.setString(3, itemDescription);
      ps.setString(4, itemImage);
      ps.setString(5, category);
      ps.setDouble(6, startPrice);
      ps.setDouble(7, startPrice);
      ps.setTimestamp(8, Timestamp.from(startTime));
      ps.setTimestamp(9, Timestamp.from(endTime));
      ps.setString(10, "PENDING");
      ps.setInt(11, 0);

      int result = ps.executeUpdate();
      if (result > 0) {
        try (ResultSet rs = ps.getGeneratedKeys()) {
          if (rs.next()) {
            Long auctionId = rs.getLong(1);
            LoggerUtil.info("Auction created: " + auctionId + " by " + sellerUsername);
            return auctionId;
          }
        }
      }
    } catch (SQLException e) {
      LoggerUtil.error("Error creating auction", e);
    }
    return null;
  }

  // ============ UPDATE ============

  /**
   * Update highest bid
   */
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
      ps.executeUpdate();
      LoggerUtil.info("Bid updated for auction " + auctionId + ": " + amount);
    } catch (SQLException e) {
      LoggerUtil.error("Error updating highest bid", e);
    }
  }

  /**
   * Update auction status
   */
  public void updateStatus(Long auctionId, String status) {
    String sql = "UPDATE auctions SET status = ?, updated_at = NOW() WHERE id = ?";
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, status);
      ps.setLong(2, auctionId);
      ps.executeUpdate();
      LoggerUtil.info("Auction " + auctionId + " status updated to: " + status);
    } catch (SQLException e) {
      LoggerUtil.error("Error updating auction status", e);
    }
  }

  /**
   * Update winner when auction ends
   */
  public void updateWinner(Long auctionId, String winnerUsername, Double finalPrice) {
    String sql = "UPDATE auctions SET winner_username = ?, final_price = ?, status = 'CLOSED', updated_at = NOW() WHERE id = ?";
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, winnerUsername);
      ps.setDouble(2, finalPrice);
      ps.setLong(3, auctionId);
      ps.executeUpdate();
      LoggerUtil.info("Auction " + auctionId + " winner set: " + winnerUsername);
    } catch (SQLException e) {
      LoggerUtil.error("Error updating winner", e);
    }
  }

  // ============ DELETE ============

  /**
   * Delete auction (only PENDING can be deleted)
   */
  public void deleteAuction(Long auctionId) {
    String sql = "DELETE FROM auctions WHERE id = ? AND status = 'PENDING'";
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setLong(1, auctionId);
      ps.executeUpdate();
      LoggerUtil.info("Auction deleted: " + auctionId);
    } catch (SQLException e) {
      LoggerUtil.error("Error deleting auction", e);
    }
  }
}