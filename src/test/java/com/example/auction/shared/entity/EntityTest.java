package com.example.auction.shared.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Instant;

/**
 * Unit tests cho Entity class
 */
class EntityTest {

    private Entity testEntity;

    @BeforeEach // chạy trước mỗi Test
    void setUp() {
        // Tạo instance của Entity con để test, tạo fresh entity cho mỗi test
        // Vì Entity là abstract, ta phải dùng anonymous class để tạo đối tượng kiểu abstract Entity
        testEntity = new Entity() {}; // đây là kiểu tạo anonymous class
    }

    @Test
    void testEntityCreation() { // TEST 1: Kiểm tra Entity được tạo đúng
        assertNotNull(testEntity);  // Entity không null?
        assertNotNull(testEntity.getCreatedAt());  // createdAt được set kh
        assertNotNull(testEntity.getUpdatedAt());  // updatedAt được set kh
    }

    @Test
    void testSetAndGetId() {  // TEST 2: Kiểm tra set/get id
        testEntity.setId(1L);  // kiểm tra setId có = 1 kh
        assertEquals(1L, testEntity.getId());  // Get ra có = 1 không?
    }

    @Test
    void testCreatedAtIsSet() {  // TEST 3: Kiểm tra createdAt được set khi tạo Entity
        Instant createdAt = testEntity.getCreatedAt();
        assertNotNull(createdAt);  // // createdAt không null?
        assertTrue(createdAt.isBefore(Instant.now().plusSeconds(1)));
        // createdAt phải trước thời điểm bây giờ (vì tạo trước đó), đây là để test thử có sau 1s kh
    }

    @Test
    void testUpdatedAtCanBeModified() {  // TEST 4: Kiểm tra có thể thay đổi updatedAt
        Instant now = Instant.now();
        testEntity.setUpdatedAt(now);
        assertEquals(now, testEntity.getUpdatedAt());
    }

    @Test
    void testToString() {  // TEST 5: Kiểm tra toString() hoạt động
        testEntity.setId(1L);
        String str = testEntity.toString();
        assertTrue(str.contains("id=1")); // Có chứa "id=1" không
        assertTrue(str.contains("createdAt")); // Có chứa "createdAt" không
    }
}