package de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions;

import org.junit.Test; 
import static org.junit.Assert.*;

import java.util.Random;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import de.uni_passau.fim.se2.se.test_prioritisation.mutations.ShiftToBeginningMutation;

public class APLCTest {
    int[] testCases = {0, 1, 2, 3, 4};
    int[] testCases1 = {0, 1, 2};

    ShiftToBeginningMutation mutation = new ShiftToBeginningMutation(new Random());
    TestOrder testOrder = new TestOrder(mutation, testCases);
    TestOrder testOrder1 = new TestOrder(mutation, testCases1);
    
    @Test
    public void testAPLC() {
        boolean[][] coverageMatrix = {
            {true, false, true, false, false},
            {false, false, true, false, false},
            {true, false, true, false, false},
            {true, false, false, true, false},
            {false, true, true, false, false},
        };

        boolean[][] coverageMatrix1 = {
            {true, true, false},
            {false, true, true},
            {true, false, true}
        };

        APLC aplc = new APLC(coverageMatrix);
        APLC aplc1 = new APLC(coverageMatrix1);
        double result = aplc.maximise(testOrder);
        double result1 = aplc1.maximise(testOrder1);
        assertTrue(result > 0.0 && result <= 1.0);
        assertTrue(aplc.minimise(testOrder) < 0.5);
        assertTrue(result1 > 0.0 && result1 <= 1.0);
    }
}
