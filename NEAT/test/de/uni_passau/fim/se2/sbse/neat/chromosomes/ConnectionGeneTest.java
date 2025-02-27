package de.uni_passau.fim.se2.sbse.neat.chromosomes;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConnectionGeneTest {

    @Test
    public void testConstructorValidInputs() {
        NeuronGene source = new NeuronGene(0, ActivationFunction.NONE, NeuronType.INPUT);
        NeuronGene target = new NeuronGene(1, ActivationFunction.TANH, NeuronType.OUTPUT);
        ConnectionGene gene = new ConnectionGene(source, target, 0.5, true, 1);

        assertEquals(source, gene.getSourceNeuron());
        assertEquals(target, gene.getTargetNeuron());
        assertEquals(0.5, gene.getWeight());
        assertTrue(gene.getEnabled());
        assertEquals(1, gene.getInnovationNumber());
    }

    @Test
    public void testConstructorNullSourceThrowsException() {
        NeuronGene target = new NeuronGene(2, ActivationFunction.SIGMOID, NeuronType.HIDDEN);
        assertThrows(NullPointerException.class, () -> new ConnectionGene(null, target, 0.5, true, 1));
    }

    @Test
    public void testConstructorNullTargetThrowsException() {
        NeuronGene source = new NeuronGene(4, ActivationFunction.NONE, NeuronType.INPUT);
        assertThrows(NullPointerException.class, () -> new ConnectionGene(source, null, 0.5, true, 1));
    }

    @Test
    public void testSetEnabled() {
        NeuronGene source = new NeuronGene(5, ActivationFunction.SIGMOID, NeuronType.HIDDEN);
        NeuronGene target = new NeuronGene(6, ActivationFunction.TANH, NeuronType.OUTPUT);
        ConnectionGene gene = new ConnectionGene(source, target, 0.5, true, 1);

        gene.setEnabled(false);
        assertFalse(gene.getEnabled());
    }

    @Test
    public void testSetWeight() {
        NeuronGene source = new NeuronGene(7, ActivationFunction.SIGMOID, NeuronType.HIDDEN);
        NeuronGene target = new NeuronGene(8, ActivationFunction.TANH, NeuronType.OUTPUT);
        ConnectionGene gene = new ConnectionGene(source, target, 0.5, true, 1);

        gene.setWeight(1.5);
        assertEquals(1.5, gene.getWeight());
    }

    @Test
    public void testCopyCreatesIdenticalObject() {
        NeuronGene source = new NeuronGene(3, ActivationFunction.NONE, NeuronType.INPUT);
        NeuronGene target = new NeuronGene(10, ActivationFunction.SIGMOID, NeuronType.HIDDEN);
        ConnectionGene original = new ConnectionGene(source, target, 0.5, true, 1);
        ConnectionGene copy = original.copy();

        assertNotSame(original, copy);
        assertEquals(original.getSourceNeuron(), copy.getSourceNeuron());
        assertEquals(original.getTargetNeuron(), copy.getTargetNeuron());
        assertEquals(original.getWeight(), copy.getWeight());
        assertEquals(original.getEnabled(), copy.getEnabled());
        assertEquals(original.getInnovationNumber(), copy.getInnovationNumber());
    }
}
