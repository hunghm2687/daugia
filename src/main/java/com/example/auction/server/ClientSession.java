package com.example.auction.server;


// ClientSession - ĐẠI DIỆN CHO 1 CLIENT CONNECTION

import com.example.auction.server.handlers.HandlerFactory;
import com.example.auction.shared.dto.MessageProtocol;
import com.example.auction.shared.dto.UserDTO;
import com.example.auction.shared.entity.Role;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Vấn đề:
 * - 100 clients online = 100 connections
 * - Server phải track state của mỗi client
 * - Mỗi client gửi multiple messages (không phải 1 request)
 * - ObjectInputStream/ObjectOutputStream cần setup phức tạp
 *
 * Giải pháp: ClientSession
 * - Mỗi client → 1 ClientSession instance
 * - Implement Runnable → chạy trên thread
 * - run() = vòng lặp: đọc messages → xử lý → gửi response
 * - Track state: currentUser, connected, socket, streams
 *
 * Ví dụ flow:
 * 1. Server accept client → new ClientSession(socket)
 * 2. executor.submit(session) → Chạy trên thread
 * 3. session.run():
 *    ├─ while (connected):
 *    │  ├─ readObject() → chặp chờ message
 *    │  ├─ handleMessage() → process
 *    │  └─ Gửi response
 *    ├─ EOFException → client close
 *    └─ disconnect() → cleanup
 * STREAM INITIALIZATION ORDER: OUTPUT TRƯỚC INPUT!
 *
 * ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
 * ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
 * như này sẽ bị deadlock
 *
 * Vấn đề: Deadlock!
 * - ObjectInputStream constructor chờ stream header từ output stream
 * - Nếu output stream chưa tạo → chờ vô hạn
 *
 * ĐÚNG:
 * ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
 * out.flush();  ← Gửi stream header
 * ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
 *
 * Output trước → tạo stream header
 * Input sau → nhận stream header → không deadlock
 * */
public class ClientSession implements Runnable {
  private Socket socket;
  private ObjectInputStream in;
  private ObjectOutputStream out;
  private UserSession currentUserSession; // user hiện tại (null nếu ch login)
  private boolean connected = true;

  public ClientSession(Socket socket) {
    this.socket = socket;

    try {
      this.out = new ObjectOutputStream(socket.getOutputStream());
      this.out.flush();  // gửi header

      // in sau, nh header
      this.in = new ObjectInputStream(socket.getInputStream());

      System.out.println("ClientSession created: " + socket.getInetAddress());
    }
    catch (IOException e) {
      e.printStackTrace();
      connected = false;
    }
  }

  @Override
  public void run() {
     try {
       // chặp chờ message từ client
       while (connected) {
         // readObject() -> chặp chờ message
         // khi client gửi -> gửi về
         MessageProtocol msg = (MessageProtocol) in.readObject();

         String user = currentUserSession != null ? currentUserSession.getUserName() : "anonymous";
         System.out.println("Message from " + user + ": " + msg.type());

         // xử lý message
         handleMessage(msg);
       }
     }
     catch (EOFException e) {
       // client close connection -> EOF
       System.out.println("Client disconnected");
     }
     catch (IOException | ClassNotFoundException e) {
       System.out.println("Error: " + e.getMessage());
     }
     finally {
       // cleanup: đóng connection, remove từ ClientManager
       disconnect();
     }
  }

  /**
   * handleMessage - Convert UserDTO → UserSession
   *
   * CHANGE:
   * 1. Receive MessageProtocol
   * 2. Cast data → UserDTO (from network)
   * 3. If type LOGIN/SIGNUP → convert UserDTO → UserSession
   * 4. Pass UserSession tới handler
   */

  private void handleMessage(MessageProtocol msg) {
    try {
      // get data từ message
      Object data =  msg.data();

      // Convert UserDTO → UserSession (nếu là login/signup)
      if ("LOGIN".equals(msg.type()) || "SIGUP".equals(msg.type())) {
        UserDTO userDTO = (UserDTO) data;

        Role role = Role.valueOf(userDTO.role());  // "GUEST" → Role.GUEST

        // CONVERT: UserDTO → UserSession
        // Tạm thời để UserSession trống (chưa có id từ DB)
        UserSession tempSession = new UserSession(
          "temp_id",              // Placeholder, sẽ update từ DB
          userDTO.username(),
          userDTO.email(),
          role
        );

        // Pass UserSession tới handler
        // Handler sẽ kiểm tra database, tạo lại UserSession với id đúng
        var handler = HandlerFactory.getHandler(msg.type());
        handler.handle(this, msg, tempSession);  // ← Pass UserSession
      }
      else {
        // Handlers khác (BID, CREATE_AUCTION, ...)
        var handler = HandlerFactory.getHandler(msg.type());
        handler.handle(this, msg, currentUserSession);
      }
    }
    catch (IllegalArgumentException e) {
      // Handler kh tồn tại
      sendMessage(new MessageProtocol(
        msg.type(),
        null,
        "ERROR",
        "Message type kh hỗ trợ: " + msg.type()
      ));
    }
    catch (Exception e) {
      sendMessage(new MessageProtocol(
        msg.type(),
        null,
        "ERROR",
        "Lỗi xử lý: " + e.getMessage()
      ));
    }
  }

  /**
   * sendMessage - Gửi message tới client
   *
   * Input: MessageProtocol message
   *
   * Synchronized:
   * - Nhiều threads có thể gọi sendMessage() cùng lúc
   * - ObjectOutputStream không thread-safe
   * - synchronized block → chỉ 1 thread ghi vào stream lúc một
   *
   * Scenario mà cần synchronized:
   * - BidHandler.handle() gọi: sendMessage(bidResponse)
   * - Cùng lúc, ClientManager.broadcast() gọi: session.sendMessage(update)
   * - Cả hai cố ghi vào ObjectOutputStream
   * - Nếu không synchronized → dữ liệu corrupt!
   *
   * Với synchronized:
   * - BidHandler vào synchronized block → ghi → exit
   * - Broadcast chờ, vào synchronized block → ghi → exit
   * - Dữ liệu an toàn
   */
  public synchronized void sendMessage(MessageProtocol message) {
    try {
      out.writeObject(message);
      out.flush();
      System.out.println("Sent: " + message.type());
    }
    catch (IOException e) {
      System.out.println("Failed to send: " + e.getMessage());
      connected = false;
    }
  }

  /**
   * setCurrentUser - Set user khi login
   *
   * Gọi từ: LoginHandler.handle()
   * Ví dụ: setCurrentUser(userDTO)
   *
   * Mục đích:
   * - Track user của session này
   * - Validation: Bidder phải là currentUser
   * - ClientManager.removeClient() sử dụng currentUser
   *
   * Vận dụng:
   * - LoginHandler:
   *   if (validate login OK):
   *     session.setCurrentUser(userDTO)
   *     ClientManager.addClient(userDTO.id(), this)
   */
  public void setCurrentUserSession(UserSession userSession) {
    this.currentUserSession = userSession;
  }

  public UserSession getCurrentUserSession() {
    return currentUserSession;
  }

  /**
   * disconnect - Đóng connection
   *
   * Gọi khi:
   * - run() exit (EOFException, IOException)
   * - User logout
   * - Timeout (not implemented yet)
   *
   * Cleanup:
   * 1. Đóng streams
   * 2. Đóng socket
   * 3. Remove từ ClientManager
   *
   * Vận dụng:
   * - run() finally block
   *   finally { disconnect(); }
   * - User logout request
   *   handler gọi: session.disconnect()
   */
  public void disconnect() {
    if (!connected) return; // đã disconnect r

    connected = false;

    try {
      if (out != null ) {
        out.close();
      }
      if (in != null) {
        in.close();
      }
      if (socket != null) {
        socket.close();
      }

      System.out.println("🔌 Connection closed: " + socket.getInetAddress());

      // Nếu user đã login, remove khỏi online list
      // ClientManager sẽ tự động broadcast "user offline"
      if (currentUserSession != null) {
        ClientManager.getInstance().removeClient(currentUserSession.getId()); // sau cần phải sửa sang Long vì đang để String
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * isConnected - Kiểm tra connection còn active
   *
   * Vận dụng:
   * - ClientManager.sendTo(): if (session.isConnected()) { sendMessage(); }
   * - ClientManager.broadcast(): if (session.isConnected()) { sendMessage(); }
   */
  public boolean isConnected() {
    return connected;
  }
}
