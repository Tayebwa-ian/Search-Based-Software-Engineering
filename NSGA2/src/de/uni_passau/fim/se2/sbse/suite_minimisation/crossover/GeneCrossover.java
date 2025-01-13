package de.uni_passau.fim.se2.sbse.suite_minimisation.crossover;

import java.util.Random;

import static java.util.Objects.requireNonNull;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.Encoding;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Pair;

public class GeneCrossover implements Crossover<Encoding> {

    /**
     * The internal source of randomness.
     */
    private final Random random;

    /**
     * Implements single point crossover.
     *
     * @param random the internal source of randomness
     */
    public GeneCrossover(final Random random) {
        requireNonNull(random, "random must not be null");
        this.random = random;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pair<Encoding> apply(Encoding parent1, Encoding parent2) {
        requireNonNull(parent1, "parent1 must not be null");
        requireNonNull(parent2, "parent2 must not be null");
        Crossover<Encoding> identity = Crossover.identity();
        Pair<Encoding> twos = identity.apply(parent1, parent2);
        Encoding pa1 = twos.getFst();
        Encoding pa2 = twos.getSnd();
        int n = pa1.chromosomeSize();
        int m = pa2.chromosomeSize();
        int[] p1 = parent1.getGenes();
        int[] p2 = parent2.getGenes();
        int[] c1 = new int[n];
        int[] c2 = new int[m];
        int pos = random.nextInt(n);
        System.arraycopy(p1, 0, c1, 0, pos);
        System.arraycopy(p2, pos, c1, pos, n - pos);
        System.arraycopy(p2, 0, c2, 0, pos);
        System.arraycopy(p1, pos, c2, pos, n - pos);
        Encoding child1 = new Encoding(parent1.getMutation(), parent1.getCrossover(), c1, parent1.getCoverageMatrix());
        Encoding child2 = new Encoding(parent2.getMutation(), parent2.getCrossover(), c2, parent2.getCoverageMatrix());
        Pair<Encoding> pair = new Pair<Encoding>(child1, child2);
        return pair;
    }
}
