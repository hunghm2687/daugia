package com.example.auction.shared.entity;
// lowps electronics đại diện cho sp điện từ: laptop, điện thoại,...

public class ElectronicsItem extends Item {
    private String brand;
    private int warranty; // bảo hành (tháng)

    public ElectronicsItem(String itemName, String mieutaItem, double startPrice, String sellerUsername, String brand, int warranty) {
        super(itemName,mieutaItem,startPrice,sellerUsername);
        this.brand = brand;
        this.warranty = warranty;
        if (brand == null || brand.trim().isEmpty()) {
            throw new IllegalArgumentException("Brand không được để trống");
        }
        if (warranty < 0) {
            throw new IllegalArgumentException("Warranty phải >= 0");
        }
    }

    @Override
    public String getDetailed() {
        return "Electronics [Brand: " + brand + ",Warranty: " + warranty + " months]";
    }
    public String getBrand() {
        return brand;
    }

    public int getWarranty() {
        return warranty;
    }
    public void setBrand(String brand) {
        this.brand = brand;
        this.setUpdatedAt(java.time.Instant.now());
    }

    public void setWarranty(int warranty) {
        if (warranty < 0) {
            throw new IllegalArgumentException("Warranty không được âm");
        }
        this.warranty = warranty;
        this.setUpdatedAt(java.time.Instant.now());
    }
    @Override
    public String toString() {
        return "ElectronicsItem{" +
                "id=" + getId() +
                ", itemName='" + getItemName() + '\'' +
                ", brand='" + brand + '\'' +
                ", warranty=" + warranty + " months" +
                ", startPrice=" + String.format("%.2f", getStartPrice()) +
                ", sellerUsername='" + getSellerUsername() + '\'' +
                '}';
    }
}
