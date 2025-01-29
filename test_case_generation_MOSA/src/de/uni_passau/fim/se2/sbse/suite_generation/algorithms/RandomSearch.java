package de.uni_passau.fim.se2.sbse.suite_generation.algorithms;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestCase;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestCaseGenerator;
import de.uni_passau.fim.se2.sbse.suite_generation.fitness_functions.BranchCoverageFitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_generation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.Branch;
import de.uni_passau.fim.se2.sbse.suite_generation.stopping_conditions.StoppingCondition;
import de.uni_passau.fim.se2.sbse.suite_generation.utils.Utils;

public class RandomSearch implements GeneticAlgorithm<TestCase> {

    private final Map<Branch, FitnessFunction<TestCase>> fitnessFunctions;
    private final StoppingCondition stoppingCondition;
    private final TestCaseGenerator generator;
    private final List<Branch> targetBranches;
    private final int populationSize;
    private final List<TestCase> archive; // Archive to maintain the best solutions

    /**
     * 
     * @param stoppingCondition
     * @param generator
     */
    public RandomSearch(
        StoppingCondition stoppingCondition,
        TestCaseGenerator generator,
        List<Branch> targetBranches,
        int populationSize
    ){
        this.generator = requireNonNull(generator);
        this.stoppingCondition = requireNonNull(stoppingCondition);
        this.targetBranches = requireNonNull(targetBranches);
        this.populationSize = requireNonNull(populationSize);
        this.archive = new ArrayList<>(); // Initialize archive

        // Initialize fitness functions for each target branch
        this.fitnessFunctions = targetBranches.stream()
            .collect(Collectors.toMap(branch -> branch, BranchCoverageFitnessFunction::new));
    }
    
    /**
     * {@inheritDoc}
     */
    public List<TestCase> findSolution(){

        archive.clear();
        stoppingCondition.notifySearchStarted();
        while (!stoppingCondition.searchMustStop()) {
            List<TestCase> population = Utils.initializePopulation(populationSize, generator);

            // 1. Evaluate fitness for all target branches
            Map<TestCase, Map<Branch, Double>> fitnessMap = Utils.evaluateFitness(
                population,
                targetBranches,
                fitnessFunctions
            );

            // 2. Update archive with the best solutions from the current generation
            Utils.updateArchive(population, fitnessMap, archive, targetBranches);
            // 3. Notify the fitness Function
            stoppingCondition.notifyFitnessEvaluation();
        }

        return archive;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StoppingCondition getStoppingCondition() {
        return stoppingCondition;
    }
}
