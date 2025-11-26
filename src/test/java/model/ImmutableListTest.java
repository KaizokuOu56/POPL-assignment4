package model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ImmutableListTest {

    @Test
    public void basics_and_equality() {
        ImmutableList<String> l = new ImmutableList<>(List.of("a", "b", "c"));
        assertEquals(3, l.size());
        assertTrue(l.contains("b"));
        assertEquals(1, l.indexOf("b"));

        ImmutableList<String> other = new ImmutableList<>(List.of("a", "b", "c"));
        assertEquals(l, other);
        assertEquals(l.hashCode(), other.hashCode());
        assertEquals("[a, b, c]", l.toString());

        // iterator / enhanced for
        StringBuilder sb = new StringBuilder();
        for (String s : l) sb.append(s);
        assertEquals("abc", sb.toString());
    }

    @Test
    public void map_filter_append_concat() {
        ImmutableList<Integer> nums = new ImmutableList<>(List.of(1,2,3));
        ImmutableList<Integer> mapped = nums.map(x -> x * 2);
        assertEquals(3, mapped.size());
        assertEquals(2, mapped.get(0));

        ImmutableList<Integer> filtered = mapped.filter(x -> x > 2);
        assertEquals(2, filtered.size());

        ImmutableList<Integer> appended = filtered.append(100);
        assertEquals(3, appended.size());
        assertEquals(100, appended.get(2));

        ImmutableList<Integer> concat = nums.concat(new ImmutableList<>(List.of(4,5)));
        assertEquals(5, concat.size());
    }
}
