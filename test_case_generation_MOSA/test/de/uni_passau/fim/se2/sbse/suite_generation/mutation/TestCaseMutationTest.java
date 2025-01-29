package de.uni_passau.fim.se2.sbse.suite_generation.mutation;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestCase;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestCaseGenerator;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.Statement;
import de.uni_passau.fim.se2.sbse.suite_generation.crossover.OnePointCrossover;
import de.uni_passau.fim.se2.sbse.suite_generation.examples.Feature;
import de.uni_passau.fim.se2.sbse.suite_generation.examples.SimpleExample;
import de.uni_passau.fim.se2.sbse.suite_generation.utils.Randomness;
import de.uni_passau.fim.se2.sbse.suite_generation.utils.Utils;

public class TestCaseMutationTest {
    private Random random;
    private List<Statement> possibleStatements;
    private TestCaseMutation mutation;
    private OnePointCrossover crossover;
    private TestCaseGenerator generator;
    private Class<?> cut;

    @BeforeEach
    public void setUp() {
        random = Randomness.random();
        possibleStatements = Utils.allStatements(SimpleExample.class);
        mutation = new TestCaseMutation(random, possibleStatements);
        crossover = new OnePointCrossover(random);
        cut = new Feature(10).getClass();
        generator = new TestCaseGenerator(random, mutation, crossover, cut);

        // Create a sample TestCase with at least one statement
        if (possibleStatements.isEmpty()) {
            fail("Need at least one statement for testing");
        }
        List<Statement> initialStatements = new ArrayList<>();
        initialStatements.add(possibleStatements.get(0));
    }

    @Test
    public void testConstructorWithNullArguments() {
        assertThrows(
            NullPointerException.class,
            () -> new TestCaseMutation(null, possibleStatements)
        );
        assertThrows(
            NullPointerException.class,
            () -> new TestCaseMutation(random, null)
        );
    }

    @Test
    public void testApplyAddStatement() {
        Random fixedRandom = new Random(1); // To control randomness for this test
        TestCaseMutation fixedMutation = new TestCaseMutation(fixedRandom, possibleStatements);
        for (int i = 0; i < 5; i++) {
            TestCase tc = generator.get();
            TestCase result = fixedMutation.apply(tc);

            List<Statement> mutatedStatements = result.getStatements();
            assertEquals(
                tc.getStatements().size(),
                mutatedStatements.size(),
                "Size should remain the same due to removal before add"
            );
            assertFalse(tc.getStatements().equals(mutatedStatements));
        }
    }

    @Test
    public void testApplyReplaceStatement() {
        Random fixedRandom = new Random(0); // To control randomness for this test
        TestCaseMutation fixedMutation = new TestCaseMutation(fixedRandom, possibleStatements);
        for (int i = 0; i < 10; i++) {
            TestCase tc = generator.get();
            TestCase result = fixedMutation.apply(tc);

            List<Statement> mutatedStatements = result.getStatements();
            assertEquals(
                tc.getStatements().size(),
                mutatedStatements.size(),
                "Size should remain the same"
            );
            assertFalse(tc.getStatements().equals(mutatedStatements));
        }
    }

    @Test
    public void testToString() {
        assertEquals("ReplaceStatementMutation", mutation.toString());
    }
}
