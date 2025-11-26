package algo;

import model.ImmutableList;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;

import static org.junit.jupiter.api.Assertions.*;

public class AggregationsTest {

    @Test
    public void frequency_and_average_and_inversions() {
        ImmutableList<String> strings = new ImmutableList<>(List.of("x","y","x","z"));
        Map<String, Long> freq = Aggregations.frequencyCount(strings);
        assertEquals(2L, freq.get("x"));
        assertEquals(1L, freq.get("y"));

        ImmutableList<Number> nums = new ImmutableList<>(List.of(1,2,3,4));
        OptionalDouble avg = Aggregations.average(nums);
        assertTrue(avg.isPresent());
        assertEquals(2.5, avg.getAsDouble());

        ImmutableList<Integer> inv = new ImmutableList<>(List.of(2,1,3));
        long c = Aggregations.countInversions(inv);
        assertEquals(1L, c);
    }
}
