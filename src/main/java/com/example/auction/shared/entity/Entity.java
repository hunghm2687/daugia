// Hệ thống đấu giá có nhiều loại object như user, seller, bidder, auction, bid
// taast cả đều có thuộc tính chung nên ta cần 1 lớp abstract để kh phải viết lại
// INHERITANCE ví dụ sau có public class User extends Entity, User có tất cả thuộc tính của Entity
// có kế thừa thì sẽ có đa hình POLYMORPHISM, Entity e1 = new User(...); Entity e2 = new Seller(...);

package com.example.auction.shared.entity; // khai báo package để tổ chức code

import java.io.Serializable;  // serializable để cho phép convert object thanhf byte ( lưu file, gửi qua network (socket) ) làm việc với object dc
// Implement Serializable để gửi qua network (Socket)
import java.time.Instant;  // lớp xử lí thời gian

/**
 *  Abstract base class cho tất cả entities trong hệ thống đấu giá.
 * Chứa các thuộc tính chung: id, createdAt, updatedAt
 */
// ABSTRACTION
public abstract class Entity implements Serializable {  // import xog còn phải implement Serializable nữa
    private static final long serialVersionUID = 1L;  // cần cho Serializable ( gắn cho 1 version cuar serializable ) // id duy nhất của entity
    // ENCAPSULATION ẩn thuộc tính, truy cập qua getter
    private Long id;  // id của đối tượng
    private Instant createdAt; // thời gian tạo đối tượng
    private Instant updatedAt; // thời gian cập nhat đối tượng cuối cùng

    // 2 Constructor vì có loại có id loại kh có id
    // Constructor mặc định
    public Entity() {
        this.id = null;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    // Constructor có id
    public Entity(Long id) {
        this.id = id;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }


    // GETTER & SETTER

    public Long getId() {
        return id;
    }

    public void setId(Long id) { //  Set id - thường dùng khi load từ DataBase
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) { // Set createdAt - thường không nên thay đổi sau khi tạo
        // nhưng giữ public để linh hoạt (ví dụ copy constructor)
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) { // Set updatedAt - gọi khi entity bị thay đổi
        this.updatedAt = updatedAt;
    }

    //  HELPER METHODS: dùng để debug, in thông tin của object

    @Override
    public String toString() {
        return "Entity{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}