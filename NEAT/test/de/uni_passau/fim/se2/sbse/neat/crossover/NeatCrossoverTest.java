package de.uni_passau.fim.se2.sbse.neat.crossover;

import de.uni_passau.fim.se2.sbse.neat.chromosomes.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NeatCrossoverTest {

    private Random random;
    private NeatCrossover neatCrossover;
    private NetworkChromosome parent1;
    private NetworkChromosome parent2;

    @BeforeEach
    public void setUp() {
        random = mock(Random.class);
        neatCrossover = new NeatCrossover(random);

        Map<Double, List<NeuronGene>> layers = new HashMap<>();
        List<NeuronGene> inputLayer = List.of(new NeuronGene(0, ActivationFunction.NONE, NeuronType.INPUT));
        List<NeuronGene> outputLayer = List.of(new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.OUTPUT));
        layers.put(NetworkChromosome.INPUT_LAYER, new ArrayList<>(inputLayer));
        layers.put(NetworkChromosome.OUTPUT_LAYER, new ArrayList<>(outputLayer));

        List<ConnectionGene> connections1 = List.of(
            new ConnectionGene(inputLayer.get(0), outputLayer.get(0), 0.5, true, 1),
            new ConnectionGene(inputLayer.get(0), outputLayer.get(0), -0.3, true, 2) // Matching gene
        );

        List<ConnectionGene> connections2 = List.of(
            new ConnectionGene(inputLayer.get(0), outputLayer.get(0), 0.8, true, 2), // Matching gene
            new ConnectionGene(inputLayer.get(0), outputLayer.get(0), 1.2, false, 3) // Disjoint gene
        );

        parent1 = new NetworkChromosome(layers, new ArrayList<>(connections1));
        parent1.setFitness(5.0);

        parent2 = new NetworkChromosome(layers, new ArrayList<>(connections2));
        parent2.setFitness(3.0);
    }

    @Test
    public void testInvalidConstructorInputs() {
        assertThrows(
            NullPointerException.class,
            () -> new NeatCrossover(null)
        );
    }

    @Test
    public void testFitterParentIsSelected() {
        when(random.nextBoolean()).thenReturn(true); // Ensure random selection for matching genes

        NetworkChromosome child = neatCrossover.apply(parent1, parent2);

        assertTrue(child.getConnections().size() >= 2); // At least matching genes + fitter parent's excess genes
        assertTrue(child.getConnections().stream().anyMatch(c -> c.getInnovationNumber() == 1)); // Parent1’s exclusive gene
        assertFalse(child.getConnections().stream().anyMatch(c -> c.getInnovationNumber() == 3)); // Parent2’s disjoint gene shouldn't be inherited
    }

    @Test
    public void testMatchingGeneSelection() {
        when(random.nextBoolean()).thenReturn(false); // Force selection from parent2 for matching genes

        NetworkChromosome child = neatCrossover.apply(parent1, parent2);

        assertEquals(2, child.getConnections().size()); // Should only inherit matching genes + fitter's genes
        assertTrue(child.getConnections().stream().anyMatch(c -> c.getInnovationNumber() == 2)); // Matching gene must be included
    }

    @Test
    public void testWeightPerturbation() {
        when(random.nextBoolean()).thenReturn(true); // Select genes randomly
        when(random.nextDouble()).thenReturn(0.05); // Ensure slight mutation to weights

        NetworkChromosome child = neatCrossover.apply(parent1, parent2);

        ConnectionGene gene = child.getConnections().get(0);
        assertNotEquals(0.5, gene.getWeight()); // Ensure weight mutation occurred
    }

    @Test
    public void testBothParentsSameFitness() {
        parent2.setFitness(5.0); // Equal fitness

        when(random.nextBoolean()).thenReturn(true); // Select randomly for matching genes

        NetworkChromosome child = neatCrossover.apply(parent1, parent2);

        assertEquals(2, child.getConnections().size()); // Should include matching genes
        assertTrue(child.getConnections().stream().anyMatch(c -> c.getInnovationNumber() == 2)); // Matching gene must be present
    }
}
