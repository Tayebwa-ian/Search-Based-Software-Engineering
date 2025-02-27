package de.uni_passau.fim.se2.sbse.neat.chromosomes;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class NeuronGeneTest {

    NeuronGene baisNeuron;
    NeuronGene inputNeuron;
    NeuronGene hiddenNeuron;
    NeuronGene outputNeuron;

    @BeforeEach
    public void setUp() {
        baisNeuron = new NeuronGene(0, ActivationFunction.NONE, NeuronType.BIAS);
        inputNeuron = new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.INPUT);
        hiddenNeuron = new NeuronGene(2, ActivationFunction.TANH, NeuronType.HIDDEN);
        outputNeuron = new NeuronGene(3, ActivationFunction.SIGMOID, NeuronType.OUTPUT);
    }

    @Test
    public void testConstructorException() {
        assertThrows(
            NullPointerException.class,
            () -> new NeuronGene(0, null, NeuronType.INPUT)
        );
        assertThrows(
            NullPointerException.class,
            () -> new NeuronGene(0, ActivationFunction.SIGMOID, null)
        );
    }

    @Test
    public void testGetId() {
        assertTrue(baisNeuron.getId() == 0);
        assertTrue(inputNeuron.getId() == 1);
        assertTrue(hiddenNeuron.getId() == 2);
        assertTrue(outputNeuron.getId() == 3);
    }

    @Test
    public void testGetNeuronType() {
        assertTrue(baisNeuron.getNeuronType() == NeuronType.BIAS);
        assertTrue(inputNeuron.getNeuronType() == NeuronType.INPUT);
        assertTrue(hiddenNeuron.getNeuronType() == NeuronType.HIDDEN);
        assertTrue(outputNeuron.getNeuronType() == NeuronType.OUTPUT);
    }
    
    @Test
    public void testCopyandEquals() {
        baisNeuron.setNeuronDepth(0.0);
        NeuronGene copy = baisNeuron.copy();
        assertTrue(baisNeuron.equals(copy));
        assertFalse(baisNeuron.equals(null));
        assertFalse(baisNeuron.equals(inputNeuron));
        assertFalse(baisNeuron.equals(new Object()));

        // change the depth
        copy.setNeuronDepth(0.2);
        assertFalse(baisNeuron.equals(copy));
    }

    @Test
    public void testActivate() {
        assertTrue(baisNeuron.activate(0.2) == 1.0);
        assertTrue(inputNeuron.activate(0.2) == 1 / (1 + Math.exp(-0.2)));
        assertTrue(outputNeuron.activate(0.2) == 1 / (1 + Math.exp(-0.2)));
        assertTrue(hiddenNeuron.activate(0.2) == Math.tanh(0.2));
    }

    @Test
    public void testHash(){
        assertTrue(
            inputNeuron.hashCode() ==
            Objects.hash(inputNeuron.getId(), inputNeuron.getNeuronDepth(), inputNeuron.getNeuronType())
        );
    }
}
