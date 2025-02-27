package de.uni_passau.fim.se2.sbse.neat.chromosomes;

import static java.util.Objects.requireNonNull;

/**
 * Represents a connection gene that is part of every NEAT chromosome.
 */
public class ConnectionGene {

    private NeuronGene sourcNeuronGene;
    private NeuronGene targetNeuronGene;
    private double weight;
    private boolean enabled;
    private int innovationNumber;

    /**
     * Creates a new connection gene with the given source and target neuron, weight, enabled flag, and innovation number.
     *
     * @param sourceNeuronGene The source neuron of the connection.
     * @param targetNeuronGene The target neuron of the connection.
     * @param weight           The weight of the connection.
     * @param enabled          Whether the connection is enabled.
     * @param innovationNumber The innovation number of the connection serving as identifier.
     */
    public ConnectionGene(
        NeuronGene sourceNeuronGene,
        NeuronGene targetNeuronGene,
        double weight,
        boolean enabled,
        int innovationNumber
    ) {
        this.sourcNeuronGene = requireNonNull(sourceNeuronGene);
        this.targetNeuronGene = requireNonNull(targetNeuronGene);
        this.weight = weight;
        this.enabled = enabled;
        this.innovationNumber = innovationNumber;
    }

    public NeuronGene getSourceNeuron() {
        return sourcNeuronGene;
    }

    public NeuronGene getTargetNeuron() {
        return targetNeuronGene;
    }

    public double getWeight() {
        return weight;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public int getInnovationNumber() {
        return innovationNumber;
    }

    public void setEnabled(boolean value) {
        this.enabled = value;
    }

    public void setWeight(double value) {
        this.weight = value;
    }

    public ConnectionGene copy() {
        ConnectionGene connection = new ConnectionGene(
            this.sourcNeuronGene,
            this.targetNeuronGene,
            this.weight,
            this.enabled,
            this.innovationNumber
        );
        connection.setWeight(getWeight());
        connection.setEnabled(getEnabled());
        return connection;
    }
}
