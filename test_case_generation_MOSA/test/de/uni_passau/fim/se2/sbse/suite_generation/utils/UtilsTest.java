package de.uni_passau.fim.se2.sbse.suite_generation.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestCase;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestCaseGenerator;
import de.uni_passau.fim.se2.sbse.suite_generation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.Branch;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.Statement;

import org.junit.jupiter.api.Test;
import java.util.*;

class UtilsTest {

    @Test
    public void testInitializePopulation() {
        // Mock dependencies
        TestCaseGenerator mockGenerator = mock(TestCaseGenerator.class);
        TestCase mockTestCase = mock(TestCase.class);
        when(mockGenerator.get()).thenReturn(mockTestCase);

        // Test the method
        List<TestCase> population = Utils.initializePopulation(5, mockGenerator);

        // Assertions
        assertEquals(5, population.size());
        assertTrue(population.stream().allMatch(tc -> tc == mockTestCase));

        // Verify interactions
        verify(mockGenerator, times(5)).get();
    }

    @Test
    public void testEvaluateFitness() {
        // Mock dependencies
        TestCase testCase1 = mock(TestCase.class);
        TestCase testCase2 = mock(TestCase.class);
        Branch branch1 = mock(Branch.class);
        Branch branch2 = mock(Branch.class);
        FitnessFunction<TestCase> fitnessFunction1 = mock(FitnessFunction.class);
        FitnessFunction<TestCase> fitnessFunction2 = mock(FitnessFunction.class);

        when(fitnessFunction1.applyAsDouble(testCase1)).thenReturn(0.5);
        when(fitnessFunction1.applyAsDouble(testCase2)).thenReturn(0.8);
        when(fitnessFunction2.applyAsDouble(testCase1)).thenReturn(1.0);
        when(fitnessFunction2.applyAsDouble(testCase2)).thenReturn(0.2);

        Map<Branch, FitnessFunction<TestCase>> fitnessFunctions = Map.of(
                branch1, fitnessFunction1,
                branch2, fitnessFunction2
        );

        List<TestCase> population = List.of(testCase1, testCase2);
        List<Branch> branches = List.of(branch1, branch2);

        // Test the method
        Map<TestCase, Map<Branch, Double>> fitnessMap = Utils.evaluateFitness(population, branches, fitnessFunctions);

        // Assertions
        assertEquals(2, fitnessMap.size());
        assertEquals(0.5, fitnessMap.get(testCase1).get(branch1));
        assertEquals(0.8, fitnessMap.get(testCase2).get(branch1));
        assertEquals(1.0, fitnessMap.get(testCase1).get(branch2));
        assertEquals(0.2, fitnessMap.get(testCase2).get(branch2));

        // Verify interactions
        verify(fitnessFunction1, times(2)).applyAsDouble(any(TestCase.class));
        verify(fitnessFunction2, times(2)).applyAsDouble(any(TestCase.class));
    }

    @Test
    public void testDominates() {
        // Mock dependencies
        TestCase p = mock(TestCase.class);
        TestCase q = mock(TestCase.class);
        Branch branch1 = mock(Branch.class);
        Branch branch2 = mock(Branch.class);

        Map<Branch, Double> pFitness = Map.of(branch1, 0.5, branch2, 0.3);
        Map<Branch, Double> qFitness = Map.of(branch1, 0.6, branch2, 0.4);

        Map<TestCase, Map<Branch, Double>> fitnessMap = Map.of(p, pFitness, q, qFitness);
        List<Branch> branches = List.of(branch1, branch2);

        // Test the method
        boolean result = Utils.dominates(p, q, fitnessMap, branches);

        // Assertions
        assertTrue(result);
    }

    @Test
    public void testNonDominatedSorting() {
        // Mock dependencies
        TestCase t1 = mock(TestCase.class);
        TestCase t2 = mock(TestCase.class);
        TestCase t3 = mock(TestCase.class);
        Branch branch1 = mock(Branch.class);
        Branch branch2 = mock(Branch.class);

        Map<TestCase, Map<Branch, Double>> fitnessMap = Map.of(
                t1, Map.of(branch1, 0.2, branch2, 0.1),
                t2, Map.of(branch1, 0.3, branch2, 0.4),
                t3, Map.of(branch1, 0.1, branch2, 0.05)
        );

        List<TestCase> population = List.of(t1, t2, t3);
        List<Branch> branches = List.of(branch1, branch2);

        // Test the method
        List<List<TestCase>> fronts = Utils.nonDominatedSorting(population, fitnessMap, branches);

        // Assertions
        assertEquals(3, fronts.size());
        assertTrue(fronts.get(0).contains(t3));
        assertTrue(fronts.get(1).contains(t1));
    }

    @Test
    public void testUpdateArchive() {
        // Mock dependencies
        TestCase t1 = mock(TestCase.class);
        TestCase t2 = mock(TestCase.class);
        Branch branch1 = mock(Branch.class);
        Branch branch2 = mock(Branch.class);

        Map<TestCase, Map<Branch, Double>> fitnessMap = Map.of(
                t1, Map.of(branch1, 0.2, branch2, 0.1),
                t2, Map.of(branch1, 0.3, branch2, 0.4)
        );

        List<TestCase> population = List.of(t1, t2);
        List<TestCase> archive = new ArrayList<>();
        List<Branch> branches = List.of(branch1, branch2);

        // Test the method
        Utils.updateArchive(population, fitnessMap, archive, branches);

        // Assertions
        assertEquals(1, archive.size());
        assertTrue(archive.contains(t1));
    }

    @Test
    public void testAllStatements() {
        // Test with a sample class
        class TestClass {
            public int field;
            public TestClass() {}
            public void method(int value) {}
        }

        // Test the method
        List<Statement> statements = Utils.allStatements(TestClass.class);

        // Assertions
        assertNotNull(statements);
        assertFalse(statements.isEmpty());
    }

    @Test
    public void testGenerateRandomValueForPrimitives() {
        // Test boolean
        Object result = Utils.generateRandomValue(boolean.class, null);
        assertTrue(result instanceof Boolean, "Result should be of type Boolean");

        // Test byte
        result = Utils.generateRandomValue(byte.class, null);
        assertTrue(result instanceof Byte, "Result should be of type Byte");

        // Test char
        result = Utils.generateRandomValue(char.class, null);
        assertTrue(result instanceof Character, "Result should be of type Character");

        // Test short
        result = Utils.generateRandomValue(short.class, null);
        assertTrue(result instanceof Short, "Result should be of type Short");

        // Test int
        result = Utils.generateRandomValue(int.class, null);
        assertTrue(result instanceof Integer, "Result should be of type Integer");
        int intResult = (int) result;
        assertTrue(intResult >= -120 && intResult < 100, "Result should be between -120 and 99");

        // Test long
        result = Utils.generateRandomValue(long.class, null);
        assertTrue(result instanceof Long, "Result should be of type Long");

        // Test float
        result = Utils.generateRandomValue(float.class, null);
        assertTrue(result instanceof Float, "Result should be of type Float");

        // Test double
        result = Utils.generateRandomValue(double.class, null);
        assertTrue(result instanceof Double, "Result should be of type Double");
    }

    @Test
    public void testGenerateRandomValueForNonPrimitives() {
        // Test String
        Object result = Utils.generateRandomValue(String.class, null);
        assertTrue(result instanceof String, "Result should be of type String");
        assertFalse(((String) result).isEmpty(), "Generated String should not be empty");

        // Test Integer
        result = Utils.generateRandomValue(Integer.class, null);
        assertTrue(result == null || result instanceof Integer, "Result should be null or of type Integer");

        // Test Long
        result = Utils.generateRandomValue(Long.class, null);
        assertTrue(result == null || result instanceof Long, "Result should be null or of type Long");

        // Test Float
        result = Utils.generateRandomValue(Float.class, null);
        assertTrue(result == null || result instanceof Float, "Result should be null or of type Float");

        // Test Double
        result = Utils.generateRandomValue(Double.class, null);
        assertTrue(result == null || result instanceof Double, "Result should be null or of type Double");
    }

    @Test
    public void testGenerateRandomValueForCustomObject() {
        // Test with a sample class
        class MyClass {
            public MyClass(String str) {}
        }
        // Test with a custom object
        MyClass obj = new MyClass("test");
        Object result = Utils.generateRandomValue(MyClass.class, obj);

        assertTrue(result == null || result instanceof MyClass, "Result should be null or of type MyClass");
        if (result != null) {
            assertEquals(obj, result, "Generated object should match the provided object");
        }
    }

    @Test
    public void testGenerateRandomValueForUnknownType() {
        // Test with a type that is not handled explicitly
        Object result = Utils.generateRandomValue(Random.class, null);
        assertNull(result, "Result should be null for unsupported types");
    }
}
