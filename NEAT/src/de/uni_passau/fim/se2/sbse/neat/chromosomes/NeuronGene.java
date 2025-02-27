package de.uni_passau.fim.se2.sbse.neat.chromosomes;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

/**
 * Represents a neuron gene that is part of every NEAT chromosome.
 */
public class NeuronGene {
    private int id;
    private ActivationFunction activationFunction;
    private NeuronType neuronType;
    private double neuronDepth;

    /**
     * Creates a new neuron with the given ID and activation function.
     *
     * @param id                 The ID of the neuron.
     * @param activationFunction The activation function of the neuron.
     */
    public NeuronGene(int id, ActivationFunction activationFunction, NeuronType neuronType) {
        this.id = id;
        this.activationFunction = requireNonNull(activationFunction);
        this.neuronType = requireNonNull(neuronType);
    }

    public int getId() {
        return id;
    }

    public NeuronType getNeuronType() {
        return neuronType;
    }

    public double activate(double input) {
        // If this neuron is a bias neuron, always return 1.0
        if (neuronType == NeuronType.BIAS) {
            return 1.0;
        }

        switch(activationFunction) {
            case SIGMOID:
                return 1 / (1 + Math.exp(-input));
            case TANH:
                return Math.tanh(input);
            default:
                return 1.0;
        }
    }

    public NeuronGene copy() {
        NeuronGene neuron = new NeuronGene(this.id, this.activationFunction, this.neuronType);
        neuron.setNeuronDepth(getNeuronDepth());
        return neuron;
    }

    public double getNeuronDepth() {
        return neuronDepth;
    }

    public void setNeuronDepth(double value) {
        this.neuronDepth = requireNonNull(value);
    }

    public boolean equals(final Object other) {
        if (other == null|| getClass() != other.getClass()) {
            return false;
        }
        final NeuronGene that = (NeuronGene) other;
        return (
                this.getId() == that.getId() &&
                this.getNeuronDepth() == that.getNeuronDepth() &&
                this.getNeuronType().equals(that.getNeuronType())
            );
    }

    public int hashCode() {
        return Objects.hash(getId(), getNeuronDepth(), getNeuronType());
    }

}
