package searchengine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class SearchingTest {

    @Test
    void linearSearchFindsItemInUnsortedArray() {
        assertEquals(1, Searching.linearSearch(new String[] {"c", "a", "b"}, "a"));
    }

    @Test
    void linearSearchFindsItemInUnsortedLinkedList() {
        LinkedList<String> list = new LinkedList<>();
        list.insert("c");
        list.insert("a");
        list.insert("b");
        assertEquals(1, Searching.linearSearch(list, "a"));
        assertEquals(-1, Searching.linearSearch(list, "z"));
    }

    @Test
    void linearSearchMiss() {
        assertEquals(-1, Searching.linearSearch(new String[] {"c", "a", "b"}, "z"));
        assertEquals(-1, Searching.linearSearch(new String[] {}, "z"));
    }

    @Test
    void binarySearchHit() {
        assertEquals(2, Searching.binarySearch(new String[] {"a", "b", "c"}, "c"));
        assertEquals(0, Searching.binarySearch(new String[] {"a", "b", "c"}, "a"));
    }

    @Test
    void binarySearchMiss() {
        assertEquals(-1, Searching.binarySearch(new String[] {"a", "b", "c"}, "z"));
        assertEquals(-1, Searching.binarySearch(new String[] {"b", "c"}, "a"));
        assertEquals(-1, Searching.binarySearch(new String[] {}, "a"));
    }

    @Test
    void binarySearchFindsEveryElementOfLargeArray() {
        Integer[] numbers = new Integer[1001];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = i * 2;
        }
        for (int i = 0; i < numbers.length; i++) {
            assertEquals(i, Searching.binarySearch(numbers, i * 2));
            assertEquals(-1, Searching.binarySearch(numbers, i * 2 + 1));
        }
    }

    @Test
    void binarySearchRejectsNullKey() {
        assertThrows(IllegalArgumentException.class, () -> Searching.binarySearch(new String[] {"a"}, null));
    }
}
