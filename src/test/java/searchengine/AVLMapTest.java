package searchengine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

class AVLMapTest extends MapContractTest {

    @Override
    protected MapADT<String, Integer> createMap() {
        return new AVLMap<>();
    }

    @Override
    protected boolean isStructurallyValid(MapADT<String, Integer> map) {
        return ((AVLMap<String, Integer>) map).isValid();
    }

    private static AVLMap<Integer, Integer> treeOf(int... keys) {
        AVLMap<Integer, Integer> tree = new AVLMap<>();
        for (int key : keys) {
            tree.insert(key, key);
        }
        return tree;
    }

    @Test
    void leftLeftCaseRotatesRight() {
        AVLMap<Integer, Integer> tree = treeOf(3, 2, 1);
        assertEquals(2, tree.rootKey());
        assertEquals(2, tree.height());
    }

    @Test
    void rightRightCaseRotatesLeft() {
        AVLMap<Integer, Integer> tree = treeOf(1, 2, 3);
        assertEquals(2, tree.rootKey());
        assertEquals(2, tree.height());
    }

    @Test
    void leftRightCaseDoubleRotates() {
        AVLMap<Integer, Integer> tree = treeOf(3, 1, 2);
        assertEquals(2, tree.rootKey());
        assertEquals(2, tree.getRotationCount());
    }

    @Test
    void rightLeftCaseDoubleRotates() {
        AVLMap<Integer, Integer> tree = treeOf(1, 3, 2);
        assertEquals(2, tree.rootKey());
        assertEquals(2, tree.getRotationCount());
    }

    @Test
    void staysBalancedAfterEverySortedInsertion() {
        AVLMap<Integer, Integer> tree = new AVLMap<>();
        for (int i = 0; i < 2000; i++) {
            tree.insert(i, i);
            assertTrue(tree.isBalanced());
        }
        double bound = 1.44 * (Math.log(2000 + 2) / Math.log(2));
        assertTrue(tree.height() <= bound);
        assertEquals(11, tree.height());
    }

    @Test
    void staysBalancedAfterRandomInsertionsAndRemovals() {
        AVLMap<Integer, Integer> tree = new AVLMap<>();
        Random random = new Random(99);
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
        AVLMap<Integer, Integer> tree = treeOf(5, 1, 9, 3, 7);
        assertEquals(List.of(1, 3, 5, 7, 9), tree.keys());
    }
}
