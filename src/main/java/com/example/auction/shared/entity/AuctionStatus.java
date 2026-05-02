package com.example.auction.shared.entity;
// lớp này thể hiện trạng thái phiên đấu giá
// enum định nghĩa các trạng thái của 1 phiên đấu giá
// trạng thái:
// pending: phiên đã tạo nhưng chưa bắt đầu
// active: phiên đang diễn ra, có thể đấu giá
// closed: phiên kết thúc nhưng chưa hoàn thành thanh toán
// completed: phiên đấu giá hoàn tất (đã thanh toán và chuyển giao)
// cancelled: phiên bị hủy (seller hủy hoặc admin hủy)

public enum AuctionStatus {
    PENDING("Pending", "Phiên chờ bắt đầu"), // phiên vừa tạo, seller tạo phiên, ch bắt đầu, ch ai dc đấu giá
    ACTIVE("Active", "Phiên đang diễn ra"), // phiên đang diễn ra, đã qua thời gian bắt đầu, còn trước tgian kết thúc, có thể đấu giá
    CLOSED("Closed", "Phiên kết thúc"), // phiên kết thúc, đã qua thời gian kết thúc, có người thắng, đang chờ thanh toán
    COMPLETED("Completed", "Phiên hoàn tất"), // phiên hoàn tất, đã thanh toán, đã chuyển giao, xog
    CANCELLED("Cancelled", "Phiên đã hủy"); // phiên bị hủy, seller huỷ trước khi bắt đầu, admin hủy vì lí do nào đó


    private final String displayName;
    private final String mieuta;

    AuctionStatus(String displayName, String mieuta) {
        this.displayName = displayName;
        this.mieuta = mieuta;
    }

    public String getDisplayName() {
        return displayName;
    }
    public String getMieuta() {
        return mieuta;
    }
    // kiểm tra phiên có đang diễn ra kh
    public boolean isActive() {
        return this == ACTIVE;
    }
    // kiểm tra phiên kết thúc chưa
    public boolean isFinished() {
        return this == CLOSED || this == COMPLETED || this == CANCELLED;
    }


}
