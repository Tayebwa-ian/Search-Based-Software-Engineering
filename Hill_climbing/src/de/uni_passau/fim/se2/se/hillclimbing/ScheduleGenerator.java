package de.uni_passau.fim.se2.se.hillclimbing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Generates random schedules for the hill climbing algorithm.
 */
public class ScheduleGenerator {

    /**
     * Generates a random schedule for the given jobs and number of machines.
     *
     * @param jobs        the jobs to schedule
     * @param numMachines the number of machines
     * @return a random schedule
     */
    public static List<List<Integer>> generateSchedule(int[] jobs, int numMachines) {
        
        // Convert the array to a list for easier shuffling
        List<Integer> jobsList = new ArrayList<>();
        for (int job : jobs) {
            jobsList.add(job);
        }

        // Shuffle the tasks randomly
        Collections.shuffle(jobsList, new Random());

        // Create a list of lists to represent the schedule(machines)
        List<List<Integer>> schedule = new ArrayList<>();
        for (int i = 1; i < numMachines; i++) {
            schedule.add(new ArrayList<>());
        }    
        
        // Assign tasks to machines in a round-robin fashion
        int machineIndex = 0;
        for (int job : jobsList) {
            schedule.get(machineIndex).add(job);
            machineIndex = (machineIndex + 1) % numMachines;
        }

        return schedule;
    }

    /**
     * Generate all neighbors of the current schedule by moving a single from one machine to another.
     *
     * @param schedule the current schedule
     * @return a list of all neighbors of the current schedule
     */
    public static List<List<List<Integer>>> getNeighbors(List<List<Integer>> schedule) {
        // making sure the list of lists of integers is always mutable
        List<List<Integer>> temp = new ArrayList<>();
        for (int i = 0; i < schedule.size(); i++) {
            List<Integer> temp2 = new ArrayList<>();
            for (int j = 0; j < schedule.get(i).size(); j++) {
                temp2.add(schedule.get(i).get(j));
            }
            temp.add(temp2);
        }

        schedule = temp;

        List<List<List<Integer>>> neighbors = new ArrayList<>();
        if (schedule.size() != 0 ) {
            neighbors.add(schedule); // add schedule as intial neighbor

            for (int i = 0; i < schedule.size(); i++) {
                for (int j = 0; j < schedule.size(); j++) {
                    if (i == j) {
                        continue;
                    }

                    for (int k = 0; k < schedule.get(i).size(); k++) {
                        List<List<Integer>> newSchedule = new ArrayList<>(schedule);
                        int job = newSchedule.get(i).remove(k);
                        newSchedule.get(j).add(job);
                        neighbors.add(newSchedule);
                    }
                }
            }
        }
        return neighbors;
    }
}
