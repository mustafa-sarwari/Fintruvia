package searchengine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

class ArrayStackTest {

    @Test
    void pushOneItem() {
        ArrayStack<String> stack = new ArrayStack<>();
        stack.push("a");
        assertEquals("a", stack.top());
        assertEquals(1, stack.size());
        assertFalse(stack.isEmpty());
    }

    @Test
    void pushMultipleThenPop() {
        ArrayStack<String> stack = new ArrayStack<>();
        stack.push("a");
        stack.push("b");
        stack.push("c");
        assertEquals("c", stack.pop());
        assertEquals("b", stack.top());
        assertEquals(2, stack.size());
    }

    @Test
    void testPushAndPop() {
        ArrayStack<String> stack = new ArrayStack<>();
        stack.push("a");
        stack.push("b");
        assertEquals(2, stack.size());
        assertEquals("b", stack.pop());
        assertEquals("a", stack.top());
        assertEquals(1, stack.size());
    }

    @Test
    void popLastItemEmptiesStack() {
        ArrayStack<String> stack = new ArrayStack<>();
        stack.push("only");
        assertEquals("only", stack.pop());
        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());
    }

    @Test
    void testPopEmptyStack() {
        ArrayStack<Integer> stack = new ArrayStack<>();
        assertThrows(NoSuchElementException.class, () -> stack.pop());
    }

    @Test
    void topOnEmptyStackThrows() {
        ArrayStack<Integer> stack = new ArrayStack<>();
        assertThrows(NoSuchElementException.class, () -> stack.top());
    }

    @Test
    void growsAndShrinksWhilePreservingLifoOrder() {
        ArrayStack<Integer> stack = new ArrayStack<>(2);
        for (int i = 0; i < 1000; i++) {
            stack.push(i);
        }
        assertEquals(1000, stack.size());
        assertTrue(stack.capacity() >= 1000);
        for (int i = 999; i >= 0; i--) {
            assertEquals(i, stack.pop());
        }
        assertTrue(stack.isEmpty());
        assertTrue(stack.capacity() < 1000);
    }

    @Test
    void toListIsBottomToTop() {
        ArrayStack<String> stack = new ArrayStack<>();
        stack.push("doc1");
        stack.push("doc2");
        assertEquals(List.of("doc1", "doc2"), stack.toList());
    }

    @Test
    void clearEmptiesStack() {
        ArrayStack<String> stack = new ArrayStack<>();
        stack.push("a");
        stack.push("b");
        stack.clear();
        assertTrue(stack.isEmpty());
        stack.push("c");
        assertEquals("c", stack.top());
    }

    @Test
    void invalidCapacityThrows() {
        assertThrows(IllegalArgumentException.class, () -> new ArrayStack<String>(0));
    }
}
