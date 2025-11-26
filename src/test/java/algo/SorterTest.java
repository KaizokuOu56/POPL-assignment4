package algo;

import model.ImmutableList;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

public class SorterTest {

    @Test
    public void mergeSort_sortsStrings() {
        ImmutableList<String> in = new ImmutableList<>(List.of("z","a","m"));
        ImmutableList<String> sorted = Sorter.mergeSort(in, Comparator.naturalOrder());
        assertEquals(List.of("a","m","z"), sorted.asList());
    }

    @Test
    public void mergeSort_sortsInts() {
        ImmutableList<Integer> in = new ImmutableList<>(List.of(3,1,2));
        ImmutableList<Integer> sorted = Sorter.mergeSort(in, Comparator.naturalOrder());
        assertEquals(List.of(1,2,3), sorted.asList());
    }
}
