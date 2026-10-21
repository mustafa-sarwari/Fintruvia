package searchengine;

import java.util.ArrayList;
import java.util.List;

public class BSTMap<K extends Comparable<? super K>, V> implements MapADT<K, V> {

    private static final class Node<K, V> {
        private K key;
        private V value;
        private Node<K, V> left;
        private Node<K, V> right;

        private Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private Node<K, V> root;
    private int size;

    @Override
    public void insert(K key, V value) {
        MapADT.requireNonNull(key, value);
        if (root == null) {
            root = new Node<>(key, value);
            size++;
            return;
        }
        Node<K, V> current = root;
        while (true) {
            int comparison = key.compareTo(current.key);
            if (comparison == 0) {
                current.value = value;
                return;
            }
            Node<K, V> next = comparison < 0 ? current.left : current.right;
            if (next == null) {
                Node<K, V> node = new Node<>(key, value);
                if (comparison < 0) {
                    current.left = node;
                } else {
                    current.right = node;
                }
                size++;
                return;
            }
            current = next;
        }
    }

    @Override
    public V search(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key must not be null");
        }
        Node<K, V> current = root;
        while (current != null) {
            int comparison = key.compareTo(current.key);
            if (comparison == 0) {
                return current.value;
            }
            current = comparison < 0 ? current.left : current.right;
        }
        return null;
    }

    @Override
    public V remove(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key must not be null");
        }
        Node<K, V> parent = null;
        Node<K, V> current = root;
        while (current != null) {
            int comparison = key.compareTo(current.key);
            if (comparison == 0) {
                break;
            }
            parent = current;
            current = comparison < 0 ? current.left : current.right;
        }
        if (current == null) {
            return null;
        }
        V removedValue = current.value;
        if (current.left != null && current.right != null) {
            Node<K, V> successorParent = current;
            Node<K, V> successor = current.right;
            while (successor.left != null) {
                successorParent = successor;
                successor = successor.left;
            }
            current.key = successor.key;
            current.value = successor.value;
            parent = successorParent;
            current = successor;
        }
        Node<K, V> child = current.left != null ? current.left : current.right;
        if (parent == null) {
            root = child;
        } else if (parent.left == current) {
            parent.left = child;
        } else {
            parent.right = child;
        }
        size--;
        return removedValue;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public List<K> keys() {
        List<K> result = new ArrayList<>(size);
        ArrayStack<Node<K, V>> stack = new ArrayStack<>();
        Node<K, V> current = root;
        while (current != null || !stack.isEmpty()) {
            while (current != null) {
                stack.push(current);
                current = current.left;
            }
            current = stack.pop();
            result.add(current.key);
            current = current.right;
        }
        return result;
    }

    public int height() {
        if (root == null) {
            return 0;
        }
        ListQueue<Node<K, V>> level = new ListQueue<>();
        level.enqueue(root);
        int height = 0;
        while (!level.isEmpty()) {
            height++;
            int nodesInLevel = level.size();
            for (int i = 0; i < nodesInLevel; i++) {
                Node<K, V> node = level.dequeue();
                if (node.left != null) {
                    level.enqueue(node.left);
                }
                if (node.right != null) {
                    level.enqueue(node.right);
                }
            }
        }
        return height;
    }

    public K rootKey() {
        return root == null ? null : root.key;
    }

    public boolean isValid() {
        List<K> ordered = keys();
        for (int i = 1; i < ordered.size(); i++) {
            if (ordered.get(i - 1).compareTo(ordered.get(i)) >= 0) {
                return false;
            }
        }
        return ordered.size() == size;
    }
}
