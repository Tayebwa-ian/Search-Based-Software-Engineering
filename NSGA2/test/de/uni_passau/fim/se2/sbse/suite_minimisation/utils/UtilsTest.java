package de.uni_passau.fim.se2.sbse.suite_minimisation.utils;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.Encoding;
import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.EncodingGenerator;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.GeneCrossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions.CoverageMaximizationFitness;
import de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions.TestCaseMinimizationFitness;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.BitFlipMutation;


public class UtilsTest {
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
    private int[] suite1 = {1, 0, 0, 1, 1};
    private int[] suite2 = {0, 1, 1, 1, 0};
    private int[] suite3 = {0, 1, 1, 0, 1};
    private int[] suite4 = {1, 1, 1, 1, 1};
    private Encoding c1 = new Encoding(mutation, crossover, suite1, coverageMatrix);  //  Should be the best
    private Encoding c2 = new Encoding(mutation, crossover, suite2, coverageMatrix);
    private Encoding c3 = new Encoding(mutation, crossover, suite3, coverageMatrix);  // have same evaluation values as c2
    private Encoding c4 = new Encoding(mutation, crossover, suite4, coverageMatrix);

    CoverageMaximizationFitness maxfunc = new CoverageMaximizationFitness(coverageMatrix);
    TestCaseMinimizationFitness minFunc = new TestCaseMinimizationFitness();

    private List<Encoding> generatePopulation(int size) {
        List<Encoding> population = new ArrayList<>();
        EncodingGenerator generator = new EncodingGenerator(
            random,
            mutation,
            crossover,
            testSuiteSize,
            coverageMatrix
        );
        for (int i = 0; i < size; i++) {
            population.add(generator.get());
        }
        return population;
    }

    @Test
    public void testIsValidFunction () {
        assertTrue(Utils.isValid(suite1));
        assertTrue(Utils.isValid(suite4));
        assertFalse(Utils.isValid(new int [] {2, 0, 1, 0, 0}));
        assertFalse(Utils.isValid(new int [] {0, 0, 0, 0, 0}));
        assertFalse(Utils.isValid(new int [] {0, 0, 1, 0, -2}));
        assertTrue(Utils.isValid(new int [] {0, 0, 0, 0, 1}));
    }

    @Test
    public void testDominatesFunction() {
        assertTrue(Utils.dominates(c1, c2));
        assertFalse(Utils.dominates(c3, c1));
        assertFalse(Utils.dominates(c1, c1));
        assertFalse(Utils.dominates(c2, c3));
        assertTrue(Utils.dominates(c1, c4));
    }

    @Test
    public void testNonDominatedSorting() {
        List<Encoding> population = generatePopulation(10);
        List<List<Encoding>> fronts = Utils.nonDominatedSorting(population);
        List<Encoding> front = fronts.get(0);  // First front
        assertTrue(front.get(0) instanceof Encoding);
    }

    @Test
    public void testComputeHyperVolume() {
        List<Encoding> population = generatePopulation(200);
        List<List<Encoding>> fronts = Utils.nonDominatedSorting(population);
        List<Encoding> front = fronts.get(0);  // Get first front
        double hyperVolume = Utils.computeHyperVolume(front, maxfunc, minFunc, 0, 1);
        assertTrue(hyperVolume >= 0);
        assertTrue(hyperVolume <= 1);
        List<Encoding> other = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () -> Utils.computeHyperVolume(other, maxfunc, minFunc, 0, 1));
        assertThrows(IllegalArgumentException.class, () -> Utils.computeHyperVolume(null, maxfunc, minFunc, 0, 1));
    }

    @Test
    public void testMatrixGenerationFromFile() {
        File file = new File("coverage_matrices/AddNumbers.txt");  // the file is at the root directory
        try {
            boolean[][] parsedMatrix = Utils.parseCoverageMatrix(file);
            assertTrue(parsedMatrix[0] instanceof boolean[]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
