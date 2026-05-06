package com.example.auction.client.view;

import com.example.auction.client.service.AuthService;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.io.IOException;

public class SignupController {
    @FXML TextField usernameTF;
    @FXML TextField passwordF;
    @FXML TextField emailTF;
    @FXML Button signupBtn;
    @FXML Button backBtn;
    @FXML private Label messageLabel;

    @FXML
    public void initialize() {
        BooleanBinding notEmpty   = usernameTF.textProperty().isEmpty();
        BooleanBinding checkPass  = passwordF.textProperty().length().lessThan(6);
        BooleanBinding checkEmail = Bindings.createBooleanBinding(
            () -> emailTF.getText().contains("@") && emailTF.getText().contains("."),
            emailTF.textProperty()
        );

        signupBtn.disableProperty().bind(notEmpty.or(checkPass).or(checkEmail.not()));

        signupBtn.setOnAction(e -> handleSignUp());
        backBtn.setOnAction(e -> handleBack());
    }

    @FXML
    private void handleBack() {
        try {
            SceneManager.getInstance().changeScene("mainscreen2.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSignUp() {
        String username = usernameTF.getText().trim();
        String password = passwordF.getText().trim();
        String email    = emailTF.getText().trim();

        Thread thread = new Thread(() -> {
            try {
                AuthService.getInstance().signup(username, password, email);

                Platform.runLater(() -> {
                    messageLabel.setText("Đăng ký thành công! Vui lòng đăng nhập.");
                    PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
                    pause.setOnFinished(evt -> {
                        try {
                            SceneManager.getInstance().changeScene("login-view.fxml");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    pause.play();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    messageLabel.setText(e.getMessage() != null ? e.getMessage() : "Lỗi kết nối server!");
                    passwordF.clear();
                });
                e.printStackTrace();
            }
        });

        thread.setDaemon(true);
        thread.start();
    }
}
