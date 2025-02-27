package de.uni_passau.fim.se2.sbse.neat.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.uni_passau.fim.se2.sbse.neat.chromosomes.NetworkChromosome;
import de.uni_passau.fim.se2.sbse.neat.crossover.NeatCrossover;
import de.uni_passau.fim.se2.sbse.neat.mutation.NeatMutation;

public class Species {
    private final List<NetworkChromosome> members;
    private NetworkChromosome representative;
    private int offspringCount;
    private static final double C1 = 1.0;  // Coefficient for disjoint genes (D)
    private static final double C2 = 1.0;  // Coefficient for excess genes (E)
    private static final double C3 = 3.0;  // Weight differences


    public Species(NetworkChromosome representative) {
        this.representative = representative;
        this.members = new ArrayList<>();
        this.members.add(representative);
        this.offspringCount = 0;
    }

    public void addMember(NetworkChromosome chromosome) {
        members.add(chromosome);
    }

    public void clearMembers() {
        members.clear();
    }

    public boolean isCompatible(NetworkChromosome chromosome, double threshold) {
        double distance = calculateCompatibilityDistance(chromosome);
        return distance < threshold;
    }

    private double calculateCompatibilityDistance(NetworkChromosome chromosome) {
        int disjointGenes = chromosome.countDisjointGenes(representative);
        int excessGenes = chromosome.countExcessGenes(representative);
        double avgWeightDiff = chromosome.averageWeightDifference(representative);
        
        int N = Math.max(chromosome.getGeneCount(), representative.getGeneCount());
        return (C1 * excessGenes / N) + (C2 * disjointGenes / N) + (C3 * avgWeightDiff);
    }

    public double getSharedFitness() {
        // Scale individual fitness by species size first
        members.forEach(m -> m.setFitness(m.getFitness() / members.size()));
        return members.stream().mapToDouble(NetworkChromosome::getFitness).sum();
    }

    public void allocateOffspring(double totalSharedFitness, int totalPopulationSize) {
        if (totalSharedFitness <= 0) {
            // Assign minimal offspring to all species to preserve diversity
            this.offspringCount = 1; 
        } else {
            this.offspringCount = Math.max(1, (int) ((getSharedFitness() / totalSharedFitness) * totalPopulationSize));
        }
    }

    public void evolveSpecies(NeatCrossover crossover, NeatMutation mutation, Random random) {
        List<NetworkChromosome> newMembers = new ArrayList<>();
        members.sort((a, b) -> Double.compare(b.getFitness(), a.getFitness()));
    
        // Preserve top 2 elites
        int elitismCount = Math.min(2, members.size());
        for (int i = 0; i < elitismCount; i++) {
            newMembers.add(members.get(i).copy());
        }
    
        // Fitness-proportional parent selection
        double totalFitness = members.stream().mapToDouble(NetworkChromosome::getFitness).sum();
        List<Double> fitnessProbabilities = new ArrayList<>();
        for (NetworkChromosome member : members) {
            fitnessProbabilities.add(member.getFitness() / totalFitness);
        }
    
        // Generate offspring
        while (newMembers.size() < offspringCount) {
            NetworkChromosome parent1 = selectParent(fitnessProbabilities, random);
            NetworkChromosome parent2 = selectParent(fitnessProbabilities, random);
            NetworkChromosome child = crossover.apply(parent1, parent2);
            mutation.apply(child);
            newMembers.add(child);
        }
    
        members.clear();
        members.addAll(newMembers);
        members.sort((a, b) -> Double.compare(b.getFitness(), a.getFitness()));
        representative = members.get(0); // Best member as representative
    }
    
    private NetworkChromosome selectParent(List<Double> fitnessProbabilities, Random random) {
        double r = random.nextDouble();
        double cumulative = 0.0;
        for (int i = 0; i < members.size(); i++) {
            cumulative += fitnessProbabilities.get(i);
            if (r <= cumulative) {
                return members.get(i);
            }
        }
        return members.get(members.size() - 1);
    }

    public List<NetworkChromosome> getMembers() {
        return members;
    }
}
