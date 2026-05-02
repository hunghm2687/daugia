package com.example.auction.shared.entity;
import java.time.Instant;
// Lớp Bid đại diện cho MỘT lần đặt giá trong một phiên đấu giá.
// mỗi bid :
// thuộc về 1 auction, do 1 bidder đặt, có số tiền, có thời gian đặt (bid time) , có status (SUCCESS, FAILED, OUTBID, CANCELLED)

public class Bid extends Entity {
    private Long auctionId; // id buổi đấu giá
    private String bidderUsername; // tên người đặt giá
    private double amount; // số tiền đặt
    private Instant bidTime; // thời gian đặt
    private BidStatus status; // trạng thái bid

    public Bid(Long auctionId, String bidderUsername, double amount) {
        super();
        this.auctionId = auctionId;
        this.bidderUsername = bidderUsername;
        this.amount = amount;
        this.bidTime = Instant.now();
        this.status = BidStatus.SUCCESS;  // Mặc định SUCCESS (sẽ đổi nếu thất bại)
        if ( auctionId == null || auctionId <= 0) {
            throw new IllegalArgumentException("AuctionId không hợp lệ");
        }
        if (bidderUsername == null || bidderUsername.isEmpty()) {
            throw new IllegalArgumentException("Bidder username không được để trống");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount phải > 0");
        }
    }
    // Constructor khác - với bidTime tùy chỉnh (dùng cho test)

    public Bid(Long auctionId, String bidderUsername, double amount, Instant bidTime) {
        this(auctionId, bidderUsername, amount);
        this.bidTime = bidTime;
    }

    public Bid(Bid other) {
        super(other.getId());
        this.auctionId = other.auctionId;
        this.bidderUsername = other.bidderUsername;
        this.amount = other.amount;
        this.bidTime = other.bidTime;
        this.status = other.status;

        this.setCreatedAt(other.getCreatedAt());
        this.setUpdatedAt(other.getUpdatedAt());
    }
    public Long getAuctionId() {
        return auctionId;
    }

    public String getBidderUsername() {
        return bidderUsername;
    }

    public double getAmount() {
        return amount;
    }

    public Instant getBidTime() {
        return bidTime;
    }

    public BidStatus getStatus() {
        return status;
    }
    public void setStatus(BidStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("status không được null");
        }
        this.status = status;
        this.setUpdatedAt(Instant.now());
    }
    // đánh dấu là bị OUTBID (bị vượt qua), chỉ có thể outbid nếu status = SUCCESS
    public void markOutbid() {
        if (status != BidStatus.SUCCESS) {
            throw new IllegalStateException("Chỉ bid SUCCESS mới có thể thấy outbid");
        }
        this.status = BidStatus.OUTBID;
        this.setUpdatedAt(Instant.now());
    }
    // đánh dấu bid là FAILED
    public void markAsFailed(String reason) {
        this.status = BidStatus.FAILED;
        this.setUpdatedAt(Instant.now());
        // thêm reason nếu muốn lưu lý do thất bại ( ch nghĩ ra ) ...
    }
    // đánh dấu bid là bị CANCELLED (hủy)
    public void markcancel() {
        this.status = BidStatus.CANCELLED;
        this.setUpdatedAt(Instant.now());
    }
    // kiểm tra bid có ACTICE không
    public boolean isActive() {
        return status == BidStatus.SUCCESS;
    }
    // kiểm tra bid có thành công không
    public boolean isSuccessful() {
        return status == BidStatus.SUCCESS;
    }
    @Override
    public String toString() {
        return "Bid{" +
                "id=" + getId() +
                ", auctionId=" + auctionId +
                ", bidder='" + bidderUsername + '\'' +
                ", amount=" + String.format("%.2f", amount) +
                ", bidTime=" + bidTime +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bid bid = (Bid) o;
        return auctionId.equals(bid.auctionId) &&
                bidderUsername.equals(bid.bidderUsername) &&
                bidTime.equals(bid.bidTime);
    }

    @Override
    public int hashCode() {
        return (auctionId + "_" + bidderUsername + "_" + bidTime).hashCode();
    }
}
