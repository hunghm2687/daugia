package com.example.auction.server.service;

import com.example.auction.shared.entity.Auction;
import com.example.auction.shared.entity.AuctionStatus;
import com.example.auction.shared.entity.Item;
import java.time.Instant;

// lớp này để kiểm tra logic hệ thống, xác thực thời gian thực, chuyển đổi status, luật của hệ thống

public class AuctionService {

    // Tạo auction mới
    public Auction createAuction(Item item, String sellerUsername, Instant startTime, Instant endTime) {
        // Check 1: Real-time validation (không thể test nếu ở constructor)
        Instant now = Instant.now();
        if (startTime.isBefore(now)) {
            throw new IllegalArgumentException("Start time phải trong tương lai");
        }

        // Check 2: Tạo auction ( constructor sẽ xác thực logic )
        Auction auction = new Auction(item, sellerUsername, startTime, endTime);

        // add more logic (save to database, etc)
        // auctionRepository.save(auction)
        // thêm sau
        // TODO: auctionRepository.save(auction);

        return auction;
    }

    // Bắt đầu phiên
    public void startAuction(Auction auction) {
        if (auction.getStatus() != AuctionStatus.PENDING) {
            throw new IllegalStateException("Chỉ có thể start phiên khi đang là PENDING");
        }
        if (!auction.hasStarted()) {
            throw new IllegalStateException("Thời gian bắt đầu chưa đến");
        }
        auction.startAuction();
        // TODO: auctionRepository.save(auction);
    }

    // Đóng phiên
    public void closeAuction(Auction auction) {
        if (!auction.hasEnded()) {
            throw new IllegalStateException("Thời gian kết thúc chưa đến");
        }
        auction.closeAuction();
        // TODO: auctionRepository.save(auction);
     }

     // Hoàn tất phiên
    public void completedAuction(Auction auction) {
        if (auction.getStatus() != AuctionStatus.CLOSED) {
            throw new IllegalStateException("Phiên phải CLOSED mới có thể complete");
        }

        auction.completeAuction();
        // TODO: auctionRepository.save(auction);

        // TODO: sau này thêm: chuyển tiền, update user ratings, etc
    }

    // Hủy phiên
    public void cancelAuction(Auction auction) {
        if (auction.getStatus() == AuctionStatus.COMPLETED) {
            throw new IllegalStateException("Không thể hủy phiên đã COMPLETED");
        }

        auction.cancelAuction();
        // TODO: auctionRepository.save(auction);
    }
}
