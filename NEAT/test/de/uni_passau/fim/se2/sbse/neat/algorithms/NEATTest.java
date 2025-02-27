package de.uni_passau.fim.se2.sbse.neat.algorithms;

import de.uni_passau.fim.se2.sbse.neat.algorithms.innovations.Innovation;
import de.uni_passau.fim.se2.sbse.neat.chromosomes.NetworkChromosome;
import de.uni_passau.fim.se2.sbse.neat.chromosomes.NetworkGenerator;
import de.uni_passau.fim.se2.sbse.neat.crossover.NeatCrossover;
import de.uni_passau.fim.se2.sbse.neat.environments.SinglePoleBalancing;
import de.uni_passau.fim.se2.sbse.neat.environments.XOR;
import de.uni_passau.fim.se2.sbse.neat.mutation.NeatMutation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class NEATTest {

    private NEAT neat;
    private Random random;
    private NeatCrossover crossover;
    private NeatMutation mutation;
    private NetworkGenerator generator;
    private SinglePoleBalancing env;
    private XOR env2;
    private Set<Innovation> innovations;

    @BeforeEach
    public void setUp() {
        random = new Random(42); // Fixed seed for consistency
        crossover = new NeatCrossover(random); // Assuming a default constructor
        innovations = new HashSet<>();
        mutation = new NeatMutation(innovations, random); // Assuming a default constructor
        env = new SinglePoleBalancing(500, 1, true, random);
        generator = new NetworkGenerator(innovations, env.stateSize(), env.actionInputSize(), random);

        neat = new NEAT(random, crossover, mutation, generator, 50, 50);
    }

    @Test
    public void testWithInvalidConstructorInputs() {
        assertThrows(
            NullPointerException.class,
            () -> new NEAT(random, crossover, mutation, null, 5, 10)
        );
        assertThrows(
            NullPointerException.class,
            () -> new NEAT(null, crossover, mutation, generator, 5, 10)
        );
        assertThrows(
            NullPointerException.class,
            () -> new NEAT(random, crossover, null, generator, 5, 10)
        );
        assertThrows(
            NullPointerException.class,
            () -> new NEAT(random, null, mutation, generator, 5, 10)
        );
    }

    @Test
    public void testSolveTerminatesWhenSolutionFound() {
        NetworkChromosome result = neat.solve(env);
        assertNotNull(result);
    }

    @Test
    public void testSolveRespectsMaxGeneration() {
        NetworkChromosome result = neat.solve(env);
        assertNotNull(result);
        assertTrue(neat.getGeneration() <= 50);
    }

    @Test
    public void testWithXOREnv() {
        env2 = new XOR();
        neat = new NEAT(random, crossover, mutation, generator, 50, 50);
        NetworkChromosome result = neat.solve(env2);
        assertNotNull(result);
    }
}
