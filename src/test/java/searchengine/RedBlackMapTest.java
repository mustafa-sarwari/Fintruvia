package searchengine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

class RedBlackMapTest extends MapContractTest {

    @Override
    protected MapADT<String, Integer> createMap() {
        return new RedBlackMap<>();
    }

    @Override
    protected boolean isStructurallyValid(MapADT<String, Integer> map) {
        return ((RedBlackMap<String, Integer>) map).isValid();
    }

    @Test
    void rootIsAlwaysBlack() {
        RedBlackMap<Integer, Integer> tree = new RedBlackMap<>();
        for (int i = 0; i < 100; i++) {
            tree.insert(i, i);
            assertFalse(tree.isRootRed());
        }
    }

    @Test
    void threeSortedInsertionsRotateToMiddleRoot() {
        RedBlackMap<Integer, Integer> tree = new RedBlackMap<>();
        tree.insert(1, 1);
        tree.insert(2, 2);
        tree.insert(3, 3);
        assertEquals(2, tree.rootKey());
        assertEquals(2, tree.height());
    }

    @Test
    void propertiesHoldAfterEverySortedInsertion() {
        RedBlackMap<Integer, Integer> tree = new RedBlackMap<>();
        for (int i = 0; i < 2000; i++) {
            tree.insert(i, i);
            assertTrue(tree.isValid());
        }
        double bound = 2 * (Math.log(2000 + 1) / Math.log(2));
        assertTrue(tree.height() <= bound);
    }

    @Test
    void propertiesHoldAfterReverseSortedInsertion() {
        RedBlackMap<Integer, Integer> tree = new RedBlackMap<>();
        for (int i = 2000; i > 0; i--) {
            tree.insert(i, i);
        }
        assertTrue(tree.isValid());
        assertTrue(tree.height() <= 2 * (Math.log(2001) / Math.log(2)));
    }

    @Test
    void propertiesHoldAfterRandomInsertionsAndRemovals() {
        RedBlackMap<Integer, Integer> tree = new RedBlackMap<>();
        Random random = new Random(2024);
        for (int i = 0; i < 3000; i++) {
            int key = random.nextInt(1000);
            if (random.nextBoolean()) {
                tree.insert(key, key);
            } else {
                tree.remove(key);
            }
            assertTrue(tree.isValid());
        }
    }

    @Test
    void keysAreReturnedInSortedOrder() {
        RedBlackMap<Integer, Integer> tree = new RedBlackMap<>();
        for (int key : new int[] {5, 1, 9, 3, 7}) {
            tree.insert(key, key);
        }
        assertEquals(List.of(1, 3, 5, 7, 9), tree.keys());
    }
}
