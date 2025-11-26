package algo;

import model.ImmutableList;
import java.util.Comparator;
import java.util.Objects;

/**
 * Searcher: contains linear search and binary search (functional style helpers).
 * Binary search assumes the list is sorted according to the Comparator you use.
 */
public final class Searcher {

    private Searcher() {}

    /** Linear search, returns index or -1 (null-safe) */
    public static <T> int linearSearch(ImmutableList<T> list, T key) {
        Objects.requireNonNull(list, "list must not be null");
        for (int i = 0; i < list.size(); i++) {
            if (Objects.equals(list.get(i), key)) return i;
        }
        return -1;
    }

    /** Binary search on an ImmutableList — returns index or -1. If `cmp` is null, tries natural ordering. */
    @SuppressWarnings("unchecked")
    public static <T> int binarySearch(ImmutableList<T> list, T key, Comparator<T> cmp) {
        Objects.requireNonNull(list, "list must not be null");
        if (list.size() == 0) return -1;

        final Comparator<T> baseCmp;
        if (cmp != null) {
            baseCmp = cmp;
        } else {
            // try to derive natural order comparator
            Object first = list.get(0);
            if (!(first instanceof Comparable)) {
                throw new IllegalArgumentException("Comparator is null and elements are not Comparable");
            }
            baseCmp = (a, b) -> {
                if (a == b) return 0;
                if (a == null) return -1;
                if (b == null) return 1;
                return ((Comparable<? super T>) a).compareTo(b);
            };
        }

        // safe comparator that handles nulls consistently
        final Comparator<T> safeCmp = (a, b) -> {
            if (a == b) return 0;
            if (a == null) return -1;
            if (b == null) return 1;
            return baseCmp.compare(a, b);
        };

        int lo = 0, hi = list.size() - 1;
        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            T midVal = list.get(mid);
            int c = safeCmp.compare(midVal, key);
            if (c < 0) lo = mid + 1;
            else if (c > 0) hi = mid - 1;
            else return mid; // found
        }
        return -1; // not found
    }
}
