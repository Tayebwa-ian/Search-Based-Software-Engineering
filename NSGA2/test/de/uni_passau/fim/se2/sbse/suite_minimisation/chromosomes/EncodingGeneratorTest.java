package de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;

import org.junit.jupiter.api.Test;

import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.GeneCrossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.BitFlipMutation;

public class EncodingGeneratorTest {
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
    private int testSuiteSize = coverageMatrix.length;

    EncodingGenerator generator = new EncodingGenerator(random, mutation, crossover, testSuiteSize, coverageMatrix);

    @Test
    public void testNullInputValues() {
        assertThrows(IllegalArgumentException.class, () -> new EncodingGenerator(null, mutation, crossover, testSuiteSize, coverageMatrix));
        assertThrows(IllegalArgumentException.class, () -> new EncodingGenerator(random, null, crossover, testSuiteSize, coverageMatrix));
        assertThrows(IllegalArgumentException.class, () -> new EncodingGenerator(null, null, null, 0, null));
        assertThrows(IllegalArgumentException.class, () -> new EncodingGenerator(random, mutation, null, testSuiteSize, coverageMatrix));
        assertThrows(IllegalArgumentException.class, () -> new EncodingGenerator(random, mutation, crossover, 0, coverageMatrix));
        assertThrows(IllegalArgumentException.class, () -> new EncodingGenerator(random, mutation, crossover, testSuiteSize, null));
    }

    @Test
    public void testWithCorrectInupts() {
        assertTrue(generator.get() instanceof Encoding);
    }
}
