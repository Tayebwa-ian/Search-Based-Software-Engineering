package de.uni_passau.fim.se2.se.test_prioritisation.algorithms;

import de.uni_passau.fim.se2.se.test_prioritisation.crossover.Crossover;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.Encoding;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.EncodingGenerator;
import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;
import de.uni_passau.fim.se2.se.test_prioritisation.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.se.test_prioritisation.parent_selection.ParentSelection;
import de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions.StoppingCondition;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public final class SimpleGeneticAlgorithm<E extends Encoding<E>> implements SearchAlgorithm<E> {

    private final StoppingCondition stoppingCondition;
    private final EncodingGenerator<E> encodingGenerator;
    private final Crossover<E> crossover;
    private final Random random;
    private final ParentSelection<E> parentSelection;
    private final FitnessFunction<E> fitnessFunction;

    /**
     * Creates a new simple genetic algorithm with the given components.
     *
     * @param stoppingCondition the stopping condition to be used by the genetic algorithm
     * @param encodingGenerator the encoding generator used to create the initial population
     * @param fitnessFunction   the fitness function used to evaluate the quality of the individuals in the population
     * @param crossover         the crossover operator used to create offspring from parents
     * @param parentSelection   the parent selection operator used to select parents for the next generation
     * @param random            the source of randomness for this algorithm
     */
    public SimpleGeneticAlgorithm(
            final StoppingCondition stoppingCondition,
            final EncodingGenerator<E> encodingGenerator,
            final FitnessFunction<E> fitnessFunction,
            final Crossover<E> crossover,
            final ParentSelection<E> parentSelection,
            final Random random) {
        this.crossover = crossover;
        this.stoppingCondition = stoppingCondition;
        this.encodingGenerator = encodingGenerator;
        this.fitnessFunction = fitnessFunction;
        this.random = random;
        this.parentSelection = parentSelection;
    }

    /**
     * Runs the genetic algorithm to find a solution to the given problem.
     *
     * @return the best individual found by the genetic algorithm
     */
    @Override
    public E findSolution() {
        // Initialize the population
        
        List<E> population = new ArrayList<>();
        int populationSize = 202;
        double P_xover = 0.5;
        double bestFitness = 0.0;
        E bestSolution = null;
        for (int i = 0; i < populationSize; i++) {
            population.add(encodingGenerator.get());
        }
        stoppingCondition.notifySearchStarted();

        for (E el : population) {
            double fitness = fitnessFunction.maximise(el);
            if(fitness > bestFitness) {
                bestFitness = fitness;
                bestSolution = el;
            }
        }

        while (!stoppingCondition.searchMustStop()) {
            List<E> newPopulation = new ArrayList<>();
            while (newPopulation.size() < populationSize) {
                E parent1 = parentSelection.selectParent(population);
                E parent2 = parentSelection.selectParent(population);
                E offSpring1, offSpring2;
                if(random.nextDouble() < P_xover) {
                    offSpring1 = crossover.apply(parent1, parent2);
                    offSpring2 = crossover.apply(parent1, parent2);
                } else {
                    offSpring1 = parent1.deepCopy();
                    offSpring2 = parent2.deepCopy();
                }
                offSpring1 = offSpring1.mutate();
                offSpring1 = offSpring2.mutate();
                newPopulation.add(offSpring1);
                newPopulation.add(offSpring2);
            }
            population = newPopulation;
            for(E el : population) {
                double fitness = fitnessFunction.maximise(el);
                if(fitness > bestFitness) {
                    bestFitness = fitness;
                    bestSolution = el;
                }
                stoppingCondition.notifyFitnessEvaluation();
            }
        }

        // Return the best individual from the final population
        return bestSolution;
    }

    @Override
    public StoppingCondition getStoppingCondition() {
        return stoppingCondition;
    }
}
