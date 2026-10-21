package searchengine;

import java.util.Random;

public final class Sorting {

    private static final Random RANDOM = new Random();

    private Sorting() {
    }

    public static <T extends Comparable<? super T>> void mergeSort(T[] array) {
        if (array.length < 2) {
            return;
        }
        T[] buffer = array.clone();
        mergeSort(array, buffer, 0, array.length - 1);
    }

    private static <T extends Comparable<? super T>> void mergeSort(T[] array, T[] buffer, int low, int high) {
        if (low >= high) {
            return;
        }
        int middle = low + (high - low) / 2;
        mergeSort(array, buffer, low, middle);
        mergeSort(array, buffer, middle + 1, high);
        if (array[middle].compareTo(array[middle + 1]) <= 0) {
            return;
        }
        merge(array, buffer, low, middle, high);
    }

    private static <T extends Comparable<? super T>> void merge(T[] array, T[] buffer, int low, int middle, int high) {
        System.arraycopy(array, low, buffer, low, high - low + 1);
        int left = low;
        int right = middle + 1;
        for (int k = low; k <= high; k++) {
            if (left > middle) {
                array[k] = buffer[right++];
            } else if (right > high) {
                array[k] = buffer[left++];
            } else if (buffer[right].compareTo(buffer[left]) < 0) {
                array[k] = buffer[right++];
            } else {
                array[k] = buffer[left++];
            }
        }
    }

    public static <T extends Comparable<? super T>> void quickSort(T[] array) {
        quickSort(array, 0, array.length - 1);
    }

    private static <T extends Comparable<? super T>> void quickSort(T[] array, int low, int high) {
        while (low < high) {
            swap(array, low, low + RANDOM.nextInt(high - low + 1));
            T pivot = array[low];
            int lessThan = low;
            int greaterThan = high;
            int i = low + 1;
            while (i <= greaterThan) {
                int comparison = array[i].compareTo(pivot);
                if (comparison < 0) {
                    swap(array, lessThan++, i++);
                } else if (comparison > 0) {
                    swap(array, i, greaterThan--);
                } else {
                    i++;
                }
            }
            if (lessThan - low < high - greaterThan) {
                quickSort(array, low, lessThan - 1);
                low = greaterThan + 1;
            } else {
                quickSort(array, greaterThan + 1, high);
                high = lessThan - 1;
            }
        }
    }

    private static <T> void swap(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
