package de.uni_passau.fim.se2.se.hillclimbing;

import java.util.List;

public class ScheduleGeneratorTest {
    public static  void testGenerateSchedule () {
        // Test with empty array
        int[] emptyArray = {};
        try {
            ScheduleGenerator.generateSchedule(emptyArray, 2);
            System.out.println("Test failed: Empty array should throw exception");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Empty array exception thrown");
        }

        // Test with null array
        try {
            ScheduleGenerator.generateSchedule(null, 2);
            System.out.println("Test failed: Null array should throw exception");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Null array exception thrown");
        }

        // Test with negative number of machines
        try {
            ScheduleGenerator.generateSchedule(new int[]{1, 2, 3}, -1);
            System.out.println("Test failed: Negative machines should throw exception");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Negative machines exception thrown");
        }

        // Test with zero number of machines
        try {
            ScheduleGenerator.generateSchedule(new int[]{1, 2, 3}, 0);
            System.out.println("Test failed: Zero machines should throw exception");
        } catch (IllegalArgumentException e) {
            System.out.println("Test passed: Zero machines exception thrown");
        }

        // Test with valid input
        int[] jobs = {1, 2, 3, 4, 5, 6};
        int numMachines = 3;
        List<List<Integer>> schedule = ScheduleGenerator.generateSchedule(jobs, numMachines);
        if (schedule.size() == numMachines) {
            System.out.println("Test passed: Schedule generated successfully");
        } else {
            System.out.println("Test failed: Schedule not generated");
        }
    }
}
