package com.example.auction.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class BaseDAO {
    // kết nối đến database bằng root user
    // cac DAO sau buoc phai ke thua
    static String URL = "jdbc:mysql://localhost:3306/auction_system";
    static String USERNAME = "root";
    static String PASSWORD = "hung123456789";

    // Method để lấy Connection (kết nối database)
    // Các DAO con gọi method này
    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
