package de.uni_passau.fim.se2.se.test_prioritisation.parent_selection;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.APLC;

import java.util.*;

public class TournamentSelection implements ParentSelection<TestOrder> {

    /**
     * A common default value for the size of the tournament.
     */
    private final static int DEFAULT_TOURNAMENT_SIZE = 5;

    private int tournamentSize;
    private final APLC fitnessFunction;
    private final Random random;

    /**
     * Creates a new tournament selection operator.
     *
     * @param tournamentSize  the size of the tournament
     * @param fitnessFunction the fitness function used to rank the test orders
     * @throws NullPointerException if any of the arguments is {@code null}
     */
    public TournamentSelection(int tournamentSize, APLC fitnessFunction, Random random) {
        this.tournamentSize = tournamentSize;
        this.fitnessFunction = fitnessFunction;
        this.random = random;
    }

    /**
     * Creates a new tournament selection operator with a default tournament size.
     *
     * @param fitnessFunction the fitness function used to rank the test orders
     * @throws NullPointerException if any of the arguments is {@code null}
     */
    public TournamentSelection(APLC fitnessFunction, Random random) {
        this(DEFAULT_TOURNAMENT_SIZE, fitnessFunction, random);
    }

    /**
     * Selects a single parent from a population to be evolved in the current generation of an evolutionary algorithm
     * using the tournament selection strategy.
     *
     * @param population the population from which to select parents
     * @return the selected parent
     */
    @Override
    public TestOrder selectParent(List<TestOrder> population) {
        List<TestOrder> tournamentPool = new ArrayList<>();
        if(tournamentSize > population.size()) {
            throw new IllegalArgumentException();
        } else if (tournamentSize == population.size()){
            tournamentPool = population;
        } else {
            // Randomly select individuals from the population
            for (int i = 0; i < tournamentSize; i++) {
                int randomIndex = random.nextInt(population.size());
                tournamentPool.add(population.get(randomIndex));
            }
        }

        // Find the fittest individual in the tournament pool
        TestOrder fittest = tournamentPool.get(0);
        for (TestOrder individual : tournamentPool) {
            if (fitnessFunction.applyAsDouble(individual) > fitnessFunction.applyAsDouble(fittest)) {
                fittest = individual;
            }
        }

        return fittest;
    }
}
