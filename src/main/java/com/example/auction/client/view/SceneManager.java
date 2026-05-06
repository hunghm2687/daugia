package com.example.auction.client.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager { // singleton để chỉ có 1 stage trong ứng dụng
    private static SceneManager instance;
    private Stage primaryStage;
    private static final String FXML_PATH = "/com/example/auction/client/view/";

    private SceneManager() {}

    public static SceneManager getInstance() {
        if (instance == null) {
            synchronized (SceneManager.class) {
                if (instance == null) {
                    instance = new SceneManager(); }
            }
        }
        return instance;
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

//    // chuyển đổi giữa các màn hình
//    public void changeToScene(String fxml) throws IOException {
//        // taoj fmxl và scene mới và thay đổi stage bằng scene mới
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/auction/client/view/"+fxml));
//        Scene scene = new Scene(fxmlLoader.load());
    ////        stage.setScene(scene);
    ////        stage.show();
//    }
//    // MỚI
//    // Load scene đơn giản
    public void changeScene(String fxmlName) throws IOException {
        FXMLLoader loader = new FXMLLoader(
          getClass().getResource(FXML_PATH + fxmlName)
        );
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Load scene với custom size
    public void changeScene(String fxmlName, double width, double height) throws IOException {
        FXMLLoader loader = new FXMLLoader(
          getClass().getResource(FXML_PATH + fxmlName)
        );
        Parent root = loader.load();
        Scene scene = new Scene(root, width, height);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Load scene và lấy controller (để pass data)
    public <T> T loadSceneGetController(String fxmlName) throws IOException {
        FXMLLoader loader = new FXMLLoader(
          getClass().getResource(FXML_PATH + fxmlName)
        );
        loader.load();
        return loader.getController();
    }
}