package de.uni_passau.fim.se2.sbse.neat.chromosomes;


import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Represents a network chromosome in the NEAT algorithm.
 */
public class NetworkChromosome implements Agent {

    public static final double INPUT_LAYER = 0;
    public static final double OUTPUT_LAYER = 1;

    /**
     * Maps the layer number to a list of neurons in that layer, with zero representing the input layer and one the output layer.
     * All hidden layers between the input and output layer are represented by values between zero and one.
     * For instance, if a new neuron gets added between the input and output layer, it might get the layer number 0.5.
     */
    private final Map<Double, List<NeuronGene>> layers;

    /**
     * Hosts all connections of the network.
     */
    private final List<ConnectionGene> connections;
    private double fitness;

    /**
     * Creates a new network chromosome with the given layers and connections.
     *
     * @param layers      The layers of the network.
     * @param connections The connections of the network.
     */
    public NetworkChromosome(Map<Double, List<NeuronGene>> layers, List<ConnectionGene> connections) {
        this.layers = requireNonNull(layers);
        this.connections = requireNonNull(connections);
    }

    public Map<Double, List<NeuronGene>> getLayers() {
        return layers;
    }

    public void addNeuron(double layer, NeuronGene value) {
        if (layers.get(layer) != null) {
            layers.get(layer).add(value);
        } else {
            List<NeuronGene> newList = new ArrayList<>();
            newList.add(value);
            layers.put(layer, newList);
        }
    }

    public List<ConnectionGene> getConnections() {
        return connections;
    }

    @Override
    public List<Double> getOutput(List<Double> state) {
        Map<Integer, Double> neuronValues = new HashMap<>();
        for (int i = 0; i < state.size(); i++) {
            neuronValues.put(i, state.get(i));
        }

        for (ConnectionGene connection : connections) {
            if (connection.getEnabled()) {
                double inputVal = neuronValues.getOrDefault(connection.getSourceNeuron().getId(), 0.0);
                double weightedVal = inputVal * connection.getWeight();
                neuronValues.merge(connection.getTargetNeuron().getId(), weightedVal, Double::sum);
            }
        }

        List<Double> outputValues = new ArrayList<>();
        for (NeuronGene neuron : layers.get(OUTPUT_LAYER)) {
            outputValues.add(neuron.activate(neuronValues.getOrDefault(neuron.getId(), 0.0)));
        }

        return outputValues;
    }

    @Override
    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    @Override
    public double getFitness() {
        return fitness;
    }

    public NetworkChromosome copy() {
        // Deep copy of layers
        Map<Double, List<NeuronGene>> copiedLayers = new HashMap<>();
        for (Map.Entry<Double, List<NeuronGene>> entry : getLayers().entrySet()) {
            List<NeuronGene> copiedNeuronList = new ArrayList<>();
            for (NeuronGene neuron : entry.getValue()) {
                copiedNeuronList.add(neuron.copy());
            }
            copiedLayers.put(entry.getKey(), copiedNeuronList);
        }

        // Deep copy of connections
        List<ConnectionGene> copiedConnections = new ArrayList<>();
        for (ConnectionGene connection : getConnections()) {
            copiedConnections.add(connection.copy());
        }

        NetworkChromosome copy = new NetworkChromosome(copiedLayers, copiedConnections);

        // Add fitness value
        copy.setFitness(getFitness());

        // Return the new NetworkChromosome with copied layers, connections, and fitness
        return copy;
    }

    public void addConnection(ConnectionGene connection) {
        this.connections.add(connection);
    }

    public boolean hasConnection(NeuronGene source, NeuronGene target) {
        boolean checker = false;
        for (ConnectionGene conn : connections) {
            if (
                conn.getSourceNeuron().equals(source) &&
                conn.getTargetNeuron().equals(target)
            ) {
                checker = true;
            }
        }
        return checker;
    }

    /**
     * Returns the total number of genes (connections).
     */
    public int getGeneCount() {
        return connections.size();
    }


    /**
     * Counts the number of disjoint genes compared to another chromosome.
     */
    public int countDisjointGenes(NetworkChromosome other) {
        Set<Integer> thisInnovationNumbers = new HashSet<>();
        Set<Integer> otherInnovationNumbers = new HashSet<>();

        for (ConnectionGene conn : connections) {
            thisInnovationNumbers.add(conn.getInnovationNumber());
        }
        for (ConnectionGene conn : other.getConnections()) {
            otherInnovationNumbers.add(conn.getInnovationNumber());
        }

        int maxThisInnovation = thisInnovationNumbers.isEmpty() ? 0 : Collections.max(thisInnovationNumbers);
        int maxOtherInnovation = otherInnovationNumbers.isEmpty() ? 0 : Collections.max(otherInnovationNumbers);

        int disjointCount = 0;
        for (int innov : thisInnovationNumbers) {
            if (!otherInnovationNumbers.contains(innov) && innov < maxOtherInnovation) {
                disjointCount++;
            }
        }
        for (int innov : otherInnovationNumbers) {
            if (!thisInnovationNumbers.contains(innov) && innov < maxThisInnovation) {
                disjointCount++;
            }
        }
        return disjointCount;
    }

    /**
     * Counts the number of excess genes compared to another chromosome.
     */
    public int countExcessGenes(NetworkChromosome other) {
        Set<Integer> thisInnovationNumbers = new HashSet<>();
        Set<Integer> otherInnovationNumbers = new HashSet<>();

        for (ConnectionGene conn : connections) {
            thisInnovationNumbers.add(conn.getInnovationNumber());
        }
        for (ConnectionGene conn : other.getConnections()) {
            otherInnovationNumbers.add(conn.getInnovationNumber());
        }

        int maxThisInnovation = thisInnovationNumbers.isEmpty() ? 0 : Collections.max(thisInnovationNumbers);
        int maxOtherInnovation = otherInnovationNumbers.isEmpty() ? 0 : Collections.max(otherInnovationNumbers);

        int excessCount = 0;
        for (int innov : thisInnovationNumbers) {
            if (!otherInnovationNumbers.contains(innov) && innov > maxOtherInnovation) {
                excessCount++;
            }
        }
        for (int innov : otherInnovationNumbers) {
            if (!thisInnovationNumbers.contains(innov) && innov > maxThisInnovation) {
                excessCount++;
            }
        }
        return excessCount;
    }

    /**
     * Computes the average weight difference between matching genes in two chromosomes.
     */
    public double averageWeightDifference(NetworkChromosome other) {
        Map<Integer, Double> thisWeights = new HashMap<>();
        Map<Integer, Double> otherWeights = new HashMap<>();

        for (ConnectionGene conn : connections) {
            thisWeights.put(conn.getInnovationNumber(), conn.getWeight());
        }
        for (ConnectionGene conn : other.getConnections()) {
            otherWeights.put(conn.getInnovationNumber(), conn.getWeight());
        }

        int matchingGenes = 0;
        double totalWeightDiff = 0.0;
        for (int innov : thisWeights.keySet()) {
            if (otherWeights.containsKey(innov)) {
                totalWeightDiff += Math.abs(thisWeights.get(innov) - otherWeights.get(innov));
                matchingGenes++;
            }
        }

        return matchingGenes == 0 ? 0.0 : totalWeightDiff / matchingGenes;
    }
}
