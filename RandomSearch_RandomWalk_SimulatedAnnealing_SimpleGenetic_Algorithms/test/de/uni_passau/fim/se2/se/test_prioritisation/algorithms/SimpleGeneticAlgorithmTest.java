package de.uni_passau.fim.se2.se.test_prioritisation.algorithms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import de.uni_passau.fim.se2.se.test_prioritisation.crossover.Crossover;
import de.uni_passau.fim.se2.se.test_prioritisation.crossover.OrderCrossover;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.EncodingGenerator;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrderGenerator;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.APLC;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.se.test_prioritisation.mutations.Mutation;
import de.uni_passau.fim.se2.se.test_prioritisation.mutations.ShiftToBeginningMutation;
import de.uni_passau.fim.se2.se.test_prioritisation.parent_selection.ParentSelection;
import de.uni_passau.fim.se2.se.test_prioritisation.parent_selection.TournamentSelection;
import de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions.MaxFitnessEvaluations;
import de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions.StoppingCondition;

public class SimpleGeneticAlgorithmTest {

    private boolean[][] coverageMatrix = {
            { true, false, true, false, false },
            { false, false, true, false, false },
            { true, true, false, false, false },
            { true, false, false, true, false },
            { false, true, false, false, true },
    };

    private Random random = new Random();
    private EncodingGenerator<TestOrder> generator = getGenerator();
    private FitnessFunction<TestOrder> fitnessFunction = new APLC(coverageMatrix);
    private APLC aplc = new APLC(coverageMatrix);
    private Crossover<TestOrder> crossover = new OrderCrossover(random);
    private ParentSelection<TestOrder> parentSelection = new TournamentSelection(aplc, random);

    private EncodingGenerator<TestOrder> getGenerator() {
        int[] testCases = { 0, 1, 2, 3, 4 };
        int numTestCases = 5;
        ShiftToBeginningMutation mutationType = new ShiftToBeginningMutation(random);
        TestOrder testOrder = new TestOrder(mutationType, testCases);
        Mutation<TestOrder> mutation = testOrder.getMutation();
        EncodingGenerator<TestOrder> generator = new TestOrderGenerator(random, mutation, numTestCases);
        return generator;
    }

    @Test
    public void testInitializationAndSolutionFinding() {
        int maxEvaluations = 10;
        StoppingCondition stoppingCondition = new MaxFitnessEvaluations(maxEvaluations);

        SimpleGeneticAlgorithm<TestOrder> algorithm = new SimpleGeneticAlgorithm<>(
                stoppingCondition, generator, fitnessFunction, crossover, parentSelection, random);

        TestOrder bestSolution = algorithm.findSolution();

        assertNotNull(bestSolution, "The best solution should not be null.");
        assertTrue(fitnessFunction.applyAsDouble(bestSolution) > 0, "The fitness value of the best solution should be non-negative.");
    }

    @Test
    public void testPopulationInitialization() {
        int populationSize = 202;

        List<TestOrder> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            population.add(generator.get());
        }

        assertEquals(populationSize, population.size(), "The population size should match the expected value.");
    }

    @Test
    public void testStoppingCondition() {
        int maxEvaluations = 5;
        StoppingCondition stoppingCondition = new MaxFitnessEvaluations(maxEvaluations);

        SimpleGeneticAlgorithm<TestOrder> algorithm = new SimpleGeneticAlgorithm<>(
                stoppingCondition, generator, fitnessFunction, crossover, parentSelection, random);

        algorithm.findSolution();

        assertTrue(algorithm.getStoppingCondition().searchMustStop(), "The search must stop after the maximum number of evaluations.");
    }

}
