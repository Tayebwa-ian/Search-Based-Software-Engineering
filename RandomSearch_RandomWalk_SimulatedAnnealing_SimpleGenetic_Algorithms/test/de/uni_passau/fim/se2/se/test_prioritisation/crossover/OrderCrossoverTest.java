package de.uni_passau.fim.se2.se.test_prioritisation.crossover;

import static org.junit.Assert.*;
import org.junit.jupiter.api.Test;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import de.uni_passau.fim.se2.se.test_prioritisation.mutations.ShiftToBeginningMutation;

import java.util.Random;

public class OrderCrossoverTest {
    ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(new Random());

    @Test
    public void testOffspringContainsAllParentElements() {
        // Arrange
        int[] parent1Positions = {0, 1, 2, 3, 4};
        int[] parent2Positions = {4, 3, 2, 1, 0};
        TestOrder parent1 = new TestOrder(mutation, parent1Positions);
        TestOrder parent2 = new TestOrder(mutation, parent2Positions);
        Random random = new Random(42); // Fixed seed for predictable crossover points
        OrderCrossover crossover = new OrderCrossover(random);

        // Act
        TestOrder offspring = crossover.apply(parent1, parent2);

        // Assert
        assertNotNull(offspring);
        int[] offspringPositions = offspring.getPositions();
        assertEquals(parent1.size(), offspringPositions.length);

        // Ensure offspring is a valid permutation
        boolean[] found = new boolean[parent1.size()];
        for (int position : offspringPositions) {
            assertTrue(position >= 0 && position < parent1.size());
            found[position] = true;
        }
        for (boolean exists : found) {
            assertTrue(exists); // All elements should be present exactly once
        }
    }

    @Test
    public void testDifferentCrossoverPoints() {
        // Arrange
        int[] parent1Positions = {0, 1, 2, 3, 4};
        int[] parent2Positions = {4, 3, 2, 1, 0};
        TestOrder parent1 = new TestOrder(mutation, parent1Positions);
        TestOrder parent2 = new TestOrder(mutation, parent2Positions);
        Random random = new Random(1); // Predictable crossover points
        OrderCrossover crossover = new OrderCrossover(random);

        // Act
        TestOrder offspring = crossover.apply(parent1, parent2);

        // Assert
        assertNotNull(offspring);
        int[] offspringPositions = offspring.getPositions();
        assertEquals(parent1.size(), offspringPositions.length);

        // Verify the crossover section and order preservation
        int[] expectedOffspring = {0, 1, 2, 3, 4};
        assertArrayEquals(expectedOffspring, offspringPositions);
    }

    @Test
    public void testWithUnEqualParents() {
        // Arrange
        int[] parent1Positions = {0, 1, 2, 3, 4};
        int[] parent2Positions = {3, 2, 1, 0};
        TestOrder parent1 = new TestOrder(mutation, parent1Positions);
        TestOrder parent2 = new TestOrder(mutation, parent2Positions);
        Random random = new Random(1); // Predictable crossover points
        OrderCrossover crossover = new OrderCrossover(random);

        assertThrows(IllegalArgumentException.class, () -> crossover.apply(parent1, parent2));
    }
}
