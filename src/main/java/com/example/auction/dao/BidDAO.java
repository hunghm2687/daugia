//package com.example.auction.dao;
//
//import com.example.auction.shared.dto.BidDTO;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//
//public class BidDAO extends BaseDAO {
//  private static BidDAO instance;
//
//  private BidDAO() {}
//
//  public static BidDAO getInstance() {
//    if (instance == null) {
//      synchronized (BidDAO.class) {
//        if (instance == null) {
//          instance = new BidDAO();
//        }
//      }
//    }
//    return instance;
//  }
//
//  // Save bid vào database
//  public void saveBid(BidDTO bidDTO) {
//    String sql = "INSERT INTO bids (auction_id, bidder_username, amount, bid_time, status) " +
//      "VALUES (?, ?, ?, ?, ?)";
//
//    try (Connection conn = getConnection();
//         PreparedStatement ps = conn.prepareStatement(sql)) {
//
//      ps.setLong(1, bidDTO.auctionId());
//      ps.setString(2, bidDTO.bidderUsername());
//      ps.setDouble(3, bidDTO.amount());
//      ps.setTimestamp(4, java.sql.Timestamp.from(bidDTO.bidtime()));
//      ps.setString(5, bidDTO.status());
//
//      ps.executeUpdate();
//      System.out.println("Bid saved: " + bidDTO.bidderUsername() + " bid " + bidDTO.amount());
//
//    } catch (SQLException e) {
//      e.printStackTrace();
//    }
//  }
//
//}
package com.example.auction.dao;

import com.example.auction.shared.dto.BidDTO;
import com.example.auction.shared.util.LoggerUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BidDAO - Manage bids
 */
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

  // ============ CREATE ============

  /**
   * Save bid to database
   */
  public boolean saveBid(BidDTO bidDTO) {
    String sql = "INSERT INTO bids (auction_id, bidder_username, amount, bid_time, status, created_at) " +
      "VALUES (?, ?, ?, ?, ?, NOW())";

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setLong(1, bidDTO.auctionId());
      ps.setString(2, bidDTO.bidderUsername());
      ps.setDouble(3, bidDTO.amount());
      ps.setTimestamp(4, java.sql.Timestamp.from(bidDTO.bidtime()));
      ps.setString(5, bidDTO.status());

      int result = ps.executeUpdate();
      if (result > 0) {
        LoggerUtil.info("Bid saved: " + bidDTO.bidderUsername() + " bid " + bidDTO.amount());
        return true;
      }
    } catch (SQLException e) {
      LoggerUtil.error("Error saving bid", e);
    }
    return false;
  }

  // ============ READ ============

  /**
   * Get all bids for auction
   */
  public List<BidDTO> getAllBidsForAuction(Long auctionId) {
    List<BidDTO> bids = new ArrayList<>();
    String sql = "SELECT auction_id, bidder_username, amount, bid_time, status " +
      "FROM bids WHERE auction_id = ? ORDER BY bid_time DESC";

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setLong(1, auctionId);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          bids.add(new BidDTO(
            rs.getLong("auction_id"),
            rs.getString("bidder_username"),
            rs.getDouble("amount"),
            rs.getTimestamp("bid_time").toInstant(),
            rs.getString("status")
          ));
        }
      }
    } catch (SQLException e) {
      LoggerUtil.error("Error getting bids for auction", e);
    }
    return bids;
  }

  /**
   * Get bids by bidder
   */
  public List<BidDTO> getBidsByBidder(String bidderUsername) {
    List<BidDTO> bids = new ArrayList<>();
    String sql = "SELECT auction_id, bidder_username, amount, bid_time, status " +
      "FROM bids WHERE bidder_username = ? ORDER BY bid_time DESC";

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, bidderUsername);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          bids.add(new BidDTO(
            rs.getLong("auction_id"),
            rs.getString("bidder_username"),
            rs.getDouble("amount"),
            rs.getTimestamp("bid_time").toInstant(),
            rs.getString("status")
          ));
        }
      }
    } catch (SQLException e) {
      LoggerUtil.error("Error getting bids by bidder", e);
    }
    return bids;
  }

  /**
   * Get highest bid for auction
   */
  public BidDTO getHighestBidForAuction(Long auctionId) {
    String sql = "SELECT auction_id, bidder_username, amount, bid_time, status " +
      "FROM bids WHERE auction_id = ? ORDER BY amount DESC LIMIT 1";

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setLong(1, auctionId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return new BidDTO(
            rs.getLong("auction_id"),
            rs.getString("bidder_username"),
            rs.getDouble("amount"),
            rs.getTimestamp("bid_time").toInstant(),
            rs.getString("status")
          );
        }
      }
    } catch (SQLException e) {
      LoggerUtil.error("Error getting highest bid", e);
    }
    return null;
  }
}