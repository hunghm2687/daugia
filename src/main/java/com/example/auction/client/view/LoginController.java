package com.example.auction.client.view;

import com.example.auction.client.AppContext;
import com.example.auction.client.AuctionClientApp;
import com.example.auction.shared.dto.MessageProtocol;
import com.example.auction.shared.dto.UserDTO;
import com.example.auction.shared.entity.Role;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class LoginController {
    @FXML
    TextField emailTextField;
    @FXML
    PasswordField passwordField;
    @FXML
    Button logInButton;
    @FXML
    Button backBtn;
    @FXML
    Label notification;

//    @FXML
//    public void initialize() {
//        // tạo ràng buộc username kh dc để trống
//        BooleanBinding notEmpty = emailTextField.textProperty().isEmpty();
//
//        // tạo ràng buộc password phải ít nhất 6 kí tự
//        BooleanBinding checkPass = passwordField.textProperty().length().lessThan(6);
//
//        // nút đăng nhập chỉ dc bấm khi username kh trống và mk phải >=6 kí tự
//        logInButton.disableProperty().bind(notEmpty.or(checkPass));
//
//        // Set action cho backBtn
//        backBtn.setOnAction(e -> back());
//    }

    //    @FXML
//    public void back(){
//        try {
//            // chuển đến màn hình chính
//            SceneManager.getInstance().changeToScene("mainscreen2.fxml");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
    @FXML
    public void initialize() {
        System.out.println("LoginController initialized");

        // Validation
        BooleanBinding emailEmpty = emailTextField.textProperty().isEmpty();
        BooleanBinding passwordShort = passwordField.textProperty().length().lessThan(6);

        // Button disable nếu validation fail
        logInButton.disableProperty().bind(emailEmpty.or(passwordShort));

        // SET ACTION TRONG initialize (KHÔNG dùng onAction trong FXML)
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

    // Private method - không cần public
    @FXML
    private void logIn() {
        String email = emailTextField.getText().trim();
        String password = passwordField.getText().trim();

        Thread thread = new Thread(() -> {
            try {
                synchronized (AppContext.getInstance().getOut()) {
                    ObjectOutputStream out = AppContext.getInstance().getOut();
                    ObjectInputStream in = AppContext.getInstance().getIn();

                    UserDTO userDTO = new UserDTO(
                      "", password, email, Role.GUEST.name()
                    );

                    MessageProtocol request = new MessageProtocol(
                      "LOGIN", userDTO, null, null
                    );

                    out.writeObject(request);
                    out.flush();

                    MessageProtocol response = (MessageProtocol) in.readObject();

                    System.out.println("Response: " + response.type() + " - " + response.status());

                    Platform.runLater(() -> {
                        if ("SUCCESS".equals(response.status())) {
                            UserDTO user = (UserDTO) response.data();
                            AppContext.getInstance().setCurrentUser(user);

                            notification.setText("Đăng nhập thành công!");

                            try {
                                Thread.sleep(1000);
                                SceneManager.getInstance().changeScene("mainscreen2.fxml");
                            } catch (InterruptedException | IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            notification.setText( response.message());
                            passwordField.clear();
                        }
                    });
                }
            } catch (Exception e) {
                Platform.runLater(() ->
                  notification.setText("Lỗi kết nối server!")
                );
                e.printStackTrace();
            }
        });

        thread.setDaemon(true);
        thread.start();
    }
}