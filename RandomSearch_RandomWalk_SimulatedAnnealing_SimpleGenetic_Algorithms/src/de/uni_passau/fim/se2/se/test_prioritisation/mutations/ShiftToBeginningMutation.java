package de.uni_passau.fim.se2.se.test_prioritisation.mutations;

import de.uni_passau.fim.se2.se.test_prioritisation.encodings.TestOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A mutation that shifts a test to the beginning of the sequence.
 */
public class ShiftToBeginningMutation implements Mutation<TestOrder> {

    /**
     * The internal source of randomness.
     */
    private final Random random;

    public ShiftToBeginningMutation(final Random random) {
        this.random = random;
    }

    /**
     * Shifts a test to the beginning of the sequence.
     *
     * @param encoding the test order to be mutated
     * @return the mutated test order
     */
    @Override
    public TestOrder apply(TestOrder encoding) {
        int encoding_length = encoding.size();
        List<Integer> holder = new ArrayList<>();
        int[] oldTestOrder = encoding.getPositions();

        // get random index between indices 1 and encoding_length
        int randomIndex = random.nextInt(encoding_length);
        if(randomIndex == 0 && encoding_length > 1) {
            randomIndex += 1;
        }
        // reorder the test cases with a random test at beginning
        holder.add(oldTestOrder[randomIndex]);
        for(int i = 0; i < encoding_length; i++) {
            if(i != randomIndex) {
                holder.add(oldTestOrder[i]);
            }
        }
        int[] newTestOrder = new int[encoding_length];

        for(int i = 0; i < encoding_length; i++) {
            newTestOrder[i] = holder.get(i);
        }
        return new TestOrder(encoding.getMutation(), newTestOrder);
    }
}
