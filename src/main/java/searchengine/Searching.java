package searchengine;

import java.util.Objects;

public final class Searching {

    private Searching() {
    }

    public static <T> int linearSearch(T[] array, T key) {
        for (int i = 0; i < array.length; i++) {
            if (Objects.equals(array[i], key)) {
                return i;
            }
        }
        return -1;
    }

    public static <T> int linearSearch(LinkedList<T> list, T key) {
        return list.indexOf(key);
    }

    public static <T extends Comparable<? super T>> int binarySearch(T[] sortedArray, T key) {
        if (key == null) {
            throw new IllegalArgumentException("Key must not be null");
        }
        int low = 0;
        int high = sortedArray.length - 1;
        while (low <= high) {
            int middle = low + (high - low) / 2;
            int comparison = key.compareTo(sortedArray[middle]);
            if (comparison == 0) {
                return middle;
            } else if (comparison < 0) {
                high = middle - 1;
            } else {
                low = middle + 1;
            }
        }
        return -1;
    }
}
