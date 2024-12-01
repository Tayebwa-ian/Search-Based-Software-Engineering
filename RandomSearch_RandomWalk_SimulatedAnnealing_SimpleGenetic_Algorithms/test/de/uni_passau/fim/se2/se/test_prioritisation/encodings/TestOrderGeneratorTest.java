package de.uni_passau.fim.se2.se.test_prioritisation.encodings;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test; 

import java.util.Random;

import java.util.HashSet;

import de.uni_passau.fim.se2.se.test_prioritisation.mutations.Mutation;
import de.uni_passau.fim.se2.se.test_prioritisation.mutations.ShiftToBeginningMutation;

public class TestOrderGeneratorTest {

    private static Mutation<TestOrder> findMutation() {
        int[] testCases = {0, 1, 2, 3, 4};
        Random random = new Random();
        ShiftToBeginningMutation mutationType = new ShiftToBeginningMutation(random);
        TestOrder testOrder = new TestOrder(mutationType, testCases);
        Mutation<TestOrder> mutation = testOrder.getMutation();
        return mutation;
    }

    @Test
    public void testRandomOrderGeneration() {
        int testCases = 5;
        Random random = new Random(42); // Fixed seed for deterministic behavior
        Mutation<TestOrder> mutation = findMutation();
        TestOrderGenerator generator = new TestOrderGenerator(random, mutation, testCases);

        TestOrder order = generator.get();
        int[] positions = order.getPositions();

        assertNotNull(positions, "Positions should not be null.");
        assertEquals(testCases, positions.length, "Positions array size should match the number of test cases.");

        // Ensure all indices are present exactly once
        HashSet<Integer> uniquePositions = new HashSet<>();
        for (int position : positions) {
            uniquePositions.add(position);
        }
        assertEquals(testCases, uniquePositions.size(), "All indices should be unique.");
    }

    @Test
    public void testOrderShufflesDifferently() {
        int testCases = 5;
        Random random1 = new Random(42); // Fixed seed for deterministic behavior
        Random random2 = new Random(43); // Different seed
        Mutation<TestOrder> mutation = findMutation();

        TestOrderGenerator generator1 = new TestOrderGenerator(random1, mutation, testCases);
        TestOrderGenerator generator2 = new TestOrderGenerator(random2, mutation, testCases);

        TestOrder order1 = generator1.get();
        TestOrder order2 = generator2.get();

        assertNotEquals(
            order1.getPositions(),
            order2.getPositions(),
            "Two generators with different seeds should produce different results."
        );
    }

    @Test
    public void testSameSeedProducesSameOrder() {
        int testCases = 5;
        Random random1 = new Random(42);
        Random random2 = new Random(42); // Same seed
        Mutation<TestOrder> mutation = findMutation();

        TestOrderGenerator generator1 = new TestOrderGenerator(random1, mutation, testCases);
        TestOrderGenerator generator2 = new TestOrderGenerator(random2, mutation, testCases);

        TestOrder order1 = generator1.get();
        TestOrder order2 = generator2.get();

        assertArrayEquals(
            order1.getPositions(),
            order2.getPositions(),
            "Two generators with the same seed should produce the same results."
        );
    }

    @Test
    public void testNoTestCases() {
        int testCases = 0;
        Random random = new Random();
        Mutation<TestOrder> mutation = findMutation();

        assertThrows(IllegalArgumentException.class, () -> new TestOrderGenerator(random, mutation, testCases));
    }
}
