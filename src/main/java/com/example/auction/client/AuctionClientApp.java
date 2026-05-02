package com.example.auction.client;

import com.example.auction.client.view.SceneManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
// Đây sẽ là nơi load file giao diện (FXML) đầu tiên (như màn hình đăng nhập)
public class AuctionClientApp extends Application {
    public void start(Stage stage) throws IOException {
        // FXMLLoader - Tải file FXML (giao diện XML)
        // getResource() - tìm file trong thư mục resources của dự án
        // Trỏ đường dẫn đến file fxml trong resources
        FXMLLoader fxmlLoader = new FXMLLoader(AuctionClientApp.class.getResource("/com/example/auction/client/view/signup-view.fxml"));
        // Tạo Scene (Cảnh) chứa giao diện, kích thước 320x240
        Scene scene = new Scene(fxmlLoader.load());
        // Cài đặt Stage (Sân khấu/Cửa sổ ứng dụng)
        // SceneManager - Lớp quản lý scenes (để dễ chuyển đổi giữa các màn hình)
        // getInstance() - Singleton pattern (chỉ có 1 instance duy nhất)
        // setStage() - lưu tham chiếu đến cửa sổ chính
        SceneManager.getInstance().setStage(stage);
        SceneManager.getInstance().changeToScene("mainscreen2.fxml");
        stage.setTitle("Phần mềm đấu giá - Team 10");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();
    }
}
