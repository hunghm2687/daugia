package com.example.auction.dao;

import com.example.auction.server.UserSession;
import com.example.auction.shared.dto.UserDTO;
import com.example.auction.shared.entity.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

// UserDAO truy cap bang lien quan den user

// Chi danh cho cac thong tin lien quan den user
public class UserDAO extends BaseDAO {
    private static UserDAO instance;

    private UserDAO() {
    }

    public static UserDAO getInstance() {
        if (instance == null) {
            synchronized (UserDAO.class) {
                if (instance == null) instance = new UserDAO();
            }
        }
        return instance;
    }

    // ktra email đã tồn tại trong database kh
    public boolean emailIsExit(String email) {
        String sql = "SELECT 1 FROM usertable where email = ? LIMIT 1 ";
        try (Connection connection = getConnection();  // lấy kết nối
             PreparedStatement prepareStatement = connection.prepareStatement(sql)) {

            // Set tham số: email = ?
            prepareStatement.setString(1, email);

            // thực thi query
            try (ResultSet rs = prepareStatement.executeQuery()) {
                return rs.next();  // Có row trả về? → true, không có → false
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    // them user vao bang (vao database)
    public void addUser(UserDTO userDTO) {
        String sql = "INSERT INTO usertable (id, email, username, password, role) VALUES (?,?,?,?,?)";

        // Kiểm tra: email chưa tồn tại mới thêm
        if (!emailIsExit(userDTO.email())) {
            System.out.println("  Email not exists, proceeding to INSERT...");
            try (Connection connection = getConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                // xét các tham số
                preparedStatement.setString(1, UUID.randomUUID().toString());
                preparedStatement.setString(2, userDTO.email());
                preparedStatement.setString(3, userDTO.username());
                preparedStatement.setString(4, userDTO.password());
                preparedStatement.setString(5, userDTO.role());

                // thực thi INSERT
                int rows = preparedStatement.executeUpdate();
                System.out.println(" User inserted: " + rows + " row(s)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Email da ton tai");
        }
    }

    // Kiểm tra login (email + password có hợp lệ không)
    public boolean checkUserLogin(UserDTO userDTO) {
        String email = userDTO.email();
        String pass = userDTO.password();
        String sql = "SELECT password FROM usertable where email = ? LIMIT 1 ";
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            // Set email parameter
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Có user với email này
                    String dbPassword = resultSet.getString("password");
                    if (dbPassword.equals(pass)) {
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Tai khoan khong ton tai");
        return false;
    }

    public UserSession createUserSession(String email) {
        String sql = "SELECT id, email, username, role FROM usertable WHERE email = ? LIMIT 1";
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, email);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String roleString = resultSet.getString("role");
                    Role role = Role.valueOf(roleString);  // Convert String → Role enum
                    return new UserSession(
                      resultSet.getString("id"),
                      resultSet.getString("username"),
                      resultSet.getString("email"),
                      role
                    );
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
