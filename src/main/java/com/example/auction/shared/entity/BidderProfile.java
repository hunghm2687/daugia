package com.example.auction.shared.entity;

import java.io.Serializable;
// Lưu thông tin tài khoản của người đấu giá (bidder)
// Mỗi User (Role.MEMBER) sẽ có 1 BidderProfile, có balance: số dư tk


class BidderProfile implements Serializable {
    private static final long serialVersionUID = 1L;
    private double balance; // so du tk

    BidderProfile(double initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Số dư ban đầu không được âm");
        }
        this.balance = initialBalance; // so du ban dau
    }

    // Copy constructor
    // Defensive copy: Khi getter trả về profile, trả về copy chứ không phải original để tránh client thay đổi profile gốc từ getter
    // Serialization: khi gửi qua socket
    BidderProfile(BidderProfile other) {
        if (other == null) {
            this.balance = 0.0;
        }
        else {
            this.balance = other.balance;
        }
    }
    public double getBalance() {
        return this.balance;
    }

    // setbalance trực tiếp, để protected cho các lớp trong package này sử dụng
    // User sử dụng addBalance() và deductBalance() thay vì setBalance
    void setBalance(double balance) {
        if (balance < 0) {
            throw new IllegalArgumentException("Số dư không được âm");
        }
        this.balance = balance;
    }
    // nạp tiền vào tk
    void addBalance(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Số tiền phải > 0");
        }
        this.balance += amount;
    }
// kh để quền truy cập thì nghĩa là package-private
    // trừ tiền khi đấu giá thanhf công
    boolean deductBalance(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Số tiền phải > 0");
        }
        if (this.balance >= amount) {
            this.balance -= amount;
            return true; // trừ thành công
        }
        return false; // kh đủ tiền
    }
}
