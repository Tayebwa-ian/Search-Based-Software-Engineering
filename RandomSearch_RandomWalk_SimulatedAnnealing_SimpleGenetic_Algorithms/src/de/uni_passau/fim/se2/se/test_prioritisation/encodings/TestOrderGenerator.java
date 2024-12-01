package de.uni_passau.fim.se2.se.test_prioritisation.encodings;

import de.uni_passau.fim.se2.se.test_prioritisation.mutations.Mutation;

import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A generator for random test case orderings of a regression test suite. In the literature, indices
 * would start at 1. However, we let them start at 0 as this simplifies the implementation. The
 * highest index is given by the number of test cases minus 1. The range of indices is contiguous.
 */
public class TestOrderGenerator implements EncodingGenerator<TestOrder> {

    private final Random random;
    private final Mutation<TestOrder> mutation;
    private final int testCases;

    /**
     * Creates a new test order generator with the given mutation and number of test cases.
     *
     * @param random     the source of randomness
     * @param mutation   the elementary transformation that the generated orderings will use
     * @param testCases  the number of test cases in the ordering
     */
    public TestOrderGenerator(final Random random, final Mutation<TestOrder> mutation, final int testCases) {
        if (testCases < 1) {
            throw new IllegalArgumentException("test cases must be 1 or more");
        }
        this.testCases = testCases;
        this.random = random;
        this.mutation = mutation;
    }

    /**
     * Creates and returns a random permutation of test cases.
     *
     * @return random test case ordering
     */
    @Override
    public TestOrder get() {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < testCases; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers, random);
        int[] positions = new int[testCases];

        for(int i = 0; i < testCases; i++) {
            positions[i] = numbers.get(i);
        }
        TestOrder newTestOrder = new TestOrder(mutation, positions);
        return newTestOrder;
    }
}
