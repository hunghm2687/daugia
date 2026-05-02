package com.example.auction.shared.entity;

import java.io.Serializable;
// lưu thông tin người bán (seller)
// Mỗi User (Role.MEMBER) khi đăng ký làm seller sẽ có 1 SellerProfile và có shopRating từ 0 đến 5

class SellerProfile implements Serializable {
    private static final long serialVersionUID = 1L;
    private double shopRating;

    SellerProfile(double initialRating) {
        if (initialRating < 0 || initialRating > 5) {
            throw new IllegalArgumentException("Rating phải từ 0 đến 5");
        }
        this.shopRating = initialRating;
    }

    // Copy constructor
    SellerProfile(SellerProfile other) {
        if (other == null) {
            this.shopRating = 5.0;
        }
        else {
            this.shopRating = other.shopRating;
        }
    }

    double getShopRating() {
        return this.shopRating;
    }

    // cập nhất rating ( khi ng mua đánh giá hoặc admin cập nhật )
    void setShopRating(double shopRating) {
        if (shopRating < 0 || shopRating > 5) {
            throw new IllegalArgumentException("Rating phải từ 0 đến 5");
        }
        else {
            this.shopRating = shopRating;
        }
    }

    // cập nhất rating dựa trên đánh giá mới, new rate = (new rate + old rate) / 2
    void updateRating(double newRating) {
        if (newRating <0 || newRating > 5) {
            throw new IllegalArgumentException("Rating phải từ 0 đến 5");
        }
        this.shopRating = (this.shopRating + newRating) / 2.0 ;
    }
}
