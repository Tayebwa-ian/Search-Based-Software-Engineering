package de.uni_passau.fim.se2.sbse.suite_generation.utils;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import de.uni_passau.fim.se2.sbse.suite_generation.algorithms.*;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.*;
import de.uni_passau.fim.se2.sbse.suite_generation.stopping_conditions.StoppingCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;

class AlgorithmBuilderTest {

    private Random mockRandom;
    private StoppingCondition mockStoppingCondition;
    private IBranchTracer mockBranchTracer;
    private IBranch mockBranch;
    private Set<IBranch> mockBranchesToCover;
    private AlgorithmBuilder algorithmBuilder;

    @BeforeEach
    public void setUp() {
        // Initialize mocks
        mockRandom = mock(Random.class);
        mockStoppingCondition = mock(StoppingCondition.class);
        mockBranchTracer = mock(IBranchTracer.class);
        mockBranch = mock(Branch.class);
        mockBranchesToCover = new HashSet<>();
        for (int i = 0; i < 10; i++) mockBranchesToCover.add(mockBranch);

        // Mock branchTracer to return branches
        when(mockBranchTracer.getBranches()).thenReturn(mockBranchesToCover);

        // Create AlgorithmBuilder instance
        algorithmBuilder = new AlgorithmBuilder(
                mockRandom,
                mockStoppingCondition,
                100, // populationSize
                "Feature",
                "de.uni_passau.fim.se2.sbse.suite_generation.examples",
                mockBranchTracer
        );
    }

    @Test
    public void testBuildMOSA() {
        // Set up mocks related to MOSA
        mock(MOSA.class);

        // Mock building MOSA algorithm
        GeneticAlgorithm<?> algorithm = algorithmBuilder.build(SearchAlgorithmType.MOSA);

        // Verify that the algorithm is an instance of MOSA
        assertTrue(algorithm instanceof MOSA, "The algorithm should be MOSA");
    }

    @Test
    public void testBuildRandomSearch() {
        // Set up mocks related to RandomSearch
        mock(RandomSearch.class);

        // Mock building RandomSearch algorithm
        GeneticAlgorithm<?> algorithm = algorithmBuilder.build(SearchAlgorithmType.RANDOM_SEARCH);

        // Verify that the algorithm is an instance of RandomSearch
        assertTrue(algorithm instanceof RandomSearch, "The algorithm should be RandomSearch");
    }

    @Test
    public void testConstructorThrowsWhenClassUnderTestIsBlank() {
        // Test if constructor throws an exception when classUnderTest is blank
        assertThrows(IllegalArgumentException.class, () -> new AlgorithmBuilder(
                mockRandom,
                mockStoppingCondition,
                100,
                null,
                "de.uni_passau.fim.se2.sbse.suite_generation.examples",
                mockBranchTracer
        ), "Constructor should throw an exception for blank classUnderTest");
    }

    @Test
    public void testConstructorThrowsWhenPackageUnderTestIsBlank() {
        // Test if constructor throws an exception when packageUnderTest is blank
        assertThrows(IllegalArgumentException.class, () -> new AlgorithmBuilder(
                mockRandom,
                mockStoppingCondition,
                100,
                "Feature",
                null,
                mockBranchTracer
        ), "Constructor should throw an exception for blank packageUnderTest");
    }

    @Test
    public void testConstructorThrowsWhenClassNotFound() {
        // Simulate ClassNotFoundException
        when(mockBranchTracer.getBranches()).thenReturn(Collections.emptySet());
        assertThrows(IllegalArgumentException.class, () -> new AlgorithmBuilder(
                mockRandom,
                mockStoppingCondition,
                100,
                "hello",
                "de.uni_passau.fim.se2.sbse.suite_generation.examples",
                mockBranchTracer
        ), "Constructor should throw an exception for class not found");
    }
}
