package com.example.auction.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AuctionServer {

    public static void main(String[] args) {
        final int PORT = 5000;
        ExecutorService executor = Executors.newCachedThreadPool();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }));

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server đã mở tại port: " + PORT);
            System.out.println("Waiting for client connections...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Đã kết nối tới client: " + clientSocket.getInetAddress());
                System.out.println("Online now: " + ClientManager.getInstance().getOnlineCount());

                ClientSession session = new ClientSession(clientSocket);
                executor.submit(session);
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}
