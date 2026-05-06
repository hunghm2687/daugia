//package com.example.auction.server.service;
//
///**
// * UserService - Business logic cho users
// */
//
//public class UserService {
//  public boolean isValidPassword(String password) {
//    return password != null && password.length() >= 6;
//  }
//
//  public boolean isValidEmail(String email) {
//    return email != null && email.contains("@");
//  }
//}
package com.example.auction.server.service;

import com.example.auction.dao.UserDAO;
import com.example.auction.shared.util.LoggerUtil;

/**
 * UserService - Validate and process user operations
 */
public class UserService {

  /**
   * Validate password
   */
  public boolean isValidPassword(String password) {
    if (password == null) return false;
    return password.length() >= 6;
  }

  /**
   * Validate email
   */
  public boolean isValidEmail(String email) {
    if (email == null) return false;
    return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
  }

  /**
   * Validate username
   */
  public boolean isValidUsername(String username) {
    if (username == null) return false;
    return username.length() >= 3 && username.length() <= 50;
  }

  /**
   * Register user
   */
  public boolean registerUser(String username, String password, String email) {
    try {
      // Validate inputs
      if (!isValidUsername(username)) {
        LoggerUtil.warning("Invalid username: " + username);
        return false;
      }

      if (!isValidPassword(password)) {
        LoggerUtil.warning("Invalid password");
        return false;
      }

      if (!isValidEmail(email)) {
        LoggerUtil.warning("Invalid email: " + email);
        return false;
      }

      // Check if email exists
      if (UserDAO.getInstance().emailExists(email)) {
        LoggerUtil.warning("Email already exists: " + email);
        return false;
      }

      // Check if username exists
      if (UserDAO.getInstance().usernameExists(username)) {
        LoggerUtil.warning("Username already exists: " + username);
        return false;
      }

      // Register user
      boolean success = UserDAO.getInstance().addUser(
        username, password, email, com.example.auction.shared.entity.Role.MEMBER
      );

      if (success) {
        LoggerUtil.info("✅ User registered: " + username);
      }
      return success;

    } catch (Exception e) {
      LoggerUtil.error("Error registering user", e);
    }
    return false;
  }
}