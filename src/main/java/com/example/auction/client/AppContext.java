package com.example.auction.client;

import com.example.auction.shared.dto.UserDTO;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * AppContext - Singleton quản lý tài nguyên toàn app
 * - 1 Socket duy nhất
 * - 1 User session
 * - Thread-safe
 */

public class AppContext {
  private static AppContext instance;

  private Socket socket;
  private ObjectOutputStream out;
  private ObjectInputStream in;
  private UserDTO currentUser;

  private AppContext() {}

  public static AppContext getInstance() {
    if (instance == null) {
      synchronized (AppContext.class) {
        if (instance == null) {
          instance = new AppContext();
        }
      }
    }
    return instance;
  }

  // SOCKET
  public void connectToServer(String host, int port) throws Exception {
    if (socket == null || socket.isClosed()) {
      this.socket = new Socket(host, port);
      this.out = new ObjectOutputStream(socket.getOutputStream());
      this.out.flush();
      this.in = new ObjectInputStream(socket.getInputStream());
      System.out.println("Connected to server: " + host + ":" + port);
    }
  }

  public ObjectOutputStream getOut() {
    return out;
  }

  public ObjectInputStream getIn() {
    return in;
  }

  public boolean isConnected() {
    return socket != null && !socket.isClosed();
  }

  public void closeConnection() {
    try {
      if (out != null) out.close();
      if (in != null) in.close();
      if (socket != null) socket.close();
      System.out.println("Disconnected from server");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // USER SESSION
  public void setCurrentUser(UserDTO user) {
    this.currentUser = user;
  }

  public UserDTO getCurrentUser() {
    return currentUser;
  }

  public boolean isLoggedIn() {
    return currentUser != null;
  }

  public void logout() {
    currentUser = null;
  }

  public String getCurrentUsername() {
    return isLoggedIn() ? currentUser.username() : null;
  }
}
