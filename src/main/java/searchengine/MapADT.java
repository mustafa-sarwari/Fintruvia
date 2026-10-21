package searchengine;

import java.util.List;

public interface MapADT<K, V> {

    void insert(K key, V value);

    V search(K key);

    V remove(K key);

    int size();

    List<K> keys();

    default boolean containsKey(K key) {
        return search(key) != null;
    }

    default boolean isEmpty() {
        return size() == 0;
    }

    static void requireNonNull(Object key, Object value) {
        if (key == null) {
            throw new IllegalArgumentException("Key must not be null");
        }
        if (value == null) {
            throw new IllegalArgumentException("Value must not be null");
        }
    }
}
