package de.uni_passau.fim.se2.sbse.suite_minimisation.algorithms;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.Encoding;
import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.EncodingGenerator;
import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.GeneCrossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.BitFlipMutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.selection.BinaryTournamentSelection;
import de.uni_passau.fim.se2.sbse.suite_minimisation.stopping_conditions.MaxFitnessEvaluations;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Utils;

public class Nsga2AlgorithmTest {

    private Random random = new Random();
    private BitFlipMutation mutation = new BitFlipMutation(random);
    private GeneCrossover crossover = new GeneCrossover(random);
    private boolean[][] coverageMatrix = {
        {true, false, true, false, true},
        {false, false, true, false, false},
        {true, false, true, false, false},
        {true, false, false, true, false},
        {false, true, true, false, false}
    };
    int testCases = coverageMatrix.length;

    private Comparator<Encoding> comparator = new Comparator<Encoding>() {
        @Override
        public int compare(Encoding E1, Encoding E2) {
            if (Utils.dominates(E1, E2)) return 1;
            return -1;
        }
    };

    private MaxFitnessEvaluations stoppingCondition = new MaxFitnessEvaluations(200);
    private EncodingGenerator encodingGenerator = new EncodingGenerator(random, mutation, crossover, testCases, coverageMatrix);
    private BinaryTournamentSelection<Encoding> selection = new BinaryTournamentSelection<>(comparator, random);
    
    private Nsga2Algorithm algorithm = new Nsga2Algorithm(stoppingCondition, encodingGenerator, selection, random);
    @Test
    public void testInvalidConstructorInputs () {
        assertThrows(
            IllegalArgumentException.class,
            () -> new Nsga2Algorithm(
                    stoppingCondition,
                    encodingGenerator,
                    null,
                    random
                )
            );
            assertThrows(
            IllegalArgumentException.class,
            () -> new Nsga2Algorithm(
                    stoppingCondition,
                    null,
                    selection,
                    random
                )
            );
            assertThrows(
            IllegalArgumentException.class,
            () -> new Nsga2Algorithm(
                    null,
                    encodingGenerator,
                    selection,
                    random
                )
            );
            assertThrows(
            IllegalArgumentException.class,
            () -> new Nsga2Algorithm(
                    stoppingCondition,
                    encodingGenerator,
                    selection,
                    null
                )
            );
            
    }

    @Test
    public void testGeneratePopulation () {
        int size = 10;
        List<Encoding> population = algorithm.generatePopulation(size);
        assertTrue(population.size() == size);
    }

    @Test
    public void testGenerateOffspring() {
        int size = 100;
        List<Encoding> population = algorithm.generatePopulation(size);
        List<Encoding> offSpring = algorithm.generateOffspring(population);

        assertTrue(population.size() == offSpring.size());
    }

    @Test
    public void testGetStoppingCondition () {
        algorithm.findSolution();
        assertTrue(algorithm.getStoppingCondition().searchMustStop());
    }
    
    @Test
    public void testReturnParetoFront () {
        List<Encoding> front = algorithm.findSolution();
        Encoding c1 = front.get(0);
        Encoding c2 = front.get(1);
        assertFalse(Utils.dominates(c1, c2));  // chromosomes in the same front should not dominate each other
    }
}
