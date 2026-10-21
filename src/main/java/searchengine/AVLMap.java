package searchengine;

import java.util.ArrayList;
import java.util.List;

public class AVLMap<K extends Comparable<? super K>, V> implements MapADT<K, V> {

    private static final class Node<K, V> {
        private final K key;
        private V value;
        private Node<K, V> left;
        private Node<K, V> right;
        private int height = 1;

        private Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private Node<K, V> root;
    private int size;
    private long rotations;
    private V removedValue;

    @Override
    public void insert(K key, V value) {
        MapADT.requireNonNull(key, value);
        root = insert(root, key, value);
    }

    private Node<K, V> insert(Node<K, V> node, K key, V value) {
        if (node == null) {
            size++;
            return new Node<>(key, value);
        }
        int comparison = key.compareTo(node.key);
        if (comparison < 0) {
            node.left = insert(node.left, key, value);
        } else if (comparison > 0) {
            node.right = insert(node.right, key, value);
        } else {
            node.value = value;
            return node;
        }
        return rebalance(node);
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
        removedValue = null;
        root = remove(root, key);
        V result = removedValue;
        removedValue = null;
        return result;
    }

    private Node<K, V> remove(Node<K, V> node, K key) {
        if (node == null) {
            return null;
        }
        int comparison = key.compareTo(node.key);
        if (comparison < 0) {
            node.left = remove(node.left, key);
        } else if (comparison > 0) {
            node.right = remove(node.right, key);
        } else {
            removedValue = node.value;
            size--;
            if (node.left == null) {
                return node.right;
            }
            if (node.right == null) {
                return node.left;
            }
            Node<K, V> successor = minimum(node.right);
            successor.right = removeMinimum(node.right);
            successor.left = node.left;
            node = successor;
        }
        return rebalance(node);
    }

    private Node<K, V> minimum(Node<K, V> node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    private Node<K, V> removeMinimum(Node<K, V> node) {
        if (node.left == null) {
            return node.right;
        }
        node.left = removeMinimum(node.left);
        return rebalance(node);
    }

    private Node<K, V> rebalance(Node<K, V> node) {
        updateHeight(node);
        int balance = balanceFactor(node);
        if (balance > 1) {
            if (balanceFactor(node.left) < 0) {
                node.left = rotateLeft(node.left);
            }
            return rotateRight(node);
        }
        if (balance < -1) {
            if (balanceFactor(node.right) > 0) {
                node.right = rotateRight(node.right);
            }
            return rotateLeft(node);
        }
        return node;
    }

    private Node<K, V> rotateRight(Node<K, V> node) {
        Node<K, V> newRoot = node.left;
        node.left = newRoot.right;
        newRoot.right = node;
        updateHeight(node);
        updateHeight(newRoot);
        rotations++;
        return newRoot;
    }

    private Node<K, V> rotateLeft(Node<K, V> node) {
        Node<K, V> newRoot = node.right;
        node.right = newRoot.left;
        newRoot.left = node;
        updateHeight(node);
        updateHeight(newRoot);
        rotations++;
        return newRoot;
    }

    private int height(Node<K, V> node) {
        return node == null ? 0 : node.height;
    }

    private void updateHeight(Node<K, V> node) {
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }

    private int balanceFactor(Node<K, V> node) {
        return height(node.left) - height(node.right);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public List<K> keys() {
        List<K> result = new ArrayList<>(size);
        collectKeys(root, result);
        return result;
    }

    private void collectKeys(Node<K, V> node, List<K> result) {
        if (node == null) {
            return;
        }
        collectKeys(node.left, result);
        result.add(node.key);
        collectKeys(node.right, result);
    }

    public int height() {
        return height(root);
    }

    public long getRotationCount() {
        return rotations;
    }

    public K rootKey() {
        return root == null ? null : root.key;
    }

    public boolean isBalanced() {
        return checkBalance(root) >= 0;
    }

    private int checkBalance(Node<K, V> node) {
        if (node == null) {
            return 0;
        }
        int leftHeight = checkBalance(node.left);
        int rightHeight = checkBalance(node.right);
        if (leftHeight < 0 || rightHeight < 0 || Math.abs(leftHeight - rightHeight) > 1) {
            return -1;
        }
        int actualHeight = 1 + Math.max(leftHeight, rightHeight);
        return actualHeight == node.height ? actualHeight : -1;
    }

    public boolean isValid() {
        List<K> ordered = keys();
        for (int i = 1; i < ordered.size(); i++) {
            if (ordered.get(i - 1).compareTo(ordered.get(i)) >= 0) {
                return false;
            }
        }
        return ordered.size() == size && isBalanced();
    }
}
