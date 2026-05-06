//package com.example.auction.server;
//
//import com.example.auction.server.exception.RequestTypeException;
//import com.example.auction.server.handlers.HandlerFactory;
//import com.example.auction.server.handlers.RequestHandler;
//import com.example.auction.shared.dto.UserDTO;
//
//import java.io.*;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.util.concurrent.Executors;
//
//public class AuctionServer {
//
////    // Xử lý mỗi client connection
////    private void clientHandler(Socket socket) {
////        UserSession currentUserSession = new UserSession();
////        try (var out = new ObjectOutputStream(socket.getOutputStream()); // out trước in tránh deadlock
////             var in = new ObjectInputStream(socket.getInputStream())) { // lý do: để in wait với ObjectInputStream()
////
////            // nhận UserDTO từ client
////            UserDTO currentUserSession = (UserDTO)  in.readObject();
////
////            // lấy handler phù hợp dựa trên requestType
////            // ví duj: requestType = "LOG IN" -> LoginHandler, requestType = "SIGN UP" -> SignupHandler
////            RequestHandler handler = HandlerFactory.getHandler(currentUserSession.requestType());
////
////            // goij handler để xử lý request
////            handler.handle(currentUserSession , out);
////        }catch (IOException e) {
////            System.out.println("Client hoặc server đã ngắt kết nối!");
////        }catch(ClassNotFoundException e) {
////            System.out.println("Lớp không được hỗ trợ!");
////        }catch (RequestTypeException e){
////            System.out.println(e.getMessage());
////        }catch (Exception e) {
////            e.printStackTrace();
////        }
////    }
//    public void main(String[] args) {
//        final int PORT = 5000;
//
//        // VirtualThread executor - mỗi task chạy trên 1 virtual thread
//        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
//            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
//                System.out.println("Server đã mở tại port: " + PORT);
//                System.out.println("Waiting for client connections...");
//
//                // Vòng lặp vô hạn: lắng nghe client connections
//                while (true) {
//                    // serverSocket.accept() - chờ client kết nối
//                    // Khi client kết nối, trả về Socket đại diện cho connection đó
//                    Socket clientSocket = serverSocket.accept();
//                    System.out.println("Đã kết nối tới client: " + clientSocket.getInetAddress());
//                    // ClientSession: Đại diện client connection
//                    ClientSession session = new ClientSession(clientSocket);
//                    ClientManager.getInstance().addClient("001" , session);
//                    // Gửi task xử lý client đến executor (chạy trên virtual thread)
//                    // - Tạo 1 virtual thread mới
//                    // - Chạy session.run() trên thread
//                    // - Mỗi client = 1 thread
//                    // - Threads chạy song song → handle multiple clients
//                    executor.submit(session);
//                    }
//                }
//            catch (IOException e) {
//                System.err.println("Server error: " + e.getMessage());
//            } catch (Exception e) {
//                System.out.println("Client hoặc server đã ngắt kết nối!");
//            }
//        }
//    }
//}
package com.example.auction.server;

import com.example.auction.dao.BaseDAO;
import com.example.auction.server.service.AuctionScheduler;
import com.example.auction.shared.util.LoggerUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * AuctionServer - Main entry point
 */
public class AuctionServer {
    private static final Logger logger = Logger.getLogger("AuctionSystem");
    private static final int PORT = 5000;

    public static void main(String[] args) {
        // Test database connection first
        LoggerUtil.info("🔍 Testing database connection...");
        BaseDAO.testConnection();

        AuctionServer server = new AuctionServer();
        server.start();
    }

    private void start() {
        AuctionScheduler.getInstance().start();
        logger.info("🕐 AuctionScheduler started");

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
             ServerSocket serverSocket = new ServerSocket(PORT)) {

            logger.info("🚀 Auction Server started on port: " + PORT);
            logger.info("⏳ Waiting for client connections...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.info("✅ Client connected: " + clientSocket.getInetAddress());

                ClientSession session = new ClientSession(clientSocket);
                executor.submit(session);
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "❌ Server error", e);
        } finally {
            AuctionScheduler.getInstance().stop();
            logger.info("🛑 Server shutdown");
        }
    }
}