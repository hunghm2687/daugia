package com.example.auction.server;

import com.example.auction.shared.entity.Role;

import java.io.Serializable;
import java.time.Instant;

/**
 * CÁI NÀY ĐỂ LÀM GÌ?
 * - Đại diện user đã login trên server
 * - Lưu info user: id, username, email, role
 * - Dùng trong server logic (handlers, managers)
 * - KHÔNG gửi qua network
 *
 * VÌ SAO CẦN?
 * - Server cần track user session
 * - Handlers, DAO sử dụng UserSession
 * - Separate từ UserDTO (network transfer)
 *
 * FLOW:
 * Server login:
 * 1. Nhận UserDTO từ network
 * 2. Verify database
 * 3. Create UserSession(id, username, email, role)
 * 4. Set session: session.setCurrentUser(userSession)
 * 5. Track: ClientManager.addClient(userSession.id(), session)
 * 6. Handlers dùng UserSession

 * ⚠️ LƯU Ý:
 * - KHÔNG có password!
 * - Chỉ lưu user info cần thiết
 * - Lưu trong server memory
 * - Session terminated khi disconnect
 *
 * DIFFERENT FROM UserDTO:
 * - UserDTO: gửi qua network, có password
 * - UserSession: server memory, không password
 */

public class UserSession implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id, userName,email;
    private Role role; // "BIDDER" , "ADMIN" , "GUEST"
    private boolean isLoggedIn; // kiem tra dang nhap
    private String requestType; // chi co "LOG IN" hoac "SIGN IN"
    private Instant loginTime;

    public UserSession(String id, String username, String email, Role role) {
        this.id = id;
        this.userName = username;
        this.email = email;
        this.role = role;
        this.loginTime = Instant.now();
        this.isLoggedIn = true;
    }
    public String getId() {
        return id;
    }

    public Role getRole() {
        return role;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public Instant getLoginTime() {
        return loginTime;
    }

    public String getRequestType() {
        return requestType;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "UserSession{" +
          "id='" + id + '\'' +
          ", username='" + userName + '\'' +
          ", email='" + email + '\'' +
          ", role='" + role + '\'' +
          ", loginTime=" + loginTime +
          ", isLoggedIn=" + isLoggedIn +
          '}';
    }

}


