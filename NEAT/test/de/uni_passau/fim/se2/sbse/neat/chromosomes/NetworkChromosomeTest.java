package de.uni_passau.fim.se2.sbse.neat.chromosomes;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class NetworkChromosomeTest {

    @Test
    public void testConstructorValidInputs() {
        Map<Double, List<NeuronGene>> layers = new HashMap<>();
        List<ConnectionGene> connections = new ArrayList<>();
        NetworkChromosome chromosome = new NetworkChromosome(layers, connections);
        
        assertNotNull(chromosome);
        assertEquals(layers, chromosome.getLayers());
        assertEquals(connections, chromosome.getConnections());
    }

    @Test
    public void testConstructorNullLayersThrowsException() {
        List<ConnectionGene> connections = new ArrayList<>();
        assertThrows(NullPointerException.class, () -> new NetworkChromosome(null, connections));
    }

    @Test
    public void testConstructorNullConnectionsThrowsException() {
        Map<Double, List<NeuronGene>> layers = new HashMap<>();
        assertThrows(NullPointerException.class, () -> new NetworkChromosome(layers, null));
    }

    @Test
    public void testAddNeuron() {
        Map<Double, List<NeuronGene>> layers = new HashMap<>();
        layers.put(0.0, new ArrayList<>());
        NetworkChromosome chromosome = new NetworkChromosome(layers, new ArrayList<>());
        
        NeuronGene neuron = new NeuronGene(1, ActivationFunction.NONE, NeuronType.HIDDEN);
        chromosome.addNeuron(0.0, neuron);
        
        assertTrue(chromosome.getLayers().get(0.0).contains(neuron));
    }

    @Test
    public void testCopy() {
        Map<Double, List<NeuronGene>> layers = new HashMap<>();
        List<ConnectionGene> connections = new ArrayList<>();
        NetworkChromosome chromosome = new NetworkChromosome(layers, connections);
        chromosome.setFitness(5.0);
        
        NetworkChromosome copy = chromosome.copy();
        
        assertNotSame(chromosome, copy);
        assertEquals(chromosome.getFitness(), copy.getFitness());
    }

    @Test
    public void testHasConnection() {
        NeuronGene source = new NeuronGene(1, ActivationFunction.NONE, NeuronType.INPUT);
        NeuronGene target = new NeuronGene(2, ActivationFunction.TANH, NeuronType.OUTPUT);
        ConnectionGene connection = new ConnectionGene(source, target, 0.5, true, 1);
        
        NetworkChromosome chromosome = new NetworkChromosome(new HashMap<>(), new ArrayList<>(List.of(connection)));
        
        assertTrue(chromosome.hasConnection(source, target));
        assertFalse(chromosome.hasConnection(target, source));
    }
}
