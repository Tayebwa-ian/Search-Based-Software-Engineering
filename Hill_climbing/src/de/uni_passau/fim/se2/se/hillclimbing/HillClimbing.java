package de.uni_passau.fim.se2.se.hillclimbing;

import java.util.List;

/**
 * Hill climbing algorithm with restarts to solve the job scheduling problem.
 */
public final class HillClimbing {

    private final ScheduleGenerator generator;
    private final FitnessFunction fitnessFunction;

    public HillClimbing(ScheduleGenerator generator, FitnessFunction fitnessFunction) {
        this.generator = generator;
        this.fitnessFunction = fitnessFunction;
    }

    /**
     * Optimize the job scheduling problem using the hill climbing algorithm with restarts.
     *
     * @param jobs             the jobs to schedule
     * @param numMachines      the number of machines
     * @param maxIterations    the maximum number of iterations
     * @param restartThreshold the number of iterations without improvement before a restart is
     *                         triggered
     * @return the optimal schedule
     */
    public List<List<Integer>> optimize(int[] jobs, int numMachines, int maxIterations, int restartThreshold) {
        throw new UnsupportedOperationException("Implement me");
    }


}
