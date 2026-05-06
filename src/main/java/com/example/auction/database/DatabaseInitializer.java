package com.example.auction.database;

import com.example.auction.shared.util.LoggerUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * DatabaseInitializer - Initialize database and tables
 * Run this ONCE before starting the server
 */
public class DatabaseInitializer {

  private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
  private static final String ROOT_URL = "jdbc:mysql://localhost:3306";
  private static final String USERNAME = "root";
  private static final String PASSWORD = "23072007";  // ⚠️ UPDATE WITH YOUR ACTUAL PASSWORD
  private static final String DATABASE = "auction_system";

  public static void main(String[] args) {
    LoggerUtil.info("🚀 Starting Database Initialization...");

    try {
      Class.forName(DRIVER);

      // Create database
      createDatabase();

      // Create tables
      createTables();

      // Insert test data
      insertTestData();

      LoggerUtil.info("✅ Database initialization completed successfully!");

    } catch (Exception e) {
      LoggerUtil.error("❌ Database initialization failed", e);
    }
  }

  /**
   * Create database
   */
  private static void createDatabase() {
    try (Connection conn = DriverManager.getConnection(ROOT_URL, USERNAME, PASSWORD);
         Statement stmt = conn.createStatement()) {

      String sql = "CREATE DATABASE IF NOT EXISTS " + DATABASE;
      stmt.execute(sql);
      LoggerUtil.info("✅ Database created: " + DATABASE);

    } catch (Exception e) {
      LoggerUtil.error("Error creating database", e);
    }
  }

  /**
   * Create all tables
   */
  private static void createTables() {
    try (Connection conn = DriverManager.getConnection(
      ROOT_URL + "/" + DATABASE, USERNAME, PASSWORD);
         Statement stmt = conn.createStatement()) {

      // Users table
      String usersSQL = "CREATE TABLE IF NOT EXISTS usertable (" +
        "id VARCHAR(50) PRIMARY KEY," +
        "username VARCHAR(50) UNIQUE NOT NULL," +
        "email VARCHAR(100) UNIQUE NOT NULL," +
        "password VARCHAR(100) NOT NULL," +
        "phone VARCHAR(20)," +
        "address VARCHAR(255)," +
        "full_name VARCHAR(100)," +
        "avatar VARCHAR(500)," +
        "bio TEXT," +
        "role ENUM('ADMIN', 'MEMBER', 'GUEST') DEFAULT 'MEMBER'," +
        "status ENUM('ACTIVE', 'BANNED') DEFAULT 'ACTIVE'," +
        "balance DECIMAL(15,2) DEFAULT 0," +
        "total_spent DECIMAL(15,2) DEFAULT 0," +
        "total_earned DECIMAL(15,2) DEFAULT 0," +
        "total_bids INT DEFAULT 0," +
        "total_auctions INT DEFAULT 0," +
        "shop_name VARCHAR(100)," +
        "shop_description TEXT," +
        "shop_image VARCHAR(500)," +
        "seller_rating DECIMAL(3,1) DEFAULT 5.0," +
        "seller_reviews INT DEFAULT 0," +
        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
        "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
        "INDEX idx_email (email)," +
        "INDEX idx_username (username)," +
        "INDEX idx_status (status)" +
        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
      stmt.execute(usersSQL);
      LoggerUtil.info("✅ Users table created");

      // Auctions table
      String auctionsSQL = "CREATE TABLE IF NOT EXISTS auctions (" +
        "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
        "seller_username VARCHAR(50) NOT NULL," +
        "item_name VARCHAR(255) NOT NULL," +
        "item_description TEXT," +
        "item_image VARCHAR(500)," +
        "category VARCHAR(50)," +
        "start_price DECIMAL(15,2) NOT NULL," +
        "current_highest_bid DECIMAL(15,2) DEFAULT 0," +
        "current_highest_bidder_username VARCHAR(50)," +
        "winner_username VARCHAR(50)," +
        "final_price DECIMAL(15,2)," +
        "start_time TIMESTAMP NOT NULL," +
        "end_time TIMESTAMP NOT NULL," +
        "status ENUM('PENDING', 'ACTIVE', 'CLOSED', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING'," +
        "bid_count INT DEFAULT 0," +
        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
        "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
        "FOREIGN KEY (seller_username) REFERENCES usertable(username)," +
        "FOREIGN KEY (current_highest_bidder_username) REFERENCES usertable(username)," +
        "FOREIGN KEY (winner_username) REFERENCES usertable(username)," +
        "INDEX idx_status (status)," +
        "INDEX idx_seller (seller_username)," +
        "INDEX idx_end_time (end_time)" +
        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
      stmt.execute(auctionsSQL);
      LoggerUtil.info("✅ Auctions table created");

      // Bids table
      String bidsSQL = "CREATE TABLE IF NOT EXISTS bids (" +
        "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
        "auction_id BIGINT NOT NULL," +
        "bidder_username VARCHAR(50) NOT NULL," +
        "amount DECIMAL(15,2) NOT NULL," +
        "bid_time TIMESTAMP NOT NULL," +
        "status ENUM('SUCCESS', 'FAILED', 'OUTBID', 'CANCELLED') DEFAULT 'SUCCESS'," +
        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
        "FOREIGN KEY (auction_id) REFERENCES auctions(id) ON DELETE CASCADE," +
        "FOREIGN KEY (bidder_username) REFERENCES usertable(username)," +
        "INDEX idx_auction (auction_id)," +
        "INDEX idx_bidder (bidder_username)," +
        "INDEX idx_bid_time (bid_time)" +
        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
      stmt.execute(bidsSQL);
      LoggerUtil.info("✅ Bids table created");

    } catch (Exception e) {
      LoggerUtil.error("Error creating tables", e);
    }
  }

  /**
   * Insert test data
   */
  private static void insertTestData() {
    try (Connection conn = DriverManager.getConnection(
      ROOT_URL + "/" + DATABASE, USERNAME, PASSWORD);
         Statement stmt = conn.createStatement()) {

      // Clear existing data
      stmt.execute("DELETE FROM bids");
      stmt.execute("DELETE FROM auctions");
      stmt.execute("DELETE FROM usertable");

      // Insert users
      String userSQL = "INSERT INTO usertable (id, username, email, password, full_name, phone, role) VALUES " +
        "('user_001', 'seller1', 'seller1@example.com', '123456', 'Seller One', '0901234567', 'MEMBER')," +
        "('user_002', 'bidder1', 'bidder1@example.com', '123456', 'Bidder One', '0902345678', 'MEMBER')," +
        "('user_003', 'bidder2', 'bidder2@example.com', '123456', 'Bidder Two', '0903456789', 'MEMBER')";
      stmt.execute(userSQL);
      LoggerUtil.info("✅ Test users inserted");

      // Insert auctions
      String auctionSQL = "INSERT INTO auctions (seller_username, item_name, item_description, item_image, " +
        "category, start_price, current_highest_bid, start_time, end_time, status, bid_count) VALUES " +
        "('seller1', 'iPhone 15 Pro', 'Brand new iPhone 15 Pro', 'https://via.placeholder.com/300', " +
        "'Electronics', 15000000, 15000000, NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), 'PENDING', 0)," +
        "('seller1', 'MacBook Pro 16', 'MacBook Pro 16 inch M3', 'https://via.placeholder.com/300', " +
        "'Electronics', 30000000, 30000000, NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), 'PENDING', 0)";
      stmt.execute(auctionSQL);
      LoggerUtil.info("✅ Test auctions inserted");

    } catch (Exception e) {
      LoggerUtil.error("Error inserting test data", e);
    }
  }
}