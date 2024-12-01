package de.uni_passau.fim.se2.se.test_prioritisation.parent_selection;

import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrderGenerator;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.APLC;
import de.uni_passau.fim.se2.se.test_prioritisation.mutations.Mutation;
import de.uni_passau.fim.se2.se.test_prioritisation.mutations.ShiftToBeginningMutation;

public class TournamentSelectionTest {
    Random random = new Random();

    private List<TestOrder> generatePopulation(int size, int[] testCases) {
        List<TestOrder> population = new ArrayList<>();
        ShiftToBeginningMutation mutationType = new ShiftToBeginningMutation(random);
        TestOrder testOrder = new TestOrder(mutationType, testCases);
        Mutation<TestOrder> mutation = testOrder.getMutation();
        TestOrderGenerator generator = new TestOrderGenerator(random, mutation, testCases.length);
        population.add(testOrder);
        for (int i = 0; i < size; i++) {
            population.add(generator.get());
        }
        return population;
    }
    
    @Test
    public void testTournamentSelectionIllegalArgumentExpection() {
        int[] testCases = {0, 1, 2, 3, 4};
        boolean[][] coverageMatrix = {
            {true, false, true, false, false},
            {false, false, true, false, false},
            {true, false, true, false, false},
            {true, false, false, true, false},
            {false, true, true, false, false}
        };

        APLC aplc = new APLC(coverageMatrix);
        List<TestOrder> population = generatePopulation(10, testCases);
        int tournamentSize = 14;
        TournamentSelection tournament1 = new TournamentSelection(tournamentSize, aplc, random);
        // throws an expectation if tournament size is greater than population
        assertThrows(IllegalArgumentException.class,
        () -> tournament1.selectParent(population));
    }

    @Test
    public void testTournamentSizeEqualsPopulation() {
        // Arrange
        int[] testCases = {0, 1, 2};
        int tournamentSize = 10;
        boolean[][] coverageMatrix = { {true, false}, {false, true}, {true, true} };
        APLC aplc = new APLC(coverageMatrix);
        TournamentSelection tournamentSelection = new TournamentSelection(tournamentSize, aplc, random);
        List<TestOrder> population = generatePopulation(10, testCases);

        // Act
        TestOrder fittest = tournamentSelection.selectParent(population);

        // Assert
        assertNotNull(fittest);
        assertTrue(population.contains(fittest)); // Fittest must be from the population
    }

    @Test
    public void testTournamentSizeLessThanPopulation() {
        // Arrange
        int[] testCases = {0, 1, 2};
        int tournamentSize = 2;
        boolean[][] coverageMatrix = { {true, false}, {false, true}, {true, true} };
        APLC aplc = new APLC(coverageMatrix);
        Random random = new Random();
        TournamentSelection tournamentSelection = new TournamentSelection(tournamentSize, aplc, random);

        List<TestOrder> population = generatePopulation(5, testCases);

        // Act
        TestOrder fittest = tournamentSelection.selectParent(population);

        // Assert
        assertNotNull(fittest);
        assertTrue(population.contains(fittest));
    }
}
