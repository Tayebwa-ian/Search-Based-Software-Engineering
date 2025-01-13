package de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes;

import java.util.Random;

import de.uni_passau.fim.se2.sbse.suite_minimisation.crossover.Crossover;
import de.uni_passau.fim.se2.sbse.suite_minimisation.mutation.Mutation;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Utils;

public class EncodingGenerator implements ChromosomeGenerator<Encoding> {

    private final Random random;
    private final Mutation<Encoding> mutation;
    private final Crossover<Encoding> crossover;
    private final int testcases;
    private final boolean[][] coverageMatrix;

    /**
     * Generates a random permutation of a chromosome Encoding
     * Testcases as 0s and 1s in the encoding
     */
    public EncodingGenerator(
        Random random,
        Mutation<Encoding> mutation,
        Crossover<Encoding> crossover,
        int testcases,
        boolean[][] coverageMatrix
    ) {
        if(
            mutation == null ||
            testcases == 0 ||
            crossover == null ||
            coverageMatrix == null ||
            random == null
        ) {
            throw new IllegalArgumentException("invalid Encoding generator Instantiation inputs");    
        }
        this.random = random;
        this.crossover = crossover;
        this.mutation = mutation;
        this.testcases = testcases;
        this.coverageMatrix = coverageMatrix;
    }
    
    /**
     * A biased random chromosome generator
     * Chooses fewer genes(test cases) and Prioritzes genes with more than 60% coverage rate
     * @return An encoding of the chromosome
     * {@inheritDoc}
     */
    public Encoding get(){
        int[] testSuite = new int[testcases];
        boolean isInvalidEncoding = true;
        boolean covers = false;  //  Used to check if the given test covers at least 60% of the lines
        while (isInvalidEncoding && !covers) {
            for(int i = 0; i < testcases; i++){
                testSuite[i] = random.nextDouble() > 0.8 ? 1 : 0;
                if (testSuite[i] == 1 && linesCovered(coverageMatrix[i]) >= 0.65) covers = true;
            }
            if (Utils.isValid(testSuite)) isInvalidEncoding = false;
        }
        return new Encoding(mutation, crossover, testSuite, coverageMatrix);
    }

    /**
     * Calculate number of lines covered by a chosen test case
     * @param test an array of boolean values presenting lines in a test case
     * @return a fraction of lines covered by the test case
     */
    private double linesCovered (boolean [] test) {
        int counter = 0;
        for (boolean line: test) if(line) counter++;
        return (double) counter / test.length;
    }
}
