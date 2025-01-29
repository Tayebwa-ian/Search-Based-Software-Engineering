package de.uni_passau.fim.se2.sbse.suite_generation.chromosomes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.Statement;
import de.uni_passau.fim.se2.sbse.suite_generation.crossover.OnePointCrossover;
import de.uni_passau.fim.se2.sbse.suite_generation.examples.SimpleExample;
import de.uni_passau.fim.se2.sbse.suite_generation.mutation.TestCaseMutation;
import de.uni_passau.fim.se2.sbse.suite_generation.utils.Pair;
import de.uni_passau.fim.se2.sbse.suite_generation.utils.Utils;

public class TestCaseTest {

    private SimpleExample cut;
    private List<Statement> allStatements;
    private Random random;
    private OnePointCrossover crossover;
    private TestCaseMutation mutation;

    @BeforeEach
    public void setUp() {
        cut = new SimpleExample(10);
        allStatements = Utils.allStatements(cut.getClass());
        random = new Random();
        crossover = new OnePointCrossover(random);
        mutation = new TestCaseMutation(random, allStatements);
    }

    @Test
    public void testConstructorWithValidStatements() throws IllegalArgumentException {
        List<Statement> statements = new ArrayList<>(allStatements.subList(0, 1)); // Take at least one statement
        TestCase testCase = new TestCase(mutation, crossover, statements);
        assertNotNull(testCase);
        assertEquals(statements, testCase.getStatements());
    }

    @Test
    public void testConstructorWithEmptyStatements() {
        List<Statement> statements = new ArrayList<>();
        assertThrows(
            IllegalArgumentException.class,
            () -> new TestCase(mutation, crossover, statements)
        );
    }

    @Test
    public void testConstructorWithNullStatements() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new TestCase(mutation, crossover, null)
        );
    }

    @Test
    public void testCallWithNoExceptions() throws RuntimeException {
        List<Statement> statements = new ArrayList<>(allStatements.subList(0, 1)); // Assuming at least one safe statement
        TestCase testCase = new TestCase(mutation, crossover, statements);
        Map<Integer, Double> result = testCase.call();
        assertNotNull(result); // Assuming call() returns something even if no branches are covered
    }

    @Test
    public void testCallWithException() {
        // Mock a statement that throws an exception
        List<Statement> statements = new ArrayList<Statement>() {{
            add(new Statement() {
                @Override
                public void run() {
                    throw new RuntimeException("Test exception");
                }
            });
        }};
        TestCase testCase = new TestCase(mutation, crossover, statements);
        assertThrows(
            RuntimeException.class,
            testCase::call, "Should throw exception due to statement execution failure"
        );
    }

    @Test
    public void testGetStatements() {
        List<Statement> statements = new ArrayList<>(allStatements.subList(0, 2)); // At least two statements
        TestCase testCase = new TestCase(mutation, crossover, statements);
        assertEquals(statements, testCase.getStatements());
    }

    @Test
    public void testCopy() {
        List<Statement> statements = new ArrayList<>(allStatements.subList(0, 1));
        TestCase original = new TestCase(mutation, crossover, statements);
        
        TestCase copy = original.copy();
        assertEquals(original.getStatements(), copy.getStatements());
        assertNotSame(original, copy, "Should be different instances");
    }

    @Test
    public void testEqualsAndHashCode() {
        List<Statement> statements = new ArrayList<>(allStatements.subList(0, 1));
        TestCase testCase1 = new TestCase(mutation, crossover, statements);
        TestCase testCase2 = new TestCase(mutation, crossover, statements);

        assertEquals(testCase1, testCase2);
        assertEquals(testCase1.hashCode(), testCase2.hashCode());

        // Different statements
        List<Statement> differentStatements = new ArrayList<>(allStatements.subList(1, 2));
        TestCase testCase3 = new TestCase(mutation, crossover, differentStatements);
        assertNotEquals(testCase1, testCase3);
    }

    @Test
    public void testDensity() {
        TestCase testCase = new TestCase(
            mutation,
            crossover,
            new ArrayList<>(allStatements.subList(0, 1))
        );
        testCase.setDensity(0.5);
        assertEquals(0.5, testCase.getDensity(), 0.0001); // Delta for floating-point comparison
    }

    @Test
    public void testSize() {
        TestCase testCase = new TestCase(
            mutation,
            crossover,
            new ArrayList<>(allStatements.subList(0, 1))
        );
        assertEquals(testCase.size(), 1);
    }

    @Test
    public void testMutation () {
        TestCase testCase = new TestCase(
            mutation,
            crossover,
            new ArrayList<>(allStatements.subList(0, 1))
        );
        TestCase other = testCase.mutate();
        TestCase testCase2 = testCase;
        assertFalse(testCase.equals(other));
        assertTrue(testCase.equals(testCase2));
    }

    @Test
    public void testCrossover() {
        TestCase testCase = new TestCase(
            mutation,
            crossover,
            new ArrayList<>(allStatements.subList(0, 3))
        );
        TestCase testCase2 = new TestCase(
            mutation,
            crossover,
            new ArrayList<>(allStatements.subList(0, 5))
        );
        Pair<TestCase> pair = testCase.crossover(testCase2);
        assertTrue(pair instanceof Pair);
    }

    @Test
    public void testIterator() {
        List<Statement> statements = new ArrayList<>(allStatements.subList(0, 1));
        TestCase testCase = new TestCase(mutation, crossover, statements);
        assertTrue(testCase.iterator() instanceof Iterator);
    }
}
