package com.example.auction.shared.entity;
// lớp ArtItem đại diện cho các sp nghệ thuật: tranh vẽ, tượng, ...

import java.time.Instant;

public class ArtItem extends Item {
    private String artistName; // tên tgia
    private int yearCreated; // năm tạo sp

    public ArtItem(String itemName, String mieutaItem, double startPrice, String sellerUsername, String artistName, int yearCreated) {
        // khởi tạo artistName, yearCreated trước rồi mới ném throw để kh bị spotBugs chặn
        this.artistName = artistName;
        this.yearCreated = yearCreated;
        // Sau đó validate (superclass constructor không throw)
        super(itemName, mieutaItem, startPrice, sellerUsername);

        // Validate riêng cho ArtItem
        if (artistName == null || artistName.trim().isEmpty()) {
            throw new IllegalArgumentException("Artist name không được để trống");
        }
        if (yearCreated <= 0 || yearCreated > java.time.Year.now().getValue()) {
            throw new IllegalArgumentException("Year created không hợp lệ");
        }
    }

    @Override
    public String getDetailed() {
        return "Art [Artist: " + artistName + ", Created in: " + yearCreated + "]";
    }
    public String getArtistName() {
        return artistName;
    }

    public int getYearCreated() {
        return yearCreated;
    }
    public void setArtistName(String artistName) {
        if (artistName == null || artistName.trim().isEmpty()) {
            throw new IllegalArgumentException("Artist name không được để trống");
        }
        this.artistName = artistName;
        this.setUpdatedAt(Instant.now());
    }

    public void setYearCreated(int yearCreated) {
        if (yearCreated <=0 || yearCreated > java.time.Year.now().getValue()) {
            throw new IllegalArgumentException("Year created không hợp lệ");
        }
        this.yearCreated = yearCreated;
        this.setUpdatedAt(Instant.now());
    }
    @Override
    public String toString() {
        return "ArtItem{" +
                "id=" + getId() +
                ", itemName='" + getItemName() + '\'' +
                ", artistName='" + artistName + '\'' +
                ", yearCreated=" + yearCreated +
                ", startPrice=" + String.format("%.2f", getStartPrice()) +
                ", sellerUsername='" + getSellerUsername() + '\'' +
                '}';
    }
}
