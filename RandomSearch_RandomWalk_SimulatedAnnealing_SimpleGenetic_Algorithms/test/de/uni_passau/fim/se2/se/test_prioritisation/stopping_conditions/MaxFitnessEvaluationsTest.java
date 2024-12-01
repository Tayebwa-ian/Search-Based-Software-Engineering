package de.uni_passau.fim.se2.se.test_prioritisation.stopping_conditions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test; 
public class MaxFitnessEvaluationsTest {
    MaxFitnessEvaluations stoppingCondition = new MaxFitnessEvaluations(10);

    @Test
    public void testNotifySearchStarted() {
        stoppingCondition.notifySearchStarted();
        assertEquals(0.0, stoppingCondition.getProgress());
    }

    @Test
    public void testNotifyFitnessEvaluation() {
        stoppingCondition.notifySearchStarted();
        stoppingCondition.notifyFitnessEvaluation();
        assertEquals(0.1, stoppingCondition.getProgress(), 0.001);
        stoppingCondition.notifyFitnessEvaluation();
        stoppingCondition.notifyFitnessEvaluation();
        assertEquals(0.3, stoppingCondition.getProgress(), 0.001);
    }

    @Test
    public void testSearchMustStop() {
        stoppingCondition.notifySearchStarted();
        for (int i = 0; i < 10; i++) {
            stoppingCondition.notifyFitnessEvaluation();
        }
        assertTrue(stoppingCondition.searchMustStop());
        stoppingCondition.notifyFitnessEvaluation();
        assertTrue(stoppingCondition.searchMustStop());
    }

    @Test
    public void testGetProgress() {
        stoppingCondition.notifySearchStarted();
        for (int i = 0; i < 5; i++) {
            stoppingCondition.notifyFitnessEvaluation();
        }
        assertEquals(0.5, stoppingCondition.getProgress(), 0.001);
    }
}
