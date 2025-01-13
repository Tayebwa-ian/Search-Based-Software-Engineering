package de.uni_passau.fim.se2.sbse.suite_minimisation.utils;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.Encoding;
import de.uni_passau.fim.se2.sbse.suite_minimisation.fitness_functions.FitnessFunction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class Utils {

    /**
     * Parses a coverage matrix from a string.
     *
     * @param matrixFile the string representation of the coverage matrix
     * @return the parsed coverage matrix
     * @throws IOException if the supplied file could not be read
     */
    public static boolean[][] parseCoverageMatrix(File matrixFile) throws IOException {
        String matrix = Files.readString(matrixFile.toPath()).replace("\n", " ");

        // Remove outer brackets
        matrix = matrix.substring(1, matrix.length() - 1);

        // Split rows by "], ["
        String[] rows = matrix.split("], \\[");

        // Initialize 2D boolean array
        boolean[][] parsedMatrix = new boolean[rows.length][];

        for (int i = 0; i < rows.length; i++) {
            // Remove any remaining brackets and split by comma
            String[] values = rows[i].replace("[", "").replace("]", "").split(", ");
            parsedMatrix[i] = new boolean[values.length];
            for (int j = 0; j < values.length; j++) {
                // Parse "true" or "false" as boolean
                parsedMatrix[i][j] = Boolean.parseBoolean(values[j].trim());
            }
        }

        return parsedMatrix;
    }

    /**
     * Computes the hyper-volume of the given Pareto {@code front}, using the given fitness
     * functions {@code f1} and {@code f2}, and {@code r1} and {@code r2} as coordinates of the
     * reference point. The fitness functions must produce normalized results between 0 and 1.
     *
     * @param front the front for which to compute the hyper-volume
     * @param f1    the first fitness function
     * @param f2    the second fitness function
     * @param r1    reference coordinate for {@code f1}
     * @param r2    reference coordinate for {@code f2}
     * @return the hyper volume of the given front w.r.t. the reference point
     * @apiNote The function uses ugly raw types because it seems the type system doesn't want to
     * let me express this in any other way :(
     * @implSpec In the implementation of this method you might need to cast or use raw types, too.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static double computeHyperVolume(
            final List front,
            final FitnessFunction f1,
            final FitnessFunction f2,
            final double r1,
            final double r2)
            throws IllegalArgumentException {
        if (front == null || front.isEmpty()) {
            throw new IllegalArgumentException("Invalid input values"); // Handle empty/null front case
        }
        // Cast and validate elements in `front` using raw types
        List<Object> Front = new ArrayList<>();
        for (Object obj : front) {
            // Ensure the object is compatible with FitnessFunction operations
            if (f1.applyAsDouble(obj) < 0 || f2.applyAsDouble(obj) < 0) {
                throw new IllegalArgumentException("Invalid object in front: fitness function failed.");
            }
            Front.add(obj); // Add to the new list if valid
        }
        // Step 1: Sort the Pareto front by test suite size in ascending order
        Collections.sort(Front, Comparator.comparingDouble(s -> f1.applyAsDouble(s)));

        // Step 2: Initialize hypervolume and the previous reference point
        double hyperVolume = 0.0;
        double previousF1 = r1; // Initial reference for f1
        double currentF2;

        // Step 3: Iterate through the sorted Pareto front to compute the hypervolume
        for (Object chromosome : Front) {
            double currentF1 = f1.applyAsDouble(chromosome); // Current f1 value
            currentF2 = f2.applyAsDouble(chromosome);       // Current f2 value

            // Calculate the rectangular area contribution from (previousF1, r2) to (currentF1, currentF2)
            double width = currentF1 - previousF1;
            double height = r2 - currentF2;

            hyperVolume += Math.abs(width * height); // Accumulate valid rectangular areas

            // Update previousF1 for the next rectangle calculation
            previousF1 = currentF1;
        }

        return hyperVolume;
    }

    /**
     * Evaluates two solutions with respect to 2 fitness functions
     * @param A     the solution A to use in comparison
     * @param B     the solution B to use in comparison
     * @param f1    the first fitness function
     * @param f2    the second fitness function
     * @return true if solution A dominates solution B otherwise false
     */
    public static boolean dominates(
        final Encoding A,
        final Encoding B
    ) {
        if (
            B.getCoverageMaxFitness() > A.getCoverageMaxFitness() ||
            B.getTestCaseMinFitness() < A.getTestCaseMinFitness()
        ) return false;
        if (
            A.getCoverageMaxFitness() > B.getCoverageMaxFitness() ||
            A.getTestCaseMinFitness() < B.getTestCaseMinFitness()
        ) return true;
        return false;
    }

    /**
     * Takes a population of encodings and returns a list of lists of non-dominating fronts
     * @param population a population of proposed solutions
     * @return a list of lists of non-dominating fronts
     */
    public static List<List<Encoding>> nonDominatedSorting(List<Encoding> population) {
        List<List<Encoding>> fronts = new ArrayList<>();
        List<Set<Integer>> dominationSets = new ArrayList<>();
        int populationSize = population.size();
        int[] dominatedCounts = new int[populationSize];
    
        for (int i = 0; i < populationSize; i++) {
            dominationSets.add(new HashSet<>());
            dominatedCounts[i] = 0;
            for (int j = 0; j < populationSize; j++) {
                if (dominates(population.get(i), population.get(j))) {
                    dominationSets.get(i).add(j);
                } else if (dominates(population.get(j), population.get(i))) {
                    dominatedCounts[i]++;
                }
            }
            if (dominatedCounts[i] == 0) {
                if (fronts.isEmpty()) fronts.add(new ArrayList<>());
                fronts.get(0).add(population.get(i));
            }
        }
    
        int currentFront = 0;
        while (currentFront < fronts.size() && !fronts.get(currentFront).isEmpty()) {
            List<Encoding> nextFront = new ArrayList<>();
            for (Encoding individual : fronts.get(currentFront)) {
                int index = population.indexOf(individual);
                for (int dominatedIndex : dominationSets.get(index)) {
                    dominatedCounts[dominatedIndex]--;
                    if (dominatedCounts[dominatedIndex] == 0) {
                        nextFront.add(population.get(dominatedIndex));
                    }
                }
            }
            if (!nextFront.isEmpty()) fronts.add(nextFront);
            currentFront++;
        }
    
        return fronts;
    }

    /**
     * Checks if a given set of test cases representations in array are valid genes
     * Valid genes(test cases representations) are respresented as 0 or 1
     * And must have at least one test case present
     * 
     * @return true if a given list has valid genes
     */
    public static boolean isValid(final int[] genesArray) {
        boolean checker = false;
        for(int el: genesArray){
            if(el == 0 || el == 1) {
                if(el == 1) {
                    checker = true;
                }
            } else return false;
        }    
        return checker;
    }
}
