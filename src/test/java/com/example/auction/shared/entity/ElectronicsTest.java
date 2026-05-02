package com.example.auction.shared.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ElectronicsTest {
    private ElectronicsItem electronicsItem;

    @BeforeEach
    void setUp() {
        electronicsItem = new ElectronicsItem(
                "MacBook Pro",
                "15-inch laptop",
                2_000_000,
                "seller_user",
                "Apple",
                24
        );
    }

    @Test
    void testCreateElectronicsItem() {
        assertEquals("MacBook Pro", electronicsItem.getItemName());
        assertEquals("Apple", electronicsItem.getBrand());
        assertEquals(24, electronicsItem.getWarranty());
    }

    @Test
    void testGetDetailed() {
        String detailed = electronicsItem.getDetailed();
        assertTrue(detailed.contains("Apple"));
        assertTrue(detailed.contains("24 months"));
    }

    @Test
    void testNegativeWarrantyThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ElectronicsItem("Item", "desc", 100, "seller", "Brand", -5);
        });
    }

    @Test
    void testSetBrand() {
        electronicsItem.setBrand("Samsung");
        assertEquals("Samsung", electronicsItem.getBrand());
    }

    @Test
    void testSetWarranty() {
        electronicsItem.setWarranty(36);
        assertEquals(36, electronicsItem.getWarranty());
    }
}
