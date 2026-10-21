package searchengine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LinkedListTest {

    private LinkedList<String> list;

    @BeforeEach
    void setUp() {
        list = new LinkedList<>();
    }

    @Test
    void insertOnEmptyList() {
        list.insert("a");
        assertEquals(1, list.size());
        assertEquals("a", list.get(0));
        assertEquals("a", list.getFirst());
        assertEquals("a", list.getLast());
        assertFalse(list.isEmpty());
    }

    @Test
    void insertAtHeadTailAndMiddle() {
        list.insert("a");
        list.insert("c");
        list.insertFirst("start");
        list.insertLast("end");
        list.insertAt(2, "b");
        assertEquals(List.of("start", "a", "b", "c", "end"), list.toList());
        assertEquals("start", list.getFirst());
        assertEquals("end", list.getLast());
        assertEquals(5, list.size());
    }

    @Test
    void insertAtBoundaryIndexes() {
        list.insertAt(0, "b");
        list.insertAt(0, "a");
        list.insertAt(2, "c");
        assertEquals(List.of("a", "b", "c"), list.toList());
        assertEquals("c", list.getLast());
    }

    @Test
    void insertAtInvalidIndexThrows() {
        list.insert("a");
        assertThrows(IndexOutOfBoundsException.class, () -> list.insertAt(5, "x"));
        assertThrows(IndexOutOfBoundsException.class, () -> list.insertAt(-1, "x"));
    }

    @Test
    void deleteOnlyItem() {
        list.insert("only");
        assertTrue(list.delete("only"));
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
        assertNull(list.getFirst());
        assertNull(list.getLast());
        list.insert("again");
        assertEquals(List.of("again"), list.toList());
        assertEquals("again", list.getLast());
    }

    @Test
    void deleteHeadTailAndMiddle() {
        for (String item : new String[] {"a", "b", "c", "d", "e"}) {
            list.insert(item);
        }
        assertTrue(list.delete("a"));
        assertTrue(list.delete("e"));
        assertTrue(list.delete("c"));
        assertEquals(List.of("b", "d"), list.toList());
        assertEquals("b", list.getFirst());
        assertEquals("d", list.getLast());
        list.insertLast("f");
        assertEquals(List.of("b", "d", "f"), list.toList());
    }

    @Test
    void deleteAtHeadTailAndMiddle() {
        for (String item : new String[] {"a", "b", "c", "d", "e"}) {
            list.insert(item);
        }
        assertEquals("a", list.deleteAt(0));
        assertEquals("e", list.deleteAt(3));
        assertEquals("c", list.deleteAt(1));
        assertEquals(List.of("b", "d"), list.toList());
        assertEquals("d", list.getLast());
    }

    @Test
    void sizeAfterMixedOperations() {
        list.insert("a");
        list.insert("b");
        list.insert("c");
        list.delete("b");
        list.insert("d");
        list.insert("e");
        list.delete("a");
        assertEquals(3, list.size());
        assertEquals(List.of("c", "d", "e"), list.toList());
    }

    @Test
    void deleteOnEmptyListDoesNotCrash() {
        assertFalse(list.delete("missing"));
        assertNull(list.deleteFirst());
        assertEquals(0, list.size());
        assertThrows(IndexOutOfBoundsException.class, () -> list.deleteAt(0));
    }

    @Test
    void deleteMissingItemLeavesListUnchanged() {
        list.insert("a");
        list.insert("b");
        assertFalse(list.delete("z"));
        assertEquals(2, list.size());
    }

    @Test
    void deleteRemovesOnlyFirstOccurrence() {
        list.insert("a");
        list.insert("b");
        list.insert("a");
        assertTrue(list.delete("a"));
        assertEquals(List.of("b", "a"), list.toList());
    }

    @Test
    void traversalVisitsItemsInOrder() {
        list.insert("x");
        list.insert("y");
        list.insert("z");
        StringBuilder visited = new StringBuilder();
        for (String item : list) {
            visited.append(item);
        }
        assertEquals("xyz", visited.toString());
        assertEquals("[x, y, z]", list.toString());
    }

    @Test
    void containsAndIndexOf() {
        list.insert("a");
        list.insert("b");
        assertTrue(list.contains("b"));
        assertFalse(list.contains("c"));
        assertEquals(1, list.indexOf("b"));
        assertEquals(-1, list.indexOf("c"));
    }

    @Test
    void getWithInvalidIndexThrows() {
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
    }

    @Test
    void clearEmptiesList() {
        list.insert("a");
        list.insert("b");
        list.clear();
        assertTrue(list.isEmpty());
        assertNull(list.getLast());
    }
}
