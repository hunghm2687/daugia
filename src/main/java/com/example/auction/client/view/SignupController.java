package com.example.auction.client.view;

import com.example.auction.client.AppContext;
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
import javafx.scene.control.Label;
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
    Button signupBtn;
    @FXML
    Button backBtn;
    @FXML
    private Label messageLabel;

    @FXML
    public void initialize() {
        BooleanBinding notEmpty = usernameTF.textProperty().isEmpty();
        BooleanBinding checkPass = passwordF.textProperty().length().lessThan(6);
        BooleanBinding checkEmail = Bindings.createBooleanBinding(() ->
            emailTF.getText().contains("@") && emailTF.getText().contains("."),
          emailTF.textProperty());

        // Nút sign up chỉ bật khi: username không trống AND pass >= 6 ký tự AND email hợp lệ
        signupBtn.disableProperty().bind(notEmpty.or(checkPass).or(checkEmail.not()));
        // SET ACTION
        signupBtn.setOnAction(e -> {
            System.out.println("🔍 DEBUG: signupBtn clicked!");
            handleSignUp();
        });
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
    //    @FXML
//    public void back(){
//        try {
//            SceneManager.getInstance().changeScene("mainscreen2.fxml");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
    @FXML
    public void handleSignUp() {
        String username = usernameTF.getText().trim();
        String password = passwordF.getText().trim();
        String email = emailTF.getText().trim();

        // Thread để gửi request
        Thread thread = new Thread(() -> {
            try {
                synchronized (AppContext.getInstance().getOut()) {
                    ObjectOutputStream out = AppContext.getInstance().getOut();
                    ObjectInputStream in = AppContext.getInstance().getIn();

                    UserDTO userDTO = new UserDTO(
                      username, password, email, Role.MEMBER.name()
                    );

                    MessageProtocol request = new MessageProtocol(
                      "SIGNUP", userDTO, null, null
                    );

                    out.writeObject(request);
                    out.flush();

                    MessageProtocol response = (MessageProtocol) in.readObject();

                    Platform.runLater(() -> {
                        if ("SUCCESS".equals(response.status())) {
                            messageLabel.setText("Đăng ký thành công! Vui lòng đăng nhập.");

                            try {
                                Thread.sleep(1500);
                                SceneManager.getInstance().changeScene("login-view.fxml");
                            } catch (InterruptedException | IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            messageLabel.setText(response.message());
                            passwordF.clear();
                        }
                    });
                }
            } catch (Exception e) {
                Platform.runLater(() ->
                  messageLabel.setText("Lỗi kết nối server!")
                );
                e.printStackTrace();
            }
        });

        thread.setDaemon(true);
        thread.start();
    }
}