package algo;

import model.ImmutableList;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

public class SearcherTest {

    @Test
    public void linear_and_binary_search() {
        ImmutableList<String> in = new ImmutableList<>(List.of("a","b","c","d"));
        assertEquals(2, Searcher.linearSearch(in, "c"));

        ImmutableList<String> sorted = new ImmutableList<>(List.of("a","b","c","d"));
        int idx = Searcher.binarySearch(sorted, "c", Comparator.naturalOrder());
        assertEquals(2, idx);

        assertEquals(-1, Searcher.linearSearch(in, "z"));
    }
}
