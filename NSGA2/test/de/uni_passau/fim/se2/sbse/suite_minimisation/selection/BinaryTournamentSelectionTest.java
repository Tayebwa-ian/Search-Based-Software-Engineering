package de.uni_passau.fim.se2.sbse.suite_minimisation.selection;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Comparator;

import org.junit.jupiter.api.Test;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.Encoding;
import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.EncodingGenerator;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.GeneCrossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.BitFlipMutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Utils;

public class BinaryTournamentSelectionTest {
    private Random random = new Random();
    private BitFlipMutation mutation = new BitFlipMutation(random);
    private GeneCrossover crossover = new GeneCrossover(random);
    private boolean[][] coverageMatrix = {
        {true, false, true, false, true},
        {false, false, true, false, false},
        {true, false, true, false, false},
        {true, false, false, true, false},
        {false, true, true, false, false}
    };
    private int testSuiteSize = coverageMatrix.length;
    private int[] suite1 = {1, 0, 0, 1, 1};
    private int[] suite2 = {0, 1, 1, 1, 0};
    private int[] suite3 = {1, 1, 1, 1, 1};
    private Encoding c1 = new Encoding(mutation, crossover, suite1, coverageMatrix);  //  Should be the best
    private Encoding c2 = new Encoding(mutation, crossover, suite2, coverageMatrix);
    private Encoding c3 = new Encoding(mutation, crossover, suite3, coverageMatrix);
    private Comparator<Encoding> comparator = new Comparator<Encoding>() {
        @Override
        public int compare(Encoding E1, Encoding E2) {
            if (Utils.dominates(E1, E2)) return 1;
            return -1;
        }
    };

    private List<Encoding> generatePopulation(int size) {
        List<Encoding> population = new ArrayList<>();
        EncodingGenerator generator = new EncodingGenerator(
            random,
            mutation,
            crossover,
            testSuiteSize,
            coverageMatrix
        );
        for (int i = 0; i < size; i++) {
            population.add(generator.get());
        }
        return population;
    }

    private BinaryTournamentSelection<Encoding> selection = new BinaryTournamentSelection<>(comparator, random);

    @Test
    public void testConstructor_nullComparator_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new BinaryTournamentSelection<>(null, random));
    }

    @Test
    public void testConstructor_nullRandom_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new BinaryTournamentSelection<>(comparator, null));
    }

    @Test
    public void testApply_nullPopulation_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> selection.apply(null));
    }

    @Test
    public void testApply_emptyPopulation_throwsNoSuchElementException() {
        List<Encoding> population = generatePopulation(0);
        assertThrows(NoSuchElementException.class, () -> selection.apply(population));
    }

    @Test
    public void testApply_withPopulationOfTwo() {
        List<Encoding> population = List.of(c1, c2);
        assertEquals(c1, selection.apply(population));
    }

    @Test
    public void testApply_withPopulationOfOne() {
        List<Encoding> population = List.of(c3);
        assertEquals(c3, selection.apply(population));
    }

    @Test
    public void testApply_withPopulation() {
        Random random = new Random(42);
        BinaryTournamentSelection<Encoding> selection = new BinaryTournamentSelection<>(comparator, random);
        List<Encoding> population = generatePopulation(testSuiteSize);
        assertTrue(selection.apply(population) instanceof Encoding);
    }
}
