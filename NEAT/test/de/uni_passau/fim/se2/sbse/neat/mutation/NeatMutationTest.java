package de.uni_passau.fim.se2.sbse.neat.mutation;

import de.uni_passau.fim.se2.sbse.neat.algorithms.innovations.Innovation;
import de.uni_passau.fim.se2.sbse.neat.chromosomes.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class NeatMutationTest {

    private Random random;
    private Set<Innovation> innovations;
    private NeatMutation mutation;
    private NetworkChromosome testNetwork;

    @BeforeEach
    public void setUp() {
        random = new Random(42);  // Fixed seed for reproducibility
        innovations = new HashSet<>();
        mutation = new NeatMutation(innovations, random);
        testNetwork = createTestNetwork();
    }

    private NetworkChromosome createTestNetwork() {
        Map<Double, List<NeuronGene>> layers = new HashMap<>();
        List<ConnectionGene> connections = new ArrayList<>();

        // Create input, hidden, and output neurons
        NeuronGene input1 = new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.INPUT);
        NeuronGene input2 = new NeuronGene(2, ActivationFunction.SIGMOID, NeuronType.INPUT);
        NeuronGene bias = new NeuronGene(3, ActivationFunction.SIGMOID, NeuronType.BIAS);
        layers.put(NetworkChromosome.INPUT_LAYER, Arrays.asList(input1, input2, bias));

        NeuronGene hidden = new NeuronGene(4, ActivationFunction.SIGMOID, NeuronType.HIDDEN);
        layers.put(1.0, List.of(hidden));

        NeuronGene output = new NeuronGene(5, ActivationFunction.SIGMOID, NeuronType.OUTPUT);
        layers.put(NetworkChromosome.OUTPUT_LAYER, List.of(output));

        // Create connections
        connections.add(new ConnectionGene(input1, hidden, 0.5, true, 1));
        connections.add(new ConnectionGene(input2, hidden, 0.3, true, 2));
        connections.add(new ConnectionGene(hidden, output, 0.6, true, 3));

        return new NetworkChromosome(layers, connections);
    }

    @Test
    public void testInvalidConstructorInputs() {
        assertThrows(
            NullPointerException.class,
            () -> new NeatMutation(null, random)
        );
        assertThrows(
            NullPointerException.class,
            () -> new NeatMutation(innovations, null)
        );
    }

    @Test
    public void testApply_AddNeuronMutation() {
        random = new Random() {
            @Override
            public double nextDouble() {
                return 0.01;  // Ensure addNeuron mutation is applied
            }
        };
        mutation = new NeatMutation(innovations, random);
        NetworkChromosome result = mutation.apply(testNetwork);
        assertTrue(countNeurons(result) > countNeurons(testNetwork), "Expected a new neuron to be added.");
    }

    @Test
    public void testApply_AddConnectionMutation() {
        random = new Random() {
            @Override
            public double nextDouble() {
                return 0.04;  // Ensure addConnection mutation is applied
            }
        };
        mutation = new NeatMutation(innovations, random);
        NetworkChromosome result = mutation.apply(testNetwork);
        assertTrue(result.getConnections().size() > testNetwork.getConnections().size(), "Expected a new connection to be added.");
    }

    @Test
    public void testApply_MutateWeights() {
        random = new Random() {
            @Override
            public double nextDouble() {
                return 0.7;  // Ensure weight mutation happens
            }
        };
        mutation = new NeatMutation(innovations, random);
        NetworkChromosome result = mutation.apply(testNetwork);
        assertNotEquals(testNetwork.getConnections().get(0).getWeight(), result.getConnections().get(0).getWeight(), "Expected connection weights to change.");
    }

    @Test
    public void testApply_ToggleConnection() {
        random = new Random() {
            @Override
            public double nextDouble() {
                return 0.09;  // Ensure toggleConnection mutation is triggered
            }
        };
        mutation = new NeatMutation(innovations, random);
        NetworkChromosome result = mutation.apply(testNetwork);
        
        // Check if at least one connection has its enabled status toggled
        boolean anyToggled = false;
        for (int i = 0; i < testNetwork.getConnections().size(); i++) {
            if (testNetwork.getConnections().get(i).getEnabled() != result.getConnections().get(i).getEnabled()) {
                anyToggled = true;
                break;
            }
        }
        assertTrue(anyToggled, "Expected a connection to be toggled.");
    }


    @Test
    public void testAddNeuron_NoConnections() {
        NetworkChromosome emptyNetwork = new NetworkChromosome(new HashMap<>(), new ArrayList<>());
        NetworkChromosome result = mutation.addNeuron(emptyNetwork);
        assertEquals(0, result.getConnections().size(), "Expected no neuron to be added in an empty network.");
    }

    @Test
    public void testAddConnection_FullyConnected() {
        NetworkChromosome fullyConnected = createFullyConnectedNetwork();
        NetworkChromosome result = mutation.addConnection(fullyConnected);
        assertEquals(fullyConnected.getConnections().size(), result.getConnections().size(), "Expected no connection to be added in a fully connected network.");
    }

    @Test
    public void testMutateWeights_WithinBounds() {
        NetworkChromosome result = mutation.mutateWeights(testNetwork);
        for (ConnectionGene conn : result.getConnections()) {
            assertTrue(conn.getWeight() >= -2.0 && conn.getWeight() <= 2.0, "Expected weights to remain within [-2.0, 2.0].");
        }
    }

    @Test
    public void testToggleConnection_NoConnections() {
        NetworkChromosome emptyNetwork = new NetworkChromosome(new HashMap<>(), new ArrayList<>());
        NetworkChromosome result = mutation.toggleConnection(emptyNetwork);
        assertEquals(0, result.getConnections().size(), "Expected no connections to be toggled in an empty network.");
    }

    // Helper methods for neuron and connection counting
    private int countNeurons(NetworkChromosome network) {
        return network.getLayers().values().stream().mapToInt(List::size).sum();
    }

    private NetworkChromosome createFullyConnectedNetwork() {
        Map<Double, List<NeuronGene>> layers = new HashMap<>();
        List<ConnectionGene> connections = new ArrayList<>();

        NeuronGene input = new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.INPUT);
        NeuronGene hidden = new NeuronGene(2, ActivationFunction.SIGMOID, NeuronType.HIDDEN);
        NeuronGene output = new NeuronGene(3, ActivationFunction.SIGMOID, NeuronType.OUTPUT);

        layers.put(NetworkChromosome.INPUT_LAYER, List.of(input));
        layers.put(1.0, List.of(hidden));
        layers.put(NetworkChromosome.OUTPUT_LAYER, List.of(output));

        connections.add(new ConnectionGene(input, hidden, 0.5, true, 1));
        connections.add(new ConnectionGene(input, output, 0.5, true, 2));
        connections.add(new ConnectionGene(hidden, output, 0.5, true, 3));

        return new NetworkChromosome(layers, connections);
    }
}
