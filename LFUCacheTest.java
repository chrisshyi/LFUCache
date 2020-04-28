import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LFUCacheTest {

    @Test
    void testNoEvictions() {
        LFUCache cache = new LFUCache(2);
        cache.put(3, 1);
        cache.put(5, 10);

        assertEquals(1, cache.get(3));
        assertEquals(10, cache.get(5));
        for (int i = 6; i < 10; i++) {
            assertEquals(-1, cache.get(i));
        }
    }

    @Test
    void testWithEviction() {
        LFUCache cache = new LFUCache(2);
        cache.put(3, 1);
        cache.put(5, 10);

        cache.get(3);

        cache.put(2, 4); // should cause 5 to get evicted
        assertEquals(-1, cache.get(5));
        assertEquals(4, cache.get(2));
        assertEquals(1, cache.get(3));
    }

    @Test
    void testWithEviction2() {
        LFUCache cache = new LFUCache(2);
        cache.put(3, 1);
        cache.put(5, 10);

        for (int i = 0; i < 51; i++) {
            cache.get(3);
        }
        for (int i = 0; i < 50; i++) {
            cache.get(5);
        }
        cache.put(2, 4); // should cause 3 to get evicted
        assertEquals(-1, cache.get(5));
        assertEquals(4, cache.get(2));
        assertEquals(1, cache.get(3));
    }

    @Test
    void testUpdateValue() {
        LFUCache cache = new LFUCache(2);
        cache.put(3, 1);
        cache.put(2, 1);
        cache.put(2, 2);
        assertEquals(2, cache.get(2));
        cache.put(4, 4);
        assertEquals(2, cache.get(2));
    }

}