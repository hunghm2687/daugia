package com.example.auction.shared.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SellerProfileTest {
    private SellerProfile sellerProfile;
    // Chuẩn bị môi trường: Tạo người bán với rating khởi điểm là 0 sao
    @BeforeEach
    void setUp() {
        sellerProfile = new SellerProfile(5.0);
    }

    // test create

    @Test
    void testCreateSellerProfile() {
        assertEquals(5.0, sellerProfile.getShopRating());
    }

    @Test
    void testCreateWithMinRating() {
        SellerProfile minProfile = new SellerProfile(0.0);
        assertEquals(0.0, minProfile.getShopRating());
    }

    @Test
    void testCreateThrowsExceptionForInvalidRating() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SellerProfile(5.5);  // > 5
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new SellerProfile(-1.0);  // < 0
        });
    }

    // test set rating

    @Test
    void testSetRating() {
        sellerProfile.setShopRating(4.5);
        assertEquals(4.5, sellerProfile.getShopRating());
    }

    @Test
    void testSetRatingThrowsExceptionForInvalidRating() {
        assertThrows(IllegalArgumentException.class, () -> {
            sellerProfile.setShopRating(6.0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            sellerProfile.setShopRating(-0.5);
        });
    }

    // test update rating

    @Test
    void testUpdateRatingFromBuyer() {
        // Profile rating = 5.0
        // Buyer rating = 3.0
        // Expected: (5.0 + 3.0) / 2 = 4.0
        sellerProfile.updateRating(3.0);
        assertEquals(4.0, sellerProfile.getShopRating());
    }

    @Test
    void testUpdateRatingMultipleTimes() {
        // Rating = 5.0
        sellerProfile.updateRating(4.0);  // (5 + 4) / 2 = 4.5
        assertEquals(4.5, sellerProfile.getShopRating());

        sellerProfile.updateRating(4.0);  // (4.5 + 4) / 2 = 4.25
        assertEquals(4.25, sellerProfile.getShopRating());
    }

    @Test
    void testUpdateRatingThrowsExceptionForInvalidRating() {
        assertThrows(IllegalArgumentException.class, () -> {
            sellerProfile.updateRating(6.0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            sellerProfile.updateRating(-1.0);
        });
    }

    // test copy constructor

    @Test
    void testCopyConstructor() {
        SellerProfile copy = new SellerProfile(sellerProfile);
        assertEquals(sellerProfile.getShopRating(), copy.getShopRating());
    }

    @Test
    void testCopyConstructorIsIndependent() {
        SellerProfile copy = new SellerProfile(sellerProfile);

        // Modify copy
        copy.setShopRating(3.0);

        // Original không thay đổi
        assertEquals(5.0, sellerProfile.getShopRating());
        assertEquals(3.0, copy.getShopRating());
    }

    @Test
    void testCopyConstructorWithNull() {
        SellerProfile copy = new SellerProfile(null);
        assertEquals(5.0, copy.getShopRating());
    }

    // test equals

    @Test
    void testEqualsWithDifferentRating() {
        SellerProfile profile2 = new SellerProfile(4.0);
        assertNotEquals(sellerProfile, profile2);
    }
}