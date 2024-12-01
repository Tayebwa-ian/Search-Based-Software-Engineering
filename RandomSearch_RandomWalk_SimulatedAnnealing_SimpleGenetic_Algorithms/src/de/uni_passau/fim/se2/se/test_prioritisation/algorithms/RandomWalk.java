package de.uni_passau.fim.se2.se.test_prioritisation.algorithms;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.Encoding;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.EncodingGenerator;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions.StoppingCondition;


/**
 * Implements a random walk through the search space.
 *
 * @param <E> the type of encoding
 */
public final class RandomWalk<E extends Encoding<E>> implements SearchAlgorithm<E> {

    private final StoppingCondition stoppingCondition;
    private final EncodingGenerator<E> encodingGenerator;
    private final FitnessFunction<E> fitnessFunction;

    /**
     * Constructs a new random walk algorithm.
     *
     * @param stoppingCondition the stopping condition to use
     * @param encodingGenerator the encoding generator to use
     * @param fitnessFunction   the fitness function to use
     */
    public RandomWalk(
            final StoppingCondition stoppingCondition,
            final EncodingGenerator<E> encodingGenerator,
            final FitnessFunction<E> fitnessFunction) {
                if (stoppingCondition == null || encodingGenerator == null ||  fitnessFunction == null) {
                    throw new IllegalArgumentException("None of the Object arguments should be null");
                }
                this.stoppingCondition = stoppingCondition;
                this.encodingGenerator = encodingGenerator;
                this.fitnessFunction = fitnessFunction;
    }

    /**
     * Implements a random walk through the search space. First, a randomly chosen configuration is used as starting point.
     * Next, the search space is explored by taking a number of consecutive steps in some direction.
     * Finally, the best encountered configuration is chosen as the solution.
     *
     * @return the best solution found
     */
    @Override
    public E findSolution() {
        E currentSolution = encodingGenerator.get();
        double fitness = fitnessFunction.applyAsDouble(currentSolution);

        E bestSolution  = currentSolution.deepCopy();
        double bestFitness = fitness;

        stoppingCondition.notifySearchStarted();
        while (stoppingCondition.getProgress() < 1.0) {
            currentSolution = currentSolution.mutate();
            fitness = fitnessFunction.applyAsDouble(currentSolution);

            if (fitness > bestFitness) {
                bestSolution = currentSolution;
                bestFitness = fitness;
            }

            stoppingCondition.notifyFitnessEvaluation();
        }
        return bestSolution;
    }

    @Override
    public StoppingCondition getStoppingCondition() {
        return stoppingCondition;
    }

}
