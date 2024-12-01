package de.uni_passau.fim.se2.se.test_prioritisation.algorithms;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.Encoding;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.EncodingGenerator;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions.StoppingCondition;

import java.util.Random;


/**
 * Implements the Simulated Annealing algorithm for test order prioritisation based on
 * -----------------------------------------------------------------------------------------
 * Flow chart of the algorithm:
 * Bastien Chopard, Marco Tomassini, "An Introduction to Metaheuristics for Optimization",
 * (Springer), Ch. 4.3, Page 63
 * -----------------------------------------------------------------------------------------
 * Note we've applied a few modifications to add elitism.
 *
 * @param <E> the type of encoding
 */
public final class SimulatedAnnealing<E extends Encoding<E>> implements SearchAlgorithm<E> {

    private final StoppingCondition stoppingCondition;
    private final EncodingGenerator<E> encodingGenerator;
    private final FitnessFunction<E> energy;
    private final Random random;
    private final int degreesOfFreedom;

    /**
     * Constructs a new simulated annealing algorithm.
     *
     * @param stoppingCondition the stopping condition to use
     * @param encodingGenerator the encoding generator to use
     * @param energy            the energy fitness function to use
     * @param degreesOfFreedom  the number of degrees of freedom of the problem, i.e. the number of variables that define a solution
     * @param random            the random number generator to use
     */
    public SimulatedAnnealing(
            final StoppingCondition stoppingCondition,
            final EncodingGenerator<E> encodingGenerator,
            final FitnessFunction<E> energy,
            final int degreesOfFreedom,
            final Random random) {
        this.stoppingCondition = stoppingCondition;
        this.encodingGenerator = encodingGenerator;
        this.energy = energy;
        this.degreesOfFreedom = degreesOfFreedom;
        this.random = random;
    }

    /**
     * Performs the Simulated Annealing algorithm to search for an optimal solution of the encoded problem.
     * Since Simulated Annealing is designed as a minimisation algorithm, optimal solutions are characterized by a minimal energy value.
     */
    @Override
    public E findSolution() {
        E currentSolution = encodingGenerator.get();
        double currentEnergy = energy.minimise(currentSolution);

        // Perform random walk to calculate average energy variation
        double averageDeltaE = averageEnergyVariation(5000);

        // Compute initial temperature
        double r0 = -Math.log(0.2);
        double temperature = averageDeltaE / r0; // intial temperature

        // intialise best solution
        E bestSolution = currentSolution.deepCopy();
        double bestEnergy = currentEnergy;

        int acceptedMutations = 0; // intialise accepted mutations

        stoppingCondition.notifySearchStarted();
        while (!stoppingCondition.searchMustStop()) {
            // Generate a neighbor solution
            E neighbor = currentSolution.mutate();
            double neighborEnergy = energy.minimise(neighbor);

            double deltaEnergy = neighborEnergy - currentEnergy;

            // Accept the neighbor solution based on the Metropolis criterion
            if (deltaEnergy < 0 || random.nextDouble() < Math.exp(-(deltaEnergy) / temperature)) {
                currentSolution = neighbor;
                currentEnergy = neighborEnergy;
                acceptedMutations++;

                // Update best solution if the new one is better
                if (currentEnergy < bestEnergy) {
                    bestSolution = currentSolution.deepCopy();
                    bestEnergy = currentEnergy;
                }
            }

            // Decrease the temperature
            temperature *= 0.9; // Adjust cooling rate as needed

            // Terminate early if temperature is very low
            if (acceptedMutations > degreesOfFreedom || temperature < 0) {
                break;
            }

            stoppingCondition.notifyFitnessEvaluation();
        }

        return bestSolution;
    }

    @Override
    public StoppingCondition getStoppingCondition() {
        return stoppingCondition;
    }

    // Perform a random walk to calculate average energy variation
    public double averageEnergyVariation(int n) {
        E currentSolution = encodingGenerator.get();
        double currentEnergy = energy.maximise(currentSolution);

        double totalDeltaE = 0;

        for (int i = 0; i < n; i++) {
            E neighbor = currentSolution.deepCopy();
            neighbor.mutate();
            double neighborEnergy = energy.maximise(neighbor);
            totalDeltaE += Math.abs(neighborEnergy - currentEnergy);
            currentEnergy = neighborEnergy; // Update for the next iteration
        }

        return totalDeltaE / n;
    }
}
