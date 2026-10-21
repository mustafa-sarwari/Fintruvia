package searchengine;

import java.util.ArrayList;
import java.util.List;

public class RedBlackMap<K extends Comparable<? super K>, V> implements MapADT<K, V> {

    private static final boolean RED = true;
    private static final boolean BLACK = false;

    private static final class Node<K, V> {
        private K key;
        private V value;
        private Node<K, V> left;
        private Node<K, V> right;
        private Node<K, V> parent;
        private boolean color;

        private Node(K key, V value, boolean color) {
            this.key = key;
            this.value = value;
            this.color = color;
        }
    }

    private final Node<K, V> nil;
    private Node<K, V> root;
    private int size;
    private long rotations;

    public RedBlackMap() {
        nil = new Node<>(null, null, BLACK);
        nil.left = nil;
        nil.right = nil;
        nil.parent = nil;
        root = nil;
    }

    @Override
    public void insert(K key, V value) {
        MapADT.requireNonNull(key, value);
        Node<K, V> parent = nil;
        Node<K, V> current = root;
        int comparison = 0;
        while (current != nil) {
            parent = current;
            comparison = key.compareTo(current.key);
            if (comparison == 0) {
                current.value = value;
                return;
            }
            current = comparison < 0 ? current.left : current.right;
        }
        Node<K, V> node = new Node<>(key, value, RED);
        node.parent = parent;
        node.left = nil;
        node.right = nil;
        if (parent == nil) {
            root = node;
        } else if (comparison < 0) {
            parent.left = node;
        } else {
            parent.right = node;
        }
        size++;
        insertFixup(node);
    }

    private void insertFixup(Node<K, V> node) {
        while (node.parent.color == RED) {
            Node<K, V> grandparent = node.parent.parent;
            if (node.parent == grandparent.left) {
                Node<K, V> uncle = grandparent.right;
                if (uncle.color == RED) {
                    node.parent.color = BLACK;
                    uncle.color = BLACK;
                    grandparent.color = RED;
                    node = grandparent;
                } else {
                    if (node == node.parent.right) {
                        node = node.parent;
                        rotateLeft(node);
                    }
                    node.parent.color = BLACK;
                    node.parent.parent.color = RED;
                    rotateRight(node.parent.parent);
                }
            } else {
                Node<K, V> uncle = grandparent.left;
                if (uncle.color == RED) {
                    node.parent.color = BLACK;
                    uncle.color = BLACK;
                    grandparent.color = RED;
                    node = grandparent;
                } else {
                    if (node == node.parent.left) {
                        node = node.parent;
                        rotateRight(node);
                    }
                    node.parent.color = BLACK;
                    node.parent.parent.color = RED;
                    rotateLeft(node.parent.parent);
                }
            }
        }
        root.color = BLACK;
    }

    @Override
    public V search(K key) {
        Node<K, V> node = findNode(key);
        return node == nil ? null : node.value;
    }

    private Node<K, V> findNode(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key must not be null");
        }
        Node<K, V> current = root;
        while (current != nil) {
            int comparison = key.compareTo(current.key);
            if (comparison == 0) {
                return current;
            }
            current = comparison < 0 ? current.left : current.right;
        }
        return nil;
    }

    @Override
    public V remove(K key) {
        Node<K, V> target = findNode(key);
        if (target == nil) {
            return null;
        }
        V removedValue = target.value;
        delete(target);
        size--;
        return removedValue;
    }

    private void delete(Node<K, V> target) {
        Node<K, V> moved = target;
        boolean movedOriginalColor = moved.color;
        Node<K, V> replacement;
        if (target.left == nil) {
            replacement = target.right;
            transplant(target, target.right);
        } else if (target.right == nil) {
            replacement = target.left;
            transplant(target, target.left);
        } else {
            moved = minimum(target.right);
            movedOriginalColor = moved.color;
            replacement = moved.right;
            if (moved.parent == target) {
                replacement.parent = moved;
            } else {
                transplant(moved, moved.right);
                moved.right = target.right;
                moved.right.parent = moved;
            }
            transplant(target, moved);
            moved.left = target.left;
            moved.left.parent = moved;
            moved.color = target.color;
        }
        if (movedOriginalColor == BLACK) {
            deleteFixup(replacement);
        }
        nil.parent = nil;
    }

    private void deleteFixup(Node<K, V> node) {
        while (node != root && node.color == BLACK) {
            if (node == node.parent.left) {
                Node<K, V> sibling = node.parent.right;
                if (sibling.color == RED) {
                    sibling.color = BLACK;
                    node.parent.color = RED;
                    rotateLeft(node.parent);
                    sibling = node.parent.right;
                }
                if (sibling.left.color == BLACK && sibling.right.color == BLACK) {
                    sibling.color = RED;
                    node = node.parent;
                } else {
                    if (sibling.right.color == BLACK) {
                        sibling.left.color = BLACK;
                        sibling.color = RED;
                        rotateRight(sibling);
                        sibling = node.parent.right;
                    }
                    sibling.color = node.parent.color;
                    node.parent.color = BLACK;
                    sibling.right.color = BLACK;
                    rotateLeft(node.parent);
                    node = root;
                }
            } else {
                Node<K, V> sibling = node.parent.left;
                if (sibling.color == RED) {
                    sibling.color = BLACK;
                    node.parent.color = RED;
                    rotateRight(node.parent);
                    sibling = node.parent.left;
                }
                if (sibling.right.color == BLACK && sibling.left.color == BLACK) {
                    sibling.color = RED;
                    node = node.parent;
                } else {
                    if (sibling.left.color == BLACK) {
                        sibling.right.color = BLACK;
                        sibling.color = RED;
                        rotateLeft(sibling);
                        sibling = node.parent.left;
                    }
                    sibling.color = node.parent.color;
                    node.parent.color = BLACK;
                    sibling.left.color = BLACK;
                    rotateRight(node.parent);
                    node = root;
                }
            }
        }
        node.color = BLACK;
    }

    private void transplant(Node<K, V> oldNode, Node<K, V> newNode) {
        if (oldNode.parent == nil) {
            root = newNode;
        } else if (oldNode == oldNode.parent.left) {
            oldNode.parent.left = newNode;
        } else {
            oldNode.parent.right = newNode;
        }
        newNode.parent = oldNode.parent;
    }

    private Node<K, V> minimum(Node<K, V> node) {
        while (node.left != nil) {
            node = node.left;
        }
        return node;
    }

    private void rotateLeft(Node<K, V> node) {
        Node<K, V> pivot = node.right;
        node.right = pivot.left;
        if (pivot.left != nil) {
            pivot.left.parent = node;
        }
        pivot.parent = node.parent;
        if (node.parent == nil) {
            root = pivot;
        } else if (node == node.parent.left) {
            node.parent.left = pivot;
        } else {
            node.parent.right = pivot;
        }
        pivot.left = node;
        node.parent = pivot;
        rotations++;
    }

    private void rotateRight(Node<K, V> node) {
        Node<K, V> pivot = node.left;
        node.left = pivot.right;
        if (pivot.right != nil) {
            pivot.right.parent = node;
        }
        pivot.parent = node.parent;
        if (node.parent == nil) {
            root = pivot;
        } else if (node == node.parent.right) {
            node.parent.right = pivot;
        } else {
            node.parent.left = pivot;
        }
        pivot.right = node;
        node.parent = pivot;
        rotations++;
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
        if (node == nil) {
            return;
        }
        collectKeys(node.left, result);
        result.add(node.key);
        collectKeys(node.right, result);
    }

    public int height() {
        return height(root);
    }

    private int height(Node<K, V> node) {
        if (node == nil) {
            return 0;
        }
        return 1 + Math.max(height(node.left), height(node.right));
    }

    public long getRotationCount() {
        return rotations;
    }

    public K rootKey() {
        return root == nil ? null : root.key;
    }

    public boolean isRootRed() {
        return root.color == RED;
    }

    public boolean isValid() {
        if (root.color != BLACK || nil.color != BLACK) {
            return false;
        }
        if (root != nil && root.parent != nil) {
            return false;
        }
        List<K> ordered = keys();
        for (int i = 1; i < ordered.size(); i++) {
            if (ordered.get(i - 1).compareTo(ordered.get(i)) >= 0) {
                return false;
            }
        }
        return ordered.size() == size && blackHeight(root) > 0;
    }

    private int blackHeight(Node<K, V> node) {
        if (node == nil) {
            return 1;
        }
        if (node.color == RED && (node.left.color == RED || node.right.color == RED)) {
            return -1;
        }
        if ((node.left != nil && node.left.parent != node) || (node.right != nil && node.right.parent != node)) {
            return -1;
        }
        int leftBlackHeight = blackHeight(node.left);
        int rightBlackHeight = blackHeight(node.right);
        if (leftBlackHeight < 0 || rightBlackHeight < 0 || leftBlackHeight != rightBlackHeight) {
            return -1;
        }
        return leftBlackHeight + (node.color == BLACK ? 1 : 0);
    }
}
