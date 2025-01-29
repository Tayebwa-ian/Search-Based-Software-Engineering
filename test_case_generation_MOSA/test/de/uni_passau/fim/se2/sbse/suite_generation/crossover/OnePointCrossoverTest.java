package de.uni_passau.fim.se2.sbse.suite_generation.crossover;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestCase;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestCaseGenerator;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.Statement;
import de.uni_passau.fim.se2.sbse.suite_generation.examples.SimpleExample;
import de.uni_passau.fim.se2.sbse.suite_generation.mutation.TestCaseMutation;
import de.uni_passau.fim.se2.sbse.suite_generation.utils.Pair;
import de.uni_passau.fim.se2.sbse.suite_generation.utils.Randomness;
import de.uni_passau.fim.se2.sbse.suite_generation.utils.Utils;

public class OnePointCrossoverTest {
    private Random random;
    private OnePointCrossover crossover;
    private TestCaseMutation mutation;
    private List<Statement> possibleStatements;
    private TestCase parent2;
    private TestCaseGenerator generator;

    @BeforeEach
    public void setUp() {
        random = Randomness.random();
        crossover = new OnePointCrossover(random);
        possibleStatements = Utils.allStatements(SimpleExample.class);
        mutation = new TestCaseMutation(random, possibleStatements);
        generator = new TestCaseGenerator(random, mutation, crossover, SimpleExample.class);
        parent2 = generator.get();
    }

    @Test
    public void testExpections() {
        assertThrows(
            NullPointerException.class,
            () -> new OnePointCrossover(null)
        );
        assertThrows(
            Exception.class,
            () -> crossover.apply(null)
        );

        assertThrows(
            Exception.class,
            () -> crossover.apply(null, null)
        );
        assertThrows(
            Exception.class,
            () -> crossover.apply(null, parent2)
        );
    }

    @Test
    public void testApplyWithBothParentsHavingStatements() {
        int size = possibleStatements.size();
        List<Statement> statements1 = new ArrayList<>(possibleStatements.subList(0, 2));
        List<Statement> statements2 = new ArrayList<>(possibleStatements.subList(2, size));
        
        TestCase parent1 = new TestCase(mutation, crossover, statements1);
        TestCase parent2 = new TestCase(mutation, crossover, statements2);

        Random fixedRandom = new Random(1); // To ensure a specific crossover point for testing
        OnePointCrossover fixedCrossover = new OnePointCrossover(fixedRandom);

        Pair<TestCase> result = fixedCrossover.apply(parent1, parent2);

        // Check if crossover happened at the expected point (assuming fixed random gives us crossover at 1)
        List<Statement> expectedOffspring1 = new ArrayList<>(statements1.subList(0, 1));
        expectedOffspring1.addAll(statements2.subList(1, statements2.size()));
        List<Statement> expectedOffspring2 = new ArrayList<>(statements2.subList(0, 1));
        expectedOffspring2.addAll(statements1.subList(1, statements1.size()));

        assertEquals(
            expectedOffspring1, result.getFst().getStatements(),
            "First offspring statements should match"
        );
        assertEquals(
            expectedOffspring2,
            result.getSnd().getStatements(),
            "Second offspring statements should match"
        );
    }

    @Test
    public void testToString() {
        assertEquals("OnePointCrossover", crossover.toString());
    }
    
}
