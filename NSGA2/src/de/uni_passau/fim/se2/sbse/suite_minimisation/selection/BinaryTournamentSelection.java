package de.uni_passau.fim.se2.sbse.suite_minimisation.selection;


import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.Chromosome;

import java.util.*;

import static java.util.Objects.requireNonNull;

/**
 * Implements a binary tournament selection operator that chooses individuals without replacement.
 *
 * @param <C> the type of chromosomes
 */
public class BinaryTournamentSelection<C extends Chromosome<C>> implements Selection<C> {

    private static final int TOURNAMENT_SIZE = 2;

    private final Random random;
    private final Comparator<C> comparator;

    /**
     * Creates a new binary tournament selection operator without replacement,
     * comparing individuals according to the given comparator.
     *
     * @param comparator for comparing chromosomes
     * @param random     the source of randomness
     * @throws NullPointerException if the comparator is null
     */
    public BinaryTournamentSelection(
            final Comparator<C> comparator,
            final Random random)
            throws NullPointerException, IllegalArgumentException {
        this.random = requireNonNull(random);
        this.comparator = requireNonNull(comparator);
    }

    /**
     * Applies binary tournament selection without replacement to the given population.
     *
     * @param population of chromosomes from which to select
     * @return the best individual in the tournament
     * @throws NullPointerException   if the population is {@code null}
     * @throws NoSuchElementException if the population is empty
     */
    @Override
    public C apply(final List<C> population) throws NullPointerException, NoSuchElementException {
        requireNonNull(population, "Population must not be null");
        if (population.isEmpty()) {
            throw new NoSuchElementException("Population is empty");
        }

        // Create a shuffled copy of the population to ensure no replacement
        List<C> shuffledPopulation = new ArrayList<>(population);
        Collections.shuffle(shuffledPopulation, random);

        C bestIndividual = shuffledPopulation.get(0);

        if (shuffledPopulation.size() > 1) {
            for (int i = 1; i < TOURNAMENT_SIZE; i++) {
                C challenger = shuffledPopulation.get(i);
                if (comparator.compare(challenger, bestIndividual) > 0) {
                    bestIndividual = challenger;
                }
            }
        }
        return bestIndividual;
    }
}
