package de.uni_passau.fim.se2.sbse.suite_generation.fitness_functions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestCase;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.Branch;

public class BranchCoverageFitnessFunctionTest {

    @Mock
    private TestCase testCase;

    @Mock
    private Branch branch;

    @InjectMocks
    private BranchCoverageFitnessFunction fitnessFunction;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // Setup the branch mock to return a specific ID
        when(branch.getId()).thenReturn(1); // Example ID
    }

    @Test
    public void testBranchNotCovered() {
        Map<Integer, Double> distances = new HashMap<>();
        when(testCase.call()).thenReturn(distances);

        double fitness = fitnessFunction.applyAsDouble(testCase);
        assertEquals(1.0, fitness, 0.0001, "Fitness should be 1.0 when branch is not covered");
    }

    @Test
    public void testBranchCoveredWithDistance() {
        Map<Integer, Double> distances = new HashMap<>();
        distances.put(1, 0.5); // Distance for the branch with ID 1
        when(testCase.call()).thenReturn(distances);

        double fitness = fitnessFunction.applyAsDouble(testCase);
        assertEquals(0.3333, fitness, 0.0001, "Fitness should be approximately 0.3333 for a distance of 0.5");
    }

    @Test
    public void testBranchCoveredCompletely() {
        Map<Integer, Double> distances = new HashMap<>();
        distances.put(1, 0.0); // Distance for the branch with ID 1
        when(testCase.call()).thenReturn(distances);

        double fitness = fitnessFunction.applyAsDouble(testCase);
        assertEquals(0.0, fitness, 0.0001, "Fitness should be 0.0 when branch is completely covered");
    }

    @Test
    public void testIsMinimizing() {
        assertTrue(fitnessFunction.isMinimizing(), "Fitness function should be minimizing");
    }

    @Test
    public void testComparator() {
        assertTrue(fitnessFunction.comparator() instanceof Comparator);
    }
}
