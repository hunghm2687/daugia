package com.example.auction.server;


import com.example.auction.shared.dto.MessageProtocol;
import com.example.auction.shared.dto.UserDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ClientManager - CORE CỦA REAL-TIME SYSTEM
 *
 * Mục đích: Quản lý tất cả client connections đang online
 *
 * Tại sao singleton?
 * - Chỉ 1 ClientManager trong toàn bộ server
 * - Tất cả threads (client sessions) cần access nó
 * - Đảm bảo chỉ 1 source of truth
 * - broadcast(message) → gửi tất cả
 * - sendTo(userId, message) → gửi 1 user
 *
 * Ví dụ:
 * - Khi Client 1 đặt giá: ClientManager broadcast cho Client 2, 3, ...
 * - Khi Client 1 logout: ClientManager remove nó, thông báo others
 * - Khi GET online users: ClientManager trả danh sách
 */
public class ClientManager {
  // phải laf Singleton để onlineClients = 1 map duy nhất, tất cả broadcast từ map này
  private static ClientManager instance;

  // Map: userId → ClientSession
  // ConcurrentHashMap vì multiple threads access cùng lúc
  // kh bij race condition vì dc tự động synchronized
  private Map<String, ClientSession> onlineClients = new ConcurrentHashMap<>();

  private ClientManager() {};

  public static ClientManager getInstance() {
    if (instance == null) {
      synchronized (ClientManager.class) {
        if (instance == null) {
          instance = new ClientManager();
        }
      }
    }
    return instance;
  }

  // CORE METHODS


  /**
   * Thêm client vào danh sách online
   *
   * Gọi khi: User login thành công
   * Ví dụ: addClient("user123", sessionObject)
   *
   * Sau:
   * - onlineClients = {"user123": sessionObject}
   * - Broadcast: "user123 vừa online"
   */
  // addClient - THÊM CLIENT ONLINE
  public void addClient(String userId, ClientSession session) {
    onlineClients.put(userId, session);

    System.out.println(userId + " online (Total: " + onlineClients.size() + ")");

    // broadcast: thông báo tất car clients có user mới login
    MessageProtocol notifyMsg = new MessageProtocol(
      "USER_ONLINE", // type: User online event
      null, // data: null (không cần data)
      "INFO", // status: INFO (notification)
      userId + " vừa online"  // message: Thông báo
    );

    // gửi tất cả trừ user này
    broadcastExcept(userId, notifyMsg);
  }

  // removeClient - XÓA CLIENT (DISCONNECT/LOGOUT)
  /**
   * Xóa client khỏi online list
   *
   * Gọi khi:
   * - User disconnect (socket close)
   * - User logout (explicit)
   *
   * Ví dụ: ClientManager.getInstance().removeClient("user123")
   *
   * Flow:
   * 1. clients.remove(userId) → Remove khỏi map
   * 2. Log info
   * 3. Broadcast "user offline" notification → all
   *
   * Tại sao broadcast?
   * - Các users khác cần biết user này offline
   * - Update UI: "Online count: 4" → "Online count: 3"
   * - Show "user123 offline" notification
   *
   * Vận dụng:
   * - ClientSession.disconnect()
   *   └─ Khi client close socket
   *      └─ if (currentUser != null)
   *      └─ ClientManager.removeClient(currentUser.id())
   *      └─ Method này tự động broadcast
   */
  public void removeClient(String userId) {
    onlineClients.remove(userId);

    System.out.println(userId + " offline (Total: " + onlineClients.size() + ")");

    // broadcast cho tất cả tương tự với lúc online
    MessageProtocol notifyMsg = new MessageProtocol(
      "USER_OFFLINE",          // type: User offline event
      null,                    // data: null
      "INFO",                  // status: INFO
      userId + " vừa offline"  // message: Thông báo
    );

    broadcast(notifyMsg);  // Gửi TỨC CẢ
  }

  // sendTo - GỬI MESSAGE TỚI 1 CLIENT CỤ THỂ
  /**
   * Gửi message tới 1 user cụ thể
   *
   * Gọi khi: Cần response riêng cho user (login, bid result)
   * Ví dụ: ClientManager.getInstance().sendTo("user123", responseMsg)
   *
   * Flow:
   * 1. Lấy ClientSession của user
   * 2. Kiểm tra user online + connected
   * 3. Gửi message
   *
   * Vận dụng:
   * - LoginHandler.handle()
   *   └─ if (login OK): sendTo(userId, success response)
   *   └─ if (login fail): sendTo(userId, error response)
   * - BidHandler.handle()
   *   └─ if (bid valid): sendTo(userId, success response)
   *   └─ if (bid invalid): sendTo(userId, error response)
   *
   * Tại sao cần sendTo?
   * - Response là riêng cho user
   * - Không cần broadcast (chỉ user đó cần biết)
   * - Ví dụ: Login fail message chỉ gửi user đó, không gửi others
   */

  public void sendTo(String userId, MessageProtocol message) {
    ClientSession session = onlineClients.get(userId);

    if (session != null && session.isConnected()) {
      session.sendMessage(message);
      System.out.println("Sent to " + userId + ": " + message.type());
    }
    else {
      System.out.println("User " + userId + " not online or disconnected");
    }
  }

  // broadcast - GỬI MESSAGE TỚI TẤT CẢ CLIENTS
  /**
   * BROADCAST message tới tất cả online clients
   *
   * Gọi khi: Sự kiện cần thông báo tất cả
   * Ví dụ: ClientManager.getInstance().broadcast(newBidMsg)
   *
   * Flow:
   * 1. Loop qua tất cả online clients
   * 2. Kiểm tra mỗi client connected
   * 3. Gửi message
   *
   * Vận dụng:
   * - BidHandler.handle()
   *   └─ Sau khi save bid vào DB
   *      └─ broadcast(new MessageProtocol("NEW_BID", bidDTO, ...))
   *      └─ Tất cả users nhận update bid mới
   * - Khi user online/offline
   *   └─ broadcast(userOnlineMsg) / broadcast(userOfflineMsg)
   *
   * Lợi ích:
   * - Real-time sync: Tất cả users thấy update đồng thời
   * - User1 bid 500 → User2, User3, User4 update UI ngay
   * - Không cần polling (chờ + request update)
   *
   * Ví dụ scenario:
   * 1. User1 bid 500 tại Auction#1
   * 2. BidHandler.handle() process
   * 3. Save database: auction.currentBid = 500
   * 4. broadcast("NEW_BID", bidDTO(amount=500))
   * 5. Server gửi message tới ClientSession1, Session2, Session3, ...
   * 6. Tất cả clients nhận → deserialize MessageProtocol
   * 7. Tất cả clients thấy "NEW_BID" type → update UI
   * 8. All UIs show: Current bid = 500
   * 9. REAL-TIME SYNC!
   */
  public void broadcast(MessageProtocol message) {
    System.out.println("BROADCAST: " + message.type() +
      " → " + onlineClients.size() + " clients");

    for (ClientSession session : onlineClients.values()) {
      // Kiểm tra session còn connected không
      if (session.isConnected()) {
        session.sendMessage(message);
      }
    }
  }

  // broadcastExcept - BROADCAST TỚI TẤT CẢ TRỪ 1 USER
  /**
   * BROADCAST tới tất cả clients NGOẠI TRỪ user chỉ định
   *
   * Gọi khi: User action, không cần feedback lại cho user
   * Ví dụ: broadcastExcept("user1", userOnlineMsg)
   *
   * Tại sao?
   * - User1 biết mình online rồi (LoginHandler gửi response riêng)
   * - Không cần broadcast "user1 online" lại cho user1
   * - Chỉ gửi cho user2, user3, ... để họ biết
   *
   * Flow:
   * 1. Loop qua tất cả clients
   * 2. Skip user nếu userId == exceptUserId
   * 3. Gửi message
   *
   * Vận dụng:
   * - ClientManager.addClient()
   *   └─ broadcastExcept(userId, "user online")
   *   └─ Gửi tất cả TRỪ user đó
   * - LoginHandler
   *   └─ sendTo(userId, "login success") - riêng cho user
   *   └─ broadcastExcept(userId, "user online") - cho others
   *
   * Scenario:
   * 1. User1 login
   * 2. Server: sendTo("user1", LoginSuccess)
   *    → User1 nhận "login success" response
   * 3. Server: broadcastExcept("user1", "user1 online")
   *    → User2, User3 nhận "user1 online" notification
   *    → User1 KHÔNG nhận (except)
   */
  public void broadcastExcept(String exceptUserId, MessageProtocol message) {
    System.out.println("BROADCAST (except " + exceptUserId + "): " + message.type());

    for (Map.Entry<String, ClientSession> entry : onlineClients.entrySet()) {
      String clientId = entry.getKey();
      ClientSession session = entry.getValue();

      // Skip user này, gửi tất cả others
      if (!clientId.equals(exceptUserId) && session.isConnected()) {
        session.sendMessage(message);
      }
    }
  }

  // getOnlineUsers - LẤY DANH SÁCH ONLINE USERS
  /**
   * Lấy danh sách tất cả users online
   *
   * Gọi khi: Client request danh sách online users
   * Return: List<UserDTO> or empty list
   *
   * Vận dụng:
   * - MainController: Show "3 users online"
   * - Chat: Show list of online users
   * - Broadcast: GET_ONLINE_USERS request
   *   └─ Handler gọi: getOnlineUsers()
   *   └─ Response: MessageProtocol("GET_ONLINE_USERS", userList, ...)
   */
  public List<UserDTO> getOnlineUsers() {
    List<UserDTO> users = new ArrayList<>();

    for (ClientSession session : onlineClients.values()) {
      UserSession userSession = session.getCurrentUserSession();
      if (userSession != null) {
        // Convert UserSession → UserDTO
        UserDTO userDTO = new UserDTO(
          userSession.getUserName(),
          null,  // không gửi password
          userSession.getEmail(),
          userSession.getRole().name(),  // Convert Role enum → String
          "ONLINE"
        );
        users.add(userDTO);
      }
    }
    return users;
  }

  // isOnline - KIỂM TRA USER ONLINE
  /**
   * Kiểm tra user có online không
   *
   * Gọi khi: Validation trước action
   * Return: true if online, false otherwise
   *
   * Vận dụng:
   * - Before sending message: if (isOnline(userId)) { sendTo(...) }
   * - Validation: Bidder phải online
   */
  public boolean isOnline(String userId) {
    ClientSession session = onlineClients.get(userId);
    return session != null && session.isConnected();
  }

  // getOnlineCount - LẤY SỐ USERS ONLINE
  /**
   * Lấy số users online hiện tại
   *
   * Gọi khi: Logging, monitoring
   * Return: int (số clients online)
   *
   * Vận dụng:
   * - Log: System.out.println("Total online: " + getOnlineCount())
   * - UI: Show "5 users online"
   */
  public int getOnlineCount() {
    return onlineClients.size();
  }
}
