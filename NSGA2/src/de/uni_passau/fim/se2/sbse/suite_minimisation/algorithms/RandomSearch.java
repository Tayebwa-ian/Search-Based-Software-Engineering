package de.uni_passau.fim.se2.sbse.suite_minimisation.algorithms;

import java.util.ArrayList;
import java.util.List;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.Encoding;
import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.EncodingGenerator;
import de.uni_passau.fim.se2.sbse.suite_minimisation.stopping_conditions.StoppingCondition;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Utils;

public class RandomSearch implements GeneticAlgorithm<Encoding> {

    private final StoppingCondition stoppingCondition;
    private final EncodingGenerator encodingGenerator;

    /**
     * Random Search Implemented
     * such that it returns the Pareto front of the set of non-dominated solutions across all sampled individuals.
     */
    public RandomSearch(
        final EncodingGenerator encodingGenerator,
        final StoppingCondition stoppingCondition
    ) {
        if (
            encodingGenerator == null ||
            stoppingCondition == null
        ) throw new IllegalArgumentException("Invalid Random search instantiation input/s");
        this.encodingGenerator = encodingGenerator;
        this.stoppingCondition = stoppingCondition;
    }

    /**
     * {@inheritDoc}
     */
    public List<Encoding> findSolution() {
        List<Encoding> solutions = new ArrayList<>();
        
        stoppingCondition.notifySearchStarted();
        while (!stoppingCondition.searchMustStop()) {
            for (int i = 0; i < 3; i++) {
                Encoding candidateSolution = encodingGenerator.get();
                solutions.add(candidateSolution);
            }

            stoppingCondition.notifyFitnessEvaluation();
        }
        List<Encoding> paretoFront = findParetoFront(solutions);
        return paretoFront;
    }

    /**
     * Create a pareto front from sampled solutions.
     *
     * @param solutions   from which a pareto front is created.
     */
    public static List<Encoding> findParetoFront(List<Encoding> solutions) {
        List<Encoding> paretoFront = new ArrayList<>();

        for (int i = 0; i < solutions.size(); i++) {
            boolean isDominated = false;
            for (int j = 0; j < solutions.size(); j++) {
                if (i != j && Utils.dominates(solutions.get(j), solutions.get(i))) {
                    isDominated = true;
                    break;
                }
            }
            if (!isDominated) {
                paretoFront.add(solutions.get(i));
            }
        }

        return paretoFront;
    }

    @Override
    public StoppingCondition getStoppingCondition() {
        return stoppingCondition;
    }
}
