package de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions;

import java.util.Arrays;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;


/**
 * The Average Percentage of Lines Covered (APLC) fitness function.
 */
public final class APLC implements FitnessFunction<TestOrder> {

    /**
     * The coverage matrix to be used when computing the APLC metric.
     */
    private final boolean[][] coverageMatrix;

    /**
     * Creates a new APLC fitness function with the given coverage matrix.
     *
     * @param coverageMatrix the coverage matrix to be used when computing the APLC metric
     */
    public APLC(final boolean[][] coverageMatrix) {
        this.coverageMatrix = coverageMatrix;
    }


    /**
     * Computes and returns the APLC for the given order of test cases.
     * Orderings that achieve a higher rate of coverage are rewarded with higher values.
     * The APLC ranges between 0.0 and 1.0.
     *
     * @param testOrder the proposed test order for which the fitness value will be computed
     * @return the APLC value of the given test order
     * @throws NullPointerException if {@code null} is given
     */
    @Override
    public double applyAsDouble(final TestOrder testOrder) throws NullPointerException {
        int n = testOrder.size();  // Number of test cases
        int x = coverageMatrix[0].length;  // Number of lines to be covered
        int[] order = testOrder.getPositions();

        // Calculate TL_k (position of the first test case covering each line)
        int[] TL = new int[x];  // To store positions for each line
        Arrays.fill(TL, 0);  // Initialize to zero value
        int m = 0; // Total lines covered

        for (int line = 0; line < x; line++) {
            for (int testPosition = 0; testPosition < n; testPosition++) {
                int testCase = order[testPosition];
                if (coverageMatrix[testCase][line] == true) {
                    // Update TL for this line to the earliest position
                    TL[line] = testPosition + 1; // Store 1-based index
                    m += 1;
                    break; // Stop searching once line is covered
                }
            }
        }

        // Avoid division by zero
        if (m == 0) return 0.0;

        // Calculate the sum of TL_k
        int sumTL = Arrays.stream(TL).sum();

        // Precompute constants to avoid redundant calculations
        double factor1 = 1.0 / (n * m);
        double factor2 = 1.0 / (2 * n);

        // Calculate APLC
        double aplc = 1 - (factor1 * sumTL) + factor2;
        return Math.round(aplc * 1000.0) / 1000.0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double maximise(TestOrder encoding) throws NullPointerException {
        return applyAsDouble(encoding);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double minimise(TestOrder encoding) throws NullPointerException {
        return 1.0 - applyAsDouble(encoding);
    }
}
