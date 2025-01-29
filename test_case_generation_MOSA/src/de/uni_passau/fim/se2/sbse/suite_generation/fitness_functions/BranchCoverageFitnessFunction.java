package de.uni_passau.fim.se2.sbse.suite_generation.fitness_functions;

import de.uni_passau.fim.se2.sbse.suite_generation.chromosomes.TestCase;
import de.uni_passau.fim.se2.sbse.suite_generation.instrumentation.Branch;

import java.util.Map;

public class BranchCoverageFitnessFunction implements FitnessFunction<TestCase> {

    private final Branch targetBranch;

    /**
     * Constructs a new fitness function for a specific target branch.
     * 
     * @param targetBranch The branch we aim to cover with this fitness function
     */
    public BranchCoverageFitnessFunction(Branch targetBranch) {
        this.targetBranch = targetBranch;
    }

    @Override
    public double applyAsDouble(final TestCase testCase) {
        // Execute the test case to gather new branch distance information
        // An get the branch distances after test case execution
        Map<Integer, Double> distances = testCase.call();

        
        // Retrieve the distance for our target branch
        Double distance = distances.get(targetBranch.getId());

        // If the branch was not executed at all, return the highest possible distance (which should be worse than any real distance)
        if (distance == null) {
            // Assuming the maximum distance for an unexecuted branch is 1 (this can be adjusted based on your branch distance calculation method)
            return 1.0;
        }

        // The fitness is inversely proportional to the distance: 
        // a lower distance means a better fitness (closer to covering the branch)
        // Here, we normalize the distance to be within [0, 1], with 0 being the best (branch covered) and 1 the worst (branch not covered at all)
        return Math.min(distance / (distance + 1), 1.0); // Normalize to [0, 1]
    }

    @Override
    public boolean isMinimizing() {
        // Since we want to minimize the distance to cover the branch, this is a minimizing problem
        return true;
    }
}
