package de.uni_passau.fim.se2.sbse.suite_generation.selection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.Chromosome;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestCase;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.Statement;

public class RankSelectionTest {
    private RankSelection<TestChromosome> rankSelection;
    private List<TestChromosome> population;
    private final Random random = new Random(42); // Fixed seed for reproducibility

    // A simple implementation of the Chromosome interface for testing
    static class TestChromosome extends Chromosome<TestChromosome>  {
        private final int fitness;

        public TestChromosome(int fitness) {
            this.fitness = fitness;
        }

        public int getFitness() {
            return fitness;
        }

        @Override
        public TestChromosome copy() {
            return new TestChromosome(fitness);
        }

        @Override
        public Map<Integer, Double> call() {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        public List<Statement> getStatements() {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        public boolean equals(final Object other) {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        public int hashCode() {
            return 0;
        }

        /**
         * {@inheritDoc}
         */
        public TestChromosome self() {
            return this;
        }

    }

    @BeforeEach
    public void setUp() {
        // Comparator based on fitness
        Comparator<TestChromosome> comparator = Comparator.comparingInt(TestChromosome::getFitness);
        // Create a population of 5 chromosomes with varying fitness
        population = List.of(
                new TestChromosome(1),
                new TestChromosome(3),
                new TestChromosome(2),
                new TestChromosome(5),
                new TestChromosome(4)
        );
        rankSelection = new RankSelection<>(comparator, 5, 1.5, random);
    }

    @Test
    public void testApply_ValidPopulation() {
        TestChromosome selected = rankSelection.apply(population);
        assertNotNull(selected, "The selected chromosome should not be null");
        assertTrue(selected instanceof TestChromosome);
    }

    @Test
    public void testApply_PopulationSorting() {
        List<TestChromosome> sortedPopulation = population.stream()
                .sorted(Comparator.comparingInt(TestChromosome::getFitness))
                .toList();

        TestChromosome selected = rankSelection.apply(population);
        assertTrue(
            sortedPopulation.contains(selected),
            "The selected chromosome must belong to the population"
        );
    }

    @Test
    public void testApply_InvalidPopulationSize() {
        List<TestChromosome> smallerPopulation = List.of(
                new TestChromosome(1),
                new TestChromosome(2)
        );

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> rankSelection.apply(smallerPopulation)
        );
        assertEquals(
            "Population must not be null and must have the fixed size: 5",
            exception.getMessage()
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> rankSelection.apply(null)
        );
    }

    @Test
    public void testConstructor_InvalidSize() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () ->new RankSelection<>(
                Comparator.comparingInt(TestChromosome::getFitness),
                1,
                1.5,
                random
            )
        );
        assertEquals("Population size must be greater than 1.", exception.getMessage());
    }

    @Test
    public void testConstructor_InvalidBiasLow() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () ->new RankSelection<>(
                Comparator.comparingInt(TestChromosome::getFitness),
                5,
                0.5,
                random
            )
        );
        assertEquals("Bias must be in the range [1, 2].", exception.getMessage());
    }

    @Test
    public void testConstructor_InvalidBiasHigh() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, () ->new RankSelection<>(
                Comparator.comparingInt(TestChromosome::getFitness),
                5, 2.5, random
            )
        );
        assertEquals("Bias must be in the range [1, 2].", exception.getMessage());
    }

    @Test
    public void testApply_Randomness() {
        List<TestChromosome> selectedChromosomes = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            selectedChromosomes.add(rankSelection.apply(population));
        }

        assertTrue(selectedChromosomes.stream().allMatch(population::contains),
                "All selected chromosomes should belong to the population");
    }

    @Test
    public void testMoreInvalidValues() {
        assertThrows(
            NullPointerException.class,
            () ->new RankSelection<>(Comparator.comparingInt(TestChromosome::getFitness), 5, 1.5, null)
        );

        assertThrows(
            NullPointerException.class,
            () ->new RankSelection<>(null, 5, 1.5, random)
        );
    }
}
