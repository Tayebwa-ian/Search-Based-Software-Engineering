package de.uni_passau.fim.se2.sbse.neat.algorithms.innovations;

import de.uni_passau.fim.se2.sbse.neat.chromosomes.ActivationFunction;
import de.uni_passau.fim.se2.sbse.neat.chromosomes.NeuronGene;
import de.uni_passau.fim.se2.sbse.neat.chromosomes.NeuronType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NetworkInnovationTest {

    private NetworkInnovation innovation;
    private NeuronGene source;
    private NeuronGene target;

    @BeforeEach
    public void setUp() {
        source = new NeuronGene(0, ActivationFunction.NONE, NeuronType.INPUT);
        target = new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.HIDDEN);
        innovation = new NetworkInnovation(100, source, target);
    }

    @Test
    public void testConstructor() {
        assertNotNull(innovation);
        assertEquals(100, innovation.getId());
        assertEquals(source, innovation.getSource());
        assertEquals(target, innovation.getTraget());
    }

    @Test
    public void testGetId() {
        assertEquals(100, innovation.getId());
    }

    @Test
    public void testGetSource() {
        assertEquals(source, innovation.getSource());
    }

    @Test
    public void testGetTarget() {
        assertEquals(target, innovation.getTraget());
    }

    @Test
    public void testExistsFlag_DefaultFalse() {
        assertFalse(innovation.getExists());
    }

    @Test
    public void testSetExists() {
        innovation.setExists(true);
        assertTrue(innovation.getExists());

        innovation.setExists(false);
        assertFalse(innovation.getExists());
    }

    @Test
    public void testHashCode() {
        NetworkInnovation sameIdInnovation = new NetworkInnovation(100, source, target);
        assertEquals(innovation.hashCode(), sameIdInnovation.hashCode());
    }

    @Test
    public void testNullArgumentsInConstructor() {
        assertThrows(NullPointerException.class, () -> new NetworkInnovation(100, null, target));
        assertThrows(NullPointerException.class, () -> new NetworkInnovation(100, source, null));
    }
}
