package algo;

import model.ImmutableList;
import java.util.*;

public final class Sorter {

    public static <T> ImmutableList<T> mergeSort(ImmutableList<T> list, Comparator<T> cmp) {
        Objects.requireNonNull(list, "list must not be null");
        if (list.size() <= 1) return list;

        Comparator<T> usedCmp = cmp;
        if (usedCmp == null) {
            // derive natural order comparator (handles nulls)
            usedCmp = (a, b) -> {
                if (a == b) return 0;
                if (a == null) return -1;
                if (b == null) return 1;
                return ((Comparable<? super T>) a).compareTo(b);
            };
        }

        int mid = list.size() / 2;

        ImmutableList<T> left = slice(list, 0, mid);
        ImmutableList<T> right = slice(list, mid, list.size());

        ImmutableList<T> sortedLeft = mergeSort(left, usedCmp);
        ImmutableList<T> sortedRight = mergeSort(right, usedCmp);

        return merge(sortedLeft, sortedRight, usedCmp);
    }

    private static <T> ImmutableList<T> slice(ImmutableList<T> l, int start, int end) {
        List<T> out = new ArrayList<>(end - start);
        for (int i = start; i < end; i++) out.add(l.get(i));
        return new ImmutableList<>(out);
    }

    private static <T> ImmutableList<T> merge(
            ImmutableList<T> a, ImmutableList<T> b, Comparator<T> cmp) {

        int i = 0, j = 0;
        List<T> out = new ArrayList<>(a.size() + b.size());

        while (i < a.size() && j < b.size()) {
            T ai = a.get(i);
            T bj = b.get(j);
            int c = cmp.compare(ai, bj);
            if (c <= 0) {
                out.add(ai);
                i++;
            } else {
                out.add(bj);
                j++;
            }
        }

        while (i < a.size()) out.add(a.get(i++));
        while (j < b.size()) out.add(b.get(j++));

        return new ImmutableList<>(out);
    }
}