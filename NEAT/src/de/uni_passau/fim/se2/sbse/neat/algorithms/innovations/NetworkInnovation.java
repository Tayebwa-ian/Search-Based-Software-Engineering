package de.uni_passau.fim.se2.sbse.neat.algorithms.innovations;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import de.uni_passau.fim.se2.sbse.neat.chromosomes.NeuronGene;

public class NetworkInnovation implements Innovation {

    private int id;
    private NeuronGene source;
    private NeuronGene target;
    private boolean exists = false;

    /**
     * Creates are new Innovation
     */
    public NetworkInnovation(int id, NeuronGene source, NeuronGene target) {
        this.id = requireNonNull(id);
        this.source = requireNonNull(source);
        this.target = requireNonNull(target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public int getId() {
        return id;
    }

    public NeuronGene getSource() {
        return source;
    }

    public NeuronGene getTraget() {
        return target;
    }

    public void setExists(boolean value) {
        this.exists = value;
    }

    public boolean getExists() {
        return exists;
    }
}
