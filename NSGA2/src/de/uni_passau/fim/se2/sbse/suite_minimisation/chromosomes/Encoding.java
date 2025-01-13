package de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes;

import java.util.Arrays;
import java.util.Objects;

import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions.CoverageMaximizationFitness;
import de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions.TestCaseMinimizationFitness;

public class Encoding extends Chromosome<Encoding> {
    /*
     * Implements a chromosome encoding for the test case minimisation problem
     * Here, every chromosome shall represent a test suite (or a subset thereof) that contains at least one test case
     * 
     * A chromosome is represented as an array of 0s and 1s, where 0 means a test case at that index is excluded
     * indices of the array represent the test cases numbers for example [1,0,1,1,0] has 5 testcases from 0 to 4
     */

    private final int[] chromosomeGenes;

    /**
     * The line coverage matrix of each individual
     * it is a list of list(testCases) of boolean values(representing lines in the testcase)
     * true means the line is covered in the testcase and false means otherwise
     */
    public final boolean[][] coverageMatrix;

    /**
     * Stores chromosome's crowding distance value
     */
    private double distance;

    /**
     * Create a chromosome encoding with a given Mutation and Crossover operator
     * 
     * @param mutation a strategy that tells how to perform mutation
     * @param crossover a strategy that tells how to perform crossover
     * @param chromosomeGenes an array of chromosome genes
    */
    public Encoding(
        final Mutation<Encoding> mutation,
        final Crossover<Encoding> crossover,
        final int[] chromosomeGenes,
        final boolean[][] coverageMatrix
    ) {
        super(mutation, crossover);
        if(coverageMatrix == null) {
            throw new IllegalArgumentException("invalid Encoding instatiation values");    
        }
        this.chromosomeGenes = chromosomeGenes;
        this.coverageMatrix = coverageMatrix;
    }

    /**
     * Create a Chromosome from an existing chromosome
     */
    public Encoding(Encoding other) {
        super(other);
        this.chromosomeGenes = other.getGenes();
        this.coverageMatrix = other.getCoverageMatrix();
        this.distance = other.getDistance();
    }

    /**
     * Get the value for a chromosome crowding distance
     * @return      the crowding distance value
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Set the value for a chromosome crowding distance value
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }

    /**
     * Calculates the length of a chromosome array
     * @return the length of the array
     */
    public int chromosomeSize() {
        return chromosomeGenes.length;
    }

    /**
     * Returns the number of test cases that represented in a chromosome.
     *
     * @return the number of test cases in a chromosome(testSuite)
     */
    public int presenTestCases() {
        return Arrays.stream(chromosomeGenes).sum();
    }

    /**
     * returns the coverage matrix
     * @return
     */
    public boolean[][] getCoverageMatrix () {
        return coverageMatrix;
    }

    /**
     * Maximization problem
     * returns coverage fitness(Ist Objective) value of this encoding/chromosome
     */
    public double getCoverageMaxFitness () {
        CoverageMaximizationFitness func = new CoverageMaximizationFitness(coverageMatrix);
        return func.applyAsDouble(this);
    }

    /**
     * Minimization problem
     * returns test case fitness(2nd Objective) value of this encoding/chromosome
     */
    public double getTestCaseMinFitness() {
        TestCaseMinimizationFitness func = new TestCaseMinimizationFitness();
        return func.applyAsDouble(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Encoding copy() {
        Encoding newCopy = new Encoding(
            getMutation(),
            getCrossover(),
            chromosomeGenes,
            coverageMatrix
        );
        return newCopy;
    }

    /**
     * Returns a reference to the underlying internal backing array.
     *
     * @return the genes(test cases) array
     */
    public int[] getGenes(){
        return chromosomeGenes;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object other) {
        if (other == null|| getClass() != other.getClass()) {
            return false;
        }
        final Encoding that = (Encoding) other;
        return (
            getGenes().equals(that.getGenes()) &&
            getMutation().equals(that.getMutation()) &&
            getCrossover().equals(that.getCrossover())
            );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(getMutation(), getCrossover(), getGenes());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Encoding self() {
        return this;
    }
}
