package searchengine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class HashMapTest extends MapContractTest {

    @Override
    protected MapADT<String, Integer> createMap() {
        return new HashMap<>();
    }

    @Override
    protected boolean isStructurallyValid(MapADT<String, Integer> map) {
        HashMap<String, Integer> hashMap = (HashMap<String, Integer>) map;
        return hashMap.keys().size() == hashMap.size() && hashMap.loadFactor() <= 0.75;
    }

    @Test
    void collidingKeysAreChainedInSameBucket() {
        HashMap<String, Integer> hashMap = new HashMap<>();
        assertEquals("Aa".hashCode(), "BB".hashCode());
        hashMap.insert("Aa", 1);
        hashMap.insert("BB", 2);
        assertEquals(1, hashMap.search("Aa"));
        assertEquals(2, hashMap.search("BB"));
        assertEquals(2, hashMap.maxChainLength());
        assertEquals(1, hashMap.remove("Aa"));
        assertNull(hashMap.search("Aa"));
        assertEquals(2, hashMap.search("BB"));
    }

    @Test
    void resizesWhenLoadFactorExceeded() {
        HashMap<String, Integer> hashMap = new HashMap<>(4);
        assertEquals(4, hashMap.capacity());
        for (int i = 0; i < 100; i++) {
            hashMap.insert("key" + i, i);
        }
        assertTrue(hashMap.capacity() >= 128);
        assertTrue(hashMap.loadFactor() <= 0.75);
        for (int i = 0; i < 100; i++) {
            assertEquals(i, hashMap.search("key" + i));
        }
    }

    @Test
    void capacityIsRoundedUpToPowerOfTwo() {
        assertEquals(16, new HashMap<String, Integer>(10).capacity());
    }
}
