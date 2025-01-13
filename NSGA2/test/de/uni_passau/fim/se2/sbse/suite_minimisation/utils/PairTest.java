package de.uni_passau.fim.se2.sbse.suite_minimisation.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.Random;
import org.junit.jupiter.api.Test;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.Encoding;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.GeneCrossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.BitFlipMutation;

public class PairTest {

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
    private int[] suite1 = {1, 0, 0, 1, 1};
    private int[] suite2 = {0, 1, 1, 1, 0};
    private int[] suite3 = {0, 1, 1, 0, 1};
    private int[] suite4 = {1, 1, 1, 1, 1};
    private Encoding c1 = new Encoding(mutation, crossover, suite1, coverageMatrix);  //  Should be the best
    private Encoding c2 = new Encoding(mutation, crossover, suite2, coverageMatrix);
    private Encoding c3 = new Encoding(mutation, crossover, suite3, coverageMatrix);  // have same evaluation values as c2
    private Encoding c4 = new Encoding(mutation, crossover, suite4, coverageMatrix);

    private Pair<Encoding> pair1 = new Pair<>(c1, c2);
    private Pair<Encoding> pair2 = new Pair<>(c2, c3);

    @Test
    public void testInvalidConstructorInputs() {
        assertThrows(
            NullPointerException.class,
            () -> new Pair<>(null)
        );
        assertThrows(
            NullPointerException.class,
            () -> new Pair<>(null, c2)
        );
        assertThrows(
            NullPointerException.class,
            () -> new Pair<>(c1, null)
        );
        assertThrows(
            NullPointerException.class,
            () -> Pair.generate(null)
        );
    }

    @Test
    public void testPair () {
        Pair<Encoding> copy = new Pair<>(pair2);
        assertTrue(copy.size() == pair2.size());
        assertTrue(copy.equals(pair2));
        assertTrue(pair2.equals(pair2));
        assertFalse(pair1.equals(pair2));
        assertTrue(copy.hashCode() == pair2.hashCode());
        assertTrue(pair1.getFst() == c1);
        assertFalse(pair2.getSnd() == c1);
        assertFalse(pair1.equals(null));
    }
    
    @Test
    public void testOfFunction() {
        Pair<Encoding> pair = Pair.of(c3, c4);
        assertTrue(pair instanceof Pair);
    }

    @Test
    public void testString() {
        String str = String.format("%s(%s, %s)", pair1.getClass().getSimpleName(), pair1.getFst(), pair1.getSnd());
        assertTrue(str.length() == pair1.toString().length());
    }
}
