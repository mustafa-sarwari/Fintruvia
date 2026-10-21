package searchengine;

import java.util.List;
import java.util.NoSuchElementException;

public class ListQueue<T> {

    private final LinkedList<T> items = new LinkedList<>();

    public void enqueue(T item) {
        items.insertLast(item);
    }

    public T dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        return items.deleteFirst();
    }

    public T front() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        return items.getFirst();
    }

    public int size() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void clear() {
        items.clear();
    }

    public List<T> toList() {
        return items.toList();
    }

    @Override
    public String toString() {
        return items.toString();
    }
}
