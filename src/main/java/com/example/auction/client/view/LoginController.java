package com.example.auction.client.view;

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
    Label notification;

    @FXML
    public void initialize() {
        // tạo ràng buộc username kh dc để trống
        BooleanBinding notEmpty = emailTextField.textProperty().isEmpty();

        // tạo ràng buộc password phải ít nhất 6 kí tự
        BooleanBinding checkPass = passwordField.textProperty().length().lessThan(6);

        // nút đăng nhập chỉ dc bấm khi username kh trống và mk phải >=6 kí tự
        logInButton.disableProperty().bind(notEmpty.or(checkPass));
    }
    @FXML
    public void back(){
        try {
            // chuển đến màn hình chính
            SceneManager.getInstance().changeToScene("mainscreen2.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    public void logIn(ActionEvent event) {
        String email = emailTextField.getText().trim();
        String password = passwordField.getText().trim();

        // taạo thread mới ddeer gửi request tới server (kh block UI)
        Thread thread = new Thread(() -> {
            try (Socket socket = new Socket("127.0.0.1", 5000);
                 // stream gửi
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 // stream nhận
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                // Tạo UserDTO để gửi tới server
                UserDTO userDTO = new UserDTO(
                  "",
                  password,                  // password
                  email,                     // email
                  Role.GUEST.name(),         // role (mặc định GUEST)
                  "LOGIN"
                );

                MessageProtocol loginRequest = new MessageProtocol(
                  "LOGIN",                // type
                  userDTO,                // data
                  null,                   // status (null for request)
                  null                    // message (null for request)
                );

                // gửi UserDTO đến server
                out.writeObject(loginRequest);
                out.flush();  // đảm bảo dữ liệu dc gửi ngay lập tức

                MessageProtocol response = (MessageProtocol) in.readObject();

                System.out.println("Response received: " + response.type() +
                  " status=" + response.status());

                // Platform.runLater() - Cập nhật UI từ JavaFX thread, kh thể cập nhật UI từ thread khác
                Platform.runLater(() -> {
                    try {
                        if ("SUCCESS".equals(response.status())) {
                            // TODO: Lưu UserDTO vào session
                            // AuctionClientApp.setCurrentUser((UserDTO) response.data());
                            SceneManager.getInstance().changeToScene("test-view.fxml");
                        } else {
                            notification.setText("Tài khoản mật khẩu không chính xác" + response.message());
                            passwordField.clear();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException | ClassNotFoundException e) {
                Platform.runLater(() -> notification.setText("Lỗi kết nối server!"));
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);  // Daemon thread - ứng dụng sẽ thoát khi main thread thoát
        thread.start();
    }};