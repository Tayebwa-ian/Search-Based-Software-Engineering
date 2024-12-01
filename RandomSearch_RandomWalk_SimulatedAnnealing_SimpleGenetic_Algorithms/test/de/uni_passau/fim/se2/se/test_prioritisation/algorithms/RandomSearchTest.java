package de.uni_passau.fim.se2.se.test_prioritisation.algorithms;

import org.junit.jupiter.api.Test;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrderGenerator;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.APLC;
import de.uni_passau.fim.se2.se.test_prioritisation.mutations.Mutation;
import de.uni_passau.fim.se2.se.test_prioritisation.mutations.ShiftToBeginningMutation;
import de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions.MaxFitnessEvaluations;
import de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions.StoppingCondition;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

class RandomSearchTest {
    private Random random = new  Random();
    private ShiftToBeginningMutation mutationType = new ShiftToBeginningMutation(random);
    private int [] testCases = {0, 1, 2};
    private boolean[][] coverageMatrix = { {true, false}, {false, true}, {true, true} };
    private TestOrder testOrder = new TestOrder(mutationType, testCases);
    private APLC fitnessFunction = new APLC(coverageMatrix); // Fitness = encoding's value
    private Mutation<TestOrder> mutation = testOrder.getMutation();
    private TestOrderGenerator encodingGenerator = new TestOrderGenerator(random, mutation, testCases.length); // Candidate values
    

    @Test
    public void testFindSolutionReturnsBestSolution() {
        // Test case: Ensure findSolution() returns the best encoding based on fitness

        // Arrange
        MaxFitnessEvaluations stoppingCondition = new MaxFitnessEvaluations(5); // Stop after 5 evaluations

        RandomSearch<TestOrder> randomSearch = new RandomSearch<>(stoppingCondition, encodingGenerator, fitnessFunction);

        // Act
        TestOrder bestSolution = randomSearch.findSolution();

        // Assert
        assertNotNull(bestSolution, "The best solution should not be null");
        assertEquals(3, bestSolution.size(), "The best solution should have the highest fitness (7)");
    }

    @Test
    public void testFindSolutionHandlesSingleEvaluation() {
        // Test case: Ensure findSolution() works with just one evaluation

        // Arrange
        MaxFitnessEvaluations stoppingCondition = new MaxFitnessEvaluations(1); // Stop after 1 evaluation

        RandomSearch<TestOrder> randomSearch = new RandomSearch<>(stoppingCondition, encodingGenerator, fitnessFunction);

        // Act
        TestOrder bestSolution = randomSearch.findSolution();

        // Assert
        assertNotNull(bestSolution, "The best solution should not be null");
        assertEquals(3, bestSolution.size(), "The best solution should match the single candidate value (42)");
    }

    @Test
    public void testFindSolutionReturnsNullWhenNoEvaluationsAllowed() {
        // Test case: Ensure findSolution() returns null when no evaluations are allowed
        MaxFitnessEvaluations stoppingCondition = new MaxFitnessEvaluations(0); // Stop after 1 evaluation

        RandomSearch<TestOrder> randomSearch = new RandomSearch<>(stoppingCondition, encodingGenerator, fitnessFunction);

        // Act
        TestOrder bestSolution = randomSearch.findSolution();

        // Assert
        assertNull(bestSolution, "The best solution should be null when no evaluations are allowed");
    }

    @Test
    public void testGetStoppingCondition() {
        // Test case: Ensure getStoppingCondition() returns the correct stopping condition

        // Arrange
        MaxFitnessEvaluations stoppingCondition = new MaxFitnessEvaluations(2); // Stop after 1 evaluation

        RandomSearch<TestOrder> randomSearch = new RandomSearch<>(stoppingCondition, encodingGenerator, fitnessFunction);

        // Act
        StoppingCondition result = randomSearch.getStoppingCondition();

        // Assert
        assertEquals(stoppingCondition, result, "getStoppingCondition() should return the correct stopping condition");
    }
}
