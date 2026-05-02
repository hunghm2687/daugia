package com.example.auction.shared.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BidderProfileTest {
    private BidderProfile bidderProfile;

    // Chuẩn bị môi trường: Tạo ra một hồ sơ người mua mới với 0 đồng
    @BeforeEach
    void setUp() {
        bidderProfile = new BidderProfile(1_000_000); // cái này chạy ở mọi test, để mỗi test kh liên quan đến nhau
    }
    // test create
    @Test
    void testCreateBidderProfile() {
        assertEquals(1_000_000, bidderProfile.getBalance());
    }

    @Test
    void testCreateWithZeroBalance() {
        BidderProfile emptyProfile = new BidderProfile(0);
        assertEquals(0, emptyProfile.getBalance());
    }
    // test add balance
    @Test
    void testAddMultipleTimes() {
        bidderProfile.addBalance(100_000);
        bidderProfile.addBalance(200_000);
        assertEquals(1_300_000, bidderProfile.getBalance());
    }

    @Test
    void testAddBalanceThrowsExceptionForNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> {
            bidderProfile.addBalance(-100_000);
        });
    }

    @Test
    void testAddBalanceThrowsExceptionForZeroAmount() {
        assertThrows(IllegalArgumentException.class, () -> {
            bidderProfile.addBalance(0);
        });
    }
    // test deduct
    @Test
    void testDeductBalanceSuccess() {
        boolean result = bidderProfile.deductBalance(300_000);
        assertTrue(result);
        assertEquals(700_000, bidderProfile.getBalance());
    }

    @Test
    void testDeductBalanceNotEnoughMoney() {
        boolean result = bidderProfile.deductBalance(2_000_000);
        assertFalse(result);
        // Balance không thay đổi
        assertEquals(1_000_000, bidderProfile.getBalance());
    }

    @Test
    void testDeductBalanceExactAmount() {
        boolean result = bidderProfile.deductBalance(1_000_000);
        assertTrue(result);
        assertEquals(0, bidderProfile.getBalance());
    }

    @Test
    void testDeductBalanceThrowsExceptionForNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> {
            bidderProfile.deductBalance(-100_000);
        });
    }
    // test copy constructor
    @Test
    void testCopyConstructor() {
        BidderProfile copy = new BidderProfile(bidderProfile);
        assertEquals(bidderProfile.getBalance(), copy.getBalance());
    }

    @Test
    void testCopyConstructorIsIndependent() {
        BidderProfile copy = new BidderProfile(bidderProfile);

        // Modify copy
        copy.addBalance(500_000);

        // Original không thay đổi
        assertEquals(1_000_000, bidderProfile.getBalance());
        assertEquals(1_500_000, copy.getBalance());
    }

    @Test
    void testCopyConstructorWithNull() {
        BidderProfile copy = new BidderProfile(null);
        assertEquals(0, copy.getBalance());
    }
    // test equals


    @Test
    void testEqualsWithDifferentBalance() {
        BidderProfile profile2 = new BidderProfile(500_000);
        assertNotEquals(bidderProfile, profile2);
    }
}