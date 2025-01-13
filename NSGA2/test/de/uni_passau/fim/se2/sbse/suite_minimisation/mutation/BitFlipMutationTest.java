package de.uni_passau.fim.se2.sbse.suite_minimisation.mutation;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.Encoding;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.GeneCrossover;

import java.util.Random;

public class BitFlipMutationTest {

    private Random random = new Random(42);
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
    private Encoding c1 = new Encoding(mutation, crossover, suite1, coverageMatrix);  // Should be the best
    private Encoding c2 = new Encoding(mutation, crossover, suite2, coverageMatrix);

    @Test
    public void testNullRandom() {
        assertThrows(NullPointerException.class, () -> new BitFlipMutation(null));
    }

    @Test
    public void testMutationRate() {

        Encoding mutated = mutation.apply(c1);
        assertArrayEquals(suite1, mutated.getGenes());
        Encoding another = mutation.apply(c2);
        assertArrayEquals(suite2, another.getGenes());
    }

    @Test
    public void testChromosomeCopying() {

        Encoding original = c1.copy();
        mutation.apply(c1);

        // Ensure the original chromosome remains unchanged
        assertArrayEquals(original.getGenes(), c1.getGenes());
    }
    
}
