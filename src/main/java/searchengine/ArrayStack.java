package searchengine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

public class ArrayStack<T> {

    private static final int DEFAULT_CAPACITY = 8;

    private T[] data;
    private int size;

    public ArrayStack() {
        this(DEFAULT_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public ArrayStack(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        data = (T[]) new Object[initialCapacity];
    }

    public void push(T item) {
        if (size == data.length) {
            resize(data.length * 2);
        }
        data[size++] = item;
    }

    public T pop() {
        if (isEmpty()) {
            throw new NoSuchElementException("Stack is empty");
        }
        T item = data[--size];
        data[size] = null;
        if (size > 0 && size == data.length / 4 && data.length > DEFAULT_CAPACITY) {
            resize(data.length / 2);
        }
        return item;
    }

    public T top() {
        if (isEmpty()) {
            throw new NoSuchElementException("Stack is empty");
        }
        return data[size - 1];
    }

    public T peek() {
        return top();
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        Arrays.fill(data, 0, size, null);
        size = 0;
    }

    public int capacity() {
        return data.length;
    }

    public List<T> toList() {
        List<T> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(data[i]);
        }
        return result;
    }

    @Override
    public String toString() {
        return toList().toString();
    }

    private void resize(int newCapacity) {
        data = Arrays.copyOf(data, newCapacity);
    }
}
