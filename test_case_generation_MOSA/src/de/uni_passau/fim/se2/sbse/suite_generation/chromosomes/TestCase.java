package de.uni_passau.fim.se2.sbse.suite_generation.chromosomes;

import java.util.Objects;
import java.util.List;
import java.util.Map;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.statements.Statement;
import de.uni_passau.fim.se2.sbse.suite_generation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.BranchTracer;
import de.uni_passau.fim.se2.sbse.suite_generation.mutation.Mutation;

public class TestCase extends Chromosome<TestCase> {

    private List<Statement> statements;
    private double density;
    /**
     * Constructs a new chromosome, using the given mutation and crossover operators for offspring
     * creation.
     *
     * @param mutation  a strategy that tells how to perform mutation, not {@code null}
     * @param crossover a strategy that tells how to perform crossover, not {@code null}
     * @param statements a list of statements that will be executed
     */
    public TestCase(
        final Mutation<TestCase> mutation,
        final Crossover<TestCase> crossover,
        final List<Statement> statements
        ) throws IllegalArgumentException {
            super(mutation, crossover);
            if (statements == null || statements.isEmpty()) {
                throw new IllegalArgumentException("Invalid statements: must not be null or empty");
            }
            this.statements = statements;
    }

    /**
     * {@inheritDoc}
     */
    public Map<Integer, Double> call() throws RuntimeException {
        // Clear any previous branch traces
        BranchTracer tracer = BranchTracer.getInstance();
        tracer.clear();

        // Execute each statement
        for (Statement statement : statements) {
            try {
                statement.run();
            } catch (Exception e) {
                // If an exception occurs during execution, throw a RuntimeException
                throw new RuntimeException("Failed to execute statement", e);
            }
        }

        // Return the branch distances after execution
        return tracer.getDistances();
    }

    /**
     * {@inheritDoc}
     */
    public List<Statement> getStatements() {
        return statements;
    }

    /**
     * {@inheritDoc}
     */
    public TestCase copy() {
        // implements a deep copy of the testcase
        TestCase testCase = new TestCase(getMutation(), getCrossover(), getStatements());
        return testCase;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(final Object other) {
        if (other == null|| getClass() != other.getClass()) {
            return false;
        }
        final TestCase that = (TestCase) other;
        return (
            getStatements().equals(that.getStatements()) &&
            getMutation().equals(that.getMutation()) &&
            getCrossover().equals(that.getCrossover())
            );
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return Objects.hash(getMutation(), getCrossover(), getStatements());
    }

    /**
     * {@inheritDoc}
     */
    public TestCase self() {
        return this;
    }

     /**
      * A getter for the density value
      * @return
      */
     public double getDensity() {
        return density;
    }

    /**
     * Sets the Density value for each TestCase
     * @param density
     */
    public void setDensity(double density) {
        this.density = density;
    }

}
