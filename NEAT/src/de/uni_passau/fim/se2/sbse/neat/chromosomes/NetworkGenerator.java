package de.uni_passau.fim.se2.sbse.neat.chromosomes;

import de.uni_passau.fim.se2.sbse.neat.algorithms.innovations.Innovation;
import de.uni_passau.fim.se2.sbse.neat.algorithms.innovations.NetworkInnovation;
import de.uni_passau.fim.se2.sbse.neat.utils.Utils;

import java.util.*;

import static java.util.Objects.requireNonNull;

/**
 * Creates fully connected feed-forward neural networks consisting of one input and one output layer.
 */
public class NetworkGenerator {
    /**
     * The number to assign a new innovation
     */
    private int innovationNumber;

    /**
     * The number of desired input neurons.
     */
    private final int inputSize;

    /**
     * The number of desired output neurons.
     */
    private final int outputSize;

    /**
     * The random number generator.
     */
    private final Random random;

    /**
     * The set of innovations that occurred so far in the search.
     * Novel innovations created during the generation of the network must be added to this set.
     */
    private final Set<Innovation> innovations;

    /**
     * Creates a new network generator.
     *
     * @param innovations The set of innovations that occurred so far in the search.
     * @param inputSize   The number of desired input neurons.
     * @param outputSize  The number of desired output neurons.
     * @param random      The random number generator.
     * @throws NullPointerException if the random number generator is {@code null}.
     */
    public NetworkGenerator(Set<Innovation> innovations, int inputSize, int outputSize, Random random) {
        this.innovations = requireNonNull(innovations);
        this.inputSize = inputSize;
        this.outputSize = outputSize;
        this.random = requireNonNull(random);
        this.innovationNumber = innovations.size();
    }

    /**
     * Generates a new fully connected feed-forward network chromosome.
     *
     * @return a new network chromosome.
     */
    public NetworkChromosome generate() {
        Map<Double, List<NeuronGene>> layers = new HashMap<>();
        List<ConnectionGene> connections = new ArrayList<>();
        List<NeuronGene> inputNeurons = new ArrayList<>();
        List<NeuronGene> outputNeurons = new ArrayList<>();

        // Create input neurons
        for (int i = 0; i < inputSize; i++) {
            NeuronGene InNeuron = new NeuronGene(i, ActivationFunction.NONE, NeuronType.INPUT);
            InNeuron.setNeuronDepth(0.0);
            inputNeurons.add(InNeuron);
        }
        // Add a bias neuron
        NeuronGene baisNeuron = new NeuronGene(inputSize, ActivationFunction.NONE, NeuronType.BIAS);
        baisNeuron.setNeuronDepth(0.0);
        inputNeurons.add(baisNeuron);

        // Create output neurons
        for (int i = 1; i <= outputSize; i++) {
            NeuronGene outNeuron = new NeuronGene(inputSize + i, ActivationFunction.TANH, NeuronType.OUTPUT);
            outNeuron.setNeuronDepth(1.0);
            outputNeurons.add(outNeuron);
        }

        layers.put(NetworkChromosome.INPUT_LAYER, inputNeurons);
        layers.put(NetworkChromosome.OUTPUT_LAYER, outputNeurons);

        // Generate connections between input and output neurons
        for (NeuronGene inputNeuron : inputNeurons) {
            for (NeuronGene outputNeuron : outputNeurons) {
                // Check if the innovation already exists
                NetworkInnovation innovation = Utils.createOrGetInnovation(inputNeuron, outputNeuron, innovations);

                int assignedInnovationNumber;
                if (!innovation.getExists()) {
                    assignedInnovationNumber = innovation.getId();
                } else {
                    assignedInnovationNumber = innovationNumber++;
                    innovations.add(new NetworkInnovation(assignedInnovationNumber, inputNeuron, outputNeuron));
                }

                // Create the connection gene with a weight and innovation number
                double weight = random.nextDouble() * 2 - 1; // Random weight between -1 and 1
                ConnectionGene connection = new ConnectionGene(inputNeuron, outputNeuron, weight, true, assignedInnovationNumber);
                connections.add(connection);
            }
        }

        // Return the generated network chromosome
        return new NetworkChromosome(layers, connections);
    }
}
