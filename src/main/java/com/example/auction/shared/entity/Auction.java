package com.example.auction.shared.entity;
// lớp này thể hiện 1 phiên đấu giá có gì
// 1 phiên có:
// 1 item dc bán, 1 seller, có nhiều bids từ nhiều bidders, có startTime, endTime
// có currentHighestBid (giá cao nhất hiện tại), có status (PENDING,ACTIVE, CLOSED, COMPLETED, CANCELLED)
// có winner (bidder có giá cao nhất khi hết tgian)

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

// Thêm timeProvider để inject thời gian (cho phép test control time)
// Không dùng timeProvider.get() trực tiếp trong method
// Mặc định dùng timeProvider.get(), test có thể inject fixed time
// Mọi check thời gian đều qua timeProvider.get()

// Validation: End > Start (logic), START > now (ở Service layer) ở AuctionService

// kế thừa Entity để có id của phiên đấu giá và thời gian tạo thời gian sửa phiên đấu giá
public class Auction extends Entity {
    private Item item;
    private String sellerUsername;
    private AuctionStatus status;

    private Instant startTime;
    private Instant endTime;
    
    private double currentHighestBid;
    private String currentHighestBidderUsername;

    private String winnerUsername;
    private double finalPrice;

    private List<Bid> bidHistory;  // Lịch sử các lần đấu giá
    // QUAN TRỌNG: timeProvider để inject thời gian
    // Lý do cần timeProvider:
    // Production: Supplier = Instant::now (dùng thời gian thực)
    // Test: Supplier = () -> FIXED_TIME (dùng thời gian cố định)
    // Nếu không có timeProvider:
    // Test sẽ phụ thuộc vào thời gian hệ thống
    // Test result sẽ không deterministic (không ổn định)
    // Cùng test code, chạy lần này pass, lần khác fail
    // Với timeProvider:
    // Test có thể kiểm soát thời gian
    // Kết quả luôn consistent
    // Supplier để inject thời gian (cho test)
    private Supplier<Instant> timeProvider;

    // Khi tạo Auction lúc production, không cần truyền timeProvider
    // Mặc định sẽ dùng Instant::now (thời gian thực)
    public Auction(Item item, String sellerUsername,
                   Instant startTime, Instant endTime) {
        this(item, sellerUsername, startTime, endTime, Instant::now);
    }

    // Constructor có timeProvider (dùng cho test)
    //  Khi test, có thể truyền Supplier tùy chỉnh để kiểm soát thời gian
    //  Ví dụ:
    //  Supplier<Instant> fixedTime = () -> Instant.parse("2026-04-11T10:00:00Z");
    //  Auction auction = new Auction(item, "seller", START, END, fixedTime);
    public Auction(Item item, String sellerUsername, Instant startTime, Instant endTime, Supplier<Instant> timeProvider) {
        super();
        // khởi tạo trước rồi mới ném ngoại lệ để kh bị spotbugs ấy
        this.timeProvider = timeProvider;
        if (item == null) {
            throw new IllegalArgumentException("Item không được null");
        }
        if (sellerUsername == null || sellerUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("Seller username không được để trống");
        }
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start/end time không được là null");
        }
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End time phải sau start time");
        }
        this.item = item;
        this.sellerUsername = sellerUsername;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = AuctionStatus.PENDING;
        this.currentHighestBid = item.getStartPrice(); // Giá khởi điểm là giá cao nhất luôn
        this.currentHighestBidderUsername = null;      // Chưa có ai đấu giá nên là null
        this.winnerUsername = null;                    // Chưa có người thắng
        this.finalPrice = 0.0;                         // Chưa có giá final
        this.bidHistory = new ArrayList<>();  // khowirr tạo lịch sử bids
    }
    // copy constructor
    public Auction(Auction other) {
        super(other.getId());
        this.timeProvider = other.timeProvider;
        this.item = other.item;
        this.sellerUsername = other.sellerUsername;
        this.status = other.status;
        this.startTime = other.startTime;
        this.endTime = other.endTime;
        this.currentHighestBid = other.currentHighestBid;
        this.currentHighestBidderUsername = other.currentHighestBidderUsername;
        this.winnerUsername = other.winnerUsername;
        this.finalPrice = other.finalPrice;
        this.bidHistory = new ArrayList<>(other.bidHistory);
        this.setCreatedAt(other.getCreatedAt());
        this.setUpdatedAt(other.getUpdatedAt());
    }
    public Item getItem() {
        return item;
    }
    public String getSellerUsername() {
        return sellerUsername;
    }
    public AuctionStatus getStatus() {
        return status;
    }
    public Instant getStartTime() {
        return startTime;
    }
    public Instant getEndTime() {
        return endTime;
    }
    public double getCurrentHighestBid() {
        return currentHighestBid;
    }
    public String getCurrentHighestBidderUsername() {
        return currentHighestBidderUsername;
    }
    public String getWinnerUsername() {
        return winnerUsername;
    }
    public double getFinalPrice() {
        return finalPrice;
    }

    // Lấy lịch sử bids
    // DEFENSIVE COPY: Trả về copy, không phải original
    // Lý do: Tránh client thay đổi bidHistory trực tiếp
    public List<Bid> getBidHistory() {
        return new ArrayList<>(bidHistory);
    }

    public int getBidCount() {
        return bidHistory.size();
    }
    public void setStatus(AuctionStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status không được null");
        }
        this.status = status;
        this.setUpdatedAt(timeProvider.get());
    }
    // LẤY THỜI GIAN TỪ timeProvider.get(), KHÔNG PHẢI timeProvider.get()
    // Lý do: Cho phép test inject thời gian cố định
    public boolean hasStarted() {
        // code trước: return timeProvider.get().isAfter(startTime);
        return timeProvider.get().isAfter(startTime);
    }
    // Dùng timeProvider thay vì timeProvider.get()
    public boolean isOngoing() {
        Instant now = timeProvider.get();
        // code trước: return now.isAfter(startTime) && now.isBefore(endTime);
        return now.isAfter(startTime) && now.isBefore(endTime);
    }

    public boolean hasEnded() {
        // code trước: return timeProvider.get().isAfter(endTime);
        return timeProvider.get().isAfter(endTime);
    }
    public long getTimeRemainingSeconds() { // tính thời gian còn lại bằng giây
        Instant now = timeProvider.get();
        if (now.isAfter(endTime)) {
            return 0;
        }
        return ChronoUnit.SECONDS.between(now, endTime); // trả về thời gian tính bănằng giây giữa now với endTime
    }
    public boolean addBid(Bid bid) {
        if (status != AuctionStatus.ACTIVE) { // kiểm tra xem status đang là gì, nếu khác active mà active là đang diễn ra thì sẽ trả vaf false kh thêm đặt giá dc
            return false;
        }
        if (bid.getAmount() <= currentHighestBid ) {
            return false;
        }
        if (bid.getBidderUsername().equals(sellerUsername)) { // kh dc cho người seller đấu giá vì sẽ gây gian lận
            return false;
        }
        this.currentHighestBid = bid.getAmount();
        this.currentHighestBidderUsername = bid.getBidderUsername();
        this.bidHistory.add(bid);
        this.setUpdatedAt(timeProvider.get());
        return true;
    }
    // Dùng timeProvider để check start time
    // bắt đầu phiên đấu giá cần:
    // Status phải là PENDING
    // Thời gian hiện tại phải >= startTime
    public void startAuction() {
        if (status != AuctionStatus.PENDING) {
            throw new IllegalStateException("chỉ có thể start phiên đang Pending");
        }
//        if (!hasStarted()) {
//            throw new IllegalStateException("Thời gian bắt đầu chưa đến");
//        }
        this.status = AuctionStatus.ACTIVE;
        this.setUpdatedAt(timeProvider.get());
    }
    //  Dùng timeProvider để check end time
    // Đóng phiên đấu giá
    // Chuyển status: ACTIVE/PENDING → CLOSED
    // Điều kiện:
    // Status không được là CLOSED hoặc COMPLETED
    // Thời gian phải >= endTime
    // Nếu có bid → set winner và finalPrice
    public void closeAuction() {
        if (status == AuctionStatus.CLOSED || status == AuctionStatus.COMPLETED ) {
            throw new IllegalStateException("Phiên đã kết thúc rồi");
        }
//        if (!hasEnded()) {
//            throw new IllegalStateException("Thời gian kết thúc chưa đến");
//        }
        if (!bidHistory.isEmpty()) {
            this.winnerUsername = currentHighestBidderUsername;
            this.finalPrice = currentHighestBid;
        }
        this.status = AuctionStatus.CLOSED;
        this.setUpdatedAt(timeProvider.get());
    }
    // hoàn tất phiên đấu giá
    // Chuyển status: CLOSED → COMPLETED
    // Điều kiện:
    // Status phải CLOSED
    // Lý do tách startAuction, closeAuction, completeAuction:
    // startAuction: Phiên bắt đầu nhận bid
    // closeAuction: Phiên kết thúc nhận bid (xác định winner)
    // completeAuction: Thanh toán xong, chuyển giao xong (cuối cùng)
    public void completeAuction() {
        if (status != AuctionStatus.CLOSED) {
            throw new IllegalStateException("Chỉ có thể complete phiên mà đã CLOSED");
        }
        this.status = AuctionStatus.COMPLETED;
        this.setUpdatedAt(timeProvider.get());
    }
    // hủy phiên đấu giá
    // Chuyển status: ANY → CANCELLED
    // Điều kiện:
    // Không được hủy phiên đã COMPLETED
    public void cancelAuction() {
        if (status == AuctionStatus.COMPLETED) {
            throw new IllegalStateException("Không thể hủy phiên đã hoàn tất");
        }

        this.status = AuctionStatus.CANCELLED;
        this.setUpdatedAt(timeProvider.get());
    }
    public boolean isSellerOf(String username) { // xem user nào đấy là người bán kh
        return sellerUsername.equals(username);
    }
    public boolean hasWinner() { // xem có người win chưa
        return winnerUsername != null && !winnerUsername.isEmpty();
    }
    @Override
    public String toString() {
        return "Auction{" +
                "id=" + getId() +
                ", item=" + item.getItemName() +
                ", status=" + status +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", currentBid=" + String.format("%.2f", currentHighestBid) +
                ", bidCount=" + bidHistory.size() +
                ", winner=" + winnerUsername +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Auction auction = (Auction) o;
        return getId() != null && getId().equals(auction.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}
