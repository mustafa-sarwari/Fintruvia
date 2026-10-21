package searchengine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

abstract class MapContractTest {

    protected MapADT<String, Integer> map;

    protected abstract MapADT<String, Integer> createMap();

    protected abstract boolean isStructurallyValid(MapADT<String, Integer> map);

    @BeforeEach
    void setUp() {
        map = createMap();
    }

    @Test
    void insertNewKey() {
        map.insert("apple", 1);
        assertEquals(1, map.search("apple"));
        assertEquals(1, map.size());
        assertTrue(map.containsKey("apple"));
    }

    @Test
    void insertExistingKeyUpdatesValue() {
        map.insert("apple", 1);
        map.insert("apple", 2);
        assertEquals(2, map.search("apple"));
        assertEquals(1, map.size());
    }

    @Test
    void searchMissReturnsNull() {
        map.insert("apple", 1);
        assertNull(map.search("zzz"));
        assertFalse(map.containsKey("zzz"));
    }

    @Test
    void searchOnEmptyMapReturnsNull() {
        assertNull(map.search("anything"));
        assertTrue(map.isEmpty());
    }

    @Test
    void removeExistingKey() {
        map.insert("a", 1);
        map.insert("b", 2);
        map.insert("c", 3);
        assertEquals(2, map.remove("b"));
        assertNull(map.search("b"));
        assertEquals(2, map.size());
        assertEquals(1, map.search("a"));
        assertEquals(3, map.search("c"));
        assertTrue(isStructurallyValid(map));
    }

    @Test
    void removeMissingKeyReturnsNull() {
        map.insert("a", 1);
        assertNull(map.remove("z"));
        assertEquals(1, map.size());
    }

    @Test
    void removeFromEmptyMapReturnsNull() {
        assertNull(map.remove("a"));
        assertEquals(0, map.size());
    }

    @Test
    void removeEveryKeyLeavesEmptyMap() {
        for (int i = 0; i < 50; i++) {
            map.insert("k" + i, i);
        }
        for (int i = 0; i < 50; i++) {
            assertEquals(i, map.remove("k" + i));
            assertTrue(isStructurallyValid(map));
        }
        assertTrue(map.isEmpty());
        assertTrue(map.keys().isEmpty());
    }

    @Test
    void nullKeyOrValueIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> map.insert(null, 1));
        assertThrows(IllegalArgumentException.class, () -> map.insert("a", null));
        assertThrows(IllegalArgumentException.class, () -> map.search(null));
        assertThrows(IllegalArgumentException.class, () -> map.remove(null));
    }

    @Test
    void keysContainEveryInsertedKey() {
        map.insert("pear", 1);
        map.insert("apple", 2);
        map.insert("mango", 3);
        List<String> keys = new ArrayList<>(map.keys());
        Collections.sort(keys);
        assertEquals(List.of("apple", "mango", "pear"), keys);
    }

    @Test
    void randomOperationsMatchJavaTreeMap() {
        Random random = new Random(12345);
        TreeMap<String, Integer> reference = new TreeMap<>();
        for (int step = 0; step < 5000; step++) {
            String key = "key" + random.nextInt(400);
            int action = random.nextInt(3);
            if (action == 0) {
                assertEquals(reference.remove(key), map.remove(key));
            } else {
                map.insert(key, step);
                reference.put(key, step);
            }
            assertEquals(reference.size(), map.size());
        }
        for (String key : reference.keySet()) {
            assertEquals(reference.get(key), map.search(key));
        }
        assertTrue(isStructurallyValid(map));
    }

    @Test
    void insertsEveryWordFromLongSortedFile() throws IOException {
        List<String> words = Files.readAllLines(Paths.get("data", "long_sorted.txt"));
        words.removeIf(String::isBlank);
        assertTrue(words.size() >= 50);
        for (int i = 0; i < words.size(); i++) {
            map.insert(words.get(i), i);
        }
        assertEquals(words.size(), map.size());
        for (int i = 0; i < words.size(); i++) {
            assertEquals(i, map.search(words.get(i)));
        }
        assertTrue(isStructurallyValid(map));
    }
}
