package searchengine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public class LinkedList<T> implements Iterable<T> {

    private static final class Node<T> {
        private T data;
        private Node<T> next;

        private Node(T data, Node<T> next) {
            this.data = data;
            this.next = next;
        }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public void insert(T item) {
        insertLast(item);
    }

    public void insertFirst(T item) {
        head = new Node<>(item, head);
        if (tail == null) {
            tail = head;
        }
        size++;
    }

    public void insertLast(T item) {
        Node<T> node = new Node<>(item, null);
        if (tail == null) {
            head = node;
        } else {
            tail.next = node;
        }
        tail = node;
        size++;
    }

    public void insertAt(int index, T item) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        if (index == 0) {
            insertFirst(item);
        } else if (index == size) {
            insertLast(item);
        } else {
            Node<T> previous = nodeAt(index - 1);
            previous.next = new Node<>(item, previous.next);
            size++;
        }
    }

    public boolean delete(T item) {
        Node<T> previous = null;
        Node<T> current = head;
        while (current != null) {
            if (Objects.equals(current.data, item)) {
                unlink(previous, current);
                return true;
            }
            previous = current;
            current = current.next;
        }
        return false;
    }

    public T deleteAt(int index) {
        checkElementIndex(index);
        Node<T> previous = index == 0 ? null : nodeAt(index - 1);
        Node<T> target = previous == null ? head : previous.next;
        unlink(previous, target);
        return target.data;
    }

    public T deleteFirst() {
        if (head == null) {
            return null;
        }
        T data = head.data;
        unlink(null, head);
        return data;
    }

    public T get(int index) {
        checkElementIndex(index);
        return nodeAt(index).data;
    }

    public T getFirst() {
        return head == null ? null : head.data;
    }

    public T getLast() {
        return tail == null ? null : tail.data;
    }

    public boolean contains(T item) {
        return indexOf(item) >= 0;
    }

    public int indexOf(T item) {
        int index = 0;
        for (Node<T> current = head; current != null; current = current.next) {
            if (Objects.equals(current.data, item)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    public List<T> toList() {
        List<T> result = new ArrayList<>(size);
        for (T item : this) {
            result.add(item);
        }
        return result;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (current == null) {
                    throw new NoSuchElementException();
                }
                T data = current.data;
                current = current.next;
                return data;
            }
        };
    }

    @Override
    public String toString() {
        return toList().toString();
    }

    private void unlink(Node<T> previous, Node<T> target) {
        if (previous == null) {
            head = target.next;
        } else {
            previous.next = target.next;
        }
        if (target == tail) {
            tail = previous;
        }
        target.next = null;
        size--;
    }

    private Node<T> nodeAt(int index) {
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current;
    }

    private void checkElementIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }
}
