package com.example.auction.shared.dto;
import java.io.Serializable;

// DTO cho BidderProfile
// Serializable: Gửi qua network dc
// Immutable: Chỉ getter, không setter để kh thay đổi dc

// tạo BidderProfileDTO vì :
// BidderProfile (entity) là package-private → Không serialize
// BidderProfileDTO (DTO) là public → Serialize được
// Transfer data từ Server → Client qua socket

public class BidderProfileDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private double balance;

    // Public constructor (để Jackson/Gson deserialize)
    public BidderProfileDTO() {
        this.balance = 0.0;
    }

    public BidderProfileDTO(double balance) {
        this.balance = balance;
    }
    public double getBalance() {
        return balance;
    }
    @Override
    public String toString() {
        return "BidderProfileDTO{balance= " + balance;
    }
}
