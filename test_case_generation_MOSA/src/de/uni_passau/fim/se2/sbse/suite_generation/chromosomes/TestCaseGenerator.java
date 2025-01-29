package de.uni_passau.fim.se2.sbse.suite_generation.chromosomes;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.Statement;
import de.uni_passau.fim.se2.sbse.suite_generation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_generation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_generation.utils.Utils;

public class TestCaseGenerator implements ChromosomeGenerator<TestCase> {

    private final Mutation<TestCase> mutation;
    private final Crossover<TestCase> crossover;
    private final Random random;
    private final Class<?> classUnderTest;

    /**
     * Generates a random statements for a Test Case
     */
    public TestCaseGenerator(
        Random random,
        final Mutation<TestCase> mutation,
        final Crossover<TestCase> crossover,
        final Class<?> classUnderTest
    ) {
        if (random == null ||
            mutation == null ||
            crossover == null ||
            classUnderTest == null
        ) throw new IllegalArgumentException("Invalid TestCaseGenerator Argument");
        this.random = random;
        this.mutation = mutation;
        this.crossover = crossover;
        this.classUnderTest = classUnderTest;
    }

    /**
     * {@inheritDoc}
     */
    public TestCase get() {
        List<Statement> statements = new ArrayList<>();
        List<Statement> allStatements = Utils.allStatements(classUnderTest);

        // Add the initialization statement
        statements.add(allStatements.get(0));

        int allStatementsSize = allStatements.size();
        int number = random.nextInt(allStatementsSize, 50);

        for (int i = 1; i < number; i++) {
            // choose a random statement to add in the test case
            int rand = random.nextInt(1, allStatementsSize);
            statements.add(allStatements.get(rand));
        }

        return new TestCase(mutation, crossover, statements);
    }
}
