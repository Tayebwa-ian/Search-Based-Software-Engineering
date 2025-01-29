package de.uni_passau.fim.se2.sbse.suite_generation.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestCase;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestCaseGenerator;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.Statement;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.StatementRepresenation.FieldAssignmentStatement;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.StatementRepresenation.InitializationStatement;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.StatementRepresenation.MethodCallStatement;
import de.uni_passau.fim.se2.sbse.suite_generation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.Branch;

/**
 * Store common functions used in both algorithms
 */
public class Utils {
    
    /**
     * Intialize the population to be used
     * @param size: Size of the population to be generated
     * @param generator: A generator of test caseses with random statements
     * @return a list of test cases
     */
    public static List<TestCase> initializePopulation(int size, TestCaseGenerator generator) {
        List<TestCase> population = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            population.add(generator.get());
        }
        return population;
    }

    /**
     * Calculate Branch distance for every branch and for each test case
     * @param population: A population of possible TestCases
     * @param targetBranches: Branches to be evaluated against the Testcases
     * @param fitnessFunctions: Function to use for evaluation
     * @return A mapping of testcases and their fitness values against all branches
     */
    public static Map<TestCase, Map<Branch, Double>> evaluateFitness(
        List<TestCase> population,
        List<Branch> targetBranches,
        Map<Branch, FitnessFunction<TestCase>> fitnessFunctions
        ) {
        Map<TestCase, Map<Branch, Double>> fitnessMap = new HashMap<>();

        for (TestCase testCase : population) {
            Map<Branch, Double> branchFitness = new HashMap<>();
            for (Branch branch : targetBranches) {
                double fitness = fitnessFunctions.get(branch).applyAsDouble(testCase);
                branchFitness.put(branch, fitness);
            }
            fitnessMap.put(testCase, branchFitness);
        }

        return fitnessMap;
    }

    /**
     * Return True if P dominates q
     * 
     * @param p: The first TestCase to use for Comparison
     * @param q: The first TestCase to use for Comparison
     * @param fitnessMap: A mapping of TestCase IDs Branches and their fitness values
     * @param targetBranches: The target braches to be considered
     * @return True  of p dominates q, otherwise false
     */
    public static boolean dominates(
        TestCase p,
        TestCase q,
        Map<TestCase, Map<Branch, Double>> fitnessMap,
        List<Branch> targetBranches
        ) {
        Map<Branch, Double> pFitness = fitnessMap.get(p);
        Map<Branch, Double> qFitness = fitnessMap.get(q);

        boolean betterInOne = false;
        for (Branch branch : targetBranches) {
            if (pFitness.get(branch) > qFitness.get(branch)) return false;
            if (pFitness.get(branch) < qFitness.get(branch)) betterInOne = true;
        }
        return betterInOne;
    }

    /**
     * Returns a non dominated list of lists of testcases
     * Uses estimated density by grouping solutions into sub-vectors based on dominance rank.
     * @param population: A population of possible TestCases
     * @param fitnessMap: A mapping of TestCase IDs Branches and their fitness values
     * @param targetBranches: The target braches to be considered
     * @return A non dominated list of lists of testcases according to ranks
     */
    public static List<List<TestCase>> nonDominatedSorting(
        List<TestCase> population,
        Map<TestCase, Map<Branch, Double>> fitnessMap,
        List<Branch> targetBranches
        ) {
        List<List<TestCase>> fronts = new ArrayList<>();
        Map<TestCase, Integer> dominationCount = new HashMap<>();
        Map<TestCase, List<TestCase>> dominatedBy = new HashMap<>();

        for (TestCase p : population) {
            dominationCount.put(p, 0);
            dominatedBy.put(p, new ArrayList<>());

            for (TestCase q : population) {
                if (dominates(p, q, fitnessMap, targetBranches)) {
                    dominatedBy.get(p).add(q);
                } else if (dominates(q, p, fitnessMap, targetBranches)) {
                    dominationCount.put(p, dominationCount.get(p) + 1);
                }
            }

            if (dominationCount.get(p) == 0) {
                if (fronts.isEmpty()) fronts.add(new ArrayList<>());
                fronts.get(0).add(p);
            }
        }

        int frontIndex = 0;
        while (frontIndex < fronts.size()) {
            List<TestCase> nextFront = new ArrayList<>();
            for (TestCase p : fronts.get(frontIndex)) {
                for (TestCase q : dominatedBy.get(p)) {
                    dominationCount.put(q, dominationCount.get(q) - 1);
                    if (dominationCount.get(q) == 0) {
                        nextFront.add(q);
                    }
                }
            }
            if (!nextFront.isEmpty()) {
                fronts.add(nextFront);
            }
            frontIndex++;
        }

        return fronts;
    }

    /**
     * 
     * @param population: A population of possible TestCases
     * @param fitnessMap: A mapping of TestCase IDs Branches and their fitness values
     * @param targetBranches: The target braches to be considered
     * @param archive: An population of the best test cases chosen from evolving population
     */
    public static void updateArchive(
        List<TestCase> population,
        Map<TestCase, Map<Branch, Double>> fitnessMap,
        List<TestCase> archive,
        List<Branch> targetBranches
        ) {
            TestCase candidate = population.get(0);
            for (int i = 1; i < population.size(); i++) {
                if (dominates(population.get(i), candidate, fitnessMap, targetBranches)) {
                    candidate = population.get(i);
                }
            }

            archive.add(candidate);
    }

    /**
     * extracts the constructor, fields and methods from a class under test
     * and constructs valid statement representations for each
     * @param classUnderTest: This is the class from which statements will be derived
     * @return a list of valid statement representations
     */
    public static List<Statement> allStatements(Class<?> classUnderTest){
        List<Statement> allStatements = new ArrayList<>();
        // Use a full qualified class name of a class under test
        try {
            Class<?> clazz = classUnderTest;
            String className = clazz.getName();
            // Collect all constructors, fields, and methods

            Constructor<?>[] constructors = clazz.getDeclaredConstructors(); // Only declared constructors
            for (Constructor<?> constructor : constructors) {
                Object[] params = generateRandomParameters(constructor.getParameterTypes(), null);
                Object instance = constructor.newInstance(params);

                // Add constructors
                if (Modifier.isPublic(constructor.getModifiers())) { // Filter out non-public constructors
                    try {
                        Statement statement = new InitializationStatement(instance, constructor, className, params);
                        allStatements.add(statement);
                    } catch (Exception e) {
                        System.err.println("Failed to invoke constructor: " + constructor.getName());
                    }
                }

                Object obj = instance;

                // Add fields
                Field[] fields = clazz.getDeclaredFields(); // Only declared fields
                for (Field field : fields) {
                    if (!Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers())) { // Ignore static and private fields
                        try {
                            Object value = generateRandomValue(field.getType(), null);
                            field.setAccessible(true); // Allow access to private fields if needed
                            Statement statement = new FieldAssignmentStatement(obj, field, value);
                            allStatements.add(statement);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                // Add methods
                Method[] methods = clazz.getDeclaredMethods(); // Only declared methods
                for (Method method : methods) {
                    if (Modifier.isPublic(method.getModifiers())) { // Filter out private methods
                        try {
                            Object[] parameters = generateRandomParameters(method.getParameterTypes(), obj);
                            Statement statement = new MethodCallStatement(obj, method, parameters);
                            allStatements.add(statement);
                        } catch (Exception e) {
                            System.err.println("Failed to process method: " + method.getName());
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error while processing class: " + classUnderTest.getName());
        }
        return allStatements;
    }

    /**
     * Generates random values for the given parameter types.
     *
     * @param parameterTypes Array of parameter types.
     * @param obj The object of the class under test
     * @return Array of randomly generated parameter values.
     */
    private static Object[] generateRandomParameters(Class<?>[] parameterTypes, Object obj) {
        Object[] parameters = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            parameters[i] = generateRandomValue(parameterTypes[i], obj);
        }
        return parameters;
    }

    /**
     * Generates a random value for a given type.
     *
     * @param type The type for which a random value will be generated.
     * @param obj The object of the class under test
     * @return A randomly generated value of the given type.
     */
    public static Object generateRandomValue(Class<?> type, Object obj) {
        Random random = Randomness.random();

        if (type.isPrimitive()) {
            if (type == boolean.class) return random.nextBoolean();
            if (type == byte.class) return (byte) random.nextInt(256);
            if (type == char.class) return (char) (random.nextInt(126 - 32 + 1) + 32);
            if (type == short.class)
                return (short) (random.nextInt(2048) - 1024); // Generate short between -1024 and 1023
            if (type == int.class) return random.nextInt(-120, 100); // Generate int between -120 and 99
            if (type == long.class) return random.nextLong(2048) - 1024; // Generate long between -1024 and 1023
            if (type == float.class) return random.nextFloat() * 2048 - 1024; // Generate float between -1024 and 1023
            if (type == double.class)
                return random.nextDouble() * 2048 - 1024; // Generate double between -1024 and 1023
        } else if (type == String.class) {
            int length = Randomness.random().nextInt(10) + 1;
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                sb.append((char) (Randomness.random().nextInt(95) + 32));
            }
            return sb.toString(); // Generate a string
        } else if (type == Integer.class) {
            return random.nextBoolean() ? random.nextInt(2048) - 1024 : null; // Generate Integer between -1024 and 1023 or null
        } else if (type == Long.class) {
            return random.nextBoolean() ? random.nextLong(2048) - 1024 : null; // Generate Long between -1024 and 1023 or null
        } else if (type == Float.class) {
            return random.nextBoolean() ? random.nextFloat() * 2048 - 1024 : null; // Generate Float between -1024 and 1023 or null
        } else if (type == Double.class) {
            return random.nextBoolean() ? random.nextDouble() * 2048 - 1024 : null; // Generate Double between -1024 and 1023 or null
        } else if (obj != null && type == obj.getClass()) {
            return random.nextBoolean() ? obj : null;
        }

        return null; // For other reference types

    }
}
