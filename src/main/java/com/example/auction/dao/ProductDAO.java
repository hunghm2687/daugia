//package com.example.auction.dao;
//
//
//import com.example.auction.shared.dto.AuctionDTO;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
//public class ProductDAO extends BaseDAO {
//  private static ProductDAO instance;
//  private ProductDAO() {}
//  public static ProductDAO getInstance() {
//    if (instance == null) {
//      synchronized (ProductDAO.class) {
//        instance = new ProductDAO();
//      }
//    }
//    return instance;
//  }
//// Lấy tất cả auctions hoạt động
//  public List<AuctionDTO> getAllActiveAuctions() {
//    List<AuctionDTO> list = new ArrayList<>();
//
//    // truy vấn các sp đang có phiên ấu giá đang ACTIVE hoặc PENDING
//    String sql = "SELECT " +
//      "id, " +
//      "item_name, " +
//      "item_image, " +
//      "start_price, " +
//      "current_highest_bid, " +
//      "current_highest_bidder_username, " +
//      "seller_username, " +
//      "start_time, " +
//      "end_time, " +
//      "status, " +
//      "bid_count " +
//      "FROM auctions " +
//      "WHERE status IN ('ACTIVE', 'PENDING') " +
//      "ORDER BY created_at DESC";
//    try (Connection conn = getConnection();
//         PreparedStatement ps = conn.prepareStatement(sql);
//         ResultSet rs = ps.executeQuery()) {
//      while (rs.next()) {
//        AuctionDTO auction = new AuctionDTO(
//          rs.getLong("id"),
//          rs.getString("seller_username"),
//          rs.getString("item_name"),
//          rs.getDouble("start_price"),
//          rs.getDouble("current_highest_bid"),
//          rs.getString("current_highest_bidder_username"),
//          rs.getTimestamp("start_time").toInstant(),
//          rs.getTimestamp("end_time").toInstant(),
//          rs.getString("status"),
//          rs.getInt("bid_count"),
//          rs.getString("item_image")
//        );
//        list.add(auction);
//      }
//      System.out.println("Loaded " + list.size() + " active auctions");
//    }
//    catch (SQLException e) {
//      e.printStackTrace();
//    }
//    return list;
//  }
//}
package com.example.auction.dao;

import com.example.auction.shared.dto.AuctionDTO;
import com.example.auction.shared.util.LoggerUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ProductDAO - Manage auction products/items
 */
public class ProductDAO extends BaseDAO {
  private static ProductDAO instance;

  private ProductDAO() {}

  public static ProductDAO getInstance() {
    if (instance == null) {
      synchronized (ProductDAO.class) {
        if (instance == null) {
          instance = new ProductDAO();
        }
      }
    }
    return instance;
  }

  // ============ PUBLIC METHODS ============

  /**
   * Get all active auctions (ACTIVE, PENDING status)
   */
  public List<AuctionDTO> getAllActiveAuctions() {
    List<AuctionDTO> list = new ArrayList<>();
    String sql = "SELECT id, item_name, item_image, start_price, current_highest_bid, " +
      "current_highest_bidder_username, seller_username, start_time, end_time, status, bid_count " +
      "FROM auctions WHERE status IN ('ACTIVE', 'PENDING') ORDER BY created_at DESC LIMIT 100";

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        list.add(mapResultSetToAuctionDTO(rs));
      }
      LoggerUtil.info("Loaded " + list.size() + " active auctions");
    } catch (SQLException e) {
      LoggerUtil.error("Error loading active auctions", e);
    }
    return list;
  }

  /**
   * Get auctions by status
   */
  public List<AuctionDTO> getAuctionsByStatus(String status) {
    List<AuctionDTO> list = new ArrayList<>();
    String sql = "SELECT id, item_name, item_image, start_price, current_highest_bid, " +
      "current_highest_bidder_username, seller_username, start_time, end_time, status, bid_count " +
      "FROM auctions WHERE status = ? ORDER BY created_at DESC LIMIT 100";

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, status);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          list.add(mapResultSetToAuctionDTO(rs));
        }
      }
    } catch (SQLException e) {
      LoggerUtil.error("Error loading auctions by status: " + status, e);
    }
    return list;
  }

  /**
   * Search auctions by keyword
   */
  public List<AuctionDTO> searchAuctions(String keyword) {
    List<AuctionDTO> list = new ArrayList<>();
    String sql = "SELECT id, item_name, item_image, start_price, current_highest_bid, " +
      "current_highest_bidder_username, seller_username, start_time, end_time, status, bid_count " +
      "FROM auctions WHERE MATCH(item_name) AGAINST(? IN BOOLEAN MODE) " +
      "AND status IN ('ACTIVE', 'PENDING') ORDER BY created_at DESC LIMIT 50";

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, keyword);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          list.add(mapResultSetToAuctionDTO(rs));
        }
      }
    } catch (SQLException e) {
      LoggerUtil.error("Error searching auctions: " + keyword, e);
    }
    return list;
  }

  /**
   * Get auction by ID
   */
  public AuctionDTO getAuctionById(Long auctionId) {
    String sql = "SELECT id, item_name, item_image, start_price, current_highest_bid, " +
      "current_highest_bidder_username, seller_username, start_time, end_time, status, bid_count " +
      "FROM auctions WHERE id = ?";

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setLong(1, auctionId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return mapResultSetToAuctionDTO(rs);
        }
      }
    } catch (SQLException e) {
      LoggerUtil.error("Error getting auction by ID: " + auctionId, e);
    }
    return null;
  }

  /**
   * Get auctions by seller
   */
  public List<AuctionDTO> getAuctionsBySeller(String sellerUsername) {
    List<AuctionDTO> list = new ArrayList<>();
    String sql = "SELECT id, item_name, item_image, start_price, current_highest_bid, " +
      "current_highest_bidder_username, seller_username, start_time, end_time, status, bid_count " +
      "FROM auctions WHERE seller_username = ? ORDER BY created_at DESC";

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, sellerUsername);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          list.add(mapResultSetToAuctionDTO(rs));
        }
      }
    } catch (SQLException e) {
      LoggerUtil.error("Error getting auctions by seller: " + sellerUsername, e);
    }
    return list;
  }

  // ============ PRIVATE HELPER METHODS ============

  private AuctionDTO mapResultSetToAuctionDTO(ResultSet rs) throws SQLException {
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