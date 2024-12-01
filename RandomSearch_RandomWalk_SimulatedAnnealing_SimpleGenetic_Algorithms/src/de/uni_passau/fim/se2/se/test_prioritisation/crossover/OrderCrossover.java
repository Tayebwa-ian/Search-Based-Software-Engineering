package de.uni_passau.fim.se2.se.test_prioritisation.crossover;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;

import java.util.*;

public class OrderCrossover implements Crossover<TestOrder> {

     /**
     * The internal source of randomness.
     */
    private final Random random;

    /**
     * Creates a new order crossover operator.
     *
     * @param random the internal source of randomness
     */
    public OrderCrossover(final Random random) {
        this.random = random;
    }

    /**
     * Combines two parent encodings to create a new offspring encoding using the order crossover operation.
     * The order crossover corresponds to a two-point crossover where the section between two random indices is copied
     * from the first parent and the remaining alleles are added in the order they appear in the second parent.
     * The resulting children must correspond to a valid test order encoding of size n that represents a permutation of tests
     * where each test value in the range [0, n-1] appears exactly once.
     *
     * @param parent1 the first parent encoding
     * @param parent2 the second parent encoding
     * @return the offspring encoding
     */
    @Override
    public TestOrder apply(TestOrder parent1, TestOrder parent2) {
        int n = parent1.size();
        int m = parent2.size();
        if (m != n) {
            throw new IllegalArgumentException("Test order must have the same number of tests");
        }

        // Generate two random crossover points
        int crossoverPoint1 = random.nextInt(n);
        int crossoverPoint2 = random.nextInt(n);

        // Ensure crossoverPoint1 is always less than crossoverPoint2
        if (crossoverPoint1 > crossoverPoint2) {
            int temp = crossoverPoint1;
            crossoverPoint1 = crossoverPoint2;
            crossoverPoint2 = temp;
        }
        // create a set to uniquely store the tests without duplication
        HashSet<Integer> holder = new HashSet<>();
        List<Integer> arrayHolder = new ArrayList<>();

        // Copy the section between crossover points from parent1
        // put the test in a set to insure they are unique
        while (crossoverPoint1 <= crossoverPoint2) {
            holder.add(parent1.getPositions()[crossoverPoint1]);
            arrayHolder.add(parent1.getPositions()[crossoverPoint1++]);
        }
        for (int i = 0; i < m; i++) {
            if(holder.add(parent2.getPositions()[i])) {
                arrayHolder.add(parent2.getPositions()[i]);
            }
        }
        int stopindex = crossoverPoint2 - crossoverPoint1;
		int[] offspring = new int[m];

        // convert the set into an array with the right arrangement
        int index = 0;
        int count = 0;
        int incr =  crossoverPoint1;
        for (Integer num : arrayHolder) {
            if(count < (stopindex + 1)) {
                offspring[incr++] = num;
                count += 1;
            } else {
                if(index == crossoverPoint1) {
                    index += (stopindex + 1);
                }
                offspring[index++] = num;
            }
        }
        return new TestOrder(parent2.getMutation(), offspring);
        }
}
