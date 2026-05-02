package com.example.auction.shared.entity;
// Enum định nghĩa trạng thái của 1 lần đặt giá (bid)
// trạng thái có:
// - SUCCESS: Bid được chấp nhận (là giá cao nhất)
// - FAILED: Bid bị từ chối (giá không hợp lệ, account hết tiền,...)
// - OUTBID: Bid bị vượt qua bởi bid khác
// - CANCELLED: Bid bị hủy

public enum BidStatus {
    SUCCESS("Success", "Bid được chấp nhận"), // bid dc chấp nhận, là giá cao nhất tại thời điểm đặt, Bidder là "current highest bidder
    FAILED("Failed", "Bid bị từ chối"), // bid bị từ chối, giá thấp hơn hiện tại, bidder kh đủ tiền, phiên kh active, bidder là seller
    OUTBID("Outbid", "Bid bị vượt qua"), // bid vị vượt qua, ban đầu là highest bid nhưng sau đó có bid cao hơn, bidder bị vuowjt qua
    CANCELLED("Cancelled", "Bid đã hủy"); // bid bị hủy, bidder hủy, admin hủy, phiên bị hủy

    private final String displayName;
    private final String mieuta;

    BidStatus(String displayName, String mieuta) {
        this.displayName = displayName;
        this.mieuta = mieuta;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return mieuta;
    }
    // kiểm tra có bid thành công kh
    public boolean isSuccess() {
        return this == SUCCESS;
    }
    // kiểm tra bid có active không (ch bị outbid hoặc bị hủy)
    public boolean isActive() {
        return this == SUCCESS;
    }

}
