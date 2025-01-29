package de.uni_passau.fim.se2.sbse.suite_generation.algorithms;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestCase;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestCaseGenerator;
import de.uni_passau.fim.se2.sbse.suite_generation.fitness_functions.BranchCoverageFitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_generation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.Branch;
import de.uni_passau.fim.se2.sbse.suite_generation.selection.RankSelection;
import de.uni_passau.fim.se2.sbse.suite_generation.stopping_conditions.StoppingCondition;
import de.uni_passau.fim.se2.sbse.suite_generation.utils.Pair;
import de.uni_passau.fim.se2.sbse.suite_generation.utils.Utils;

public class MOSA implements GeneticAlgorithm<TestCase> {
    private final int populationSize;
    private final Random random;
    private final TestCaseGenerator generator;
    private final List<Branch> targetBranches;
    private final Map<Branch, FitnessFunction<TestCase>> fitnessFunctions;
    private final StoppingCondition stoppingCondition;
    private final List<TestCase> archive; // Archive to maintain the best solutions

    public MOSA(
        int populationSize,
        Random random,
        TestCaseGenerator generator,
        List<Branch> targetBranches,
        StoppingCondition stoppingCondition
        ) {
        this.populationSize = requireNonNull(populationSize);
        this.random = requireNonNull(random);
        this.generator = requireNonNull(generator);
        this.targetBranches = requireNonNull(targetBranches);
        this.stoppingCondition = requireNonNull(stoppingCondition);
        this.archive = new ArrayList<>(); // Initialize archive

        // Initialize fitness functions for each target branch
        this.fitnessFunctions = targetBranches.stream()
            .collect(Collectors.toMap(branch -> branch, BranchCoverageFitnessFunction::new));
    }

    /**
     * {@inheritDoc}
     */
    public List<TestCase> findSolution() {
        archive.clear();
        stoppingCondition.notifySearchStarted();

        while (!stoppingCondition.searchMustStop()) {

            List<TestCase> population = Utils.initializePopulation(populationSize, generator);

            // Evaluate fitness for all target branches
            Map<TestCase, Map<Branch, Double>> fitnessMap = Utils.evaluateFitness(
                population,
                targetBranches,
                fitnessFunctions
            );

            // Update archive with the best solutions from the current generation
            Utils.updateArchive(population, fitnessMap, archive, targetBranches);

            // Generate offspring population
            List<TestCase> offSpringPopulation = generateOffspring(population, fitnessMap);
            List<TestCase> combinedPopulation = new ArrayList<>(population);
            combinedPopulation.addAll(offSpringPopulation);

            Map<TestCase, Map<Branch, Double>> fitnessMap2 = Utils.evaluateFitness(
                combinedPopulation,
                targetBranches,
                fitnessFunctions
            );

            // Sort by Pareto dominance
            List<List<TestCase>> fronts = Utils.nonDominatedSorting(
                combinedPopulation,
                fitnessMap2,
                targetBranches
            );

            // Estimate density using subvector dominance
            for (List<TestCase> front : fronts) {
                calculateSubvectorDensity(front, fitnessMap2);
            }

            // Notify the stoppingCodition
            stoppingCondition.notifyFitnessEvaluation();
        }

        return new ArrayList<>(archive);
    }

    public void calculateSubvectorDensity(
        List<TestCase> front,
        Map<TestCase, Map<Branch, Double>> fitnessMap
    ) {
        int objectiveCount = targetBranches.size();
        double[][] subvectorDistances = new double[front.size()][objectiveCount];

        for (int obj = 0; obj < targetBranches.size(); obj++) {
            Branch branch = targetBranches.get(obj);
            front.sort(Comparator.comparingDouble(tc -> fitnessMap.get(tc).get(branch)));

            for (int i = 0; i < front.size(); i++) {
                double lower = (i == 0) ? Double.POSITIVE_INFINITY :
                        fitnessMap.get(front.get(i)).get(branch) - fitnessMap.get(front.get(i - 1)).get(branch);
                double upper = (i == front.size() - 1) ? Double.POSITIVE_INFINITY :
                        fitnessMap.get(front.get(i + 1)).get(branch) - fitnessMap.get(front.get(i)).get(branch);

                subvectorDistances[i][obj] = lower + upper;
            }
        }

        for (int i = 0; i < front.size(); i++) {
            double density = Arrays.stream(subvectorDistances[i]).sum();
            front.get(i).setDensity(density);
        }
    }

    /**
     * Generates offspring from the current population.
     * @param population the current population
     * @return the offspring population
     */
    private List<TestCase> generateOffspring (
        List<TestCase> population, Map<TestCase,
        Map<Branch, Double>> fitnessMap
    ) {
        List<TestCase> offspringPopulation = new ArrayList<>();

        Comparator<TestCase> comparator = new Comparator<TestCase>() {
            @Override
            public int compare(TestCase p1, TestCase p2) {
                if (Utils.dominates(p1, p2, fitnessMap, targetBranches)) return 1;
                return -1;
            }
        };

        RankSelection<TestCase> selection = new RankSelection<>(
            comparator,
            population.size(),
            1.9,
            random
        );
        TestCase offspring1;
        TestCase offspring2;
        while (offspringPopulation.size() < population.size()){
            TestCase parent1 = selection.apply(population);
            TestCase parent2 = selection.apply(population);
            if (random.nextDouble() < 0.8) {
                Pair<TestCase> pair = parent1.crossover(parent2);
                offspring1 = pair.getFst();
                offspring2 = pair.getSnd();
            } else {
                offspring1 = parent1;
                offspring2 = parent2;
            }
            offspring1 = offspring1.mutate();
            offspring2 = offspring2.mutate();

            offspringPopulation.add(offspring1);
            offspringPopulation.add(offspring2);
        }
        return offspringPopulation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StoppingCondition getStoppingCondition() {
        return stoppingCondition;
    }
}
