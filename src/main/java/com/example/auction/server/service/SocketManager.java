package com.example.auction.server.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * SocketManager (Server) - Manage server socket
 * - Singleton
 * - Keep 1 socket for server (khác client-side)
 */


public class SocketManager {
  private static SocketManager instance;
  private Socket socket;
  private ObjectOutputStream out;
  private ObjectInputStream in;

  private SocketManager() {};

  public static SocketManager getInstance() {
    if (instance == null) {
      synchronized (SocketManager.class) {
        instance = new SocketManager();
      }
    }
    return instance;
  }

  // hàm kt nối chin, gọi 1 lần khi gọi Main2Controller
  public void connect(String host, int port) throws IOException {
    if (socket == null || socket.isClosed()) {
      this.socket = new Socket(host, port);

      this.out = new ObjectOutputStream(socket.getOutputStream());
      this.out.flush();

      this.in = new ObjectInputStream(socket.getInputStream());
      System.out.println("Đã kết nối tới Server hiện tại " + host + ":" + port);
    }
  }

  public ObjectInputStream getIn() {
    return in;
  }

  public ObjectOutputStream getOut() {
    return out;
  }

  public Socket getSocket() {
    return socket;
  }

  // đóng kết nối khi tắt ứng dụng
  public void close() {
    try {
      if (out != null) {
        out.close();
      }
      if (in != null) {
        in.close();
      }
      if (socket != null) {
        socket.close();
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }
}
