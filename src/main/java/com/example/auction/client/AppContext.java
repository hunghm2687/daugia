package com.example.auction.client;

import com.example.auction.shared.dto.MessageProtocol;
import com.example.auction.shared.dto.UserDTO;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * AppContext - Singleton quản lý tài nguyên toàn app
 * - 1 Socket duy nhất
 * - 1 User session
 * - Thread-safe sendAndReceive
 */
public class AppContext {
  private static AppContext instance;

  private Socket socket;
  private ObjectOutputStream out;
  private ObjectInputStream in;
  private UserDTO currentUser;

  private static final int CONNECT_TIMEOUT_MS = 10_000;
  private static final int READ_TIMEOUT_MS    = 30_000;

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
      socket = new Socket();
      socket.setSoTimeout(READ_TIMEOUT_MS);
      socket.connect(new InetSocketAddress(host, port), CONNECT_TIMEOUT_MS);

      out = new ObjectOutputStream(socket.getOutputStream());
      out.flush();
      in  = new ObjectInputStream(socket.getInputStream());
      System.out.println("Connected to server: " + host + ":" + port);
    }
  }

  /**
   * Thread-safe request/response over the single socket.
   * All controllers must use this method instead of accessing out/in directly.
   */
  public synchronized MessageProtocol sendAndReceive(MessageProtocol request) throws Exception {
    out.writeObject(request);
    out.flush();
    return (MessageProtocol) in.readObject();
  }

  public boolean isConnected() {
    return socket != null && !socket.isClosed();
  }

  public void closeConnection() {
    try {
      if (out    != null) out.close();
      if (in     != null) in.close();
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

  public void clearCurrentUser() {
    currentUser = null;
  }

  /** @deprecated use clearCurrentUser() */
  public void logout() {
    clearCurrentUser();
  }

  public String getCurrentUsername() {
    return isLoggedIn() ? currentUser.username() : null;
  }
}
