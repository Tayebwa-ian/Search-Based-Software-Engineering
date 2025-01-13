package de.uni_passau.fim.se2.sbse.suite_minimisation.crossover;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;
import org.junit.jupiter.api.Test;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.Encoding;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.BitFlipMutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Pair;

public class GeneCrossoverTest {

    private Random random = new Random();
    private BitFlipMutation mutation = new BitFlipMutation(random);
    private GeneCrossover crossover = new GeneCrossover(random);
    private boolean[][] coverageMatrix = {
        {true, false, true, false, true},
        {false, false, true, false, false},
        {true, false, true, false, false},
        {true, false, false, true, false},
        {false, true, true, false, false}
    };
    private int[] suite1 = {1, 0, 0, 1, 1};
    private int[] suite2 = {0, 1, 1, 1, 0};
    private Encoding c1 = new Encoding(mutation, crossover, suite1, coverageMatrix);  //  Should be the best
    private Encoding c2 = new Encoding(mutation, crossover, suite2, coverageMatrix);

    @Test
    public void testNullRandom() {
        assertThrows(NullPointerException.class, () -> new GeneCrossover(null));
    }

    @Test
    public void testNullParents() {
        assertThrows(NullPointerException.class, () -> crossover.apply(null, null));
        assertThrows(NullPointerException.class, () -> crossover.apply(c1, null));
        assertThrows(NullPointerException.class, () -> crossover.apply(null, c2));
    }
    
    @Test
    public void testWithTwoParents() {
        assertTrue(crossover.apply(c1, c2) instanceof Pair);
        Pair<Encoding> pair = new Pair<>(c1, c2);
        assertTrue(crossover.apply(pair) instanceof Pair);
    }
}
