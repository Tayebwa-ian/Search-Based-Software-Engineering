package de.uni_passau.fim.se2.sbse.suite_minimisation.algorithms;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.Encoding;
import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.EncodingGenerator;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.GeneCrossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.BitFlipMutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.stopping_conditions.MaxFitnessEvaluations;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Utils;


public class RandomSearchTest {

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
    int testCases = coverageMatrix.length;
    private MaxFitnessEvaluations stoppingCondition = new MaxFitnessEvaluations(100);
    private EncodingGenerator encodingGenerator = new EncodingGenerator(random, mutation, crossover, testCases, coverageMatrix);

    @Test
    public void testInvalidConstructorInputs () {
        assertThrows(IllegalArgumentException.class, () -> new RandomSearch(null, null));
        assertThrows(IllegalArgumentException.class, () -> new RandomSearch(null, stoppingCondition));
        assertThrows(IllegalArgumentException.class, () -> new RandomSearch(encodingGenerator, null));
    }

    @Test
    public void testGetStoppingCondition () {
        MaxFitnessEvaluations condition = new MaxFitnessEvaluations(5);
        RandomSearch algorithm = new RandomSearch(encodingGenerator, condition);
        algorithm.findSolution();
        assertTrue(algorithm.searchMustStop());
        assertTrue(algorithm.getProgress() == 1.0);
    }

    @Test
    public void testMoreStoppingCondition () {
        MaxFitnessEvaluations condition = new MaxFitnessEvaluations(10);
        RandomSearch algorithm = new RandomSearch(encodingGenerator, condition);
        algorithm.notifySearchStarted();
        assertTrue(algorithm.getProgress() == 0.0);
        algorithm.notifyFitnessEvaluation();
        assertTrue(algorithm.getProgress() == 0.1);
        algorithm.notifyFitnessEvaluation(4);
        assertTrue(algorithm.getProgress() == 0.5);
    }

    @Test
    public void testReturnParetoFront () {
        RandomSearch algorithm = new RandomSearch(encodingGenerator, stoppingCondition);
        List<Encoding> front = algorithm.findSolution();
        Encoding c1 = front.get(0);
        Encoding c2 = front.get(1);
        assertFalse(Utils.dominates(c1, c2));  // chromosomes in the same front should not dominate each other
    }
}
