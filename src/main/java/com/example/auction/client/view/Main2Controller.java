////package com.example.auction.client.view;
////
////import com.example.auction.client.AppContext;
////import com.example.auction.client.AuctionClientApp;
////import com.example.auction.server.service.SocketManager;
////import com.example.auction.shared.dto.AuctionDTO;
////import com.example.auction.shared.dto.MessageProtocol;
////import javafx.animation.Animation;
////import javafx.animation.KeyFrame;
////import javafx.animation.Timeline;
////import javafx.application.Platform;
////import javafx.fxml.FXML;
////import javafx.fxml.FXMLLoader;
////import javafx.geometry.Insets;
////import javafx.scene.Parent;
////import javafx.scene.control.Button;
////import javafx.scene.control.Label;
////import javafx.scene.control.TextField;
////import javafx.scene.image.Image;
////import javafx.scene.image.ImageView;
////import javafx.scene.layout.FlowPane;
////import javafx.scene.layout.VBox;
////import javafx.geometry.Pos;
////import javafx.stage.Stage;
////import javafx.util.Duration;
////
////import java.io.IOException;
////import java.io.ObjectInputStream;
////import java.io.ObjectOutputStream;
////import java.net.Socket;
////import java.time.Instant;
////import java.time.LocalDateTime;
////import java.time.ZoneId;
////import java.time.format.DateTimeFormatter;
////import java.util.List;
////import java.util.Locale;
////
////public class Main2Controller {
////
////    @FXML
////    private TextField searchField;
////
////    @FXML
////    private FlowPane productsPane;
////
////    @FXML
////    private Button loginBtn;
////
////    @FXML
////    private Button signupBtn;
////
////    @FXML
////    private Label labelTime;
////
////    @FXML
////    private Label labelDate;
////
////    @FXML
////    private Label userStatusLabel;
////
////    private static List<AuctionDTO> cachedProducts = null;
////
////    @FXML
////    public void initialize() { // kết nối socket ngay khi mở app
////        System.out.println("Main2Controller initialized!");
////        // Update user status
////        updateUserStatus();
////        initClock(labelTime , labelDate);
////        // Nếu có cached products, hiện ngay
////        if (cachedProducts != null && !cachedProducts.isEmpty()) {
////            System.out.println("Using cached products: " + cachedProducts.size());
////            displayProducts(cachedProducts);
////        } else {
////            System.out.println("🔍 Loading products from server...");
////            loadProductsFromServer();
////        }
////        setupButtons();
////    }
////
////    private void updateUserStatus() {
////        if (AppContext.getInstance().isLoggedIn()) {
////            String username = AppContext.getInstance().getCurrentUsername();
////            userStatusLabel.setText("Xin chào: " + username);
////            loginBtn.setText("Đăng xuất");
////        } else {
////            userStatusLabel.setText("👤 Bạn chưa đăng nhập");
////            loginBtn.setText("Đăng nhập");
////        }
////    }
////
////    private void setupButtons() {
////        loginBtn.setOnAction(e -> handleLoginLogout());
////        signupBtn.setOnAction(e -> handleSignup());
////    }
////
////    private void handleLoginLogout() {
////        try {
////            if (AppContext.getInstance().isLoggedIn()) {
////                AppContext.getInstance().logout();
////                updateUserStatus();
////            } else {
////                SceneManager.getInstance().changeScene("login-view.fxml");
////            }
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////    }
////
////    @FXML
////    private void handleSearch() {
////        String searchText = searchField.getText();
////        System.out.println("Tìm kiếm: " + searchText);
////        // TODO: Thêm logic tìm kiếm từ database
////    }
////
//////    // gán sự kiện click cho các nút
//////    private void setupMenuButtonActions() {
//////        loginButton.setOnAction(event -> handleLogin());
//////        signupButton.setOnAction(event -> handleSignup());
//////    }
////
//////    private void handleHome() {
//////        System.out.println("Trang chủ clicked");
//////        // TODO: Chuyển về trang chủ
//////    }
////
//////    private void handleLogin() {
//////        try {
//////            if (AuctionClientApp.isLoggedIn()) {
//////                // Đăng xuất
//////                AuctionClientApp.logout();
//////                updateUserStatus();
//////            } else {
//////                // Đăng nhập
//////                SceneManager.getInstance().changeToScene("login-view.fxml");
//////            }
//////        } catch (IOException e) {
//////            throw new RuntimeException(e);
//////        }
//////    }
////
////    private void handleSignup() {
////        try {
////            SceneManager.getInstance().changeScene("signup-view.fxml");
////        } catch (IOException e) {
////            throw new RuntimeException(e);
////        }
////    }
////
////    // Hàm load sản phẩm mẫu, tạo 100 sp
////    private void loadProductsFromServer() {
////        Thread thread = new Thread(() -> {
////            try {
////                // Synchronized để tránh race condition
////                synchronized (AppContext.getInstance().getOut()) {
////                    ObjectOutputStream out = AppContext.getInstance().getOut();
////                    ObjectInputStream in = AppContext.getInstance().getIn();
////
////                    MessageProtocol request = new MessageProtocol(
////                      "GET_PRODUCTS_LIST", null, null, null
////                    );
////                    out.writeObject(request);
////                    out.flush();
////
////                    MessageProtocol response = (MessageProtocol) in.readObject();
////
////                    if ("SUCCESS".equals(response.status())) {
////                        List<AuctionDTO> products = (List<AuctionDTO>) response.data();
////
////                        cachedProducts = products;
////
////                        Platform.runLater(() -> displayProducts(products));
////                    }
////                }
////            } catch (Exception e) {
////                Platform.runLater(() ->
////                  userStatusLabel.setText("Lỗi load sản phẩm")
////                );
////                e.printStackTrace();
////            }
////        });
////        thread.setDaemon(true);
////        thread.start();
////    }
////
////    private void displayProducts(List<AuctionDTO> products) {
////        productsPane.getChildren().clear();
////        for (AuctionDTO auction : products) {
////            productsPane.getChildren().add(createProductCard(auction));
////        }
////        System.out.println("Displayed " + products.size() + " products");
////    }
////
////    // Hàm tạo 1 card sản phẩm
////    private VBox createProductCard(AuctionDTO auction) {
////        VBox card = new VBox(10);
////        card.getStyleClass().add("product-card");
////        // Thêm -fx-overflow: hidden để phần màu đỏ không bị lòi ra ngoài khi bo góc thẻ
////        card.setStyle("-fx-border-color: #ddd; -fx-padding: 0 0 10 0; -fx-border-radius: 5; -fx-background-radius: 5; -fx-overflow: hidden;");
////        card.setAlignment(Pos.TOP_CENTER);
////        card.setPrefWidth(200);
////
////        // 1. Đồng hồ đếm ngược (Đưa lên đầu thẻ)
////        Label labelCountdown = new Label("Loading...");
////        labelCountdown.setMaxWidth(Double.MAX_VALUE);
////        labelCountdown.setAlignment(Pos.CENTER);
////        // Bo góc trên (5px) để khớp với card, góc dưới để 0px
////        labelCountdown.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5; -fx-background-radius: 5 5 0 0;");
////
////        // 2. Image (Thêm padding hoặc margin để không dính sát thanh countdown)
////        ImageView imageView = new ImageView();
////        imageView.setFitWidth(150);
////        imageView.setFitHeight(150); // Chỉnh lại chút để cân đối
////        imageView.setPreserveRatio(true);
////
////        if (auction.itemImage() != null && !auction.itemImage().isEmpty()) {
////            try {
////                imageView.setImage(new Image(auction.itemImage(), true));
////            } catch (Exception e) {
////                System.out.println("Lỗi load ảnh");
////            }
////        }
////
////        // 3. Thông tin sản phẩm (Nên cho vào một VBox phụ có padding để chữ không sát lề)
////        VBox infoBox = new VBox(5);
////        infoBox.setAlignment(Pos.CENTER);
////        infoBox.setPadding(new Insets(0, 5, 0, 5));
////
////        Label nameLabel = new Label(auction.itemName());
////        nameLabel.setWrapText(true);
////        nameLabel.setAlignment(Pos.CENTER);
////        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");
////
////        Label priceLabel = new Label("Giá: " + String.format("%.0f", auction.currentHighestBid()) + " đ");
////        priceLabel.setStyle("-fx-font-size: 11; -fx-text-fill: red;");
////
////        Label sellerLabel = new Label("Seller: " + auction.sellerUsername());
////        sellerLabel.setStyle("-fx-font-size: 10;");
////
////        Button detailButton = new Button("Xem chi tiết");
////        detailButton.setPrefWidth(130);
////        detailButton.setStyle("-fx-font-size: 10; -fx-cursor: hand;");
////        detailButton.setOnAction(e -> {
////            try {
////                DetailController.selectedAuction = auction;
////                SceneManager.getInstance().changeScene("detail-view.fxml", 900, 600);
////            } catch (IOException ex) {
////                ex.printStackTrace();
////            }
////        });
////
////        infoBox.getChildren().addAll(nameLabel, priceLabel, sellerLabel, detailButton);
////
////        // Thêm tất cả vào card chính - labelCountdown nằm vị trí index 0
////        card.getChildren().addAll(labelCountdown, imageView, infoBox);
////
////        // FIX LỖI: Chuyển đổi thời gian an toàn từ Instant/Z
////        try {
////            // Parse sang Instant trước để xử lý ký tự 'Z', sau đó mới chuyển sang LocalDateTime địa phương
////            Instant instant = Instant.parse(auction.endTime().toString());
////            LocalDateTime endLDT = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
////            startCountdown(labelCountdown, endLDT);
////        } catch (Exception e) {
////            // Nếu auction.endTime() đã là LocalDateTime sẵn hoặc format khác
////            try {
////                startCountdown(labelCountdown, LocalDateTime.from(auction.endTime()));
////            } catch (Exception ex) {
////                labelCountdown.setText("Lỗi định dạng giờ");
////            }
////        }
////
////        return card;
////    }
////
////    private void initClock(Label labelTime , Label labelDate){
////        // Định dạng cho Giờ:Phút:Giây
////        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
////
////        // Định dạng cho Thứ, Ngày/Tháng/Năm (Dùng Locale Tiếng Việt)
////        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy", new Locale("vi", "VN"));
////
////        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
////            LocalDateTime now = LocalDateTime.now();
////            labelTime.setText(now.format(timeFormatter));
////            labelDate.setText(now.format(dateFormatter));
////        }), new KeyFrame(Duration.seconds(1)));
////
////        clock.setCycleCount(Timeline.INDEFINITE);
////        clock.play();
////    }
////    public void startCountdown(Label labelCountdown, LocalDateTime endTime) {
////        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
////            LocalDateTime now = LocalDateTime.now();
////            java.time.Duration duration = java.time.Duration.between(now, endTime);
////
////            if (duration.isNegative() || duration.isZero()) {
////                labelCountdown.setText("ĐÃ KẾT THÚC");
////                labelCountdown.getStyleClass().clear();
////                labelCountdown.getStyleClass().add("finished-label"); // Màu xám khi hết giờ
////            } else {
////                labelCountdown.getStyleClass().add("countdown-label");
////                long days = duration.toDays();
////                long hours = duration.toHoursPart();
////                long minutes = duration.toMinutesPart();
////                long seconds = duration.toSecondsPart();
////
////                // Hiển thị định dạng: 02 ngày 05 giờ 30 phút 15 giây
////                String timeLeft = String.format("%02d ngày %02d giờ %02d phút %02d giây",
////                    days, hours, minutes, seconds);
////                labelCountdown.setText(timeLeft);
////            }
////        }));
////        timeline.setCycleCount(Animation.INDEFINITE);
////        timeline.play();
////    }
////
//////    // Hàm xử lý khi click "Xem chi tiết"
//////    private void handleViewDetail(AuctionDTO auctionDTO) {
//////        try {
//////            FXMLLoader loader = new FXMLLoader(
//////              getClass().getResource("/com/example/auction/client/view/detail-view.fxml")
//////            );
//////            javafx.scene.Parent detailView = loader.load();
//////
//////            // Lấy DetailController và truyền data
//////            DetailController controller = loader.getController();
//////            controller.setAuctionData(auctionDTO);
//////
//////            // Tạo scene mới
//////            javafx.scene.Scene scene = new javafx.scene.Scene(detailView);
//////            SceneManager.getInstance().setScene(scene);
//////            SceneManager.getInstance().setScene(scene);
//////
//////        } catch (IOException e) {
//////            e.printStackTrace();
//////        }
//////    }
////}
//package com.example.auction.client.view;
//
//import com.example.auction.client.AppContext;
//import com.example.auction.shared.dto.AuctionDTO;
//import com.example.auction.shared.dto.MessageProtocol;
//import javafx.application.Platform;
//import javafx.fxml.FXML;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.control.TextField;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.FlowPane;
//import javafx.scene.layout.VBox;
//import javafx.util.Duration;
//
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.time.*;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//import java.util.Locale;
//import javafx.animation.Animation;
//import javafx.animation.KeyFrame;
//import javafx.animation.Timeline;
//
//public class Main2Controller {
//    @FXML
//    private TextField searchField;
//    @FXML
//    private FlowPane productsPane;
//    @FXML
//    private Button loginBtn;
//    @FXML
//    private Button signupBtn;
//    @FXML
//    private Label labelTime;
//    @FXML
//    private Label labelDate;
//    @FXML
//    private Label userStatusLabel;
//
//    private static List<AuctionDTO> cachedProducts = null;
//    private Thread serverListenerThread;
//    private volatile boolean isListening = true;
//
//    @FXML
//    public void initialize() {
//        System.out.println("Main2Controller initialized!");
//        updateUserStatus();
//        initClock(labelTime, labelDate);
//
//        if (cachedProducts != null && !cachedProducts.isEmpty()) {
//            System.out.println("Using cached products: " + cachedProducts.size());
//            displayProducts(cachedProducts);
//        } else {
//            System.out.println("Loading products from server...");
//            loadProductsFromServer();
//        }
//
//        setupButtons();
//        startServerListener(); // Start listening for server broadcasts
//    }
//
//    private void startServerListener() {
//        serverListenerThread = new Thread(() -> {
//            try {
//                ObjectInputStream in = AppContext.getInstance().getIn();
//
//                while (isListening && AppContext.getInstance().isConnected()) {
//                    try {
//                        MessageProtocol msg = (MessageProtocol) in.readObject();
//                        handleServerMessage(msg);
//                    } catch (EOFException e) {
//                        System.out.println("Server connection closed");
//                        break;
//                    }
//                }
//            } catch (Exception e) {
//                System.err.println("Error in server listener: " + e.getMessage());
//            }
//        });
//
//        serverListenerThread.setDaemon(true);
//        serverListenerThread.start();
//    }
//
//    private void handleServerMessage(MessageProtocol msg) {
//        if ("NEW_BID".equals(msg.type())) {
//            Platform.runLater(() -> {
//                // Reload products to show updated bid
//                loadProductsFromServer();
//            });
//        } else if ("AUCTION_STATUS_CHANGED".equals(msg.type())) {
//            Platform.runLater(() -> {
//                loadProductsFromServer();
//            });
//        }
//    }
//
//    private void updateUserStatus() {
//        if (AppContext.getInstance().isLoggedIn()) {
//            String username = AppContext.getInstance().getCurrentUsername();
//            userStatusLabel.setText("Xin chào: " + username);
//            loginBtn.setText("Đăng xuất");
//        } else {
//            userStatusLabel.setText("👤 Bạn chưa đăng nhập");
//            loginBtn.setText("Đăng nhập");
//        }
//    }
//
//    private void setupButtons() {
//        loginBtn.setOnAction(e -> handleLoginLogout());
//        signupBtn.setOnAction(e -> handleSignup());
//        searchField.setOnAction(e -> handleSearch());
//    }
//
//    private void handleLoginLogout() {
//        try {
//            if (AppContext.getInstance().isLoggedIn()) {
//                // Logout
//                AppContext.getInstance().logout();
//                updateUserStatus();
//            } else {
//                // Go to login
//                SceneManager.getInstance().changeScene("login-view.fxml");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void handleSignup() {
//        try {
//            SceneManager.getInstance().changeScene("signup-view.fxml");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @FXML
//    private void handleSearch() {
//        String keyword = searchField.getText().trim();
//        if (keyword.isEmpty()) {
//            loadProductsFromServer();
//            return;
//        }
//
//        System.out.println("Tìm kiếm: " + keyword);
//
//        Thread searchThread = new Thread(() -> {
//            try {
//                synchronized (AppContext.getInstance().getOut()) {
//                    ObjectOutputStream out = AppContext.getInstance().getOut();
//                    ObjectInputStream in = AppContext.getInstance().getIn();
//
//                    MessageProtocol request = new MessageProtocol(
//                      "SEARCH", keyword, null, null
//                    );
//
//                    out.writeObject(request);
//                    out.flush();
//
//                    MessageProtocol response = (MessageProtocol) in.readObject();
//
//                    if ("SUCCESS".equals(response.status())) {
//                        List<AuctionDTO> results = (List<AuctionDTO>) response.data();
//                        cachedProducts = results;
//                        Platform.runLater(() -> displayProducts(results));
//                    }
//                }
//            } catch (Exception e) {
//                Platform.runLater(() ->
//                  userStatusLabel.setText("Lỗi tìm kiếm")
//                );
//                e.printStackTrace();
//            }
//        });
//
//        searchThread.setDaemon(true);
//        searchThread.start();
//    }
//
//    private void loadProductsFromServer() {
//        Thread thread = new Thread(() -> {
//            try {
//                synchronized (AppContext.getInstance().getOut()) {
//                    ObjectOutputStream out = AppContext.getInstance().getOut();
//                    ObjectInputStream in = AppContext.getInstance().getIn();
//
//                    MessageProtocol request = new MessageProtocol(
//                      "GET_PRODUCTS_LIST", null, null, null
//                    );
//                    out.writeObject(request);
//                    out.flush();
//
//                    MessageProtocol response = (MessageProtocol) in.readObject();
//
//                    if ("SUCCESS".equals(response.status())) {
//                        List<AuctionDTO> products = (List<AuctionDTO>) response.data();
//                        cachedProducts = products;
//                        Platform.runLater(() -> displayProducts(products));
//                    }
//                }
//            } catch (Exception e) {
//                Platform.runLater(() ->
//                  userStatusLabel.setText("Lỗi load sản phẩm")
//                );
//                e.printStackTrace();
//            }
//        });
//        thread.setDaemon(true);
//        thread.start();
//    }
//
//    private void displayProducts(List<AuctionDTO> products) {
//        productsPane.getChildren().clear();
//        for (AuctionDTO auction : products) {
//            productsPane.getChildren().add(createProductCard(auction));
//        }
//        System.out.println("Displayed " + products.size() + " products");
//    }
//
//    private VBox createProductCard(AuctionDTO auction) {
//        VBox card = new VBox(10);
//        card.getStyleClass().add("product-card");
//        card.setStyle("-fx-border-color: #ddd; -fx-padding: 0 0 10 0; -fx-border-radius: 5; -fx-background-radius: 5; -fx-overflow: hidden;");
//        card.setAlignment(Pos.TOP_CENTER);
//        card.setPrefWidth(200);
//
//        // Countdown
//        Label labelCountdown = new Label("Loading...");
//        labelCountdown.setMaxWidth(Double.MAX_VALUE);
//        labelCountdown.setAlignment(Pos.CENTER);
//        labelCountdown.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5; -fx-background-radius: 5 5 0 0;");
//
//        // Image
//        ImageView imageView = new ImageView();
//        imageView.setFitWidth(150);
//        imageView.setFitHeight(150);
//        imageView.setPreserveRatio(true);
//
//        if (auction.itemImage() != null && !auction.itemImage().isEmpty()) {
//            try {
//                imageView.setImage(new Image(auction.itemImage(), true));
//            } catch (Exception e) {
//                System.out.println("Lỗi load ảnh");
//            }
//        }
//
//        // Info
//        VBox infoBox = new VBox(5);
//        infoBox.setAlignment(Pos.CENTER);
//        infoBox.setPadding(new Insets(0, 5, 0, 5));
//
//        Label nameLabel = new Label(auction.itemName());
//        nameLabel.setWrapText(true);
//        nameLabel.setAlignment(Pos.CENTER);
//        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");
//
//        Label priceLabel = new Label("Giá: " + String.format("%.0f", auction.currentHighestBid()) + " đ");
//        priceLabel.setStyle("-fx-font-size: 11; -fx-text-fill: red;");
//
//        Label sellerLabel = new Label("Seller: " + auction.sellerUsername());
//        sellerLabel.setStyle("-fx-font-size: 10;");
//
//        Button detailButton = new Button("Xem chi tiết");
//        detailButton.setPrefWidth(130);
//        detailButton.setStyle("-fx-font-size: 10; -fx-cursor: hand;");
//        detailButton.setOnAction(e -> {
//            try {
//                DetailController.selectedAuction = auction;
//                SceneManager.getInstance().changeScene("detail-view.fxml", 900, 600);
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        });
//
//        infoBox.getChildren().addAll(nameLabel, priceLabel, sellerLabel, detailButton);
//        card.getChildren().addAll(labelCountdown, imageView, infoBox);
//
//        // Start countdown
//        try {
//            Instant instant = Instant.parse(auction.endTime().toString());
//            LocalDateTime endLDT = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
//            startCountdown(labelCountdown, endLDT);
//        } catch (Exception e) {
//            labelCountdown.setText("Lỗi định dạng giờ");
//        }
//
//        return card;
//    }
//
//    private void initClock(Label labelTime, Label labelDate) {
//        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
//        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy", new Locale("vi", "VN"));
//
//        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
//            LocalDateTime now = LocalDateTime.now();
//            labelTime.setText(now.format(timeFormatter));
//            labelDate.setText(now.format(dateFormatter));
//        }), new KeyFrame(Duration.seconds(1)));
//
//        clock.setCycleCount(Timeline.INDEFINITE);
//        clock.play();
//    }
//
//    private void startCountdown(Label labelCountdown, LocalDateTime endTime) {
//        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
//            LocalDateTime now = LocalDateTime.now();
//            java.time.Duration duration = java.time.Duration.between(now, endTime);
//
//            if (duration.isNegative() || duration.isZero()) {
//                labelCountdown.setText("ĐÃ KẾT THÚC");
//                labelCountdown.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5; -fx-background-radius: 5 5 0 0;");
//            } else {
//                long days = duration.toDays();
//                long hours = duration.toHoursPart();
//                long minutes = duration.toMinutesPart();
//                long seconds = duration.toSecondsPart();
//
//                String timeLeft = String.format("%02d ngày %02d giờ %02d phút %02d giây",
//                  days, hours, minutes, seconds);
//                labelCountdown.setText(timeLeft);
//
//                if (days == 0 && hours == 0) {
//                    labelCountdown.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5; -fx-background-radius: 5 5 0 0;");
//                }
//            }
//        }));
//        timeline.setCycleCount(Animation.INDEFINITE);
//        timeline.play();
//    }
//
//    @FXML
//    public void onWindowClose() {
//        isListening = false;
//        if (serverListenerThread != null) {
//            serverListenerThread.interrupt();
//        }
//    }
//}
package com.example.auction.client.view;

import com.example.auction.client.AppContext;
import com.example.auction.shared.dto.AuctionDTO;
import com.example.auction.shared.dto.MessageProtocol;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Main2Controller - Main screen for auction listing with search
 */
public class Main2Controller {

    // ============ FXML INJECTED FIELDS ============
    @FXML
    private TextField searchField;

    @FXML
    private FlowPane productsPane;

    @FXML
    private Button loginBtn;

    @FXML
    private Button signupBtn;

    @FXML
    private Button profileBtn;

    @FXML
    private Button createAuctionBtn;

    @FXML
    private Label labelTime;

    @FXML
    private Label labelDate;

    @FXML
    private Label userStatusLabel;

    // ============ INSTANCE VARIABLES ============
    private static List<AuctionDTO> cachedProducts;
    private Thread serverListenerThread;
    private volatile boolean isListening = true;

    // ============ INITIALIZATION ============

    /**
     * Initialize controller (called after FXML is loaded)
     */
    @FXML
    public void initialize() {
        System.out.println("✅ Main2Controller initialized!");

        try {
            // Verify all FXML components are injected
            verifyComponents();

            // Update user status
            updateUserStatus();

            // Initialize clock
            initClock(labelTime, labelDate);

            // Load products
            if (cachedProducts != null && !cachedProducts.isEmpty()) {
                System.out.println("📦 Using cached products: " + cachedProducts.size());
                displayProducts(cachedProducts);
            } else {
                System.out.println("📥 Loading products from server...");
                loadProductsFromServer();
            }

            // Setup button handlers
            setupButtons();

            // Start listening for server messages
            startServerListener();

        } catch (Exception e) {
            System.err.println("❌ Initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Verify all FXML components are injected
     */
    private void verifyComponents() {
        if (searchField == null) throw new IllegalStateException("searchField is null");
        if (productsPane == null) throw new IllegalStateException("productsPane is null");
        if (loginBtn == null) throw new IllegalStateException("loginBtn is null");
        if (signupBtn == null) throw new IllegalStateException("signupBtn is null");
        if (profileBtn == null) throw new IllegalStateException("profileBtn is null");
        if (createAuctionBtn == null) throw new IllegalStateException("createAuctionBtn is null");
        if (labelTime == null) throw new IllegalStateException("labelTime is null");
        if (labelDate == null) throw new IllegalStateException("labelDate is null");
        if (userStatusLabel == null) throw new IllegalStateException("userStatusLabel is null");

        System.out.println("✅ All FXML components verified");
    }

    // ============ BUTTON HANDLERS ============

    /**
     * Initialize button event handlers
     */
    private void setupButtons() {
        loginBtn.setOnAction(event -> handleLoginLogout());
        signupBtn.setOnAction(event -> handleSignup());
        profileBtn.setOnAction(event -> handleProfile());
        createAuctionBtn.setOnAction(event -> handleCreateAuction());
        searchField.setOnAction(event -> handleSearch());
    }

    /**
     * Update user status display
     */
    private void updateUserStatus() {
        if (AppContext.getInstance().isLoggedIn()) {
            String username = AppContext.getInstance().getCurrentUsername();
            userStatusLabel.setText("👤 Xin chào: " + username);
            loginBtn.setText("Đăng xuất");
            profileBtn.setDisable(false);
            createAuctionBtn.setDisable(false);
        } else {
            userStatusLabel.setText("👤 Bạn chưa đăng nhập");
            loginBtn.setText("Đăng nhập");
            profileBtn.setDisable(true);
            createAuctionBtn.setDisable(true);
        }
    }

    /**
     * Handle login/logout button
     */
    private void handleLoginLogout() {
        try {
            if (AppContext.getInstance().isLoggedIn()) {
                AppContext.getInstance().logout();
                updateUserStatus();
            } else {
                SceneManager.getInstance().changeScene("login-view.fxml");
            }
        } catch (IOException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    /**
     * Handle signup button
     */
    private void handleSignup() {
        try {
            SceneManager.getInstance().changeScene("signup-view.fxml");
        } catch (IOException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    /**
     * Handle profile button
     */
    private void handleProfile() {
        try {
            SceneManager.getInstance().changeScene("profile-view.fxml");
        } catch (IOException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    /**
     * Handle create auction button
     */
    private void handleCreateAuction() {
        try {
            SceneManager.getInstance().changeScene("create-auction-view.fxml");
        } catch (IOException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    /**
     * Handle search
     */
    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadProductsFromServer();
            return;
        }

        System.out.println("🔍 Searching: " + keyword);

        Thread searchThread = new Thread(() -> {
            try {
                synchronized (AppContext.getInstance().getOut()) {
                    ObjectOutputStream out = AppContext.getInstance().getOut();
                    ObjectInputStream in = AppContext.getInstance().getIn();

                    MessageProtocol request = new MessageProtocol(
                      "SEARCH", keyword, null, null
                    );

                    out.writeObject(request);
                    out.flush();

                    MessageProtocol response = (MessageProtocol) in.readObject();

                    if ("SUCCESS".equals(response.status())) {
                        @SuppressWarnings("unchecked")
                        List<AuctionDTO> results = (List<AuctionDTO>) response.data();
                        cachedProducts = results;
                        Platform.runLater(() -> displayProducts(results));
                    } else {
                        Platform.runLater(() ->
                          userStatusLabel.setText("❌ " + response.message())
                        );
                    }
                }
            } catch (Exception e) {
                Platform.runLater(() ->
                  userStatusLabel.setText("❌ Lỗi tìm kiếm")
                );
                System.err.println("❌ Search error: " + e.getMessage());
            }
        });

        searchThread.setDaemon(true);
        searchThread.start();
    }

    // ============ PRODUCT LOADING ============

    /**
     * Load products from server
     */
    private void loadProductsFromServer() {
        Thread thread = new Thread(() -> {
            try {
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
                        @SuppressWarnings("unchecked")
                        List<AuctionDTO> products = (List<AuctionDTO>) response.data();
                        cachedProducts = products;
                        Platform.runLater(() -> displayProducts(products));
                    }
                }
            } catch (Exception e) {
                Platform.runLater(() ->
                  userStatusLabel.setText("❌ Lỗi load sản phẩm")
                );
                System.err.println("❌ Load products error: " + e.getMessage());
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Display products in UI
     */
    private void displayProducts(List<AuctionDTO> products) {
        productsPane.getChildren().clear();
        for (AuctionDTO auction : products) {
            productsPane.getChildren().add(createProductCard(auction));
        }
        System.out.println("📊 Displayed " + products.size() + " products");
    }

    /**
     * Create product card UI component
     */
    private VBox createProductCard(AuctionDTO auction) {
        VBox card = new VBox(10);
        card.setStyle("-fx-border-color: #ddd; -fx-padding: 0 0 10 0; -fx-border-radius: 5; " +
          "-fx-background-radius: 5; -fx-overflow: hidden;");
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(200);

        // Countdown label
        Label labelCountdown = new Label("⏳ Loading...");
        labelCountdown.setMaxWidth(Double.MAX_VALUE);
        labelCountdown.setAlignment(Pos.CENTER);
        labelCountdown.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; " +
          "-fx-font-weight: bold; -fx-padding: 5; -fx-background-radius: 5 5 0 0;");

        // Image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);

        if (auction.itemImage() != null && !auction.itemImage().isEmpty()) {
            try {
                imageView.setImage(new Image(auction.itemImage(), true));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading image");
            }
        }

        // Info box
        VBox infoBox = new VBox(5);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.setPadding(new Insets(0, 5, 0, 5));

        Label nameLabel = new Label(auction.itemName());
        nameLabel.setWrapText(true);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");

        Label priceLabel = new Label("💰 " + String.format("%.0f", auction.currentHighestBid()) + " đ");
        priceLabel.setStyle("-fx-font-size: 11; -fx-text-fill: red;");

        Label sellerLabel = new Label("👤 " + auction.sellerUsername());
        sellerLabel.setStyle("-fx-font-size: 10;");

        Button detailButton = new Button("Xem chi tiết");
        detailButton.setPrefWidth(130);
        detailButton.setStyle("-fx-font-size: 10; -fx-cursor: hand;");
        detailButton.setOnAction(event -> {
            try {
                DetailController.selectedAuction = auction;
                SceneManager.getInstance().changeScene("detail-view.fxml", 900, 600);
            } catch (IOException e) {
                System.err.println("❌ Error: " + e.getMessage());
            }
        });

        infoBox.getChildren().addAll(nameLabel, priceLabel, sellerLabel, detailButton);
        card.getChildren().addAll(labelCountdown, imageView, infoBox);

        // Start countdown
        try {
            LocalDateTime endLDT = LocalDateTime.ofInstant(auction.endTime(), ZoneId.systemDefault());
            startCountdown(labelCountdown, endLDT);
        } catch (Exception e) {
            labelCountdown.setText("❌ Lỗi");
        }

        return card;
    }

    // ============ CLOCK & COUNTDOWN ============

    /**
     * Initialize clock display
     */
    private void initClock(Label time, Label date) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy",
          new Locale("vi", "VN"));

        Timeline clock = new Timeline(
          new KeyFrame(Duration.ZERO, event -> {
              LocalDateTime now = LocalDateTime.now();
              time.setText(now.format(timeFormatter));
              date.setText(now.format(dateFormatter));
          }),
          new KeyFrame(Duration.seconds(1))
        );

        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }

    /**
     * Start countdown timer for auction
     */
    private void startCountdown(Label labelCountdown, LocalDateTime endTime) {
        Timeline timeline = new Timeline(
          new KeyFrame(Duration.seconds(1), event -> {
              LocalDateTime now = LocalDateTime.now();
              java.time.Duration duration = java.time.Duration.between(now, endTime);

              if (duration.isNegative() || duration.isZero()) {
                  labelCountdown.setText("⏰ ĐÃ KẾT THÚC");
                  labelCountdown.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; " +
                    "-fx-font-weight: bold; -fx-padding: 5; -fx-background-radius: 5 5 0 0;");
              } else {
                  long days = duration.toDays();
                  long hours = duration.toHoursPart();
                  long minutes = duration.toMinutesPart();
                  long seconds = duration.toSecondsPart();

                  String timeLeft = String.format("⏳ %02d:%02d:%02d:%02d", days, hours, minutes, seconds);
                  labelCountdown.setText(timeLeft);

                  if (days == 0 && hours == 0 && minutes < 10) {
                      labelCountdown.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                        "-fx-font-weight: bold; -fx-padding: 5; -fx-background-radius: 5 5 0 0;");
                  }
              }
          })
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    // ============ SERVER LISTENER ============

    /**
     * Start listening for server messages (real-time updates)
     */
    private void startServerListener() {
        serverListenerThread = new Thread(() -> {
            try {
                ObjectInputStream in = AppContext.getInstance().getIn();

                while (isListening && AppContext.getInstance().isConnected()) {
                    try {
                        MessageProtocol msg = (MessageProtocol) in.readObject();
                        handleServerMessage(msg);
                    } catch (EOFException e) {
                        System.out.println("⚠️ Server connection closed");
                        break;
                    }
                }
            } catch (Exception e) {
                System.err.println("❌ Server listener error: " + e.getMessage());
            }
        });

        serverListenerThread.setDaemon(true);
        serverListenerThread.start();
    }

    /**
     * Handle real-time messages from server
     */
    private void handleServerMessage(MessageProtocol msg) {
        if ("NEW_BID".equals(msg.type())) {
            Platform.runLater(this::loadProductsFromServer);
        } else if ("AUCTION_STARTED".equals(msg.type()) || "AUCTION_ENDED".equals(msg.type())) {
            Platform.runLater(this::loadProductsFromServer);
        }
    }

    /**
     * Cleanup on window close
     */
    @FXML
    public void onWindowClose() {
        isListening = false;
        if (serverListenerThread != null) {
            serverListenerThread.interrupt();
        }
    }
}