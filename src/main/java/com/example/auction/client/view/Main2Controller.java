package com.example.auction.client.view;

import com.example.auction.client.AuctionClientApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.stage.Stage;

import java.io.IOException;

public class Main2Controller {

    @FXML
    private TextField searchField;

    @FXML
    private FlowPane productsPane;

    @FXML
    private Button homeButton;

    @FXML
    private Button loginButton;

    @FXML
    private Button signupButton;

    @FXML
    public void initialize() {
        System.out.println("Main2Controller initialized!");
        loadProducts();  // tải sp mẫu
        setupMenuButtonActions();  // gán sự kiện cho các nút
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText();
        System.out.println("Tìm kiếm: " + searchText);
        // TODO: Thêm logic tìm kiếm từ database
    }

    // gán sự kiện click cho các nút
    private void setupMenuButtonActions() {
        homeButton.setOnAction(event -> handleHome());
        loginButton.setOnAction(event -> handleLogin());
        signupButton.setOnAction(event -> handleSignup());
    }

    private void handleHome() {
        System.out.println("Trang chủ clicked");
        // TODO: Chuyển về trang chủ
    }

    private void handleLogin() {
        try {
            SceneManager.getInstance().changeToScene("login-view.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void handleSignup() {
        try {
            SceneManager.getInstance().changeToScene("signup-view.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Hàm load sản phẩm mẫu, tạo 100 sp
    private void loadProducts() {
        for (int i = 1; i <= 100; i++) {
            // tạo card cho mỗi sp
            VBox productCard = createProductCard(
                    "Sản phẩm " + i,
                    "Giá: " + (i * 100000) + " đ",
                    "https://via.placeholder.com/150?text=Product+" + i
            );
            // Thêm card vào FlowPane
            productsPane.getChildren().add(productCard);
        }
    }

    // Hàm tạo 1 card sản phẩm
    private VBox createProductCard(String productName, String price, String imageUrl) {
        VBox card = new VBox();
        card.getStyleClass().add("product-card");  // gán CSS class
        card.setAlignment(Pos.TOP_CENTER);  // căn chỉnh: trên giữa

        // Hiển thị ảnh
        ImageView imageView = new ImageView();
        imageView.prefWidth(130);
        imageView.prefHeight(120);
        imageView.setFitWidth(130);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(true);

        try {
            // tải ảnh từ Url
            Image image = new Image(imageUrl, true);
            imageView.setImage(image);
        } catch (Exception e) {
            System.out.println("Lỗi load ảnh: " + e.getMessage());
        }

        // Tên sản phẩm
        Label nameLabel = new Label(productName);
        nameLabel.getStyleClass().add("product-name");

        // Giá
        Label priceLabel = new Label(price);
        priceLabel.getStyleClass().add("product-price");

        // Button xem chi tiết
        Button detailButton = new Button("Xem chi tiết");
        detailButton.getStyleClass().add("product-button");
        detailButton.setOnAction(event -> handleViewDetail(productName));

        // Thêm tất cả thành phần vào card
        card.getChildren().addAll(imageView, nameLabel, priceLabel, detailButton);
        return card;
    }

    // Hàm xử lý khi click "Xem chi tiết"
    private void handleViewDetail(String productName) {
        System.out.println("Xem chi tiết: " + productName);
        // TODO: Chuyển sang trang chi tiết sản phẩm
    }
}