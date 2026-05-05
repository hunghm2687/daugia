package com.example.auction.client;

import com.example.auction.client.view.SceneManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

// Đây sẽ là nơi load file giao diện (FXML) đầu tiên (như màn hình đăng nhập)
public class AuctionClientApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Init singleton
        SceneManager.getInstance().setPrimaryStage(stage);

        // Connect to server
        try {
            AppContext.getInstance().connectToServer("127.0.0.1", 5000);
        } catch (Exception e) {
            System.err.println("Cannot connect to server!");
            e.printStackTrace();
            return;
        }

        // Load main screen
        FXMLLoader loader = new FXMLLoader(
          getClass().getResource("/com/example/auction/client/view/mainscreen2.fxml")
        );
        Scene scene = new Scene(loader.load(), 1200, 700);

        stage.setTitle("Phần mềm Đấu giá - Team 10");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setResizable(false);

        // Cleanup on exit
        stage.setOnCloseRequest(e -> {
            AppContext.getInstance().logout();
            AppContext.getInstance().closeConnection();
            System.exit(0);
        });

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
