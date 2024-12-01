package de.uni_passau.fim.se2.se.test_prioritisation.mutations;

import org.junit.jupiter.api.Test; 
import static org.junit.Assert.*;

import java.util.Random;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;

public class ShiftToBeginningMutationTest {
    ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(new Random());
    int[] testCases = {0, 1, 2, 3, 4};
    TestOrder testOrder = new TestOrder(mutation, testCases);

    @Test
    public void testMutation() {
        TestOrder mutedOrder = mutation.apply(testOrder);
        int[] mutedcases = mutedOrder.getPositions();

        assertNotEquals(0, mutedcases[0]);
        assertEquals(testCases.length, mutedcases.length);
    }
}
