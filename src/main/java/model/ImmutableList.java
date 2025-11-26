package model;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public final class ImmutableList<T> implements Iterable<T> {
    private final List<T> items;

    public ImmutableList(List<T> items) {
        // defensive copy + truly immutable backing
        this.items = List.copyOf(items);
    }

    public static <T> ImmutableList<T> of(List<T> list) {
        return new ImmutableList<>(list);
    }

    public static <T> ImmutableList<T> empty() {
        return new ImmutableList<>(List.of());
    }

    public int size() {
        return items.size();
    }

    public T get(int i) {
        return items.get(i);
    }

    public List<T> asList() {
        return items;
    }

    @Override
    public Iterator<T> iterator() {
        return items.iterator();
    }

    /** Functional map */
    public <R> ImmutableList<R> map(Function<? super T, ? extends R> f) {
        List<R> out = new ArrayList<>(items.size());
        for (T t : items) out.add(f.apply(t));
        return new ImmutableList<>(out);
    }

    /** Functional filter (returns new list) */
    public ImmutableList<T> filter(Predicate<? super T> p) {
        List<T> out = new ArrayList<>();
        for (T t : items) if (p.test(t)) out.add(t);
        return new ImmutableList<>(out);
    }

    /** Append returns a NEW ImmutableList */
    public ImmutableList<T> append(T value) {
        List<T> out = new ArrayList<>(items);
        out.add(value);
        return new ImmutableList<>(out);
    }

    /** Concat returns a NEW ImmutableList */
    public ImmutableList<T> concat(ImmutableList<T> other) {
        List<T> out = new ArrayList<>(items.size() + other.size());
        out.addAll(this.items);
        out.addAll(other.items);
        return new ImmutableList<>(out);
    }

    /** Returns true if the list contains the value. Uses `equals`. */
    public boolean contains(Object o) {
        return items.contains(o);
    }

    /** Returns the first index of the value or -1. */
    public int indexOf(Object o) {
        return items.indexOf(o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImmutableList)) return false;
        ImmutableList<?> that = (ImmutableList<?>) o;
        return Objects.equals(this.items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(items);
    }

    @Override
    public String toString() {
        return items.toString();
    }
}
