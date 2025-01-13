package de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Comparator;
import java.util.Random;

import org.junit.jupiter.api.Test;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.Encoding;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.GeneCrossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.BitFlipMutation;

public class TestCaseMinimizationFitnessTest {

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
    private int[] suite3 = {0, 1, 1, 0, 1};
    private int[] suite4 = {1, 1, 1, 1, 1};
    private Encoding c1 = new Encoding(mutation, crossover, suite1, coverageMatrix);  //  Should be the best
    private Encoding c2 = new Encoding(mutation, crossover, suite2, coverageMatrix);
    private Encoding c3 = new Encoding(mutation, crossover, suite3, coverageMatrix);  // have same evaluation values as c2
    private Encoding c4 = new Encoding(mutation, crossover, suite4, coverageMatrix);

    TestCaseMinimizationFitness fitness = new TestCaseMinimizationFitness();

    @Test
    public void testConstructorInvalidInputs () {
        assertThrows(NullPointerException.class, () -> fitness.applyAsDouble(null));
    }

    @Test
    public void testTestSize() {
        assertTrue(fitness.applyAsDouble(c4) == 1.0);
        assertTrue(fitness.applyAsDouble(c2) == fitness.applyAsDouble(c3));
        assertTrue(fitness.applyAsDouble(c1) == (3.0 / 5));
    }

    @Test
    public void testIfMinimizationFunction () {
        assertTrue(fitness.isMinimizing());
        assertFalse(fitness.isMaximizing());
    }

    @Test
    public void testComparator() {
        Comparator<Encoding> comparator = fitness.comparator();
        assertTrue(comparator.compare(c2, c4) > 0);
        assertTrue(comparator.compare(c2, c3) == 0);
        assertFalse(comparator.compare(c4, c1) > 0);
    }

    @Test
    public void testBestFunction () {
        assertTrue(fitness.best().apply(c4, c1) == c4);
    }

    @Test
    public void testBoxed () {
        assertTrue(fitness.boxed().apply(c1) > 0.5);
    }

    @Test
    public void testAndThenAsDouble() {
        MinimizingFitnessFunction<Encoding> scaledFitness = fitness.andThenAsDouble(f -> f * 2);
        assertTrue(2 * fitness.applyAsDouble(c1) == scaledFitness.applyAsDouble(c1));
    }
}
