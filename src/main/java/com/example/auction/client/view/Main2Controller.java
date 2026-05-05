package com.example.auction.client.view;

import com.example.auction.client.AppContext;
import com.example.auction.shared.dto.AuctionDTO;
import com.example.auction.shared.dto.MessageProtocol;
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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class Main2Controller {

    @FXML
    private TextField searchField;

    @FXML
    private FlowPane productsPane;

    @FXML
    private Button loginBtn;

    @FXML
    private Button signupBtn;

    @FXML
    private Label userStatusLabel;

    private static List<AuctionDTO> cachedProducts = null;

    @FXML
    public void initialize() { // kết nối socket ngay khi mở app
        System.out.println("Main2Controller initialized!");
        // Update user status
        updateUserStatus();
        // Nếu có cached products, hiện ngay
        if (cachedProducts != null && !cachedProducts.isEmpty()) {
            System.out.println("Using cached products: " + cachedProducts.size());
            displayProducts(cachedProducts);
        } else {
            System.out.println("🔍 Loading products from server...");
            loadProductsFromServer();
        }
        setupButtons();
    }

    private void updateUserStatus() {
        if (AppContext.getInstance().isLoggedIn()) {
            String username = AppContext.getInstance().getCurrentUsername();
            userStatusLabel.setText("Xin chào: " + username);
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
        try {
            if (AppContext.getInstance().isLoggedIn()) {
                AppContext.getInstance().logout();
                updateUserStatus();
            } else {
                SceneManager.getInstance().changeScene("login-view.fxml");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText();
        System.out.println("Tìm kiếm: " + searchText);
        // TODO: Thêm logic tìm kiếm từ database
    }

//    // gán sự kiện click cho các nút
//    private void setupMenuButtonActions() {
//        loginButton.setOnAction(event -> handleLogin());
//        signupButton.setOnAction(event -> handleSignup());
//    }

//    private void handleHome() {
//        System.out.println("Trang chủ clicked");
//        // TODO: Chuyển về trang chủ
//    }

//    private void handleLogin() {
//        try {
//            if (AuctionClientApp.isLoggedIn()) {
//                // Đăng xuất
//                AuctionClientApp.logout();
//                updateUserStatus();
//            } else {
//                // Đăng nhập
//                SceneManager.getInstance().changeToScene("login-view.fxml");
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    private void handleSignup() {
        try {
            SceneManager.getInstance().changeScene("signup-view.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Hàm load sản phẩm mẫu, tạo 100 sp
    private void loadProductsFromServer() {
        Thread thread = new Thread(() -> {
            try {
                // Synchronized để tránh race condition
                synchronized (AppContext.getInstance().getOut()) {
                    ObjectOutputStream out = AppContext.getInstance().getOut();
                    ObjectInputStream in = AppContext.getInstance().getIn();

                    MessageProtocol request = new MessageProtocol(
                      "GET_PRODUCTS_LIST", null, null, null
                    );
                    out.writeObject(request);
                    out.flush();

                    MessageProtocol response = (MessageProtocol) in.readObject();

                    if ("SUCCESS".equals(response.status())) {
                        List<AuctionDTO> products = (List<AuctionDTO>) response.data();

                        cachedProducts = products;

                        Platform.runLater(() -> displayProducts(products));
                    }
                }
            } catch (Exception e) {
                Platform.runLater(() ->
                  userStatusLabel.setText("Lỗi load sản phẩm")
                );
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

    // Hàm tạo 1 card sản phẩm
    private VBox createProductCard(AuctionDTO auction) {
        VBox card = new VBox(8);
        card.getStyleClass().add("product-card");
        card.setStyle("-fx-border-color: #ddd; -fx-padding: 10; -fx-border-radius: 5;");
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(150);

        // Image
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

        // Tên sản phẩm
        Label nameLabel = new Label(auction.itemName());
        nameLabel.getStyleClass().add("product-name");
        nameLabel.setWrapText(true);
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");

        // Giá
        Label priceLabel = new Label("Giá: " + String.format("%.0f", auction.currentHighestBid()) + " đ");
        priceLabel.getStyleClass().add("product-price");
        priceLabel.setStyle("-fx-font-size: 11; -fx-text-fill: red;");

        // Seller
        Label sellerLabel = new Label("Seller: " + auction.sellerUsername());
        sellerLabel.setStyle("-fx-font-size: 10;");


        // Button xem chi tiết
        Button detailButton = new Button("Xem chi tiết");
        detailButton.setPrefWidth(130);
        detailButton.setStyle("-fx-font-size: 10;");
        detailButton.setOnAction(e -> {
            try {
                // Pass data via static variable
                DetailController.selectedAuction = auction;
                SceneManager.getInstance().changeScene("detail-view.fxml", 900, 600);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        card.getChildren().addAll(imageView, nameLabel, priceLabel, sellerLabel, detailButton);
        return card;
    }

//    // Hàm xử lý khi click "Xem chi tiết"
//    private void handleViewDetail(AuctionDTO auctionDTO) {
//        try {
//            FXMLLoader loader = new FXMLLoader(
//              getClass().getResource("/com/example/auction/client/view/detail-view.fxml")
//            );
//            javafx.scene.Parent detailView = loader.load();
//
//            // Lấy DetailController và truyền data
//            DetailController controller = loader.getController();
//            controller.setAuctionData(auctionDTO);
//
//            // Tạo scene mới
//            javafx.scene.Scene scene = new javafx.scene.Scene(detailView);
//            SceneManager.getInstance().setScene(scene);
//            SceneManager.getInstance().setScene(scene);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}