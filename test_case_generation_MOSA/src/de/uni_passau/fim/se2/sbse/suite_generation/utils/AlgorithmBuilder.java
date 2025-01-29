package de.uni_passau.fim.se2.sbse.suite_generation.utils;

import de.uni_passau.fim.se2.sbse.suite_generation.algorithms.GeneticAlgorithm;
import de.uni_passau.fim.se2.sbse.suite_generation.algorithms.MOSA;
import de.uni_passau.fim.se2.sbse.suite_generation.algorithms.RandomSearch;
import de.uni_passau.fim.se2.sbse.suite_generation.algorithms.SearchAlgorithmType;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestCaseGenerator;
import de.uni_passau.fim.se2.sbse.suite_generation.crossover.OnePointCrossover;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.Branch;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.IBranch;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.IBranchTracer;
import de.uni_passau.fim.se2.sbse.suite_generation.mutation.TestCaseMutation;
import de.uni_passau.fim.se2.sbse.suite_generation.stopping_conditions.StoppingCondition;

import java.util.Random;
import java.util.Set;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

public class AlgorithmBuilder {
    /**
     * The default source randomness.
     */
    private final Random random;

    /**
     * The stopping condition to use.
     */
    private final StoppingCondition stoppingCondition;

    /**
     * The population size of the genetic algorithm.
     */
    private final int populationSize;

    /**
     * The reflected class for which we want to generate tests.
     */
    private final Class<?> testGenerationTarget;

    /**
     * Reference to the branch tracer that tells which branches have been taken by a test execution.
     */
    private final IBranchTracer branchTracer;

    /**
     * The set of branches that should be covered by the generated test suite.
     */
    private final Set<IBranch> branchesToCover;


    public AlgorithmBuilder(final Random random,
                            final StoppingCondition stoppingCondition,
                            final int populationSize,
                            final String classUnderTest,
                            final String packageUnderTest,
                            final IBranchTracer branchTracer)
            throws IllegalArgumentException {
        this.random = requireNonNull(random);
        this.stoppingCondition = requireNonNull(stoppingCondition);
        this.populationSize = populationSize;

        if (classUnderTest == null || classUnderTest.isBlank()) {
            throw new IllegalArgumentException("No CUT specified");
        }

        if (packageUnderTest == null || packageUnderTest.isBlank()) {
            throw new IllegalArgumentException("No PUT specified");
        }

        // On Windows it might be necessary to explicitly load the (instrumented) class under test.
        final String classToLoad = packageUnderTest + "." + classUnderTest;
        try {
            this.testGenerationTarget = Class.forName(classToLoad);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Unable to load class: " + classToLoad, e);
        }

        this.branchTracer = requireNonNull(branchTracer);

        // Important: retrieve the set of branches AFTER the class has been loaded. (Otherwise it
        // would be empty.)
        this.branchesToCover = this.branchTracer.getBranches();
    }

    /**
     * Builds the specified search algorithm using the fields of this class.
     *
     * @param algorithm the algorithm to build
     * @return the algorithm
     */
    public GeneticAlgorithm<?> build(final SearchAlgorithmType algorithm) {
        return switch (algorithm) {
            case RANDOM_SEARCH -> buildRandomSearch();
            case MOSA -> buildMOSA();
        };
    }

    /**
     * Returns an instance of the MOSA search algorithm to generate tests for the target class.
     * The algorithm is constructed using the fields of this class.
     *
     * @return the search algorithm
     */
    private GeneticAlgorithm<?> buildMOSA() {
        OnePointCrossover crossover = new OnePointCrossover(random);
        TestCaseMutation mutation = new TestCaseMutation(
            random,
            Utils.allStatements(testGenerationTarget)
        );
        TestCaseGenerator generator = new TestCaseGenerator(
            random,
            mutation,
            crossover,
            testGenerationTarget
        );
        List<Branch> targetBranches = new ArrayList<>();
        for (IBranch branch : branchesToCover) {
            if (branch instanceof Branch) {
                targetBranches.add((Branch) branch);
            } else {
                throw new UnsupportedOperationException(
                    "Operation not supported for this IBranch type: " +
                    branch.getClass().getName()
                );
            }
        }

        MOSA algorithm = new MOSA(
            populationSize,
            random,
            generator,
            targetBranches,
            stoppingCondition
        );

        return algorithm;
    }

    /**
     * Returns an instance of the Random Search algorithm to generate tests for the target class.
     * The algorithm is constructed using the fields of this class.
     * <p>
     * Instead of sampling a number of test suites at random and simply returning the best one, we
     * consider all sampled test cases, and reuse the MOSA archive to find the shortest covering
     * ones. These test cases are then returned as the overall result of Random Search.
     *
     * @return the search algorithm
     */
    private GeneticAlgorithm<?> buildRandomSearch() {
        OnePointCrossover crossover = new OnePointCrossover(random);
        TestCaseMutation mutation = new TestCaseMutation(
            random,
            Utils.allStatements(testGenerationTarget)
        );
        TestCaseGenerator generator = new TestCaseGenerator(
            random,
            mutation,
            crossover,
            testGenerationTarget
        );
        List<Branch> targetBranches = new ArrayList<>();
        for (IBranch branch : branchesToCover) {
            if (branch instanceof Branch) {
                targetBranches.add((Branch) branch);
            } else {
                throw new UnsupportedOperationException(
                    "Operation not supported for this IBranch type: " +
                    branch.getClass().getName()
                );
            }
        }
        RandomSearch alogrithm = new RandomSearch(
            stoppingCondition,
            generator,
            targetBranches,
            populationSize
        );
        return alogrithm;
    }
}
