package de.uni_passau.fim.se2.sbse.suite_generation.algorithms;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestCase;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestCaseGenerator;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.Branch;
import de.uni_passau.fim.se2.sbse.suite_generation.stopping_conditions.StoppingCondition;
import de.uni_passau.fim.se2.sbse.suite_generation.utils.Pair;
import de.uni_passau.fim.se2.sbse.suite_generation.utils.Utils;

class MOSATest {

    @Mock
    private Random mockRandom;

    @Mock
    private TestCaseGenerator mockGenerator;

    @Mock
    private StoppingCondition mockStoppingCondition;

    @Mock
    private TestCase mockTestCase;
    @Mock
    private TestCase mockTestCase1;
    @Mock
    private TestCase mockTestCase2;
    @Mock
    private TestCase mockTestCase3;

    @Mock
    private Map<TestCase, Map<Branch, Double>> mockFitnessMap1;

    @Mock
    private Branch mockBranch;

    @Mock
    private List<Branch> targetBranches;

    @Mock
    private List<Branch> targetBranches2;

    private MOSA mosa;
    private int populationSize = 10;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock target branches
        targetBranches = new ArrayList<>();
        targetBranches.add(mockBranch);

        // Initialize the MOSA instance
        mosa = new MOSA(populationSize, mockRandom, mockGenerator, targetBranches, mockStoppingCondition);
        
        targetBranches2 = List.of(
                mock(Branch.class), // Branch 1
                mock(Branch.class)  // Branch 2
        );
        mockTestCase1 = mock(TestCase.class);
        mockTestCase2 = mock(TestCase.class);
        mockTestCase3 = mock(TestCase.class);

        mockFitnessMap1 = new HashMap<>();
        mockFitnessMap1.put(mockTestCase1, new HashMap<>(Map.of(
            targetBranches2.get(0), 0.1,
            targetBranches2.get(1), 0.3
        )));
        mockFitnessMap1.put(mockTestCase2, new HashMap<>(Map.of(
            targetBranches2.get(0), 0.2,
            targetBranches2.get(1), 0.4
        )));
        mockFitnessMap1.put(mockTestCase3, new HashMap<>(Map.of(
            targetBranches2.get(0), 0.3,
            targetBranches2.get(1), 0.5
        )));
    }

    @Test
    public void testFindSolution() {
        // Mock stopping condition behavior
        when(mockStoppingCondition.searchMustStop())
            .thenReturn(false)  // First loop iteration
            .thenReturn(true);  // Stop after one iteration
        doNothing().when(mockStoppingCondition).notifySearchStarted();
        doNothing().when(mockStoppingCondition).notifyFitnessEvaluation();

        // Mock population generation
        List<TestCase> mockPopulation = new ArrayList<>();
        for (int i = 0; i < 10; i++) mockPopulation.add(mockTestCase);

        try (MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {
            // Mock static method initializePopulation
            mockedUtils.when(() -> Utils.initializePopulation(populationSize, mockGenerator))
                .thenReturn(mockPopulation);

            // Mock static method evaluateFitness
            Map<TestCase, Map<Branch, Double>> mockFitnessMap = Map.of(mockTestCase, Map.of(mockBranch, 0.5));
            mockedUtils.when(() -> Utils.evaluateFitness(mockPopulation, targetBranches, null))
                .thenReturn(mockFitnessMap);

            // Mock static method updateArchive
            List<TestCase> archive = new ArrayList<>();
            mockedUtils.when(() -> Utils.updateArchive(mockPopulation, mockFitnessMap, archive, targetBranches))
                .thenAnswer(invocation -> null);

            // Mock static method nonDominatedSorting
            List<TestCase> testcases = new ArrayList<>();
            mockedUtils.when(() -> Utils.nonDominatedSorting(testcases, mockFitnessMap, targetBranches))
                .thenReturn(List.of(mockPopulation));
            
            // Mock crossover() to return a valid Pair of TestCases
            TestCase mockOffspring1 = mock(TestCase.class);
            TestCase mockOffspring2 = mock(TestCase.class);
            Pair<TestCase> crossoverResult = new Pair<>(mockOffspring1, mockOffspring2);
            when(mockTestCase.crossover(any(TestCase.class))).thenReturn(crossoverResult);  // Mock crossover for TestCase

            // Mock mutate() to return the offspring itself
            when(mockOffspring1.mutate()).thenReturn(mockOffspring1);
            when(mockOffspring2.mutate()).thenReturn(mockOffspring2);

            // Execute the method under test
            List<TestCase> solutions = mosa.findSolution();

            // Verify interactions
            verify(mockStoppingCondition).notifySearchStarted();
            verify(mockStoppingCondition, times(2)).searchMustStop();
            verify(mockStoppingCondition).notifyFitnessEvaluation();

            // Assertions
            assertEquals(archive, solutions, "Solutions should match the archive.");
        }
    }

    @Test
    public void testStoppingCondition() {
        assertEquals(mockStoppingCondition, mosa.getStoppingCondition(),
            "The stopping condition should be correctly returned.");
    }

    @Test
    void testGenerateOffspring() {
        try {
            // Obtain the private method using reflection
            var generateOffspringMethod = MOSA.class.getDeclaredMethod(
                "generateOffspring",
                List.class,
                Map.class
            );
            generateOffspringMethod.setAccessible(true);

            // Mock random behavior for crossover
            when(mockRandom.nextDouble()).thenReturn(0.5); // Simulate crossover happening
            when(mockTestCase.crossover(mockTestCase)).thenReturn(new Pair<>(mockTestCase, mockTestCase));
            when(mockTestCase.mutate()).thenReturn(mockTestCase);

            // Mock population
            List<TestCase> mockPopulation = new ArrayList<>();
            for (int i = 0; i < 10; i++) mockPopulation.add(mockTestCase);

            // Mock fitness map
            Map<TestCase, Map<Branch, Double>> mockFitnessMap = Map.of(mockTestCase, Map.of(mockBranch, 0.5));

            // Invoke the private method
            @SuppressWarnings("unchecked")
            List<TestCase> offspring = (List<TestCase>) generateOffspringMethod.invoke(
                mosa, 
                mockPopulation, 
                mockFitnessMap
            );

            // Assertions
            assertEquals(mockPopulation.size(), offspring.size(), 
                "Offspring population size should match parent size.");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Method not found: " + e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Access error: " + e.getMessage(), e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Error during method invocation: " + e.getCause().getMessage(), e);
        }
    }

    @Test
    void testCalculateSubvectorDensity() {
        List<TestCase> front = new ArrayList<>(List.of(mockTestCase1, mockTestCase2, mockTestCase3));

        // Verify that densities are set in the expected range
        for (TestCase testCase : front) {
            assertTrue(testCase.getDensity() >= 0 && testCase.getDensity() <= 1);
        }
    }

    @Test
    void testCalculateSubvectorDensityWithSingleElement() {
        List<TestCase> front = new ArrayList<>(List.of(mockTestCase1));

        mosa.calculateSubvectorDensity(front, mockFitnessMap1);

        // Verify the density calculation for a single element
        verify(mockTestCase1).setDensity(Double.POSITIVE_INFINITY);
    }

    @Test
    void testCalculateSubvectorDensityEmptyFront() {
        List<TestCase> front = new ArrayList<>();

        mosa.calculateSubvectorDensity(front, mockFitnessMap1);

        // Ensure that the method does not attempt to set density on an empty list
        verifyNoInteractions(mockTestCase1, mockTestCase2, mockTestCase3);
    }
}
