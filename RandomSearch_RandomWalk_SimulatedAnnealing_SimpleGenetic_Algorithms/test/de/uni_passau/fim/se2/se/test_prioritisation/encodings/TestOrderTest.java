package de.uni_passau.fim.se2.se.test_prioritisation.encodings;
import org.junit.jupiter.api.Test; 
import static org.junit.Assert.*;

import java.util.Random;

import de.uni_passau.fim.se2.se.test_prioritisation.mutations.ShiftToBeginningMutation;

public class TestOrderTest {
    @Test
    public void testValidInputs() {
        int[] testCases = {0, 1, 2, 3, 4};
        ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(new Random());
        TestOrder testOrder = new TestOrder(mutation, testCases);

        assertSame(testOrder.getPositions(), testOrder.deepCopy().getPositions());
        assertEquals(testCases, testOrder.getPositions());
        assertEquals(testOrder, testOrder.self());
        assertEquals(5, testOrder.size());
    }

    @Test
    public void testInValidInputs() {
        int[] testCases = {1, 2, 3, 4, 5};
        int[] testCases1 = {-1, 2, 3, 4, 4};
        int[] testCases2 = {};
        ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(new Random());

        assertThrows(IllegalArgumentException.class, () -> new TestOrder(mutation, testCases));
        assertThrows(IllegalArgumentException.class, () -> new TestOrder(mutation, testCases1));
        assertThrows(IllegalArgumentException.class, () -> new TestOrder(mutation, testCases2));
    }
}
