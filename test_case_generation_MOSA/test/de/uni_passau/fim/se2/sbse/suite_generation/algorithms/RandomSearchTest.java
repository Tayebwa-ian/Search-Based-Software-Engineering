package de.uni_passau.fim.se2.sbse.suite_generation.algorithms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestCase;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestCaseGenerator;
import de.uni_passau.fim.se2.sbse.suite_generation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.Branch;
import de.uni_passau.fim.se2.sbse.suite_generation.stopping_conditions.StoppingCondition;
import de.uni_passau.fim.se2.sbse.suite_generation.utils.Utils;

class RandomSearchTest {

    @Mock
    private StoppingCondition mockStoppingCondition;

    @Mock
    private TestCaseGenerator mockGenerator;

    @Mock
    private FitnessFunction<TestCase> mockFitnessFunction;

    @Mock
    private TestCase mockTestCase;

    @Mock
    private Branch mockBranch;

    private List<Branch> targetBranches;
    private RandomSearch randomSearch;
    private int populationSize = 10;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mocked list of branches
        targetBranches = new ArrayList<>();
        targetBranches.add(mockBranch);

        // Initialize the RandomSearch instance
        randomSearch = new RandomSearch(mockStoppingCondition, mockGenerator, targetBranches, populationSize);
    }

    @Test
    public void testFindSolution() {
        // Mock behavior of stopping condition
        when(mockStoppingCondition.searchMustStop())
            .thenReturn(false)  // First loop iteration
            .thenReturn(true);  // Stop after one iteration
        doNothing().when(mockStoppingCondition).notifySearchStarted();
        doNothing().when(mockStoppingCondition).notifyFitnessEvaluation();

        // Mock behavior of generator
        List<TestCase> mockPopulation = new ArrayList<>();
        mockPopulation.add(mockTestCase);

        try (MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {
            // Mock static method initializePopulation
            mockedUtils.when(() -> Utils.initializePopulation(populationSize, mockGenerator)).thenReturn(mockPopulation);

            // Mock static method evaluateFitness
            Map<TestCase, Map<Branch, Double>> mockFitnessMap = Map.of(mockTestCase, Map.of(mockBranch, 0.5));
            mockedUtils.when(() -> Utils.evaluateFitness(mockPopulation, targetBranches, null))
                .thenReturn(mockFitnessMap);

            // Mock static method updateArchive
            List<TestCase> archive = new ArrayList<>();
            mockedUtils.when(() -> Utils.updateArchive(mockPopulation, mockFitnessMap, archive, targetBranches))
                    .thenAnswer(invocation -> null); // Stub the void static method

            // Execute the method
            List<TestCase> solutions = randomSearch.findSolution();

            // Verify interactions
            verify(mockStoppingCondition).notifySearchStarted();
            verify(mockStoppingCondition, times(2)).searchMustStop();
            verify(mockStoppingCondition).notifyFitnessEvaluation();

            // Assertions
            assertEquals(0, solutions.size(), "Archive should start empty and only be updated in the loop.");
        }
    }

    @Test
    public void testStoppingCondition() {
        assertEquals(mockStoppingCondition, randomSearch.getStoppingCondition(), 
            "The stopping condition should be correctly returned.");
    }
}
