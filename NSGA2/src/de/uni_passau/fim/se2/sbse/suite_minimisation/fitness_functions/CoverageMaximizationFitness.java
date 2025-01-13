package de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions;

import static java.util.Objects.requireNonNull;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.Encoding;

public class CoverageMaximizationFitness implements MaximizingFitnessFunction<Encoding> {

    /**
     * The coverage matrix to be used when computing the fitness.
     * each line is represented as true or false meaning it is included or excluded in a test case respectively
     * 
     */
    private final boolean[][] coverageMatrix;

    public CoverageMaximizationFitness(final boolean[][] coverageMatrix) {
        requireNonNull(coverageMatrix, "coverage matrix can not be null");
        this.coverageMatrix = coverageMatrix;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double applyAsDouble(Encoding c) throws NullPointerException {
        requireNonNull(c, "Encoding can not be null");
        int coverage = 0;
        int totalLines = coverageMatrix[0].length;
        int[] chromosome = c.getGenes();
        for (int i = 0; i < totalLines; i++) {
            for (int j = 0; j < coverageMatrix.length; j++) {
                if (chromosome[j] == 1 && coverageMatrix[j][i]) {
                    coverage++;
                    break;
                }
            }
        }
        double result = (double) coverage / totalLines;
        return result;
    }
}
