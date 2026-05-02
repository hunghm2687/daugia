package com.example.auction.shared.dto;
// tương tự BidderProfile
import java.io.Serializable;

public class SellerProfileDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private double shopRating;

    // Public constructor (để Jackson/Gson deserialize)
    public SellerProfileDTO() {
        this.shopRating = 5.0;
    }
    public SellerProfileDTO(double shopRating) {
        this.shopRating = shopRating;
    }
    public double getShopRating() {
        return shopRating;
    }
    @Override
    public String toString() {
        return "SellerProfileDTO{shopRating= " + shopRating;
    }
}
