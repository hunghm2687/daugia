//package com.example.auction.dao;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//
//public abstract class BaseDAO {
//    // kết nối đến database bằng root user
//    // cac DAO sau buoc phai ke thua
//    static String URL = "jdbc:mysql://localhost:3306/demo";
//    static String USERNAME = "root";
//    static String PASSWORD = "23072007";
//
//    // Method để lấy Connection (kết nối database)
//    // Các DAO con gọi method này
//    protected Connection getConnection() throws SQLException {
//        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
//    }
//}
package com.example.auction.dao;

import com.example.auction.shared.util.LoggerUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * BaseDAO - Base class for all DAOs
 * Manages database connection
 */
public abstract class BaseDAO {
    // ============ DATABASE CONFIGURATION ============
    static final String URL = "jdbc:mysql://localhost:3306/auction_system";
    static final String USERNAME = "root";
    static final String PASSWORD = "hung123456789";  // ⚠️ UPDATE THIS WITH YOUR ACTUAL PASSWORD
    static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    static {
        try {
            Class.forName(DRIVER);
            LoggerUtil.info("✅ MySQL JDBC Driver loaded");
        } catch (ClassNotFoundException e) {
            LoggerUtil.error("❌ MySQL JDBC Driver not found", e);
        }
    }

    /**
     * Get database connection
     */
    protected Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            LoggerUtil.debug("✅ Database connection established");
            return conn;
        } catch (SQLException e) {
            LoggerUtil.error("❌ Database connection failed - URL: " + URL + ", User: " + USERNAME, e);
            throw e;
        }
    }

    /**
     * Test connection (for debugging)
     */
    public static void testConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            if (conn != null) {
                LoggerUtil.info("✅ DATABASE CONNECTION SUCCESSFUL!");
                LoggerUtil.info("   URL: " + URL);
                LoggerUtil.info("   User: " + USERNAME);
                conn.close();
            }
        } catch (SQLException e) {
            LoggerUtil.error("❌ DATABASE CONNECTION FAILED!", e);
            LoggerUtil.info("   URL: " + URL);
            LoggerUtil.info("   User: " + USERNAME);
            LoggerUtil.info("   Password: " + PASSWORD);
        }
    }
}