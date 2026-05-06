package com.example.auction.client.view;

import com.example.auction.client.AppContext;
import com.example.auction.shared.dto.CreateAuctionDTO;
import com.example.auction.shared.dto.MessageProtocol;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * CreateAuctionController - Tạo phiên đấu giá mới
 */
public class CreateAuctionController {
  @FXML private TextField itemNameField;
  @FXML private TextArea itemDescField;
  @FXML private TextField itemImageField;
  @FXML private ComboBox<String> categoryCombo;
  @FXML private TextField startPriceField;
  @FXML private DatePicker startDatePicker;
  @FXML private Spinner<Integer> startHourSpinner;
  @FXML private Spinner<Integer> startMinSpinner;
  @FXML private DatePicker endDatePicker;
  @FXML private Spinner<Integer> endHourSpinner;
  @FXML private Spinner<Integer> endMinSpinner;
  @FXML private ComboBox<String> conditionCombo;
  @FXML private Button createBtn;
  @FXML private Button cancelBtn;
  @FXML private Label messageLabel;

  @FXML
  public void initialize() {
    System.out.println("CreateAuctionController initialized");

    // Setup category combo
    categoryCombo.getItems().addAll(
      "Electronics", "Fashion", "Books", "Art",
      "Jewelry", "Sports", "Toys", "Other"
    );
    categoryCombo.getSelectionModel().selectFirst();

    // Setup condition combo
    conditionCombo.getItems().addAll("NEW", "LIKE_NEW", "GOOD", "FAIR");
    conditionCombo.getSelectionModel().selectFirst();

    // Setup spinners
    startHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));
    startMinSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
    endHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));
    endMinSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));

    // Set default dates
    LocalDateTime now = LocalDateTime.now();
    startDatePicker.setValue(now.toLocalDate());
    endDatePicker.setValue(now.toLocalDate().plusDays(7));

    setupButtons();
  }

  private void setupButtons() {
    createBtn.setOnAction(e -> handleCreate());
    cancelBtn.setOnAction(e -> handleCancel());
  }

  private void handleCreate() {
    // Validate inputs
    if (itemNameField.getText().trim().isEmpty()) {
      messageLabel.setText("Tên sản phẩm không được để trống");
      return;
    }

    double startPrice;
    try {
      startPrice = Double.parseDouble(startPriceField.getText());
      if (startPrice <= 0) throw new NumberFormatException();
    } catch (NumberFormatException e) {
      messageLabel.setText("Giá khởi điểm phải > 0");
      return;
    }

    // Build start and end times
    LocalDateTime startDT = LocalDateTime.of(
      startDatePicker.getValue(),
      java.time.LocalTime.of(startHourSpinner.getValue(), startMinSpinner.getValue())
    );

    LocalDateTime endDT = LocalDateTime.of(
      endDatePicker.getValue(),
      java.time.LocalTime.of(endHourSpinner.getValue(), endMinSpinner.getValue())
    );

    Instant startInstant = startDT.atZone(ZoneId.systemDefault()).toInstant();
    Instant endInstant = endDT.atZone(ZoneId.systemDefault()).toInstant();

    if (startInstant.isBefore(Instant.now())) {
      messageLabel.setText("Thời gian bắt đầu phải trong tương lai");
      return;
    }

    if (endInstant.isBefore(startInstant)) {
      messageLabel.setText("Thời gian kết thúc phải sau thời gian bắt đầu");
      return;
    }

    // Create DTO
    CreateAuctionDTO auctionData = new CreateAuctionDTO(
      itemNameField.getText(),
      itemDescField.getText(),
      itemImageField.getText(),
      categoryCombo.getValue(),
      startPrice,
      startInstant,
      endInstant,
      conditionCombo.getValue()
    );

    // Send to server
    Thread thread = new Thread(() -> {
      try {
        synchronized (AppContext.getInstance().getOut()) {
          ObjectOutputStream out = AppContext.getInstance().getOut();
          ObjectInputStream in = AppContext.getInstance().getIn();

          MessageProtocol request = new MessageProtocol(
            "CREATE_AUCTION", auctionData, null, null
          );
          out.writeObject(request);
          out.flush();

          MessageProtocol response = (MessageProtocol) in.readObject();

          Platform.runLater(() -> {
            if ("SUCCESS".equals(response.status())) {
              messageLabel.setText("Tạo phiên thành công! ID: " + response.data());
              messageLabel.setStyle("-fx-text-fill: green;");

              // Go back after 2 seconds
              new java.util.Timer().schedule(
                new java.util.TimerTask() {
                  @Override
                  public void run() {
                    try {
                      SceneManager.getInstance().changeScene("mainscreen2.fxml");
                    } catch (IOException e) {
                      e.printStackTrace();
                    }
                  }
                },
                2000
              );
            } else {
              messageLabel.setText(response.message());
              messageLabel.setStyle("-fx-text-fill: red;");
            }
          });
        }
      } catch (Exception e) {
        Platform.runLater(() -> messageLabel.setText("Lỗi tạo phiên"));
        e.printStackTrace();
      }
    });
    thread.setDaemon(true);
    thread.start();
  }

  private void handleCancel() {
    try {
      SceneManager.getInstance().changeScene("mainscreen2.fxml");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}