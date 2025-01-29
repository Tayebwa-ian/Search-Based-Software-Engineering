package de.uni_passau.fim.se2.sbse.suite_generation.crossover;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestCase;
import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.Statement;
import de.uni_passau.fim.se2.sbse.suite_generation.utils.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.util.Objects.requireNonNull;

/**
 * Implementation of a one-point crossover for TestCase chromosomes.
 */
public class OnePointCrossover implements Crossover<TestCase> {

    private final Random random;

    /**
     * Constructs a OnePointCrossover with the given Random instance for selecting the crossover point.
     *
     * @param random the Random instance used for crossover point selection
     */
    public OnePointCrossover(Random random) {
        this.random = requireNonNull(random, "Random instance must not be null");
    }

    /**
     * Applies one-point crossover to two parent TestCases to produce two offspring TestCases.
     *
     * @param parent1 the first parent TestCase
     * @param parent2 the second parent TestCase
     * @return a pair of new TestCase instances formed by crossover
     */
    @Override
    public Pair<TestCase> apply(final TestCase parent1, final TestCase parent2) {
        requireNonNull(parent1, "First parent must not be null");
        requireNonNull(parent2, "Second parent must not be null");

        List<Statement> statements1 = parent1.getStatements();
        List<Statement> statements2 = parent2.getStatements();

        int minSize = Math.min(statements1.size(), statements2.size());
        if (minSize == 0) {
            // If either parent has no statements, return copies of the parents.
            return Pair.of(parent1.copy(), parent2.copy());
        }

        // Select a random crossover point within the range of the shorter parent
        int crossoverPoint = random.nextInt(1, minSize);

        // Create new lists for offspring statements
        List<Statement> offspring1Statements = new ArrayList<>(statements1.subList(0, crossoverPoint));
        offspring1Statements.addAll(statements2.subList(crossoverPoint, statements2.size()));

        List<Statement> offspring2Statements = new ArrayList<>(statements2.subList(0, crossoverPoint));
        offspring2Statements.addAll(statements1.subList(crossoverPoint, statements1.size()));

        // Construct and return new TestCase instances
        TestCase offspring1 = new TestCase(parent1.getMutation(), parent1.getCrossover(), offspring1Statements);
        TestCase offspring2 = new TestCase(parent2.getMutation(), parent2.getCrossover(), offspring2Statements);

        return Pair.of(offspring1, offspring2);
    }

    @Override
    public String toString() {
        return "OnePointCrossover";
    }
}
