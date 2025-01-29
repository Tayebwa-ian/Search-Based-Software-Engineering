package de.uni_passau.fim.se2.sbse.suite_generation.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestCase;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestCaseGenerator;
import de.uni_passau.fim.se2.sbse.suite_generation.crossover.OnePointCrossover;
import de.uni_passau.fim.se2.sbse.suite_generation.examples.Feature;
import de.uni_passau.fim.se2.sbse.suite_generation.mutation.TestCaseMutation;

public class PairTest {

    private Random random;
    private OnePointCrossover crossover;
    private TestCaseMutation mutation;
    private Class<?> classUnderTest;
    private TestCaseGenerator generator;

    @BeforeEach
    public void setUp() {
        random = Randomness.random();
        mutation = new TestCaseMutation(random, new ArrayList<>());
        crossover = new OnePointCrossover(random);
        classUnderTest = new Feature(10).getClass();
        generator = new TestCaseGenerator(random, mutation, crossover, classUnderTest);
    }

    @Test
    public void testInvalidConstructorInputs() {
        TestCase tc1 = generator.get();
        TestCase tc2 = generator.get();
        assertThrows(
            NullPointerException.class,
            () -> new Pair<>(null)
        );
        assertThrows(
            NullPointerException.class,
            () -> new Pair<>(null, tc2)
        );
        assertThrows(
            NullPointerException.class,
            () -> new Pair<>(tc1, null)
        );
        assertThrows(
            NullPointerException.class,
            () -> Pair.generate(null)
        );
    }

    @Test
    public void testPair () {
        TestCase tc1 = generator.get();
        TestCase tc2 = generator.get();
        TestCase tc3 = generator.get();
        TestCase tc4 = generator.get();
        Pair<TestCase> pair1 = new Pair<>(tc1, tc2);
        Pair<TestCase> pair2 = new Pair<>(tc3, tc4);
        Pair<TestCase> copy = new Pair<>(pair2);
        assertTrue(copy.size() == pair2.size());
        assertTrue(copy.equals(pair2));
        assertTrue(pair2.equals(pair2));
        assertFalse(pair1.equals(pair2));
        assertTrue(copy.hashCode() == pair2.hashCode());
        assertTrue(pair1.getFst() == tc1);
        assertFalse(pair2.getSnd() == tc2);
        assertFalse(pair1.equals(null));
    }

    @Test
    public void testOfFunction() {
        TestCase tc1 = generator.get();
        TestCase tc2 = generator.get();
        Pair<TestCase> pair = Pair.of(tc1, tc2);
        assertTrue(pair instanceof Pair);
    }

    @Test
    public void testString() {
        TestCase tc1 = generator.get();
        TestCase tc2 = generator.get();
        Pair<TestCase> pair1 = new Pair<>(tc1, tc2);
        String str = String.format(
            "%s(%s, %s)",
            pair1.getClass().getSimpleName(),
            pair1.getFst(),
            pair1.getSnd()
        );
        assertTrue(str.length() == pair1.toString().length());
    }
}
