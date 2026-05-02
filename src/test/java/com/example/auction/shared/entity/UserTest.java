package com.example.auction.shared.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;

import java.time.Instant;

public class UserTest {
    private User testuser;
    // Chuẩn bị môi trường trước mỗi Test Case
    private User memberUser;
    private User adminUser;
    private User guestUser;

    @BeforeEach
    void setUp() {
        memberUser = new User("hung", "password123", "hung@email.com", Role.MEMBER);
        adminUser = new User("admin", "admin123", "admin@email.com", Role.ADMIN);
        guestUser = new User("guest", "guest123", "guest@email.com", Role.GUEST);
    }

    // test create

    @Test
    void testCreateMemberUser() {
        assertEquals("hung", memberUser.getUsername());
        assertEquals("password123", memberUser.getPassword());
        assertEquals("hung@email.com", memberUser.getEmail());
        assertEquals(Role.MEMBER, memberUser.getRole());
        assertEquals("ACTIVE", memberUser.getStatus());
    }

    @Test
    void testMemberHasBidderProfile() {
        // MEMBER phải có BidderProfile
        assertNotNull(memberUser.getBidderProfile());
        assertEquals(0.0, memberUser.getBidderBalance());
    }

    @Test
    void testMemberDoesNotHaveSellerProfile() {
        // MEMBER mới tạo không phải seller
        assertNull(memberUser.getSellerProfile());
        assertFalse(memberUser.isSeller());
    }

    @Test
    void testAdminDoesNotHaveProfiles() {
        assertNull(adminUser.getBidderProfile());
        assertNull(adminUser.getSellerProfile());
        assertFalse(adminUser.canBid());
        assertFalse(adminUser.isSeller());
    }

    @Test
    void testGuestDoesNotHaveProfiles() {
        assertNull(guestUser.getBidderProfile());
        assertNull(guestUser.getSellerProfile());
    }

    // test balance management

    @Test
    void testAddBalance() {
        memberUser.addBalance(1_000_000);
        assertEquals(1_000_000, memberUser.getBidderBalance());
    }

    @Test
    void testDeductBalanceSuccess() {
        memberUser.addBalance(1_000_000);
        boolean result = memberUser.deductBalance(300_000);

        assertTrue(result);
        assertEquals(700_000, memberUser.getBidderBalance());
    }

    @Test
    void testDeductBalanceNotEnoughMoney() {
        memberUser.addBalance(100_000);
        boolean result = memberUser.deductBalance(500_000);

        assertFalse(result);
        assertEquals(100_000, memberUser.getBidderBalance());  // Không thay đổi
    }

    @Test
    void testAdminCannotAddBalance() {
        // Admin không có bidderProfile
        adminUser.addBalance(1_000_000);
        assertEquals(0.0, adminUser.getBidderBalance());
    }

    // test seller register

    @Test
    void testRegisterAsSeller() {
        assertFalse(memberUser.isSeller());

        memberUser.registerAsSeller();

        assertTrue(memberUser.isSeller());
        assertNotNull(memberUser.getSellerProfile());
        assertEquals(5.0, memberUser.getSellerRating());
    }

    @Test
    void testRegisterAsSellerTwiceDoesNotReplace() {
        memberUser.registerAsSeller();
        double firstRating = memberUser.getSellerRating();

        memberUser.registerAsSeller();
        double secondRating = memberUser.getSellerRating();

        assertEquals(firstRating, secondRating);
    }

    @Test
    void testAdminCannotRegisterAsSeller() {
        assertThrows(IllegalStateException.class, () -> {
            adminUser.registerAsSeller();
        });
    }

    // test defensive copy - getter return copy

    @Test
    void testGetBidderProfileReturnsCopy() {
        memberUser.addBalance(1_000_000);

        // Lấy copy
        BidderProfile profile1 = memberUser.getBidderProfile();
        profile1.addBalance(500_000);

        // User balance không thay đổi (vì profile1 là copy)
        assertEquals(1_000_000, memberUser.getBidderBalance());
    }

    @Test
    void testGetSellerProfileReturnsCopy() {
        memberUser.registerAsSeller();

        // Lấy copy
        SellerProfile profile1 = memberUser.getSellerProfile();
        profile1.setShopRating(2.0);

        // User rating không thay đổi (vì profile1 là copy)
        assertEquals(5.0, memberUser.getSellerRating());
    }

    @Test
    void testModifyProfileThroughUserMethod() {
        memberUser.addBalance(1_000_000);
        memberUser.deductBalance(300_000);

        // Cách đúng: Thay đổi qua User method
        assertEquals(700_000, memberUser.getBidderBalance());
    }

    // test copy constructor

    @Test
    void testCopyConstructor() {
        memberUser.addBalance(1_000_000);
        memberUser.registerAsSeller();
        memberUser.updateSellerRating(4.0);

        // Copy user
        User copy = new User(memberUser);

        // Verify copy có cùng dữ liệu
        assertEquals("hung", copy.getUsername());
        assertEquals(1_000_000, copy.getBidderBalance());
        assertEquals(4.5, copy.getSellerRating());  // (5 + 4) / 2
        assertEquals(memberUser.getId(), copy.getId());
    }

    @Test
    void testCopyConstructorIsIndependent() {
        memberUser.addBalance(1_000_000);

        User copy = new User(memberUser);

        // Modify copy
        copy.addBalance(500_000);

        // Original không thay đổi
        assertEquals(1_000_000, memberUser.getBidderBalance());
        assertEquals(1_500_000, copy.getBidderBalance());
    }

    // test status management

    @Test
    void testDefaultStatusIsActive() {
        assertEquals("ACTIVE", memberUser.getStatus());
    }

    @Test
    void testSetStatus() {
        memberUser.setStatus("BANNED");
        assertEquals("BANNED", memberUser.getStatus());
    }

    // test update at timestap

    @Test
    void testUpdatedAtChangesWhenBalanceModified() {
        Instant before = memberUser.getUpdatedAt();

        // Chờ một chút (để timestamp khác)
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        memberUser.addBalance(100_000);
        Instant after = memberUser.getUpdatedAt();

        // after phải sau before
        assertTrue(after.isAfter(before));
    }

    // test equals and hashcode

    @Test
    void testEqualsBasedOnUsername() {
        User user2 = new User("hung", "different_password", "hung@email.com", Role.MEMBER);
        assertEquals(memberUser, user2);  // Same username = equal
    }

    @Test
    void testEqualsWithDifferentUsername() {
        User user2 = new User("alice", "password123", "hung@email.com", Role.MEMBER);
        assertNotEquals(memberUser, user2);
    }

    @Test
    void testHashCodeConsistent() {
        int hash1 = memberUser.hashCode();
        int hash2 = memberUser.hashCode();
        assertEquals(hash1, hash2);
    }

    // test toString

    @Test
    void testToString() {
        memberUser.addBalance(1_000_000);
        String str = memberUser.toString();

        assertTrue(str.contains("hung"));
        assertTrue(str.contains("1000000"));
    }
}
