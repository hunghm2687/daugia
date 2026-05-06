//package com.example.auction.dao;
//
//import com.example.auction.server.UserSession;
//import com.example.auction.shared.dto.UserDTO;
//import com.example.auction.shared.entity.Role;
//import com.example.auction.shared.entity.User;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.UUID;
//
//// UserDAO truy cap bang lien quan den user
//
//// Chi danh cho cac thong tin lien quan den user
//public class UserDAO extends BaseDAO {
//    private static UserDAO instance;
//
//    private UserDAO() {
//    }
//
//    public static UserDAO getInstance() {
//        if (instance == null) {
//            synchronized (UserDAO.class) {
//                if (instance == null) instance = new UserDAO();
//            }
//        }
//        return instance;
//    }
//
//    // ktra email đã tồn tại trong database kh
//    public boolean emailIsExit(String email) {
//        String sql = "SELECT 1 FROM usertable where email = ? LIMIT 1 ";
//        try (Connection connection = getConnection();  // lấy kết nối
//             PreparedStatement prepareStatement = connection.prepareStatement(sql)) {
//
//            // Set tham số: email = ?
//            prepareStatement.setString(1, email);
//
//            // thực thi query
//            try (ResultSet rs = prepareStatement.executeQuery()) {
//                return rs.next();  // Có row trả về? → true, không có → false
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//    // them user vao bang (vao database)
//    public void addUser(UserDTO userDTO) {
//        String sql = "INSERT INTO usertable (id, email, username, password, role) VALUES (?,?,?,?,?)";
//
//        // Kiểm tra: email chưa tồn tại mới thêm
//        if (!emailIsExit(userDTO.email())) {
//            System.out.println("  Email not exists, proceeding to INSERT...");
//            try (Connection connection = getConnection()) {
//                PreparedStatement preparedStatement = connection.prepareStatement(sql);
//                // xét các tham số
//                preparedStatement.setString(1, UUID.randomUUID().toString());
//                preparedStatement.setString(2, userDTO.email());
//                preparedStatement.setString(3, userDTO.username());
//                preparedStatement.setString(4, userDTO.password());
//                preparedStatement.setString(5, userDTO.role());
//
//                // thực thi INSERT
//                int rows = preparedStatement.executeUpdate();
//                System.out.println(" User inserted: " + rows + " row(s)");
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        } else {
//            System.out.println("Email da ton tai");
//        }
//    }
//
//    // Kiểm tra login (email + password có hợp lệ không)
//    public boolean checkUserLogin(UserDTO userDTO) {
//        String email = userDTO.email();
//        String pass = userDTO.password();
//        String sql = "SELECT password FROM usertable where email = ? LIMIT 1 ";
//        try (Connection connection = getConnection()) {
//            PreparedStatement preparedStatement = connection.prepareStatement(sql);
//
//            // Set email parameter
//            preparedStatement.setString(1, email);
//            try (ResultSet resultSet = preparedStatement.executeQuery()) {
//                if (resultSet.next()) {
//                    // Có user với email này
//                    String dbPassword = resultSet.getString("password");
//                    if (dbPassword.equals(pass)) {
//                        return true;
//                    }
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        System.out.println("Tai khoan khong ton tai");
//        return false;
//    }
//
//    public UserSession createUserSession(String email) {
//        String sql = "SELECT id, email, username, role FROM usertable WHERE email = ? LIMIT 1";
//        try (Connection connection = getConnection()) {
//            PreparedStatement preparedStatement = connection.prepareStatement(sql);
//
//            preparedStatement.setString(1, email);
//
//            try (ResultSet resultSet = preparedStatement.executeQuery()) {
//                if (resultSet.next()) {
//                    String roleString = resultSet.getString("role");
//                    Role role = Role.valueOf(roleString);  // Convert String → Role enum
//                    return new UserSession(
//                      resultSet.getString("id"),
//                      resultSet.getString("username"),
//                      resultSet.getString("email"),
//                      role
//                    );
//                }
//            }
//        }
//        catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//}
package com.example.auction.dao;

import com.example.auction.server.UserSession;
import com.example.auction.shared.dto.UserProfileDTO;
import com.example.auction.shared.entity.Role;
import com.example.auction.shared.util.LoggerUtil;

import java.sql.*;
import java.time.Instant;
import java.util.UUID;

/**
 * UserDAO - User data access object
 */
public class UserDAO extends BaseDAO {
    private static UserDAO instance;

    private UserDAO() {}

    public static UserDAO getInstance() {
        if (instance == null) {
            synchronized (UserDAO.class) {
                if (instance == null) {
                    instance = new UserDAO();
                }
            }
        }
        return instance;
    }

    // ============ CREATE ============

    /**
     * Add new user
     */
    public boolean addUser(String username, String password, String email, Role role) {
        String sql = "INSERT INTO usertable (id, username, email, password, role, status, created_at, updated_at) " +
          "VALUES (?,?,?,?,?,?,NOW(),NOW())";

        if (emailExists(email)) {
            LoggerUtil.warning("Email already exists: " + email);
            return false;
        }

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, UUID.randomUUID().toString());
            ps.setString(2, username);
            ps.setString(3, email);
            ps.setString(4, password);
            ps.setString(5, role.name());
            ps.setString(6, "ACTIVE");

            int result = ps.executeUpdate();
            if (result > 0) {
                LoggerUtil.info("User registered: " + username);
                return true;
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error adding user", e);
        }
        return false;
    }

    // ============ READ ============

    /**
     * Check if email exists
     */
    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM usertable WHERE email = ? LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error checking email", e);
        }
        return false;
    }

    /**
     * Check if username exists
     */
    public boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM usertable WHERE username = ? LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error checking username", e);
        }
        return false;
    }

    /**
     * Check user login credentials
     */
    public boolean checkUserLogin(String email, String password) {
        String sql = "SELECT password FROM usertable WHERE email = ? LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String dbPassword = rs.getString("password");
                    return dbPassword.equals(password);
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error checking login", e);
        }
        return false;
    }

    /**
     * Create user session
     */
    public UserSession createUserSession(String email) {
        String sql = "SELECT id, username, email, role FROM usertable WHERE email = ? LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String roleString = rs.getString("role");
                    Role role = Role.valueOf(roleString);
                    return new UserSession(
                      rs.getString("id"),
                      rs.getString("username"),
                      rs.getString("email"),
                      role
                    );
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error creating user session", e);
        }
        return null;
    }

    /**
     * Get user profile
     */
    public UserProfileDTO getUserProfile(String username) {
        String sql = "SELECT * FROM usertable WHERE username = ? LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new UserProfileDTO(
                      rs.getString("username"),
                      rs.getString("email"),
                      rs.getString("phone"),
                      rs.getString("address"),
                      rs.getString("full_name"),
                      rs.getString("avatar"),
                      rs.getString("bio"),
                      rs.getString("role"),
                      rs.getString("status"),
                      rs.getDouble("balance"),
                      rs.getDouble("total_spent"),
                      rs.getDouble("total_earned"),
                      rs.getInt("total_bids"),
                      rs.getInt("total_auctions"),
                      rs.getDouble("seller_rating"),
                      rs.getInt("seller_reviews"),
                      rs.getTimestamp("created_at").toInstant(),
                      rs.getTimestamp("updated_at").toInstant()
                    );
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error getting user profile", e);
        }
        return null;
    }

    // ============ UPDATE ============

    /**
     * Update user profile
     */
    public void updateUserProfile(String username, String phone, String address,
                                  String fullName, String bio, String avatar) {
        String sql = "UPDATE usertable SET phone=?, address=?, full_name=?, bio=?, avatar=?, updated_at=NOW() WHERE username=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            ps.setString(2, address);
            ps.setString(3, fullName);
            ps.setString(4, bio);
            ps.setString(5, avatar);
            ps.setString(6, username);
            ps.executeUpdate();
            LoggerUtil.info("Profile updated: " + username);
        } catch (SQLException e) {
            LoggerUtil.error("Error updating profile", e);
        }
    }

    /**
     * Update balance
     */
    public void updateBalance(String username, double newBalance) {
        String sql = "UPDATE usertable SET balance=?, updated_at=NOW() WHERE username=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newBalance);
            ps.setString(2, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            LoggerUtil.error("Error updating balance", e);
        }
    }

    /**
     * Register as seller
     */
    public void registerAsSeller(String username, String shopName, String shopDesc, String shopImage) {
        String sql = "UPDATE usertable SET shop_name=?, shop_description=?, shop_image=?, seller_rating=5.0, updated_at=NOW() WHERE username=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, shopName);
            ps.setString(2, shopDesc);
            ps.setString(3, shopImage);
            ps.setString(4, username);
            ps.executeUpdate();
            LoggerUtil.info("Seller registered: " + username);
        } catch (SQLException e) {
            LoggerUtil.error("Error registering seller", e);
        }
    }

    /**
     * Ban user
     */
    public void banUser(String username) {
        String sql = "UPDATE usertable SET status='BANNED' WHERE username=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.executeUpdate();
            LoggerUtil.info("User banned: " + username);
        } catch (SQLException e) {
            LoggerUtil.error("Error banning user", e);
        }
    }
}