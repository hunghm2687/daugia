package com.example.auction.dao;


import com.example.auction.shared.dto.AuctionDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO extends BaseDAO {
  private static ProductDAO instance;
  private ProductDAO() {}
  public static ProductDAO getInstance() {
    if (instance == null) {
      synchronized (ProductDAO.class) {
        instance = new ProductDAO();
      }
    }
    return instance;
  }
// Lấy tất cả auctions hoạt động
  public List<AuctionDTO> getAllActiveAuctions() {
    List<AuctionDTO> list = new ArrayList<>();

    // truy vấn các sp đang có phiên ấu giá đang ACTIVE hoặc PENDING
    String sql = "SELECT " +
      "id, " +
      "item_name, " +
      "item_image, " +
      "start_price, " +
      "current_highest_bid, " +
      "current_highest_bidder_username, " +
      "seller_username, " +
      "start_time, " +
      "end_time, " +
      "status, " +
      "bid_count " +
      "FROM auctions " +
      "WHERE status IN ('ACTIVE', 'PENDING') " +
      "ORDER BY created_at DESC";
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        AuctionDTO auction = new AuctionDTO(
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
        list.add(auction);
      }
      System.out.println("Loaded " + list.size() + " active auctions");
    }
    catch (SQLException e) {
      e.printStackTrace();
    }
    return list;
  }
}
