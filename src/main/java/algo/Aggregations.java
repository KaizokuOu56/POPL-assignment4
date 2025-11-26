package algo;

import model.ImmutableList;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Aggregations: pure functions returning new values (counts, frequencies, inversion count)
 */
public final class Aggregations {
    private Aggregations() {}

    /**
     * Frequency counts: returns a Map from T -> count (immutable map not required)
     */
    public static <T> Map<T, Long> frequencyCount(ImmutableList<T> list) {
        Objects.requireNonNull(list, "list must not be null");
        return list.asList().stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    /**
     * Average for numbers (double). Returns OptionalDouble empty if list is empty.
     */
    public static OptionalDouble average(ImmutableList<? extends Number> list) {
        Objects.requireNonNull(list, "list must not be null");
        if (list.size() == 0) return OptionalDouble.empty();
        double sum = 0.0;
        for (int i = 0; i < list.size(); i++) {
            Number n = list.get(i);
            if (n == null) throw new IllegalArgumentException("List contains null element");
            sum += n.doubleValue();
        }
        return OptionalDouble.of(sum / list.size());
    }

    /**
     * Count inversions using merge-sort technique (O(n log n)). Works for Comparable elements.
     */
    public static <T extends Comparable<T>> long countInversions(ImmutableList<T> list) {
        Objects.requireNonNull(list, "list must not be null");
        if (list.size() <= 1) return 0L;
        // convert to array for faster manipulation in helper
        @SuppressWarnings("unchecked")
        T[] arr = (T[]) new Comparable[list.size()];
        for (int i = 0; i < list.size(); i++) arr[i] = list.get(i);
        return mergeCount(arr, 0, arr.length - 1);
    }

    private static <T extends Comparable<T>> long mergeCount(T[] arr, int l, int r) {
        if (l >= r) return 0L;
        int m = (l + r) >>> 1;
        long left = mergeCount(arr, l, m);
        long right = mergeCount(arr, m + 1, r);
        long split = mergeAndCount(arr, l, m, r);
        return left + right + split;
    }

    private static <T extends Comparable<T>> long mergeAndCount(T[] arr, int l, int m, int r) {
        T[] left = Arrays.copyOfRange(arr, l, m + 1);
        T[] right = Arrays.copyOfRange(arr, m + 1, r + 1);
        int i = 0, j = 0, k = l;
        long count = 0L;
        while (i < left.length && j < right.length) {
            if (left[i].compareTo(right[j]) <= 0) {
                arr[k++] = left[i++];
            } else {
                arr[k++] = right[j++];
                count += (left.length - i); // all remaining in left are inversions
            }
        }
        while (i < left.length) arr[k++] = left[i++];
        while (j < right.length) arr[k++] = right[j++];
        return count;
    }
}
