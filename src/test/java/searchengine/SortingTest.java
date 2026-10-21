package searchengine;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.Arrays;
import java.util.Random;
import org.junit.jupiter.api.Test;

class SortingTest {

    private static void assertBothSort(String[] input, String[] expected) {
        String[] merged = input.clone();
        Sorting.mergeSort(merged);
        assertArrayEquals(expected, merged);
        String[] quick = input.clone();
        Sorting.quickSort(quick);
        assertArrayEquals(expected, quick);
    }

    @Test
    void emptyArray() {
        assertBothSort(new String[] {}, new String[] {});
    }

    @Test
    void singleElement() {
        assertBothSort(new String[] {"a"}, new String[] {"a"});
    }

    @Test
    void preSorted() {
        assertBothSort(new String[] {"a", "b", "c"}, new String[] {"a", "b", "c"});
    }

    @Test
    void reverseSorted() {
        assertBothSort(new String[] {"c", "b", "a"}, new String[] {"a", "b", "c"});
    }

    @Test
    void duplicates() {
        assertBothSort(new String[] {"c", "a", "c", "b", "a"}, new String[] {"a", "a", "b", "c", "c"});
    }

    @Test
    void allEqualElements() {
        assertBothSort(new String[] {"x", "x", "x", "x"}, new String[] {"x", "x", "x", "x"});
    }

    @Test
    void largeRandomArrayMatchesJavaSort() {
        Random random = new Random(7);
        String[] input = new String[5000];
        for (int i = 0; i < input.length; i++) {
            input[i] = Integer.toString(random.nextInt(2000), 36);
        }
        String[] expected = input.clone();
        Arrays.sort(expected);
        assertBothSort(input, expected);
    }

    @Test
    void worksForIntegersToo() {
        Integer[] numbers = {5, -1, 3, 3, 0};
        Sorting.mergeSort(numbers);
        assertArrayEquals(new Integer[] {-1, 0, 3, 3, 5}, numbers);
        Integer[] more = {9, 8, 7};
        Sorting.quickSort(more);
        assertArrayEquals(new Integer[] {7, 8, 9}, more);
    }
}
