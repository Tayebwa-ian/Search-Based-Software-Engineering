package de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.Encoding;

public class TestCaseMinimizationFitness implements MinimizingFitnessFunction<Encoding> {
    /**
     * Calculates the number of test cases present in a test suite
     * divides it by maximum number of total tests that can be in a test suite
     */

     /**
      * {@inheritDoc}
      */
     @Override
     public double applyAsDouble(Encoding c) throws NullPointerException {
        if (c == null) {
            throw new NullPointerException("Encoding cannot be null"); 
        }
        int size = c.chromosomeSize();
        int presentTestCases = c.presenTestCases();
        double result = (double) presentTestCases / size;
        return result;
     }
}
