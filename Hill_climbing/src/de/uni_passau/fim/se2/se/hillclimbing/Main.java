package de.uni_passau.fim.se2.se.hillclimbing;

import picocli.CommandLine;
import picocli.CommandLine.Option;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Entry point for the hill climbing algorithm to solve the job scheduling problem.
 */
public class Main implements Callable<Integer> {

    private int maxIterations;
    private int restartThreshold;
    private int[] jobs;
    private int getNumMachines;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    public Integer call() {
        HillClimbing hillClimbing = new HillClimbing(new ScheduleGenerator(), new FitnessFunction());
        List<List<Integer>> result =
                hillClimbing.optimize(jobs, getNumMachines, maxIterations, restartThreshold);
        FitnessFunction fitness = new FitnessFunction();
        System.out.printf("Optimal schedule %s with a makespan of %d%n", result, fitness.calculateMakespan(result));
        return 0;
    }

    @Option(
            names = {"-i", "--max-iterations"},
            description = "The maximum number of hill climbing iterations to perform.",
            required = true)
    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    @Option(
            names = {"-r", "--restart-threshold"},
            description = "The number of iterations without improvement before a restart is triggered.",
            required = true)
    public void setRestartThreshold(int restartThreshold) {
        this.restartThreshold = restartThreshold;
    }

    @Option(
            names = {"-j", "--jobs"},
            description = "The jobs to schedule.",
            required = true,
            split = ",")
    public void setJobs(int[] jobs) {
        this.jobs = jobs;
    }

    @Option(
            names = {"-m", "--num-machines"},
            description = "The number of machines.",
            required = true)
    public void setNumMachines(int numMachines) {
        this.getNumMachines = numMachines;
    }
}
