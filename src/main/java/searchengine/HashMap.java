package searchengine;

import java.util.ArrayList;
import java.util.List;

public class HashMap<K, V> implements MapADT<K, V> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final double MAX_LOAD_FACTOR = 0.75;

    private static final class Entry<K, V> {
        private final K key;
        private V value;

        private Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private LinkedList<Entry<K, V>>[] buckets;
    private int size;

    public HashMap() {
        this(DEFAULT_CAPACITY);
    }

    public HashMap(int initialCapacity) {
        int capacity = 1;
        while (capacity < Math.max(1, initialCapacity)) {
            capacity <<= 1;
        }
        buckets = createTable(capacity);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private LinkedList<Entry<K, V>>[] createTable(int capacity) {
        return (LinkedList<Entry<K, V>>[]) new LinkedList[capacity];
    }

    @Override
    public void insert(K key, V value) {
        MapADT.requireNonNull(key, value);
        Entry<K, V> existing = findEntry(key);
        if (existing != null) {
            existing.value = value;
            return;
        }
        addToBucket(buckets, new Entry<>(key, value));
        size++;
        if (size > MAX_LOAD_FACTOR * buckets.length) {
            resize(buckets.length * 2);
        }
    }

    @Override
    public V search(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key must not be null");
        }
        Entry<K, V> entry = findEntry(key);
        return entry == null ? null : entry.value;
    }

    @Override
    public V remove(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key must not be null");
        }
        LinkedList<Entry<K, V>> bucket = buckets[indexFor(key, buckets.length)];
        if (bucket == null) {
            return null;
        }
        for (Entry<K, V> entry : bucket) {
            if (entry.key.equals(key)) {
                bucket.delete(entry);
                size--;
                return entry.value;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public List<K> keys() {
        List<K> result = new ArrayList<>(size);
        for (LinkedList<Entry<K, V>> bucket : buckets) {
            if (bucket != null) {
                for (Entry<K, V> entry : bucket) {
                    result.add(entry.key);
                }
            }
        }
        return result;
    }

    public int capacity() {
        return buckets.length;
    }

    public double loadFactor() {
        return (double) size / buckets.length;
    }

    public int maxChainLength() {
        int longest = 0;
        for (LinkedList<Entry<K, V>> bucket : buckets) {
            if (bucket != null) {
                longest = Math.max(longest, bucket.size());
            }
        }
        return longest;
    }

    private Entry<K, V> findEntry(K key) {
        LinkedList<Entry<K, V>> bucket = buckets[indexFor(key, buckets.length)];
        if (bucket == null) {
            return null;
        }
        for (Entry<K, V> entry : bucket) {
            if (entry.key.equals(key)) {
                return entry;
            }
        }
        return null;
    }

    private void resize(int newCapacity) {
        LinkedList<Entry<K, V>>[] newBuckets = createTable(newCapacity);
        for (LinkedList<Entry<K, V>> bucket : buckets) {
            if (bucket != null) {
                for (Entry<K, V> entry : bucket) {
                    addToBucket(newBuckets, entry);
                }
            }
        }
        buckets = newBuckets;
    }

    private void addToBucket(LinkedList<Entry<K, V>>[] table, Entry<K, V> entry) {
        int index = indexFor(entry.key, table.length);
        if (table[index] == null) {
            table[index] = new LinkedList<>();
        }
        table[index].insertFirst(entry);
    }

    private static int indexFor(Object key, int length) {
        int hash = key.hashCode();
        hash ^= (hash >>> 16);
        return hash & (length - 1);
    }
}
