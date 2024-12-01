package de.uni_passau.fim.se2.se.test_prioritisation.algorithms;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.Test;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.EncodingGenerator;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrderGenerator;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.APLC;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.se.test_prioritisation.mutations.Mutation;
import de.uni_passau.fim.se2.se.test_prioritisation.mutations.ShiftToBeginningMutation;
import de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions.MaxFitnessEvaluations;
import de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions.StoppingCondition;

public class RandomWalkTest {

    private boolean[][] coverageMatrix = {
        {true, false, true, false, false},
        {false, false, true, false, false},
        {true, true, false, false, false},
        {true, false, false, true, false},
        {false, true, false, false, true},
    };

    private EncodingGenerator<TestOrder> getGenerator() {
        int[] testCases = {0, 1, 2, 3, 4};
        int numTestCases = 5;
        Random random = new Random();
        ShiftToBeginningMutation mutationType = new ShiftToBeginningMutation(random);
        TestOrder testOrder = new TestOrder(mutationType, testCases);
        Mutation<TestOrder> mutation = testOrder.getMutation();
        EncodingGenerator<TestOrder> generator = new TestOrderGenerator(random, mutation, numTestCases);
        return generator;
    }

    @Test
    public void testFindSolutionWithLimitedEvaluations() {
        int maxEvaluations = 10;
        StoppingCondition stoppingCondition = new MaxFitnessEvaluations(maxEvaluations);
        EncodingGenerator<TestOrder> generator = getGenerator();
        FitnessFunction<TestOrder> fitnessFunction = new APLC(coverageMatrix);

        RandomWalk<TestOrder> randomWalk = new RandomWalk<>(stoppingCondition, generator, fitnessFunction);

        TestOrder solution = randomWalk.findSolution();
        assertNotNull(solution, "The solution should not be null.");
        assertTrue(fitnessFunction.applyAsDouble(solution) > 0, "The solution's fitness value should be greater than zero.");
        assertEquals(1.0, randomWalk.getStoppingCondition().getProgress(), "The stopping condition progress should reach 1.0.");
    }

    @Test
    public void testGivenNullArguments() {
        int maxEvaluations = 5;
        StoppingCondition stoppingCondition = new MaxFitnessEvaluations(maxEvaluations);
        EncodingGenerator<TestOrder> generator = getGenerator();
        FitnessFunction<TestOrder> fitnessFunction = new APLC(coverageMatrix);

        assertThrows(IllegalArgumentException.class, () -> new RandomWalk<TestOrder>(null, null, null));
        assertThrows(IllegalArgumentException.class, () -> new RandomWalk<TestOrder>(stoppingCondition, null, null));
        assertThrows(IllegalArgumentException.class, () -> new RandomWalk<TestOrder>(null, generator, null));
        assertThrows(IllegalArgumentException.class, () -> new RandomWalk<TestOrder>(null, null, fitnessFunction));
    }    
}
