package com.example.auction.client.view;

import com.example.auction.client.service.AuthService;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.io.IOException;

public class LoginController {
    @FXML TextField emailTextField;
    @FXML PasswordField passwordField;
    @FXML Button logInButton;
    @FXML Button backBtn;
    @FXML Label notification;

    @FXML
    public void initialize() {
        System.out.println("LoginController initialized");

        BooleanBinding emailEmpty    = emailTextField.textProperty().isEmpty();
        BooleanBinding passwordShort = passwordField.textProperty().length().lessThan(6);
        logInButton.disableProperty().bind(emailEmpty.or(passwordShort));

        logInButton.setOnAction(e -> logIn());
        backBtn.setOnAction(e -> back());
    }

    @FXML
    private void back() {
        try {
            SceneManager.getInstance().changeScene("mainscreen2.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void logIn() {
        String email    = emailTextField.getText().trim();
        String password = passwordField.getText().trim();

        Thread thread = new Thread(() -> {
            try {
                AuthService.getInstance().login(email, password);

                Platform.runLater(() -> {
                    notification.setText("Đăng nhập thành công!");
                    PauseTransition pause = new PauseTransition(Duration.seconds(1));
                    pause.setOnFinished(evt -> {
                        try {
                            SceneManager.getInstance().changeScene("mainscreen2.fxml");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    pause.play();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    notification.setText(e.getMessage() != null ? e.getMessage() : "Lỗi kết nối server!");
                    passwordField.clear();
                });
                e.printStackTrace();
            }
        });

        thread.setDaemon(true);
        thread.start();
    }
}
