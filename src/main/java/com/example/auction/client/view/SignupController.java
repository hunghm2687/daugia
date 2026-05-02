package com.example.auction.client.view;

import com.example.auction.server.ClientSession;
import com.example.auction.server.UserSession;
import com.example.auction.shared.dto.MessageProtocol;
import com.example.auction.shared.dto.UserDTO;
import com.example.auction.shared.entity.Role;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SignupController {
    @FXML
    TextField usernameTF;
    @FXML
    TextField passwordF;
    @FXML
    TextField emailTF;
    @FXML
    Button signUpButton;

    public void initialize() {
        BooleanBinding notEmpty = usernameTF.textProperty().isEmpty();
        BooleanBinding checkPass = passwordF.textProperty().length().lessThan(6);
        BooleanBinding checkEmail = Bindings.createBooleanBinding(() ->
                        emailTF.getText().contains("@") && emailTF.getText().contains("."),
                emailTF.textProperty());

        // Nút sign up chỉ bật khi: username không trống AND pass >= 6 ký tự AND email hợp lệ
        signUpButton.disableProperty().bind(notEmpty.or(checkPass).or(checkEmail.not()));
    }
    @FXML
    public void back(){
        try {
            SceneManager.getInstance().changeToScene("mainscreen2.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    public void signUp(ActionEvent event) {
        // Thread để gửi request
        Thread thread = new Thread(() -> {
            try (Socket socket = new Socket("127.0.0.1", 5000)) { // Mở socket kết nối tới server
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                String username = usernameTF.getText().trim();
                String password = passwordF.getText().trim();
                String email = emailTF.getText().trim();

                // Tạo UserDTO để đăng ký
                UserDTO userDTO = new UserDTO(
                  usernameTF.getText().trim(),     // username
                  passwordF.getText().trim(),      // password
                  emailTF.getText().trim(),        // email
                  Role.GUEST.name(),                      // role (mặc định GUEST)
                  "SIGN UP"                        // requestType
                );

                MessageProtocol signupRequest = new MessageProtocol(
                  "SIGNUP",               // type
                  userDTO,                // data
                  null,                   // status (null for request)
                  null                    // message (null for request)
                );

                out.writeObject(signupRequest);
                out.flush();

                MessageProtocol response = (MessageProtocol) in.readObject();

                System.out.println("Response received: " + response.type() +
                  " status=" + response.status());

                Platform.runLater(() -> {
                    try {
                        // Check status
                        if ("SUCCESS".equals(response.status())) {
                            // Signup thành công
                            System.out.println("Signup SUCCESS");

                            // Chuyển tới trang login
                            SceneManager.getInstance().changeToScene("login-view.fxml");
                        } else if ("ERROR".equals(response.status())) {
                            // Signup thất bại
                            System.out.println("Signup FAILED");

                            // Xóa form
                            usernameTF.clear();
                            passwordF.clear();
                            emailTF.clear();

                            // Show error message (TODO: thêm Label notification)
                            System.out.println("Error: " + response.message());
                        }
                    } catch (IOException e) {
                        System.out.println("Lỗi chuyển cảnh");
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }
}