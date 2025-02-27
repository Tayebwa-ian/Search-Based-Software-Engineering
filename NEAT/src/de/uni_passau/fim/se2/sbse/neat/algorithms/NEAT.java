package de.uni_passau.fim.se2.sbse.neat.algorithms;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.uni_passau.fim.se2.sbse.neat.chromosomes.NetworkChromosome;
import de.uni_passau.fim.se2.sbse.neat.chromosomes.NetworkGenerator;
import de.uni_passau.fim.se2.sbse.neat.crossover.NeatCrossover;
import de.uni_passau.fim.se2.sbse.neat.environments.Environment;
import de.uni_passau.fim.se2.sbse.neat.mutation.NeatMutation;

public class NEAT implements Neuroevolution {
    private final int populationSize;
    private final NeatMutation mutation;
    private final NeatCrossover crossover;
    private final Random random;
    private List<NetworkChromosome> population;
    private final NetworkGenerator generator;
    private int maxGeneration;
    int generation;
    private final List<Species> species;
    private double COMPATIBILITY_THRESHOLD = 3.0;
    private static final double THRESHOLD_ADJUSTMENT = 0.4;
    private static final int TARGET_SPECIES = 5;
    
    public NEAT(
        Random random,
        NeatCrossover crossover,
        NeatMutation mutation,
        NetworkGenerator generator,
        int populationSize,
        int maxGeneration
    ) {
        this.random = requireNonNull(random);
        this.crossover = requireNonNull(crossover);
        this.mutation = requireNonNull(mutation);
        this.populationSize = populationSize;
        this.generator = requireNonNull(generator);
        this.maxGeneration = maxGeneration;
        this.species = new ArrayList<>();
    }

    /**
     * Solves the given reinforcement learning task.
     *
     * @return The agent that solves the task.
     */
    @Override
    public NetworkChromosome solve(Environment environment) {
        // Initialize the first generation
        initializePopulation();

        generation = 0;
        NetworkChromosome bestOverall = null;
        while (true) {
            evaluateFitness(environment);

            // Track the best chromosome
            NetworkChromosome currentBest = population.stream()
                .max((a, b) -> Double.compare(a.getFitness(), b.getFitness()))
                .orElse(null);
            if (bestOverall == null || 
                (currentBest != null && currentBest.getFitness() > bestOverall.getFitness())) {
                bestOverall = currentBest.copy();
            }

            // Check for a solution
            if (currentBest != null && environment.solved(currentBest)) {
                return currentBest;
            }

            evolve();
            generation++;

            // Terminate after max generations and return the best found
            if (generation >= maxGeneration) {
                return bestOverall;
            }
        }
    }

    /**
     * Returns the current generation of the neuroevolution algorithm.
     *
     * @return The current generation of the neuroevolution algorithm.
     */
    @Override
    public int getGeneration() {
        return generation;
    }

    /**
     * Intializing the population
     */
    private void initializePopulation() {
        population = new ArrayList<>();
        species.clear();  // Reset species for fresh runs
        for (int i = 0; i < populationSize; i++) {
            NetworkChromosome chromosome = generator.generate();
            population.add(chromosome);
            assignToSpecies(chromosome);
        }
    }

    private void evaluateFitness(Environment environment) {
        for (NetworkChromosome chromosome : population) {
            double fitness = environment.evaluate(chromosome);
            chromosome.setFitness(fitness);
        }
    }

    private NetworkChromosome selectParent() {
        // Tournament selection with size 5
        int tournamentSize = 5;
        List<NetworkChromosome> tournament = new ArrayList<>();
        for (int i = 0; i < tournamentSize; i++) {
            tournament.add(population.get(random.nextInt(population.size())));
        }
        return tournament.stream()
                .max((a, b) -> Double.compare(a.getFitness(), b.getFitness()))
                .orElse(null);
    }

    private void evolve() {
        List<NetworkChromosome> newPopulation = new ArrayList<>();
        speciatePopulation();
        adjustCompatibilityThreshold();
        allocateOffspring();
    
        // Preserve top 2 elites globally (not just per species)
        population.stream()
        .sorted((a, b) -> Double.compare(b.getFitness(), a.getFitness()))
        .limit(2)
        .forEach(newPopulation::add);

        for (Species s : species) {
            s.evolveSpecies(crossover, mutation, random);
            newPopulation.addAll(s.getMembers());
        }
    
        while (newPopulation.size() < populationSize) {
            NetworkChromosome parent1 = selectParent();
            NetworkChromosome parent2 = selectParent();
            if (parent1 != null && parent2 != null) {
                NetworkChromosome child = crossover.apply(parent1, parent2);
                mutation.apply(child);
                newPopulation.add(child);
            } else {
                // Fallback to random generation
                newPopulation.add(generator.generate());
            }
        }
        population = new ArrayList<>(newPopulation.subList(0, populationSize));
    }

    private void speciatePopulation() {
        species.forEach(Species::clearMembers);
        for (NetworkChromosome chromosome : population) {
            assignToSpecies(chromosome);
        }

        // Remove empty species
        species.removeIf(s -> s.getMembers().isEmpty());
    }

    private void assignToSpecies(NetworkChromosome chromosome) {
        for (Species s : species) {
            if (s.isCompatible(chromosome, COMPATIBILITY_THRESHOLD)) {
                s.addMember(chromosome);
                return;
            }
        }
        species.add(new Species(chromosome));
    }

    private void allocateOffspring() {
        double totalSharedFitness = species.stream().mapToDouble(Species::getSharedFitness).sum();
        if (totalSharedFitness <= 0) {
            // Distribute offspring equally if no species has fitness
            species.forEach(s -> s.allocateOffspring(1.0 / species.size(), populationSize));
        } else {
            species.forEach(s -> s.allocateOffspring(totalSharedFitness, populationSize));
        }
    }

    private void adjustCompatibilityThreshold() {
        if (species.size() < TARGET_SPECIES) {
            COMPATIBILITY_THRESHOLD -= THRESHOLD_ADJUSTMENT;
        } else if (species.size() > TARGET_SPECIES) {
            COMPATIBILITY_THRESHOLD += THRESHOLD_ADJUSTMENT;
        }
        COMPATIBILITY_THRESHOLD = Math.max(1.0, COMPATIBILITY_THRESHOLD); // Prevent negative thresholds
    }
}
