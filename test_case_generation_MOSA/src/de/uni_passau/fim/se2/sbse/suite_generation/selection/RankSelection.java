package de.uni_passau.fim.se2.sbse.suite_generation.selection;


import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.Chromosome;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;


/**
 * Implements rank selection for populations of a user-specified constant size. The operator works
 * as follows:
 * <ol>
 *     <li>First, the population is sorted by their fitness, according to the comparator this
 *     selection operator was constructed with.</li>
 *     <li>Then, every individual is assigned a rank, starting at rank 1 for the worst
 *     individual and finishing with the highest rank for the best individual. Note that all
 *     individuals get a different rank, even if they have the same fitness value.</li>
 *     <li>Finally, the selection probability is linearly assigned to the individuals according
 *     to their rank.</li>
 * </ol>
 * <p>
 * Rank selection overcomes some issues of fitness-proportionate (roulette-wheel) selection
 * because it is unbiased whether the fittest candidate is, for example, 10 times fitter than the
 * next fittest or just 0.1 times fitter. Furthermore, rank selection provides a degree of control
 * over selective pressure that is not possible with fitness-proportionate selection.
 * <p>
 * In particular, users of this class can choose the extent to which selective pressure is exercised
 * by specifying the so called <em>selection bias</em>. For populations of size {@code N > 1} the
 * best individual is assigned rank {@code N} and the worst individual rank {@code 1}. With this,
 * the selection probability {@code p(i)} for an individual with rank {@code 1 <= i <= N} is
 * computed as follows:
 * <pre>{@code
 * p(i) = (2 - c + 2 * (c - 1) * (i - 1) / (N - 1)) / N
 * }</pre>
 * The parameter {@code 1 < c <= 2} is the user-specified selection bias. Here, {@code p(1) = (2 -
 * c) / N} is the probability of the worst individual to be selected, and {@code p(N) = c / N} the
 * probability of the best individual to be selected. Since all individuals were assigned a
 * different rank, the selection probability is different for every individual, even for these with
 * the same fitness value. Note that once the population size has been set, the rank based selection
 * operator cannot be applied to populations of different size as the computed probabilities would
 * no longer match.
 * <p>
 * By default, when no user-specified selection bias is given, the operator assumes a sane default
 * of {@code c = 2 * N / (N + 1)}. This way,
 * <pre>{@code
 * p(1) =     2 / (N * (N + 1)) = 1 / (1 + 2 + 3 + ... + N)
 * p(i) = i * 2 / (N * (N + 1)) = i / (1 + 2 + 3 + ... + N) = i * p(1)
 * p(N) = N * 2 / (N * (N + 1)) = 2 / (N + 1)
 * }</pre>
 * This means the best individual is {@code N} times as likely to be selected as the worst
 * individual, the second best individual {@code N - 1} times as likely as the worst, and so on.
 * <p>
 * Apart from that, the operator can be used in two modes:
 * <ul>
 *     <li>In fast selection mode, the operator approximates the index {@code i} of the selected
 *     individual by transforming an equally distributed random variable {@code 0 <= r <= 1}:
 *     <pre>{@code
 *     i = floor(N * (c - Math.sqrt(c * c - 4.0 * (c - 1.0) * r)) / 2.0 / (c - 1.0));
 *     }</pre>
 *     This method has first been described been used by Whitley in the GENITOR algorithm (1989).
 *     The details are outlined in the literature. For selection biases between 1 and 2, it produces
 *     results almost identical to slow selection mode.</li>
 *     <li>In slow selection mode, the operator does not rely on approximations and has to fall back
 *     to exact (but slow) computations to determine the selection index.</li>
 * </ul>
 * @param <C> the type of chromosomes supported by this operator
 */
public class RankSelection<C extends Chromosome<C>> implements Selection<C> {

    private final Comparator<C> comparator;
    private final int size;
    private final double bias;
    private final Random random;

    /**
     * Constructs a new rank selection operator that uses the given comparator to rank the
     * individuals that are part of a population with the specified fixed size, and applies the
     * specified selection bias when picking individuals. Note that the selection bias must be in
     * the interval {@code [1, 2]}.
     *
     * @param comparator the comparator for ranking individuals
     * @param size       the size of the population the selection operator should be applied to
     * @param bias       the selection bias to exercise
     * @param random     the source of randomness
     */
    public RankSelection(final Comparator<C> comparator, final int size, final double bias, final Random random) {
        if (size <= 1) {
            throw new IllegalArgumentException("Population size must be greater than 1.");
        }
        if (bias < 1.0 || bias > 2.0) {
            throw new IllegalArgumentException("Bias must be in the range [1, 2].");
        }
        this.comparator = Objects.requireNonNull(comparator, "Comparator must not be null.");
        this.size = size;
        this.bias = bias;
        this.random = Objects.requireNonNull(random, "Random must not be null.");
    }

    /**
     * Chooses an individual from the given population using rank selection.
     *
     * @param population the population of chromosomes from which to select
     * @return the selected individual
     */
    @Override
    public C apply(final List<C> population) {
        if (population == null || population.size() != size) {
            throw new IllegalArgumentException("Population must not be null and must have the fixed size: " + size);
        }

        // Step 1: Sort the population by fitness using the comparator
        List<C> sortedPopulation = population.stream()
                .sorted(comparator)
                .collect(Collectors.toList());

        // Step 2: Assign ranks and calculate cumulative probabilities
        double[] cumulativeProbabilities = new double[size];
        for (int i = 0; i < size; i++) {
            double rank = i + 1; // Ranks start from 1 for the worst individual
            double probability = (2 - bias + 2 * (bias - 1) * (rank - 1) / (size - 1)) / size;
            cumulativeProbabilities[i] = (i == 0) ? probability : cumulativeProbabilities[i - 1] + probability;
        }

        // Step 3: Generate a random number and find the corresponding individual
        double randomValue = random.nextDouble();
        for (int i = 0; i < size; i++) {
            if (randomValue <= cumulativeProbabilities[i]) {
                return sortedPopulation.get(i);
            }
        }

        // Fallback, should not be reached
        return sortedPopulation.get(size - 1);
    }
}
