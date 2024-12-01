package de.uni_passau.fim.se2.se.test_prioritisation.encodings;

import java.util.HashSet;

import de.uni_passau.fim.se2.se.test_prioritisation.mutations.Mutation;


public class TestOrder extends Encoding<TestOrder> {

    /**
     * Internal backing array that stores the actual ordering of the tests. By convention, we assign
     * a unique number to every test case. The number range starts at 0, ends at n-1 and is
     * contiguous. The position of a test case in the regression test suite corresponds to the
     * position of its unique number in this array. A valid test case prioritization must not
     * contain numbers outside this range. Also, the same number must not occur twice. These
     * requirements can be checked with {@code isValid(final int[] tests)}.
     */
    private final int[] positions;

    /**
     * Creates a new test order with the given mutation and test case ordering.
     *
     * @param mutation  the mutation to be used with this encoding
     * @param positions the test case ordering
     */
    public TestOrder(Mutation<TestOrder> mutation, int[] positions) {
        super(mutation);
        if(!isValid(positions) || positions.length == 0) {
            throw new IllegalArgumentException("invalid input test cases");    
        }
        this.positions = positions;
    }

    /**
     * Tells whether the given array represents a valid regression test case prioritization encoding.
     * By convention, we require that every test must have a unique identifier starting at 0.
     * Since ranges are contiguous, this implies that numbers must only occur once and be located in the range from 0 to n -1.
     *
     * @param tests the test suite prioritization array to check
     * @return {@code true} if the given prioritization is valid, {@code false} otherwise
     */
    public static boolean isValid(final int[] tests) {
        int n = tests.length;
        if (n == 0) {
            return false;
        }
        HashSet<Integer> seen = new HashSet<>();

        for (int test : tests) {
            // Check if test ID is out of range or already exists in the set
            if (test < 0 || test >= n || !seen.add(test)) {
                return false; // Invalid if test is out of range or not unique
            }
        }
        // If all IDs are unique and in range, the array is valid
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TestOrder deepCopy() {
        TestOrder newCopy = new TestOrder(getMutation(), positions);
        return newCopy;
    }

    /**
     * Returns the number of test cases in this test case ordering.
     *
     * @return the number of test cases
     */
    public int size() {
        return positions.length;
    }

    /**
     * Returns a reference to the underlying internal backing array.
     *
     * @return the orderings array
     */
    public int[] getPositions() {
        return positions;
    }


    @Override
    public TestOrder self() {
        return this;
    }

}