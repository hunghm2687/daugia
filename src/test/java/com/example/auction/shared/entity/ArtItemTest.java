package com.example.auction.shared.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Year;
import static org.junit.jupiter.api.Assertions.*;

public class ArtItemTest {
    private ArtItem artItem;

    @BeforeEach
    void setUp() {
        artItem = new ArtItem(
                "Starry Night",
                "A beautiful painting of stars",
                10_000_000,
                "seller_user",
                "Van Gogh",
                1889
        );
    }

    @Test
    void testCreateArtItem() {
        assertEquals("Starry Night", artItem.getItemName());
        assertEquals("Van Gogh", artItem.getArtistName());
        assertEquals(1889, artItem.getYearCreated());
    }

    @Test
    void testGetDetailed() {
        String detailed = artItem.getDetailed();
        assertTrue(detailed.contains("Van Gogh"));
        assertTrue(detailed.contains("1889"));
    }

    @Test
    void testInvalidYearThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ArtItem("Art", "desc", 100, "seller", "Artist", -500);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new ArtItem("Art", "desc", 100, "seller", "Artist", 3000);
        });
    }

    @Test
    void testSetArtistName() {
        artItem.setArtistName("Picasso");
        assertEquals("Picasso", artItem.getArtistName());
    }

    @Test
    void testSetYearCreated() {
        artItem.setYearCreated(1920);
        assertEquals(1920, artItem.getYearCreated());
    }
}
