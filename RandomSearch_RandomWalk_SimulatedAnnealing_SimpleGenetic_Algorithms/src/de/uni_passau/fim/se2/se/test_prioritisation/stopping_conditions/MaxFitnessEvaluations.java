package de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions;

/**
 * Stopping condition that stops the search after a specified number of fitness evaluations.
 */
public class MaxFitnessEvaluations implements StoppingCondition {

    private final int maxFitnessEvaluations;
    private int currentFitnessEvaluations;

    public MaxFitnessEvaluations(final int maxFitnessEvaluations) {
        this.maxFitnessEvaluations = maxFitnessEvaluations;
    }

    @Override
    public void notifySearchStarted() {
        currentFitnessEvaluations = 0;
    }

    @Override
    public void notifyFitnessEvaluation() {
        currentFitnessEvaluations++;
    }

    @Override
    public boolean searchMustStop() {
        return currentFitnessEvaluations >= maxFitnessEvaluations;
    }

    @Override
    public double getProgress() {
        return (double) currentFitnessEvaluations / maxFitnessEvaluations;
    }
}
