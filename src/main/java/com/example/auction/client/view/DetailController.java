package com.example.auction.client.view;

import com.example.auction.client.AppContext;
import com.example.auction.client.service.BidService;
import com.example.auction.shared.dto.AuctionDTO;
import com.example.auction.shared.dto.MessageProtocol;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class DetailController {

  // Static để pass data từ Main2Controller
  public static AuctionDTO selectedAuction;

  @FXML private ImageView itemImage;
  @FXML private Label itemName;
  @FXML private Label startPrice;
  @FXML private Label currentBid;
  @FXML private Label currentBidder;
  @FXML private Label seller;
  @FXML private Label status;

  @FXML private TextField bidAmount;
  @FXML private Button placeBidBtn;
  @FXML private Button backBtn;
  @FXML private Label messageLabel;

  @FXML
  private void handleBack() {
    try {
      SceneManager.getInstance().changeScene("mainscreen2.fxml");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  public void initialize() {
    if (selectedAuction != null) {
      displayAuctionInfo();
    }

    placeBidBtn.setOnAction(e -> handlePlaceBid());
    backBtn.setOnAction(e -> {
      try {
        SceneManager.getInstance().changeScene("mainscreen2.fxml");
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    });
  }

  private void displayAuctionInfo() {
    itemName.setText(selectedAuction.itemName());
    startPrice.setText(String.format("Giá khởi: %.0f đ", selectedAuction.startPrice()));
    currentBid.setText(String.format("Giá cao nhất: %.0f đ", selectedAuction.currentHighestBid()));
    currentBidder.setText("Người đặt: " +
      (selectedAuction.currentHighestBidderUsername() != null ?
        selectedAuction.currentHighestBidderUsername() : "Chưa có"));
    seller.setText("Người bán: " + selectedAuction.sellerUsername());
    status.setText("Trạng thái: " + selectedAuction.status());

    if (selectedAuction.itemImage() != null && !selectedAuction.itemImage().isEmpty()) {
      try {
        itemImage.setImage(new Image(selectedAuction.itemImage(), true));
        itemImage.setFitWidth(300);
        itemImage.setFitHeight(300);
      } catch (Exception e) {
        System.out.println("Lỗi load ảnh");
      }
    }

    if (!AppContext.getInstance().isLoggedIn()) {
      placeBidBtn.setDisable(true);
      placeBidBtn.setText("Vui lòng đăng nhập");
    } else if (!"ACTIVE".equals(selectedAuction.status())) {
      placeBidBtn.setDisable(true);
      placeBidBtn.setText("Phiên không hoạt động");
    } else {
      placeBidBtn.setDisable(false);
      placeBidBtn.setText("Đặt giá");
    }
  }

  @FXML
  private void handlePlaceBid() {
    if (!AppContext.getInstance().isLoggedIn()) {
      messageLabel.setText("Vui lòng đăng nhập!");
      return;
    }

    String amountStr = bidAmount.getText().trim();
    if (amountStr.isEmpty()) {
      messageLabel.setText("Vui lòng nhập số tiền!");
      return;
    }

    double amount;
    try {
      amount = Double.parseDouble(amountStr);
    } catch (NumberFormatException e) {
      messageLabel.setText("Số tiền không hợp lệ!");
      return;
    }

    if (amount <= selectedAuction.currentHighestBid()) {
      messageLabel.setText("Giá phải cao hơn giá hiện tại!");
      return;
    }

    final double finalAmount = amount;
    Thread thread = new Thread(() -> {
      try {
        MessageProtocol response = BidService.getInstance().placeBid(selectedAuction.id(), finalAmount);

        Platform.runLater(() -> {
          if ("SUCCESS".equals(response.status())) {
            messageLabel.setText("Đặt giá thành công!");
            bidAmount.clear();

            // Update local auction state to reflect new bid
            selectedAuction = new AuctionDTO(
              selectedAuction.id(),
              selectedAuction.sellerUsername(),
              selectedAuction.itemName(),
              selectedAuction.startPrice(),
              finalAmount,
              AppContext.getInstance().getCurrentUsername(),
              selectedAuction.startTime(),
              selectedAuction.endTime(),
              selectedAuction.status(),
              selectedAuction.bidCount() + 1,
              selectedAuction.itemImage()
            );
            displayAuctionInfo();
          } else {
            messageLabel.setText(response.message());
          }
        });
      } catch (Exception e) {
        Platform.runLater(() -> messageLabel.setText("Lỗi server!"));
        e.printStackTrace();
      }
    });

    thread.setDaemon(true);
    thread.start();
  }
}
