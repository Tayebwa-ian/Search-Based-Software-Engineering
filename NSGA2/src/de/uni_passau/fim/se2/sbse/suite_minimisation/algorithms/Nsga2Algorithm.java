package de.uni_passau.fim.se2.sbse.suite_minimisation.algorithms;

import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.Encoding;
import de.uni_passau.fim.se2.sbse.suite_minimisation.chromosomes.EncodingGenerator;
import de.uni_passau.fim.se2.sbse.suite_minimisation.selection.BinaryTournamentSelection;
import de.uni_passau.fim.se2.sbse.suite_minimisation.stopping_conditions.StoppingCondition;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Pair;
import de.uni_passau.fim.se2.sbse.suite_minimisation.utils.Utils;

public class Nsga2Algorithm implements GeneticAlgorithm<Encoding> {

    private final StoppingCondition stoppingCondition;
    private final EncodingGenerator encodingGenerator;
    private final Random random;
    private final BinaryTournamentSelection<Encoding> selection;

    /**
     * Creates a new NSGA II algorithm with the given components.
     *
     * @param stoppingCondition the stopping condition to be used by the genetic algorithm
     * @param encodingGenerator the encoding generator used to create the initial population
     * @param fitnessFunction   the fitness function used to evaluate the quality of the individuals in the population
     * @param crossover         the crossover operator used to create offspring from parents
     * @param mutation          the mutation operator used to mutate individuals
     * @param selection         the parent selection operator used to select parents for the next generation
     * @param random            the source of randomness for this algorithm
     */
    public Nsga2Algorithm (
        final StoppingCondition stoppingCondition,
        final EncodingGenerator encodingGenerator,
        final BinaryTournamentSelection<Encoding> selection,
        final Random random
    ) {
        if (
            stoppingCondition == null ||
            encodingGenerator == null ||
            selection == null ||
            random == null
        ) throw new IllegalArgumentException("Invalid input value for NSGA2 instatiation");
        this.stoppingCondition = stoppingCondition;
        this.random = random;
        this.encodingGenerator = encodingGenerator;
        this.selection = selection;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Encoding> findSolution() {
        List<Encoding> population = generatePopulation(600); // Generate initial population of size 600
        stoppingCondition.notifySearchStarted();

        while (!stoppingCondition.searchMustStop()) {
            stoppingCondition.notifyFitnessEvaluation();

            // Rank the population based on non-domination
            List<List<Encoding>> fronts = Utils.nonDominatedSorting(population);

            // Assign crowding distance to individuals in each front
            for (List<Encoding> front : fronts) {
                crowdingDistanceAssignment(front);
            }

            // Generate offspring population
            List<Encoding> offspringPopulation = generateOffspring(population);

            // Combine population and offspring
            List<Encoding> combinedPopulation = new ArrayList<>(population);
            combinedPopulation.addAll(offspringPopulation);

            // Perform a non-dominated sort on the combined population
            List<List<Encoding>> combinedFronts = Utils.nonDominatedSorting(combinedPopulation);

            // Build the next generation population
            population = buildNextGeneration(combinedFronts, 20, 0.7, 0.12);
        }

        return population; // Final solution population
    }

    /**
     * Generates intial chromosomes population
     * @param size      the size of the population to be genarated
     * @return          the population that is randomly generated
     */
    public List<Encoding> generatePopulation(int size) {
        List<Encoding> population = new ArrayList<>();
        EncodingGenerator generator = encodingGenerator;
        for (int i = 0; i < size; i++) {
            population.add(generator.get());
        }
        return population;
    }

    /**
     * Generates offspring from the current population.
     * @param population the current population
     * @return the offspring population
     */
    public List<Encoding> generateOffspring (List<Encoding> population) {
        List<Encoding> offspringPopulation = new ArrayList<>();
        Encoding offspring1;
        Encoding offspring2;
        while (offspringPopulation.size() < population.size()){
            Encoding parent1 = selection.apply(population);
            Encoding parent2 = selection.apply(population);
            if (random.nextDouble() < 0.7) {
                Pair<Encoding> pair = parent1.crossover(parent2);
                offspring1 = pair.getFst();
                offspring2 = pair.getSnd();
            } else {
                offspring1 = parent1;
                offspring2 = parent2;
            }
            offspring1 = offspring1.mutate();
            offspring2 = offspring2.mutate();

            offspringPopulation.add(offspring1);
            offspringPopulation.add(offspring2);
        }
        return offspringPopulation;
    }

    /**
     * Assigns crowding distance to individuals in a front.
     * @param front the individuals in the current front
     */
    private void crowdingDistanceAssignment(List<Encoding> front) {
        int size = front.size();
        if (size == 0) return;

        double[] distances = new double[size];
        Arrays.fill(distances, 0.0);

        for (int i = 0; i < 2; i++) {
            final int obj = i;
            front.sort((a, b) -> {
                if (obj == 0) return Double.compare(a.getCoverageMaxFitness(), b.getCoverageMaxFitness());
                return Double.compare(a.getTestCaseMinFitness(), b.getTestCaseMinFitness());
            });

            distances[0] = distances[size - 1] = Double.POSITIVE_INFINITY;
            for (int j = 1; j < size - 1; j++) {
                if (obj == 0) {
                    distances[j] += (front.get(j + 1).getCoverageMaxFitness() - front.get(j - 1).getCoverageMaxFitness());
                } else {
                    distances[j] += (front.get(j + 1).getTestCaseMinFitness() - front.get(j - 1).getTestCaseMinFitness());
                }
            }
        }

        for (int i = 0; i < size; i++) {
            front.get(i).setDistance(distances[i]);
        }
    }

    /**
     * Builds the next generation population from the sorted fronts.
     * Allows the next generation to be smaller than the specified population size
     * by retaining only better individuals based on a fitness threshold.
     *
     * @param fronts the sorted fronts
     * @param maxPopulationSize the maximum desired population size
     * @param minFitnessThreshold the minimum fitness value required to include an individual
     * @return the next generation population
     */
    private List<Encoding> buildNextGeneration(
            List<List<Encoding>> fronts, 
            int maxPopulationSize, 
            double minFitnessThreshold,
            double maxThreshold
        ) {
        List<Encoding> nextGeneration = new ArrayList<>();

        for (List<Encoding> front : fronts) {
            // Filter individuals in the current front based on fitness threshold
            List<Encoding> filteredFront = new ArrayList<>();
            for (Encoding individual : front) {
                if (
                    individual.getCoverageMaxFitness() >= minFitnessThreshold ||
                    individual.getTestCaseMinFitness() <= maxThreshold
                ) filteredFront.add(individual);
            }

            if (filteredFront.isEmpty()) filteredFront.addAll(front);  //  Ensure it is never empty

            // Check if the filtered front can fit into the next generation
            if (nextGeneration.size() + filteredFront.size() <= maxPopulationSize) {
                nextGeneration.addAll(filteredFront);
            } else {
                // Sort the filtered front by crowding distance and select top individuals
                filteredFront.sort((a, b) -> Double.compare(b.getDistance(), a.getDistance()));
                nextGeneration.addAll(filteredFront.subList(0, Math.min(filteredFront.size(), maxPopulationSize - nextGeneration.size())));
                break;
            }
        }

        return nextGeneration;
    }

    @Override
    public StoppingCondition getStoppingCondition() {
        return stoppingCondition;
    }
}
