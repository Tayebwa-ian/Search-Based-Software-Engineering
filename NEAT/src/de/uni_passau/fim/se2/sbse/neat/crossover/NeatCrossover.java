package de.uni_passau.fim.se2.sbse.neat.crossover;


import de.uni_passau.fim.se2.sbse.neat.chromosomes.ConnectionGene;
import de.uni_passau.fim.se2.sbse.neat.chromosomes.NetworkChromosome;

import java.util.*;

import static java.util.Objects.requireNonNull;

/**
 * A NEAT crossover operation that is used by the NEAT algorithm to combine two parent chromosomes.
 */
public class NeatCrossover implements Crossover<NetworkChromosome> {

    /**
     * The random number generator to use.
     */
    private final Random random;

    /**
     * Creates a new NEAT crossover operator with the given random number generator.
     *
     * @param random The random number generator to use.
     */
    public NeatCrossover(Random random) {
        this.random = requireNonNull(random);
    }

    /**
     * Applies a crossover operation to the given parent chromosomes by combining their genes.
     * During the crossover operation, we determine for each gene whether it is a matching gene or a disjoint/excess gene.
     * Matching genes are inherited with a 50% chance from either parent,
     * while disjoint/excess genes are only inherited from the fitter parent.
     *
     * @param parent1 The first crossover parent.
     * @param parent2 The second crossover parent.
     * @return A new chromosome resulting from the crossover operation.
     */
    @Override
    public NetworkChromosome apply(NetworkChromosome parent1, NetworkChromosome parent2) {
        List<ConnectionGene> childConnections = new ArrayList<>();
        Set<Integer> inheritedInnovations = new HashSet<>();

        // Determine the fitter parent
        NetworkChromosome fitterParent = parent1.getFitness() > parent2.getFitness() ? parent1 : parent2;
        NetworkChromosome otherParent = (fitterParent == parent1) ? parent2 : parent1;

        // Create a map of innovation numbers to ConnectionGene for the other parent
        Map<Integer, ConnectionGene> otherParentConnections = new HashMap<>();
        for (ConnectionGene conn : otherParent.getConnections()) {
            otherParentConnections.put(conn.getInnovationNumber(), conn);
        }

        // Inherit matching genes (those present in both parents) with a 50% probability
        for (ConnectionGene conn : fitterParent.getConnections()) {
            int innovationNumber = conn.getInnovationNumber();

            ConnectionGene inheritedGene;
            if (otherParentConnections.containsKey(innovationNumber)) {
                // Mark as inherited
                inheritedInnovations.add(innovationNumber);

                // Randomly select from either parent
                if (random.nextBoolean()) {
                    inheritedGene = cloneConnection(conn);
                } else {
                    inheritedGene = cloneConnection(otherParentConnections.get(innovationNumber));
                }
            } else {
                // If only present in the fitter parent, inherit it
                inheritedGene = cloneConnection(conn);
            }

            // Ensure child's weights differ by perturbing slightly
            inheritedGene.setWeight(inheritedGene.getWeight() + (random.nextDouble() * 0.2 - 0.1));
            childConnections.add(inheritedGene);
        }

        // Return the child chromosome with inherited connections
        return new NetworkChromosome(new HashMap<>(fitterParent.getLayers()), childConnections);
    }

    /**
     * Creates a copy of a ConnectionGene to avoid modifying the original parent genes.
     */
    private ConnectionGene cloneConnection(ConnectionGene original) {
        return new ConnectionGene(
            original.getSourceNeuron(),
            original.getTargetNeuron(),
            original.getWeight(),  // Cloned weight
            original.getEnabled(),
            original.getInnovationNumber()
        );
    }
}
