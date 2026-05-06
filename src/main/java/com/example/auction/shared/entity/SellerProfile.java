//package com.example.auction.shared.entity;
//
//import java.io.Serializable;
//// lưu thông tin người bán (seller)
//// Mỗi User (Role.MEMBER) khi đăng ký làm seller sẽ có 1 SellerProfile và có shopRating từ 0 đến 5
//
//class SellerProfile implements Serializable {
//    private static final long serialVersionUID = 1L;
//    private double shopRating;
//
//    SellerProfile(double initialRating) {
//        if (initialRating < 0 || initialRating > 5) {
//            throw new IllegalArgumentException("Rating phải từ 0 đến 5");
//        }
//        this.shopRating = initialRating;
//    }
//
//    // Copy constructor
//    SellerProfile(SellerProfile other) {
//        if (other == null) {
//            this.shopRating = 5.0;
//        }
//        else {
//            this.shopRating = other.shopRating;
//        }
//    }
//
//    double getShopRating() {
//        return this.shopRating;
//    }
//
//    // cập nhất rating ( khi ng mua đánh giá hoặc admin cập nhật )
//    void setShopRating(double shopRating) {
//        if (shopRating < 0 || shopRating > 5) {
//            throw new IllegalArgumentException("Rating phải từ 0 đến 5");
//        }
//        else {
//            this.shopRating = shopRating;
//        }
//    }
//
//    // cập nhất rating dựa trên đánh giá mới, new rate = (new rate + old rate) / 2
//    void updateRating(double newRating) {
//        if (newRating <0 || newRating > 5) {
//            throw new IllegalArgumentException("Rating phải từ 0 đến 5");
//        }
//        this.shopRating = (this.shopRating + newRating) / 2.0 ;
//    }
//}
package com.example.auction.shared.entity;

import java.io.Serializable;
import java.time.Instant;

/**
 * SellerProfile - Thông tin cửa hàng bán hàng
 */
public class SellerProfile implements Serializable {
    private static final long serialVersionUID = 1L;

    private double shopRating;
    private String shopName;
    private String shopDescription;
    private String shopImage;
    private int totalSold;
    private int totalReviews;
    private double totalCommission;
    private Instant shopCreatedAt;

    public SellerProfile(double initialRating) {
        if (initialRating < 0 || initialRating > 5) {
            throw new IllegalArgumentException("Rating phải từ 0 đến 5");
        }
        this.shopRating = initialRating;
        this.totalSold = 0;
        this.totalReviews = 0;
        this.totalCommission = 0;
        this.shopCreatedAt = Instant.now();
    }

    public SellerProfile(SellerProfile other) {
        if (other == null) {
            this.shopRating = 5.0;
            this.totalSold = 0;
            this.totalReviews = 0;
            this.totalCommission = 0;
            this.shopCreatedAt = Instant.now();
        } else {
            this.shopRating = other.shopRating;
            this.shopName = other.shopName;
            this.shopDescription = other.shopDescription;
            this.shopImage = other.shopImage;
            this.totalSold = other.totalSold;
            this.totalReviews = other.totalReviews;
            this.totalCommission = other.totalCommission;
            this.shopCreatedAt = other.shopCreatedAt;
        }
    }

    // ============ GETTERS ============
    public double getShopRating() { return this.shopRating; }
    public String getShopName() { return shopName; }
    public String getShopDescription() { return shopDescription; }
    public String getShopImage() { return shopImage; }
    public int getTotalSold() { return totalSold; }
    public int getTotalReviews() { return totalReviews; }
    public double getTotalCommission() { return totalCommission; }
    public Instant getShopCreatedAt() { return shopCreatedAt; }

    // ============ SETTERS ============
    public void setShopRating(double shopRating) {
        if (shopRating < 0 || shopRating > 5) {
            throw new IllegalArgumentException("Rating phải từ 0 đến 5");
        }
        this.shopRating = shopRating;
    }

    public void setShopName(String shopName) {
        if (shopName == null || shopName.trim().isEmpty()) {
            throw new IllegalArgumentException("Shop name không được để trống");
        }
        this.shopName = shopName;
    }

    public void setShopDescription(String shopDescription) {
        this.shopDescription = shopDescription;
    }

    public void setShopImage(String shopImage) {
        this.shopImage = shopImage;
    }

    public void updateRating(double newRating) {
        if (newRating < 0 || newRating > 5) {
            throw new IllegalArgumentException("Rating phải từ 0 đến 5");
        }
        this.shopRating = (this.shopRating + newRating) / 2.0;
        this.totalReviews++;
    }

    public void addSale(double amount) {
        this.totalSold++;
        this.totalCommission += amount * 0.05; // 5% commission
    }

    @Override
    public String toString() {
        return "SellerProfile{" +
          "shopName='" + shopName + '\'' +
          ", shopRating=" + String.format("%.1f", shopRating) +
          ", totalSold=" + totalSold +
          ", totalReviews=" + totalReviews +
          ", totalCommission=" + String.format("%.0f", totalCommission) +
          '}';
    }
}