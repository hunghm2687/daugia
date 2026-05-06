package com.example.auction.shared.dto;

import java.io.Serializable;
import java.time.Instant;
import com.example.auction.shared.entity.Role;

// DTO cho User
// Public: Serialize qua socket được
// No Password: Bảo mật
// Immutable: Chỉ getter de unchange
// Transfer user info từ client -> server

// kh gui user qua socket luon ma gui userdto
// ma userdto kh co password de bao mat

//FLOW:
// Client login:
// 1. User nhập email + password
// 2. LoginController create UserDTO(email, password, ...)
// 3. Send: MessageProtocol("LOGIN", userDTO)
// 4. Server nhận UserDTO
// 5. Convert UserDTO → UserSession
// 6. Process logic với UserSession

// LƯU Ý:
// - Chỉ để transfer (có password!)
// - Server nhận → convert UserSession
// - Session trên server (không dùng UserDTO trong server)

// Record: Tự động generate boilerplate code (getter, constructor, ...)
public record UserDTO(
  String username,
  String password,
  String email,
  String role
) implements Serializable {
  private static final long serialVersionUID = 1L;

}