package de.uni_passau.fim.se2.sbse.neat.chromosomes;

import org.junit.jupiter.api.Test;

import de.uni_passau.fim.se2.sbse.neat.algorithms.innovations.Innovation;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class NetworkGeneratorTest {

    @Test
    public void testConstructorValidInputs() {
        Set<Innovation> innovations = new HashSet<>();
        Random random = new Random();
        NetworkGenerator generator = new NetworkGenerator(innovations, 3, 2, random);

        assertNotNull(generator);
    }

    @Test
    public void testConstructorNullInnovationsThrowsException() {
        Random random = new Random();
        assertThrows(NullPointerException.class, () -> new NetworkGenerator(null, 3, 2, random));
    }

    @Test
    public void testConstructorNullRandomThrowsException() {
        Set<Innovation> innovations = new HashSet<>();
        assertThrows(NullPointerException.class, () -> new NetworkGenerator(innovations, 3, 2, null));
    }

    @Test
    public void testGenerateCreatesValidNetwork() {
        Set<Innovation> innovations = new HashSet<>();
        Random random = new Random();
        NetworkGenerator generator = new NetworkGenerator(innovations, 3, 2, random);
        NetworkChromosome chromosome = generator.generate();

        assertNotNull(chromosome);
        assertFalse(chromosome.getConnections().isEmpty());
        assertEquals(3 + 1, chromosome.getLayers().get(NetworkChromosome.INPUT_LAYER).size()); // Including bias neuron
        assertEquals(2, chromosome.getLayers().get(NetworkChromosome.OUTPUT_LAYER).size());
    }
}
