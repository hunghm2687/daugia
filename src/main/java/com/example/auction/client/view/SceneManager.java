package com.example.auction.client.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager { // singleton để chỉ có 1 stage trong ứng dụng
    private static SceneManager instance;
    private Stage stage;
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private SceneManager() {
    }

    public static SceneManager getInstance() {
        if (instance == null) {
            synchronized (SceneManager.class) {
                if (instance == null) instance = new SceneManager();
            }
        }
        return instance;
    }

    // chuyển đổi giữa các màn hình
    public void changeToScene(String fxml) throws IOException {
        // taoj fmxl và scene mới và thay đổi stage bằng scene mới
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/auction/client/view/"+fxml));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }


}
