package com.example.auction.client.view;

import com.example.auction.client.AppContext;
import com.example.auction.client.service.AuthService;
import com.example.auction.client.service.ProductService;
import com.example.auction.shared.dto.AuctionDTO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class Main2Controller {

    @FXML private TextField searchField;
    @FXML private FlowPane productsPane;
    @FXML private Button loginBtn;
    @FXML private Button signupBtn;
    @FXML private Label userStatusLabel;

    private static List<AuctionDTO> cachedProducts = null;

    @FXML
    public void initialize() {
        System.out.println("Main2Controller initialized!");
        updateUserStatus();
        if (cachedProducts != null && !cachedProducts.isEmpty()) {
            System.out.println("Using cached products: " + cachedProducts.size());
            displayProducts(cachedProducts);
        } else {
            System.out.println("Loading products from server...");
            loadProductsFromServer();
        }
        setupButtons();
    }

    private void updateUserStatus() {
        if (AppContext.getInstance().isLoggedIn()) {
            userStatusLabel.setText("Xin chào: " + AppContext.getInstance().getCurrentUsername());
            loginBtn.setText("Đăng xuất");
        } else {
            userStatusLabel.setText("👤 Bạn chưa đăng nhập");
            loginBtn.setText("Đăng nhập");
        }
    }

    private void setupButtons() {
        loginBtn.setOnAction(e -> handleLoginLogout());
        signupBtn.setOnAction(e -> handleSignup());
    }

    private void handleLoginLogout() {
        if (AppContext.getInstance().isLoggedIn()) {
            // Send LOGOUT to server on a background thread, then update UI
            Thread thread = new Thread(() -> {
                AuthService.getInstance().logout();
                Platform.runLater(this::updateUserStatus);
            });
            thread.setDaemon(true);
            thread.start();
        } else {
            try {
                SceneManager.getInstance().changeScene("login-view.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText();
        System.out.println("Tìm kiếm: " + searchText);
        // TODO: Thêm logic tìm kiếm từ database
    }

    private void handleSignup() {
        try {
            SceneManager.getInstance().changeScene("signup-view.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadProductsFromServer() {
        Thread thread = new Thread(() -> {
            try {
                List<AuctionDTO> products = ProductService.getInstance().getProducts();
                cachedProducts = products;
                Platform.runLater(() -> displayProducts(products));
            } catch (Exception e) {
                Platform.runLater(() -> userStatusLabel.setText("Lỗi load sản phẩm"));
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void displayProducts(List<AuctionDTO> products) {
        productsPane.getChildren().clear();
        for (AuctionDTO auction : products) {
            productsPane.getChildren().add(createProductCard(auction));
        }
        System.out.println("Displayed " + products.size() + " products");
    }

    private VBox createProductCard(AuctionDTO auction) {
        VBox card = new VBox(8);
        card.getStyleClass().add("product-card");
        card.setStyle("-fx-border-color: #ddd; -fx-padding: 10; -fx-border-radius: 5;");
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(150);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(130);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(true);

        if (auction.itemImage() != null && !auction.itemImage().isEmpty()) {
            try {
                imageView.setImage(new Image(auction.itemImage(), true));
            } catch (Exception e) {
                System.out.println("Lỗi load ảnh");
            }
        }

        Label nameLabel = new Label(auction.itemName());
        nameLabel.getStyleClass().add("product-name");
        nameLabel.setWrapText(true);
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");

        Label priceLabel = new Label("Giá: " + String.format("%.0f", auction.currentHighestBid()) + " đ");
        priceLabel.getStyleClass().add("product-price");
        priceLabel.setStyle("-fx-font-size: 11; -fx-text-fill: red;");

        Label sellerLabel = new Label("Seller: " + auction.sellerUsername());
        sellerLabel.setStyle("-fx-font-size: 10;");

        Button detailButton = new Button("Xem chi tiết");
        detailButton.setPrefWidth(130);
        detailButton.setStyle("-fx-font-size: 10;");
        detailButton.setOnAction(e -> {
            try {
                DetailController.selectedAuction = auction;
                SceneManager.getInstance().changeScene("detail-view.fxml", 900, 600);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        card.getChildren().addAll(imageView, nameLabel, priceLabel, sellerLabel, detailButton);
        return card;
    }
}
