package com.example.auction.client.view;

import com.example.auction.client.AppContext;
import com.example.auction.shared.dto.MessageProtocol;
import com.example.auction.shared.dto.UserProfileDTO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ProfileController {
  @FXML private ImageView avatarImageView;
  @FXML private Label usernameLabel;
  @FXML private Label emailLabel;
  @FXML private Label statusLabel;
  @FXML private Label balanceLabel;
  @FXML private Label ratingLabel;
  @FXML private Label totalBidsLabel;
  @FXML private Label totalAuctionsLabel;
  @FXML private Label totalSpentLabel;
  @FXML private Label totalEarnedLabel;

  @FXML private TextField phoneField;
  @FXML private TextField addressField;
  @FXML private TextField fullNameField;
  @FXML private TextArea bioField;
  @FXML private TextField avatarUrlField;

  @FXML private Button editBtn;
  @FXML private Button saveBtn;
  @FXML private Button cancelBtn;
  @FXML private Button topUpBtn;
  @FXML private Button becomeSellerBtn;
  @FXML private Button goBackBtn;

  @FXML private Label messageLabel;

  private UserProfileDTO currentProfile;
  private boolean isEditing = false;

  @FXML
  public void initialize() {
    System.out.println("ProfileController initialized");
    loadProfile();
    setupButtons();
  }

  private void loadProfile() {
    Thread thread = new Thread(() -> {
      try {
        synchronized (AppContext.getInstance().getOut()) {
          ObjectOutputStream out = AppContext.getInstance().getOut();
          ObjectInputStream in = AppContext.getInstance().getIn();

          MessageProtocol request = new MessageProtocol(
            "GET_PROFILE", null, null, null
          );
          out.writeObject(request);
          out.flush();

          MessageProtocol response = (MessageProtocol) in.readObject();

          if ("SUCCESS".equals(response.status())) {
            currentProfile = (UserProfileDTO) response.data();
            Platform.runLater(this::displayProfile);
          }
        }
      } catch (Exception e) {
        Platform.runLater(() -> messageLabel.setText("Lỗi tải profile"));
        e.printStackTrace();
      }
    });
    thread.setDaemon(true);
    thread.start();
  }

  private void displayProfile() {
    usernameLabel.setText(currentProfile.username());
    emailLabel.setText(currentProfile.email());
    statusLabel.setText(currentProfile.status());
    balanceLabel.setText(String.format("%.0f đ", currentProfile.balance()));
    ratingLabel.setText(String.format("%.1f ⭐", currentProfile.sellerRating()));
    totalBidsLabel.setText(String.valueOf(currentProfile.totalBids()));
    totalAuctionsLabel.setText(String.valueOf(currentProfile.totalAuctions()));
    totalSpentLabel.setText(String.format("%.0f đ", currentProfile.totalSpent()));
    totalEarnedLabel.setText(String.format("%.0f đ", currentProfile.totalEarned()));

    phoneField.setText(currentProfile.phone() != null ? currentProfile.phone() : "");
    addressField.setText(currentProfile.address() != null ? currentProfile.address() : "");
    fullNameField.setText(currentProfile.fullName() != null ? currentProfile.fullName() : "");
    bioField.setText(currentProfile.bio() != null ? currentProfile.bio() : "");
    avatarUrlField.setText(currentProfile.avatar() != null ? currentProfile.avatar() : "");

    // Load avatar
    if (currentProfile.avatar() != null && !currentProfile.avatar().isEmpty()) {
      try {
        avatarImageView.setImage(new Image(currentProfile.avatar(), true));
        avatarImageView.setFitWidth(150);
        avatarImageView.setFitHeight(150);
        avatarImageView.setPreserveRatio(true);
      } catch (Exception e) {
        System.out.println("Lỗi load avatar");
      }
    }

    // Update button visibility
    if ("MEMBER".equals(currentProfile.role()) && currentProfile.sellerRating() == 0) {
      becomeSellerBtn.setVisible(true);
    }

    disableEditing();
  }

  private void setupButtons() {
    editBtn.setOnAction(e -> enableEditing());
    saveBtn.setOnAction(e -> saveProfile());
    cancelBtn.setOnAction(e -> disableEditing());
    topUpBtn.setOnAction(e -> handleTopUp());
    becomeSellerBtn.setOnAction(e -> handleBecomeSeller());
    goBackBtn.setOnAction(e -> handleGoBack());
  }

  private void enableEditing() {
    isEditing = true;
    phoneField.setEditable(true);
    addressField.setEditable(true);
    fullNameField.setEditable(true);
    bioField.setEditable(true);
    avatarUrlField.setEditable(true);

    editBtn.setVisible(false);
    saveBtn.setVisible(true);
    cancelBtn.setVisible(true);
  }

  private void disableEditing() {
    isEditing = false;
    phoneField.setEditable(false);
    addressField.setEditable(false);
    fullNameField.setEditable(false);
    bioField.setEditable(false);
    avatarUrlField.setEditable(false);

    editBtn.setVisible(true);
    saveBtn.setVisible(false);
    cancelBtn.setVisible(false);
  }

  private void saveProfile() {
    Thread thread = new Thread(() -> {
      try {
        synchronized (AppContext.getInstance().getOut()) {
          ObjectOutputStream out = AppContext.getInstance().getOut();
          ObjectInputStream in = AppContext.getInstance().getIn();

          java.util.Map<String, String> data = new java.util.HashMap<>();
          data.put("phone", phoneField.getText());
          data.put("address", addressField.getText());
          data.put("fullName", fullNameField.getText());
          data.put("bio", bioField.getText());
          data.put("avatar", avatarUrlField.getText());

          MessageProtocol request = new MessageProtocol(
            "UPDATE_PROFILE", data, null, null
          );
          out.writeObject(request);
          out.flush();

          MessageProtocol response = (MessageProtocol) in.readObject();

          Platform.runLater(() -> {
            if ("SUCCESS".equals(response.status())) {
              messageLabel.setText("Cập nhật thành công");
              loadProfile();
            } else {
              messageLabel.setText(response.message());
            }
          });
        }
      } catch (Exception e) {
        Platform.runLater(() -> messageLabel.setText("Lỗi cập nhật"));
        e.printStackTrace();
      }
    });
    thread.setDaemon(true);
    thread.start();
  }

  private void handleTopUp() {
    Dialog<Double> dialog = new Dialog<>();
    dialog.setTitle("Nạp tiền");
    dialog.setHeaderText("Nhập số tiền muốn nạp");

    TextField amountField = new TextField();
    amountField.setPromptText("Nhập số tiền...");

    VBox content = new VBox(10);
    content.setPadding(new Insets(20));
    content.getChildren().add(amountField);
    dialog.getDialogPane().setContent(content);

    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    dialog.setResultConverter(dialogButton -> {
      if (dialogButton == ButtonType.OK) {
        try {
          return Double.parseDouble(amountField.getText());
        } catch (NumberFormatException e) {
          return null;
        }
      }
      return null;
    });

    dialog.showAndWait().ifPresent(amount -> {
      if (amount != null && amount > 0) {
        performTopUp(amount);
      }
    });
  }

  private void performTopUp(Double amount) {
    Thread thread = new Thread(() -> {
      try {
        synchronized (AppContext.getInstance().getOut()) {
          ObjectOutputStream out = AppContext.getInstance().getOut();
          ObjectInputStream in = AppContext.getInstance().getIn();

          MessageProtocol request = new MessageProtocol(
            "TOP_UP_BALANCE", amount, null, null
          );
          out.writeObject(request);
          out.flush();

          MessageProtocol response = (MessageProtocol) in.readObject();

          Platform.runLater(() -> {
            if ("SUCCESS".equals(response.status())) {
              messageLabel.setText("Nạp tiền thành công. Số dư: " + response.data());
              loadProfile();
            } else {
              messageLabel.setText(response.message());
            }
          });
        }
      } catch (Exception e) {
        Platform.runLater(() -> messageLabel.setText("Lỗi nạp tiền"));
        e.printStackTrace();
      }
    });
    thread.setDaemon(true);
    thread.start();
  }

  private void handleBecomeSeller() {
    Dialog<String> dialog = new Dialog<>();
    dialog.setTitle("Đăng ký bán hàng");
    dialog.setHeaderText("Nhập thông tin cửa hàng");

    TextField shopNameField = new TextField();
    shopNameField.setPromptText("Tên cửa hàng...");
    TextArea shopDescField = new TextArea();
    shopDescField.setPromptText("Mô tả cửa hàng...");
    shopDescField.setPrefRowCount(3);
    TextField shopImageField = new TextField();
    shopImageField.setPromptText("URL ảnh cửa hàng...");

    VBox content = new VBox(10);
    content.setPadding(new Insets(20));
    content.getChildren().addAll(
      new Label("Tên cửa hàng:"), shopNameField,
      new Label("Mô tả:"), shopDescField,
      new Label("Ảnh cửa hàng:"), shopImageField
    );
    dialog.getDialogPane().setContent(content);

    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

    dialog.showAndWait().ifPresent(result -> {
      performRegisterSeller(
        shopNameField.getText(),
        shopDescField.getText(),
        shopImageField.getText()
      );
    });
  }

  private void performRegisterSeller(String shopName, String shopDesc, String shopImage) {
    Thread thread = new Thread(() -> {
      try {
        synchronized (AppContext.getInstance().getOut()) {
          ObjectOutputStream out = AppContext.getInstance().getOut();
          ObjectInputStream in = AppContext.getInstance().getIn();

          java.util.Map<String, String> data = new java.util.HashMap<>();
          data.put("shopName", shopName);
          data.put("shopDesc", shopDesc);
          data.put("shopImage", shopImage);

          MessageProtocol request = new MessageProtocol(
            "REGISTER_SELLER", data, null, null
          );
          out.writeObject(request);
          out.flush();

          MessageProtocol response = (MessageProtocol) in.readObject();

          Platform.runLater(() -> {
            if ("SUCCESS".equals(response.status())) {
              messageLabel.setText("Đăng ký bán hàng thành công!");
              loadProfile();
            } else {
              messageLabel.setText(response.message());
            }
          });
        }
      } catch (Exception e) {
        Platform.runLater(() -> messageLabel.setText("Lỗi đăng ký bán hàng"));
        e.printStackTrace();
      }
    });
    thread.setDaemon(true);
    thread.start();
  }

  private void handleGoBack() {
    try {
      SceneManager.getInstance().changeScene("mainscreen2.fxml");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}