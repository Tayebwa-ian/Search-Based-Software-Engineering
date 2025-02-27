package de.uni_passau.fim.se2.sbse.neat.mutation;

import de.uni_passau.fim.se2.sbse.neat.algorithms.innovations.Innovation;
import de.uni_passau.fim.se2.sbse.neat.algorithms.innovations.NetworkInnovation;
import de.uni_passau.fim.se2.sbse.neat.chromosomes.*;
import de.uni_passau.fim.se2.sbse.neat.utils.Utils;

import java.util.Random;
import java.util.Set;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implements the mutation operator for the Neat algorithm, which applies four types of mutations based on probabilities:
 * 1. Add a new neuron to the network.
 * 2. Add a new connection to the network.
 * 3. Mutate the weights of the connections in the network.
 * 4. Toggle the enabled status of a connection in the network.
 */
public class NeatMutation implements Mutation<NetworkChromosome> {

    /**
     * The random number generator to use.
     */
    private final Random random;

    /**
     * The list of innovations that occurred so far in the search.
     * Since Neat applies mutations that change the structure of the network,
     * the set of innovations must be updated appropriately.
     */
    private final Set<Innovation> innovations;

    /**
     * Constructs a new NeatMutation with the given random number generator and the list of innovations that occurred so far in the search.
     *
     * @param innovations The list of innovations that occurred so far in the search.
     * @param random      The random number generator.
     */
    public NeatMutation(Set<Innovation> innovations, Random random) {
        this.innovations = requireNonNull(innovations);
        this.random = requireNonNull(random);
    }


    /**
     * Applies mutation to the given network chromosome.
     * If a structural mutation is applied, no further non-structural mutations are applied.
     * Otherwise, the weights of the connections are mutated and/or the enabled status of a connection is toggled.
     *
     * @param parent The parent chromosome to mutate.
     * @return The mutated parent chromosome.
     */
    @Override
    public NetworkChromosome apply(NetworkChromosome parent) {
        if (random.nextDouble() < 0.2) return addNeuron(parent);
        if (random.nextDouble() < 0.3) return addConnection(parent);
        if (random.nextDouble() < 0.8) return mutateWeights(parent);
        return toggleConnection(parent);
    }


    /**
     * Adds a hidden neuron to the given network chromosome by splitting an existing connection.
     * The connection to be split is chosen randomly from the list of connections in the network chromosome.
     * The connection is disabled and two new connections are added to the network chromosome:
     * One connection with a weight of 1.0 from the source neuron of the split connection to the new hidden neuron,
     * and one connection with the weight of the split connection from the new hidden neuron to the target neuron of the split connection.
     * <p>
     * Since this mutation changes the structure of the network,
     * novel innovations for the new connections must be created if the same mutation has not occurred before.
     * If the same innovation has occurred before, the corresponding innovation numbers must be reused.
     *
     * @param parent The network chromosome to which the new neuron and connections will be added.
     * @return The mutated network chromosome.
     */
    public NetworkChromosome addNeuron(NetworkChromosome parent) {
        NetworkChromosome ParentCopy = parent.copy();
        if (ParentCopy.getConnections().isEmpty()) return ParentCopy;
        
        ConnectionGene connection = ParentCopy.getConnections().get(random.nextInt(ParentCopy.getConnections().size()));
        connection.setEnabled(false);

        // Generate a unique ID for the new neuron
        int maxId = ParentCopy.getLayers().values().stream()
        .flatMap(List::stream)
        .mapToInt(NeuronGene::getId)
        .max()
        .orElse(0);
        int newNeuronId = maxId + 1;
        
        NeuronGene newNeuron = new NeuronGene(newNeuronId, ActivationFunction.TANH, NeuronType.HIDDEN);
        // Set the depth of the newly created Neuron
        double sourceDepth = connection.getSourceNeuron().getNeuronDepth();
        double targetDepth = connection.getTargetNeuron().getNeuronDepth();
        double newNeuronDepth = (sourceDepth + targetDepth) / 2.0;
        newNeuron.setNeuronDepth(newNeuronDepth);

        // add Neuron to exisiting layers
        ParentCopy.addNeuron(newNeuronDepth, newNeuron);

        // Replace the hardcoded innovation numbers with historical tracking:
        NetworkInnovation inInnov = Utils.createOrGetInnovation(connection.getSourceNeuron(), newNeuron, innovations);
        NetworkInnovation outInnov = Utils.createOrGetInnovation(newNeuron, connection.getTargetNeuron(), innovations);
        
        ConnectionGene firstNewConnection = new ConnectionGene(connection.getSourceNeuron(), newNeuron, 1.0, true, inInnov.getId());
        ConnectionGene secondNewConnection = new ConnectionGene(newNeuron, connection.getTargetNeuron(), connection.getWeight(), true, outInnov.getId());
        
        ParentCopy.addConnection(firstNewConnection);
        ParentCopy.addConnection(secondNewConnection);

        return ParentCopy;
    }

    /**
     * Adds a connection to the given network chromosome.
     * The source neuron of the connection is chosen randomly from the list of neurons in the network chromosome,
     * excluding output neurons.
     * The target neuron of the connection is chosen randomly from the list of neurons in the network chromosome,
     * excluding input and bias neurons.
     * The connection is added to the network chromosome with a random weight between -1.0 and 1.0.
     * The connection must not be recurrent.
     * <p>
     * Since this mutation changes the structure of the network,
     * novel innovations for the new connection must be created if the same mutation has not occurred before.
     * If the same innovation has occurred before, the corresponding innovation number must be reused.
     *
     * @param parent The network chromosome to which the new connection will be added.
     * @return The mutated network chromosome.
     */
    public NetworkChromosome addConnection(NetworkChromosome parent) {
        // Create a copy of the parent chromosome to modify
        NetworkChromosome parentCopy = parent.copy();

        List<NeuronGene> neurons = new ArrayList<>();
        parentCopy.getLayers().values().forEach(neurons::addAll);

        // Valid neuron lists for selection
        List<NeuronGene> possibleSources = new ArrayList<>();
        List<NeuronGene> possibleTargets = new ArrayList<>();

        for (NeuronGene neuron : neurons) {
            if (neuron.getNeuronType() != NeuronType.OUTPUT) {
                possibleSources.add(neuron);
            }
            if (neuron.getNeuronType() != NeuronType.INPUT && neuron.getNeuronType() != NeuronType.BIAS) {
                possibleTargets.add(neuron);
            }
        }

        // If there's no valid source or target, return without modifying
        if (possibleSources.isEmpty() || possibleTargets.isEmpty()) {
            return parentCopy;
        }

        // Shuffle neuron lists to ensure random selection without retries
        Collections.shuffle(possibleSources, random);
        Collections.shuffle(possibleTargets, random);

        // Find a valid source-target pair
        for (NeuronGene source : possibleSources) {
            for (NeuronGene target : possibleTargets) {
                if (source != target &&
                    source.getNeuronDepth() < target.getNeuronDepth() &&
                    !parentCopy.hasConnection(source, target)
                ) {

                    // Get or create an innovation for this connection
                    NetworkInnovation innovation = Utils.createOrGetInnovation(source, target, innovations);
                    
                    ConnectionGene newConnection = new ConnectionGene(
                                source,
                                target,
                                random.nextDouble() * 2 - 1,
                                true,
                                innovation.getId()
                            );
                    parentCopy.addConnection(newConnection);

                    return parentCopy; // Return after adding a valid connection
                }
            }
        }

        return parentCopy; // No valid connection found, return unchanged
    }

    /**
     * Mutates the weights of the connections in the given network chromosome.
     * The weight is mutated by adding gaussian noise to every weight in the network chromosome.
     *
     * @param parent The network chromosome to mutate.
     * @return The mutated network chromosome.
     */
    public NetworkChromosome mutateWeights(NetworkChromosome parent) {
        NetworkChromosome ParentCopy = parent.copy();
        for (ConnectionGene connection : ParentCopy.getConnections()) {
            if (random.nextDouble() < 0.1) { // 10% chance for large mutation
                connection.setWeight(random.nextDouble() * 2 - 1); 
            } else {
                connection.setWeight(connection.getWeight() + random.nextGaussian() * 0.3);
            }
        }
        return ParentCopy;
    }

    /**
     * Toggles the enabled status of a random connection in the given network chromosome.
     *
     * @param parent The network chromosome to mutate.
     * @return The mutated network chromosome.
     */
    public NetworkChromosome toggleConnection(NetworkChromosome parent) {
        NetworkChromosome ParentCopy = parent.copy();
        if (!parent.getConnections().isEmpty()) {
            ConnectionGene connection = ParentCopy.getConnections().get(random.nextInt(ParentCopy.getConnections().size()));
            boolean enabled = connection.getEnabled();
            connection.setEnabled(!enabled);
        }
        return ParentCopy;
    }

}
