package de.uni_passau.fim.se2.sbse.suite_generation.chromosomes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.Statement;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.StatementRepresenation.InitializationStatement;
import de.uni_passau.fim.se2.sbse.suite_generation.crossover.OnePointCrossover;
import de.uni_passau.fim.se2.sbse.suite_generation.examples.Feature;
import de.uni_passau.fim.se2.sbse.suite_generation.mutation.TestCaseMutation;

public class TestCaseGeneratorTest {

    private Random random;
    private OnePointCrossover crossover;
    private TestCaseMutation mutation;
    private Class<?> classUnderTest;

    @BeforeEach
    public void setUp() {
        random = new Random(42);
        mutation = new TestCaseMutation(random, new ArrayList<>());
        crossover = new OnePointCrossover(random);
        classUnderTest = new Feature(10).getClass();
    }

    @Test
    public void testConstructorWithNullArguments() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new TestCaseGenerator(null, mutation, crossover, classUnderTest)
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> new TestCaseGenerator(random, null, crossover, classUnderTest)
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> new TestCaseGenerator(random, mutation, null, classUnderTest)
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> new TestCaseGenerator(random, mutation, crossover, null)
        );
    }

    @Test
    public void testGetMethod() {
        TestCaseGenerator generator = new TestCaseGenerator(
            random,
            mutation,
            crossover,
            classUnderTest
        );

        TestCase generatedTestCase = generator.get();
        assertNotNull(generatedTestCase, "Generated TestCase should not be null");
        
        List<Statement> statements = generatedTestCase.getStatements();
        assertFalse(
            statements.isEmpty(),
            "Generated TestCase should have at least one statement"
        );
        assertTrue(
            statements.get(0) instanceof InitializationStatement,
            "First statement should be the initialization statement"
        );

        // Checking if the number of statements is within the expected range
        int statementsCount = statements.size();
        assertTrue(
            statementsCount >= 2,
            "Should have at least one init statement plus one additional"
        );
        assertTrue(statementsCount <= 50, "Should not exceed 50 statements");
    }

    @Test
    public void testRandomStatementSelection() {
        // This test checks if the statements selected are indeed random and from the available list
        TestCaseGenerator generator = new TestCaseGenerator(
            random,
            mutation,
            crossover,
            classUnderTest
        );

        TestCase tc1 = generator.get();
        TestCase tc2 = generator.get();

        // They should not be identical since selection is random
        assertNotEquals(tc1.getStatements(), tc2.getStatements());
        
        // They should be of different size
        assertFalse(tc1.size() == tc2.size());
    }
}
