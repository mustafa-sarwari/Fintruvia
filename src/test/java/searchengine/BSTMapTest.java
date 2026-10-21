package searchengine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class BSTMapTest extends MapContractTest {

    @Override
    protected MapADT<String, Integer> createMap() {
        return new BSTMap<>();
    }

    @Override
    protected boolean isStructurallyValid(MapADT<String, Integer> map) {
        return ((BSTMap<String, Integer>) map).isValid();
    }

    @Test
    void keysAreReturnedInSortedOrder() {
        for (String key : new String[] {"m", "c", "x", "a", "e"}) {
            map.insert(key, 0);
        }
        assertEquals(List.of("a", "c", "e", "m", "x"), map.keys());
    }

    @Test
    void sortedInsertionBuildsDegenerateTree() {
        BSTMap<Integer, Integer> tree = new BSTMap<>();
        for (int i = 0; i < 500; i++) {
            tree.insert(i, i);
        }
        assertEquals(500, tree.height());
    }

    @Test
    void removeRootWithTwoChildren() {
        BSTMap<Integer, String> tree = new BSTMap<>();
        for (int key : new int[] {50, 30, 70, 20, 40, 60, 80}) {
            tree.insert(key, "v" + key);
        }
        assertEquals("v50", tree.remove(50));
        assertEquals(60, tree.rootKey());
        assertEquals(List.of(20, 30, 40, 60, 70, 80), tree.keys());
        assertTrue(tree.isValid());
    }

    @Test
    void deepDegenerateTreeDoesNotOverflowStack() {
        BSTMap<Integer, Integer> tree = new BSTMap<>();
        for (int i = 0; i < 50_000; i++) {
            tree.insert(i, i);
        }
        assertEquals(49_999, tree.search(49_999));
        assertEquals(50_000, tree.keys().size());
        assertEquals(0, tree.remove(0));
    }
}
