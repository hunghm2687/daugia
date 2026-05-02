module com.auction {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    opens com.example.auction.client to javafx.graphics;
    opens com.example.auction.client.view to javafx.fxml;
    exports com.example.auction.client.view;
}