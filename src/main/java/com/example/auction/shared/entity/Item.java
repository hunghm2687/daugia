package com.example.auction.shared.entity;
//Abstract base class cho tất cả loại sản phẩm được đấu giá
// kế thừa từ Entity id, createAt, updateAt
// các subclass là - ArtItem (tranh vẽ, tượng,...) , ElectronicsItem (laptop, điện thoại,...)

public abstract class Item extends Entity {
    private String itemName;
    private String mieutaItem;
    private double startPrice;
    private String sellerUsername;

    public Item(String itemName, String mieutaItem, double startPrice, String sellerUsername) {
        super(); // Gọi Entity constructor để set createdAt, updatedAt
        this.itemName = itemName;
        this.mieutaItem = mieutaItem;
        this.startPrice = startPrice;
        this.sellerUsername = sellerUsername;
        if (itemName == null || itemName.trim().isEmpty()) {
            throw new IllegalArgumentException("Item name không được để trống");
        }
        if (startPrice <= 0) {
            throw new IllegalArgumentException("Start price phải > 0");
        }
        if (sellerUsername == null || sellerUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("Seller username không được để trống");
        }
    }

    public abstract String getDetailed(); // mỗi loại sp sẽ tự định nghĩa cách hiển thị thông tin chi tiết của nó

    public String getItemName() { return itemName; }
    public String getMieutaItem() { return mieutaItem; }
    public double getStartPrice() { return startPrice; }
    public String getSellerUsername() { return sellerUsername; }
    public void setItemName(String itemName) {
        if (itemName == null || itemName.trim().isEmpty()) {
            throw new IllegalArgumentException("Item name không được để trống");
        }
        this.itemName = itemName.trim();
        this.setUpdatedAt(java.time.Instant.now());
    }
    public void setMieutaItem(String mieutaItem) {
        if (mieutaItem == null || mieutaItem.trim().isEmpty()) {
            throw new IllegalArgumentException("Mô tả item không được để trống");
        }
        this.mieutaItem= mieutaItem.trim();
        this.setUpdatedAt(java.time.Instant.now());
    }
    public void setStartPrice(double startPrice) {
        if (startPrice <= 0) {
            throw new IllegalArgumentException("Start price phải > 0");
        }
        this.startPrice = startPrice;
        this.setUpdatedAt(java.time.Instant.now());
    }
    @Override
    public String toString() {
        return "Item{" +
                "id=" + getId() +
                ", itemName='" + itemName + '\'' +
                ", startPrice=" + String.format("%.2f", startPrice) +
                ", sellerUsername='" + sellerUsername + '\'' +
                ", createdAt=" + getCreatedAt() +
                '}';
    }

}
