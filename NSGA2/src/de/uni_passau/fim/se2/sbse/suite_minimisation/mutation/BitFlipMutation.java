package de.uni_passau.fim.se2.sbse.suite_minimisation.mutation;

import java.util.Random;

import static java.util.Objects.requireNonNull;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.Encoding;

public class BitFlipMutation implements Mutation<Encoding> {
    /**
     * The internal source of randomness.
     */
    private final Random random;

    /**
     * Implement bit flips on a given encoding hence mutating the solution
     * @param random
     */
    public BitFlipMutation(Random random) {
        requireNonNull(random, "random must not be null");
        this.random = random;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Encoding apply(Encoding c) {
        Mutation<Encoding> identity = Mutation.identity();
        int size = c.chromosomeSize();
        double mutationRate = 1/size;
        Encoding copy = identity.apply(c);
        int[] individual = copy.getGenes();  //  mutate a copy
        for (int i = 0; i < size; i++) {
            if (random.nextDouble() < mutationRate) {
                individual[i] = 1 - individual[i];
            }
        }
        return new Encoding(c.getMutation(), c.getCrossover(), individual, c.getCoverageMatrix());
    }
}
