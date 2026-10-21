package searchengine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

class ListQueueTest {

    @Test
    void enqueueOneItem() {
        ListQueue<String> queue = new ListQueue<>();
        queue.enqueue("a");
        assertEquals("a", queue.front());
        assertEquals(1, queue.size());
        assertFalse(queue.isEmpty());
    }

    @Test
    void enqueueMultipleThenDequeue() {
        ListQueue<String> queue = new ListQueue<>();
        queue.enqueue("a");
        queue.enqueue("b");
        queue.enqueue("c");
        assertEquals("a", queue.dequeue());
        assertEquals("b", queue.front());
        assertEquals(2, queue.size());
    }

    @Test
    void dequeueLastItemEmptiesQueue() {
        ListQueue<String> queue = new ListQueue<>();
        queue.enqueue("only");
        assertEquals("only", queue.dequeue());
        assertTrue(queue.isEmpty());
    }

    @Test
    void dequeueOnEmptyQueueThrows() {
        ListQueue<String> queue = new ListQueue<>();
        assertThrows(NoSuchElementException.class, () -> queue.dequeue());
    }

    @Test
    void frontOnEmptyQueueThrows() {
        ListQueue<String> queue = new ListQueue<>();
        assertThrows(NoSuchElementException.class, () -> queue.front());
    }

    @Test
    void preservesFifoOrderForManyItems() {
        ListQueue<Integer> queue = new ListQueue<>();
        for (int i = 0; i < 500; i++) {
            queue.enqueue(i);
        }
        for (int i = 0; i < 500; i++) {
            assertEquals(i, queue.dequeue());
        }
        assertTrue(queue.isEmpty());
    }

    @Test
    void interleavedOperationsKeepOrder() {
        ListQueue<String> queue = new ListQueue<>();
        queue.enqueue("a");
        queue.enqueue("b");
        queue.dequeue();
        queue.enqueue("c");
        queue.dequeue();
        queue.dequeue();
        queue.enqueue("d");
        assertEquals("d", queue.front());
        assertEquals(List.of("d"), queue.toList());
    }
}
