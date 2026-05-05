package com.example.auction.server.service;

/**
 * UserService - Business logic cho users
 */

public class UserService {
  public boolean isValidPassword(String password) {
    return password != null && password.length() >= 6;
  }

  public boolean isValidEmail(String email) {
    return email != null && email.contains("@");
  }
}
