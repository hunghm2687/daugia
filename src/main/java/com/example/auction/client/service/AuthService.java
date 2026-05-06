package com.example.auction.client.service;

import com.example.auction.client.AppContext;
import com.example.auction.shared.dto.MessageProtocol;
import com.example.auction.shared.dto.UserDTO;
import com.example.auction.shared.entity.Role;

/**
 * AuthService - Client-side auth operations (login, signup, logout).
 * Controllers call these methods; no raw socket code in controllers.
 */
public class AuthService {

    private static AuthService instance;

    private AuthService() {}

    public static AuthService getInstance() {
        if (instance == null) {
            synchronized (AuthService.class) {
                if (instance == null) {
                    instance = new AuthService();
                }
            }
        }
        return instance;
    }

    /**
     * Send LOGIN request; returns logged-in UserDTO on success.
     * Throws RuntimeException with server error message on failure.
     */
    public UserDTO login(String email, String password) throws Exception {
        UserDTO requestDTO = new UserDTO("", password, email, Role.GUEST.name(), "LOGIN");
        MessageProtocol request = new MessageProtocol("LOGIN", requestDTO, null, null);

        MessageProtocol response = AppContext.getInstance().sendAndReceive(request);

        if ("SUCCESS".equals(response.status())) {
            UserDTO user = (UserDTO) response.data();
            AppContext.getInstance().setCurrentUser(user);
            return user;
        }
        throw new RuntimeException(response.message());
    }

    /**
     * Send SIGNUP request.
     * Throws RuntimeException with server error message on failure.
     */
    public void signup(String username, String password, String email) throws Exception {
        UserDTO requestDTO = new UserDTO(username, password, email, Role.MEMBER.name(), "SIGNUP");
        MessageProtocol request = new MessageProtocol("SIGNUP", requestDTO, null, null);

        MessageProtocol response = AppContext.getInstance().sendAndReceive(request);

        if (!"SUCCESS".equals(response.status())) {
            throw new RuntimeException(response.message());
        }
    }

    /**
     * Send LOGOUT request to server so it removes this client from ClientManager,
     * then clear the local session.
     */
    public void logout() {
        if (!AppContext.getInstance().isLoggedIn()) return;

        try {
            MessageProtocol request = new MessageProtocol("LOGOUT", null, null, null);
            AppContext.getInstance().sendAndReceive(request);
        } catch (Exception e) {
            System.err.println("Logout request failed (best-effort): " + e.getMessage());
        } finally {
            AppContext.getInstance().clearCurrentUser();
        }
    }
}
