package searchengine;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;

public class Benchmark {

    private static final int[] SORT_SIZES = {1_000, 10_000, 50_000, 100_000};
    private static final int[] SEARCH_SIZES = {1_000, 10_000, 100_000};
    private static final int[] MAP_SIZES = {1_000, 5_000, 10_000, 20_000};
    private static final int SORT_TRIALS = 5;
    private static final int MAP_TRIALS = 3;
    private static final int SEARCH_QUERIES = 1_000;

    private final Random random = new Random(42);
    private final StringBuilder report = new StringBuilder();

    public static void main(String[] args) throws IOException {
        Path longSorted = Paths.get(args.length > 0 ? args[0] : "data/long_sorted.txt");
        Path output = Paths.get(args.length > 1 ? args[1] : "results/benchmark_results.md");
        Benchmark benchmark = new Benchmark();
        benchmark.run(longSorted);
        Files.createDirectories(output.toAbsolutePath().getParent());
        Files.write(output, benchmark.report.toString().getBytes(StandardCharsets.UTF_8));
        System.out.println("Results written to " + output);
    }

    public void run(Path longSorted) throws IOException {
        line("# Benchmark Results");
        line("");
        line("Java " + System.getProperty("java.version") + ", " + System.getProperty("os.name")
                + ", " + Runtime.getRuntime().availableProcessors() + " cores. Times are medians.");
        line("");
        warmUp();
        sortingExperiment();
        searchingExperiment();
        mapExperiment();
        longSortedExperiment(longSorted);
    }

    private void warmUp() {
        String[] words = randomWords(5_000).toArray(new String[0]);
        for (int i = 0; i < 5; i++) {
            Sorting.mergeSort(words.clone());
            Sorting.quickSort(words.clone());
            for (MapType type : MapType.values()) {
                MapADT<String, Integer> map = type.create();
                for (String word : words) {
                    map.insert(word, 1);
                    map.search(word);
                }
            }
        }
    }

    private void sortingExperiment() {
        line("## 1. Sorting (ms)");
        line("");
        line("| n | input | merge sort | quick sort | merge ns/(n log2 n) | quick ns/(n log2 n) |");
        line("|---:|---|---:|---:|---:|---:|");
        for (int n : SORT_SIZES) {
            String[] randomInput = randomWords(n).toArray(new String[0]);
            String[] sortedInput = randomInput.clone();
            Arrays.sort(sortedInput);
            String[] reversedInput = sortedInput.clone();
            Collections.reverse(Arrays.asList(reversedInput));
            String[][] inputs = {randomInput, sortedInput, reversedInput};
            String[] labels = {"random", "sorted", "reversed"};
            for (int i = 0; i < inputs.length; i++) {
                double merge = medianSortTime(inputs[i], Sorting::mergeSort);
                double quick = medianSortTime(inputs[i], Sorting::quickSort);
                double nLogN = n * (Math.log(n) / Math.log(2));
                line(String.format("| %,d | %s | %.2f | %.2f | %.1f | %.1f |", n, labels[i], merge, quick,
                        merge * 1e6 / nLogN, quick * 1e6 / nLogN));
            }
        }
        line("");
    }

    private double medianSortTime(String[] input, Consumer<String[]> sorter) {
        double[] times = new double[SORT_TRIALS];
        for (int t = 0; t < SORT_TRIALS; t++) {
            String[] copy = input.clone();
            long start = System.nanoTime();
            sorter.accept(copy);
            times[t] = (System.nanoTime() - start) / 1e6;
        }
        return median(times);
    }

    private void searchingExperiment() {
        line("## 2. Linear vs. binary search (average ns per lookup, " + SEARCH_QUERIES + " lookups, 50% hits)");
        line("");
        line("| n | linear search | binary search | speedup |");
        line("|---:|---:|---:|---:|");
        for (int n : SEARCH_SIZES) {
            List<String> words = randomWords(n + SEARCH_QUERIES / 2);
            List<String> present = new ArrayList<>(words.subList(0, n));
            List<String> absent = words.subList(n, words.size());
            String[] unsorted = present.toArray(new String[0]);
            String[] sorted = unsorted.clone();
            Sorting.mergeSort(sorted);
            String[] queries = new String[SEARCH_QUERIES];
            for (int i = 0; i < SEARCH_QUERIES; i++) {
                queries[i] = i % 2 == 0 ? present.get(random.nextInt(n)) : absent.get(i / 2);
            }
            double linear = medianOf(3, () -> {
                long start = System.nanoTime();
                for (String query : queries) {
                    Searching.linearSearch(unsorted, query);
                }
                return (System.nanoTime() - start) / (double) SEARCH_QUERIES;
            });
            double binary = medianOf(3, () -> {
                long start = System.nanoTime();
                for (String query : queries) {
                    Searching.binarySearch(sorted, query);
                }
                return (System.nanoTime() - start) / (double) SEARCH_QUERIES;
            });
            line(String.format("| %,d | %,.0f | %,.0f | %,.0fx |", n, linear, binary, linear / binary));
        }
        line("");
    }

    private void mapExperiment() {
        line("## 3. Map implementations");
        line("");
        line("Insert = total ms to insert all n keys. Search = average ns per successful lookup (keys queried in random order).");
        line("Height counts nodes on the longest root-to-leaf path. For the hash table, the height column shows the longest chain.");
        line("");
        line("| n | key order | structure | insert (ms) | search (ns) | height | rotations |");
        line("|---:|---|---|---:|---:|---:|---:|");
        for (int n : MAP_SIZES) {
            List<String> randomKeys = randomWords(n);
            List<String> sortedKeys = new ArrayList<>(randomKeys);
            Collections.sort(sortedKeys);
            List<String> lookupOrder = new ArrayList<>(randomKeys);
            Collections.shuffle(lookupOrder, random);
            for (String order : new String[] {"random", "sorted"}) {
                List<String> keys = order.equals("random") ? randomKeys : sortedKeys;
                for (MapType type : MapType.values()) {
                    MapResult result = measureMap(type, keys, lookupOrder);
                    line(String.format("| %,d | %s | %s | %.2f | %,.0f | %d | %s |", n, order, type,
                            result.insertMillis, result.searchNanos, result.shape,
                            result.rotations < 0 ? "-" : String.format("%,d", result.rotations)));
                }
            }
        }
        line("");
    }

    private MapResult measureMap(MapType type, List<String> keys, List<String> lookupOrder) {
        double[] insertTimes = new double[MAP_TRIALS];
        double[] searchTimes = new double[MAP_TRIALS];
        MapADT<String, Integer> map = null;
        for (int t = 0; t < MAP_TRIALS; t++) {
            map = type.create();
            long start = System.nanoTime();
            for (int i = 0; i < keys.size(); i++) {
                map.insert(keys.get(i), i);
            }
            insertTimes[t] = (System.nanoTime() - start) / 1e6;
            start = System.nanoTime();
            for (String key : lookupOrder) {
                if (map.search(key) == null) {
                    throw new IllegalStateException("Lookup failed for " + key);
                }
            }
            searchTimes[t] = (System.nanoTime() - start) / (double) lookupOrder.size();
        }
        return new MapResult(median(insertTimes), median(searchTimes), shapeOf(map), rotationsOf(map));
    }

    private void longSortedExperiment(Path longSorted) throws IOException {
        line("## 4. Critical test: inserting long_sorted.txt");
        line("");
        if (!Files.exists(longSorted)) {
            line("File not found: " + longSorted);
            return;
        }
        List<String> words = Files.readAllLines(longSorted, StandardCharsets.UTF_8);
        words.removeIf(String::isBlank);
        line("Words in file: " + words.size() + " (already in alphabetical order)");
        line("");
        line("| structure | insert time (microseconds) | height | rotations |");
        line("|---|---:|---:|---:|");
        for (MapType type : MapType.values()) {
            double[] times = new double[51];
            MapADT<String, Integer> map = null;
            for (int t = 0; t < times.length; t++) {
                map = type.create();
                long start = System.nanoTime();
                for (int i = 0; i < words.size(); i++) {
                    map.insert(words.get(i).trim(), i);
                }
                times[t] = (System.nanoTime() - start) / 1e3;
            }
            long rotations = rotationsOf(map);
            line(String.format("| %s | %.1f | %d | %s |", type, median(times), shapeOf(map),
                    rotations < 0 ? "-" : String.valueOf(rotations)));
        }
        line("");
    }

    private int shapeOf(MapADT<String, Integer> map) {
        if (map instanceof BSTMap) {
            return ((BSTMap<String, Integer>) map).height();
        }
        if (map instanceof AVLMap) {
            return ((AVLMap<String, Integer>) map).height();
        }
        if (map instanceof RedBlackMap) {
            return ((RedBlackMap<String, Integer>) map).height();
        }
        return ((HashMap<String, Integer>) map).maxChainLength();
    }

    private long rotationsOf(MapADT<String, Integer> map) {
        if (map instanceof AVLMap) {
            return ((AVLMap<String, Integer>) map).getRotationCount();
        }
        if (map instanceof RedBlackMap) {
            return ((RedBlackMap<String, Integer>) map).getRotationCount();
        }
        return -1;
    }

    private List<String> randomWords(int count) {
        Set<String> words = new LinkedHashSet<>();
        char[] buffer = new char[8];
        while (words.size() < count) {
            for (int i = 0; i < buffer.length; i++) {
                buffer[i] = (char) ('a' + random.nextInt(26));
            }
            words.add(new String(buffer));
        }
        return new ArrayList<>(words);
    }

    private interface Measurement {
        double measure();
    }

    private double medianOf(int trials, Measurement measurement) {
        double[] values = new double[trials];
        for (int i = 0; i < trials; i++) {
            values[i] = measurement.measure();
        }
        return median(values);
    }

    private static double median(double[] values) {
        double[] copy = values.clone();
        Arrays.sort(copy);
        int middle = copy.length / 2;
        return copy.length % 2 == 1 ? copy[middle] : (copy[middle - 1] + copy[middle]) / 2.0;
    }

    private void line(String text) {
        report.append(text).append(System.lineSeparator());
        System.out.println(text);
    }

    private static final class MapResult {
        private final double insertMillis;
        private final double searchNanos;
        private final int shape;
        private final long rotations;

        private MapResult(double insertMillis, double searchNanos, int shape, long rotations) {
            this.insertMillis = insertMillis;
            this.searchNanos = searchNanos;
            this.shape = shape;
            this.rotations = rotations;
        }
    }
}
