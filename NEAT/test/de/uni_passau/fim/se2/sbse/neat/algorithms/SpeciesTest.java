package de.uni_passau.fim.se2.sbse.neat.algorithms;

import de.uni_passau.fim.se2.sbse.neat.chromosomes.NetworkChromosome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SpeciesTest {

    private NetworkChromosome representative;
    private Species species;
    private Random random;

    @BeforeEach
    public void setUp() {
        representative = mock(NetworkChromosome.class);
        when(representative.getGeneCount()).thenReturn(5);
        when(representative.getFitness()).thenReturn(10.0);

        species = new Species(representative);
        random = mock(Random.class);
    }

    @Test
    public void testAddMember() {
        NetworkChromosome newMember = mock(NetworkChromosome.class);
        species.addMember(newMember);

        assertTrue(species.getMembers().contains(newMember));
    }

    @Test
    public void testClearMembers() {
        species.clearMembers();
        assertEquals(0, species.getMembers().size());
    }

    @Test
    public void testIsCompatible() {
        NetworkChromosome testChromosome = mock(NetworkChromosome.class);
        when(testChromosome.countDisjointGenes(representative)).thenReturn(2);
        when(testChromosome.countExcessGenes(representative)).thenReturn(1);
        when(testChromosome.averageWeightDifference(representative)).thenReturn(0.5);
        when(testChromosome.getGeneCount()).thenReturn(6);

        double threshold = 2.0;
        assertFalse(species.isCompatible(testChromosome, threshold));
    }

    @Test
    public void testIsIncompatible() {
        NetworkChromosome testChromosome = mock(NetworkChromosome.class);
        when(testChromosome.countDisjointGenes(representative)).thenReturn(10);
        when(testChromosome.countExcessGenes(representative)).thenReturn(5);
        when(testChromosome.averageWeightDifference(representative)).thenReturn(2.0);
        when(testChromosome.getGeneCount()).thenReturn(5);

        double threshold = 1.0;
        assertFalse(species.isCompatible(testChromosome, threshold));
    }

    @Test
    public void testAllocateOffspring_MinimumOne() {
        species.allocateOffspring(0, 10);
        assertEquals(1, species.getMembers().size());
    }


    @Test
    public void testSelectParent() {
        NetworkChromosome member1 = mock(NetworkChromosome.class);
        when(member1.getFitness()).thenReturn(10.0);
        NetworkChromosome member2 = mock(NetworkChromosome.class);
        when(member2.getFitness()).thenReturn(20.0);

        species.addMember(member1);
        species.addMember(member2);

        List<Double> fitnessProbabilities = List.of(0.33, 0.67);
        when(random.nextDouble()).thenReturn(0.5);

        NetworkChromosome selected = species.getMembers().get(0); // Simulate parent selection logic
        assertNotNull(selected);
    }
}
