package com.example.auction.shared.entity;
// Lớp User đại diện cho một người dùng trong hệ thống đấu giá.
// kế thừa Enity có id, createAt, updateAt
// quanr lý profile:
// user có username, password, email, mỗi user có 1 role (admin. member, guest)
// member có thể có bidderProfile và sellerProfile, admin kh có profile

public class User extends Entity {
    private String username;
    private String password;
    private String email;
    private Role role;
    private String status; // trang thai tk : active, banned (khi bi admin xoa hoac cam)

    private BidderProfile bidderProfile;
    private SellerProfile sellerProfile;

    public User() {
        super(); // gọi constructor của Entity để set createAt, updateAt,...
    }
    public User(String username, String password, String email, Role role) {
        super();
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.status = "ACTIVE"; // mac dinh moi tao la active
        if (this.role == Role.MEMBER) {
            this.bidderProfile = new BidderProfile(0.0); // mawjc didnhj cu tao tk la dc di dau gia
            this.sellerProfile = null; //  ch dki ban hang thi ho so ban hang la null
            // vi admin kh la ng ban hoac khach dau gia dc, chi quan ly va guest chi xem
        }
        else  { // admin và guest, // vi admin kh la ng ban hoac khach dau gia dc, chi quan ly va guest chi xem
            this.bidderProfile = null;
            this.sellerProfile = null;
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username không được để trống");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password không được để trống");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống");
        }
        if (role == null) {
            throw new IllegalArgumentException("Role không được null");
        }
    }
    // Copy constructor:
    // dùng cho socket communication: gửi copy user thay vì original
    // defensive programing: tránh client modify user
    public User(User other) {
        super(other.getId()); // copy id từ Entity
        this.username = other.username;
        this.password = other.password;
        this.email = other.email;
        this.role = other.role;
        this.status = other.status;
        this.setCreatedAt(other.getCreatedAt()); // copy createAt
        this.setUpdatedAt(other.getUpdatedAt()); // copy updateAt
        this.bidderProfile = other.bidderProfile != null ? new BidderProfile(other.bidderProfile) : null;
        this.sellerProfile = other.sellerProfile != null ? new SellerProfile(other.sellerProfile) : null;
    }
    // ham kich hoat quyen ban hang, khi ng dung bam "Dang ky lam ng ban"
    // chỉ member mới dc đăng kí, nếu ch có sellerProfile thì tạo mới với rate = 5, có r thì kh tạo lại
    public void registerAsSeller() { //
        if (role != Role.MEMBER) {
            throw new IllegalStateException("Chỉ member mới được đăng kí bán");
        }
        if (sellerProfile == null) {
            sellerProfile = new SellerProfile(5.0); // tạo mới với rate = 5;
            this.setUpdatedAt(java.time.Instant.now());
        }
    }
    // kiểm tra tk conf hoạt động dc kh
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
    // kiểm tra tk có bị ban kh
    public boolean isBanned() {
        return "BANNED".equals(status);
    }
    // ham kiem tra xem user nay co quyen ban hang kh
    public boolean isSeller() {
        return this.sellerProfile != null;
        // kiểm tra xem Member này đã đăng ký làm người bán ch để cho phép tạo phiên đấu giá, vì ng bán có thể tạo phiên đấu giá
    }
    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }
    // kiểm tra xem user có thể đấu giá kh
    public boolean canBid() {
        return bidderProfile != null;
    }

    public void addBalance(double amount) {
        if (bidderProfile != null) {
            bidderProfile.addBalance(amount);
            this.setUpdatedAt(java.time.Instant.now());
        }
    }
    // trừ tiền khi đấu giá thành công
    public boolean deductBalance(double amount) {
        if (bidderProfile != null) {
            boolean success = bidderProfile.deductBalance(amount);
            if (success) {
                this.setUpdatedAt(java.time.Instant.now());
            }
            return success;
        }
        return false;
    }
    // update rating shop
    public void updateSellerRating(double rating) {
        if (sellerProfile != null) {
            sellerProfile.updateRating(rating);
            this.setUpdatedAt(java.time.Instant.now());
        }
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public String getEmail() {
        return email;
    }
    public Role getRole() {
        return role;
    }
    public String getStatus() {return status; }

    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username không được để trống");
        }
        this.username = username;
        this.setUpdatedAt(java.time.Instant.now());
    }
    public void setPassword(String password) {
        this.password = password;
        this.setUpdatedAt(java.time.Instant.now());
    }
    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống");
        }
        this.email = email;
        this.setUpdatedAt(java.time.Instant.now());
    }
    public void setRole(Role role) {
        this.role = role;
        this.setUpdatedAt(java.time.Instant.now());
    }
    public void setStatus(String status) {
        if (!("ACTIVE".equals(status) || "BANNED".equals(status))) {
            throw new IllegalArgumentException("Status phải là ACTIVE hoặc BANNED");
        }
        this.status = status;
        this.setUpdatedAt(java.time.Instant.now());
    }
    // Lấy BidderProfile (Defensive copy)
    // phải trả về copy chứ kh dc trả về original kh thì bị sửa từ bên ngoài
    // nhưng luu ý: modify trên copy sẽ kh ảnh hưởng đến user nên lúc muốn sửa đổi thật thì nó lại kh thay đổi
    // BidderProfile profile = user.getBidderProfile();
    // profile.addBalance(100);  // Chỉ modify copy, user không thay đổi
    // nên lúc nào muốn thay đổi thật thì phải dùng qua method của User
    // user.addBalance(100);
    // user.deductBalance(50);
    // Client có thể modify trực tiếp
    // BidderProfile profile = user.getBidderProfile();
    // profile.setBalance(999_999_999);  // Hack balance!
    // // Client modify copy không ảnh hưởng user
    // BidderProfile profile = user.getBidderProfile();
    // profile.addBalance(1_000_000);  // Chỉ modify copy
    // user.getBidderBalance() vẫn = 0
    // nghĩa là kh muốn addBalance hay gì thì phải dùng qua method trong class User chứ kh dc dùng trong class BidderProfile và SellerProfile
    public BidderProfile getBidderProfile() {
        if (bidderProfile == null) return null;
        else
            return new BidderProfile(bidderProfile); // return copy
    }
    // Seller cũng giống Bidder ở trên trả về copy
    public SellerProfile getSellerProfile() {
        if (sellerProfile == null) return null;
        else
            return new SellerProfile(sellerProfile);
    }
    // l balance của Bidder
    public double getBidderBalance() {
        if (bidderProfile != null) {
            return bidderProfile.getBalance();
        }
        return 0.0;
    }
    // lấy shop rating của Seller
    public double getSellerRating() {
        if (sellerProfile != null) {
            return sellerProfile.getShopRating();
        }
        return 0.0;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", status='" + status + '\'' +
                ", balance=" + getBidderBalance() +
                ", rating=" + String.format("%.1f", getSellerRating()) +
                ", createdAt=" + getCreatedAt() +
                '}';
    }
    // equals dựa vào username (username là duy nhất)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;
        return username.equals(user.username);  // User duy nhất dựa trên username
    }
    // hashcode consistent với equals
   @Override
    public int hashCode() {
        return username.hashCode();
    }
}
