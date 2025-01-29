package de.uni_passau.fim.se2.sbse.suite_generation.mutation;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestCase;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.util.Objects.requireNonNull;

/**
 * Implements a mutation strategy where one statement might be randomly replaced with another.
 */
public class TestCaseMutation implements Mutation<TestCase> {

    private final Random random;
    private final List<Statement> possibleStatements;

    /**
     * Constructs a TestCaseNutation with a Random instance and a list of possible statements to use for mutation.
     *
     * @param random the Random instance used for mutation
     * @param possibleStatements a list of statements that can be used to replace existing statements during mutation
     */
    public TestCaseMutation(Random random, List<Statement> possibleStatements) {
        this.random = requireNonNull(random, "Random instance must not be null");
        this.possibleStatements = new ArrayList<>(requireNonNull(possibleStatements, "List of possible statements must not be null"));
    }

    /**
     * Applies mutation to the given TestCase 
     * By potentially replacing, adding or removing a statements.
     *
     * @param testCase the TestCase to mutate
     * @return a new TestCase with one statement potentially replaced
     */
    @Override
    public TestCase apply(final TestCase testCase) {
        requireNonNull(testCase, "TestCase to mutate must not be null");

        List<Statement> statements = testCase.getStatements();

        // Create a copy of the original statements to avoid modifying in-place
        List<Statement> mutatedStatements = new ArrayList<>(statements);

        int mutationType = random.nextInt(2); // Choose a mutation type: add, replace, or remove
        switch (mutationType) {
            case 0: // Add a random statement
                Statement newStatement = possibleStatements.get(random.nextInt(possibleStatements.size()));
                mutatedStatements.remove(mutatedStatements.size()-1);
                mutatedStatements.add(newStatement);
                break;
            case 1: // Replace a random statement
                if (!mutatedStatements.isEmpty()) {
                    int indexToReplace = random.nextInt(mutatedStatements.size());
                    Statement replacementStatement = possibleStatements.get(random.nextInt(possibleStatements.size()));
                    mutatedStatements.set(indexToReplace, replacementStatement);
                }
                break;
        }

        // Return the mutated TestCase
        return new TestCase(testCase.getMutation(), testCase.getCrossover(), mutatedStatements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "ReplaceStatementMutation";
    }
}
