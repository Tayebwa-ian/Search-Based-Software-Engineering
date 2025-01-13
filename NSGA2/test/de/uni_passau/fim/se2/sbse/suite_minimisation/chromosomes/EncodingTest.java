package de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;

import org.junit.jupiter.api.Test;

import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.GeneCrossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions.CoverageMaximizationFitness;
import de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions.TestCaseMinimizationFitness;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.BitFlipMutation;

public class EncodingTest {

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
    private int[] genes = {1, 0, 0, 1, 1};
    private int[] genes2 = {0, 0, 1, 1, 1};
    CoverageMaximizationFitness maxfunc = new CoverageMaximizationFitness(coverageMatrix);
    TestCaseMinimizationFitness minFunc = new TestCaseMinimizationFitness();

    Encoding encoding = new Encoding(mutation, crossover, genes, coverageMatrix);
    double coverageFitness = encoding.getCoverageMaxFitness();
    double sizeFitness = encoding.getTestCaseMinFitness();

    @Test
    public void testConstructor_NullMutation() {
        assertThrows(NullPointerException.class, () -> new Encoding(null, crossover, genes, coverageMatrix));
    }

    @Test
    public void testConstructor_NullCoverageMatrix() {
        assertThrows(IllegalArgumentException.class, () -> new Encoding(mutation, crossover, genes, null));
    }

    @Test
    public void testConstructor_ValidInput() {
        assertEquals(genes, encoding.getGenes());
        assertEquals(coverageMatrix, encoding.getCoverageMatrix());
        assertEquals(genes.length, encoding.chromosomeSize());
        assertTrue(coverageFitness == maxfunc.applyAsDouble(encoding));
        assertTrue(sizeFitness == minFunc.applyAsDouble(encoding));
        assertNotEquals(encoding.getGenes(), encoding.copy());
    }

    @Test
    public void testSelfFunction() {
        assertEquals(encoding, encoding.self());
    }

    @Test
    public void testEqualFunction() {

        Encoding copy = encoding.copy();
        assertTrue(encoding.equals(copy));
        Encoding other = new Encoding(mutation, crossover, genes, coverageMatrix);
        assertTrue(encoding.equals(other));
        assertFalse(encoding.equals(null));
        Encoding other2 = new Encoding(mutation, crossover, genes2, coverageMatrix);
        assertFalse(encoding.equals(other2));
    }

    @Test
    public void testHashCodeFunction() {
        Encoding other = new Encoding(mutation, crossover, genes, coverageMatrix);
        assertTrue(encoding.hashCode() == other.hashCode());
        Encoding other2 = new Encoding(mutation, crossover, genes2, coverageMatrix);
        assertTrue(encoding.hashCode() != other2.hashCode());
    }

    @Test
    public void testGetterAndSetterFunctions () {
        encoding.setDistance(0.3);
        assertTrue(encoding.getDistance() == 0.3);
    }

    @Test
    public void testCreationFromExistingEncoding() {
        Encoding c = new Encoding(encoding);
        assertTrue(c != encoding);  //  check if they are different objects
        assertTrue(c instanceof Encoding);
        assertTrue(c.getCoverageMaxFitness() == encoding.getCoverageMaxFitness());
    }
}
