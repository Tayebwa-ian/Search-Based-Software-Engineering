package de.uni_passau.fim.se2.se.test_prioritisation.algorithms;
import org.junit.jupiter.api.Test;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.EncodingGenerator;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrderGenerator;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.APLC;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.se.test_prioritisation.mutations.Mutation;
import de.uni_passau.fim.se2.se.test_prioritisation.mutations.ShiftToBeginningMutation;
import de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions.MaxFitnessEvaluations;
import de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions.StoppingCondition;

public class SimulatedAnnealingTest {

    private boolean[][] coverageMatrix = {
            { true, false, true, false, false },
            { false, false, true, false, false },
            { true, true, false, false, false },
            { true, false, false, true, false },
            { false, true, false, false, true },
    };


    private EncodingGenerator<TestOrder> getGenerator() {
        int[] testCases = { 0, 1, 2, 3, 4 };
        int numTestCases = 5;
        ShiftToBeginningMutation mutationType = new ShiftToBeginningMutation(random);
        TestOrder testOrder = new TestOrder(mutationType, testCases);
        Mutation<TestOrder> mutation = testOrder.getMutation();
        EncodingGenerator<TestOrder> generator = new TestOrderGenerator(random, mutation, numTestCases);
        return generator;
    }

    private Random random = new Random();
    private EncodingGenerator<TestOrder> generator = getGenerator();
    private FitnessFunction<TestOrder> energy = new APLC(coverageMatrix);

    @Test
    public void testFindSolution() {
        int maxEvaluations = 50;
        int degreesOfFreedom = 5;

        StoppingCondition stoppingCondition = new MaxFitnessEvaluations(maxEvaluations);

        SimulatedAnnealing<TestOrder> simulatedAnnealing = new SimulatedAnnealing<>(
                stoppingCondition,
                generator,
                energy,
                degreesOfFreedom,
                random
        );

        TestOrder solution = simulatedAnnealing.findSolution();

        assertNotNull(solution, "The solution should not be null.");
        assertTrue(energy.applyAsDouble(solution) > 0, "The solution's value should be non-negative.");
    }

    @Test
    public void testAverageEnergyVariation() {
        int numSteps = 1000;

        StoppingCondition stoppingCondition = new MaxFitnessEvaluations(50);

        SimulatedAnnealing<TestOrder> simulatedAnnealing = new SimulatedAnnealing<>(
                stoppingCondition,
                generator,
                energy,
                5,
                random
        );

        double averageDeltaE = simulatedAnnealing.averageEnergyVariation(numSteps);

        assertTrue(averageDeltaE >= 0, "The average energy variation should be non-negative.");
    }

    @Test
    public void testCoolingRateAndAcceptance() {
        int maxEvaluations = 20;
        int degreesOfFreedom = 5;

        StoppingCondition stoppingCondition = new MaxFitnessEvaluations(maxEvaluations);

        SimulatedAnnealing<TestOrder> simulatedAnnealing = new SimulatedAnnealing<>(
                stoppingCondition,
                generator,
                energy,
                degreesOfFreedom,
                random
        );

        TestOrder solution = simulatedAnnealing.findSolution();

        assertNotNull(solution, "The solution should not be null.");
        assertTrue(energy.applyAsDouble(solution) > 0, "The final solution should reflect the acceptance of neighbor solutions.");
    }

    @Test
    public void testStoppingCondition() {
        int degreesOfFreedom = 5;
        int maxEvaluations = 20;
        StoppingCondition stoppingCondition = new MaxFitnessEvaluations(maxEvaluations);

        SimulatedAnnealing<TestOrder> simulatedAnnealing = new SimulatedAnnealing<>(
                stoppingCondition,
                generator,
                energy,
                degreesOfFreedom,
                random
        );

        simulatedAnnealing.findSolution();

        assertTrue(simulatedAnnealing.getStoppingCondition().searchMustStop(), "The search must stop after the maximum number of evaluations.");
    }
}
