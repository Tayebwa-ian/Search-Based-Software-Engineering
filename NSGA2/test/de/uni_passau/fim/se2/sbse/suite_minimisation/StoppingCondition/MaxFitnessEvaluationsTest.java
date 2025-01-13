package de.uni_passau.fim.se2.sbse.suite_minimisation.StoppingCondition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.uni_passau.fim.se2.sbse.suite_minimisation.stopping_conditions.MaxFitnessEvaluations;


public class MaxFitnessEvaluationsTest {
    MaxFitnessEvaluations stoppingCondition = new MaxFitnessEvaluations(10);

    @Test
    public void testof() {
        MaxFitnessEvaluations condition = MaxFitnessEvaluations.of(10);
        condition.notifySearchStarted();
        for (int i = 0; i < 10; i++) condition.notifyFitnessEvaluation();
        assertEquals(1.0, condition.getProgress());
    }

    @Test
    public void testConstructor_NegativeMaxEvaluations() {
        assertThrows(IllegalArgumentException.class, () -> new MaxFitnessEvaluations(-10));
    }

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
    public void testNotifyFitnessEvaluationInvalidInputs() {
        stoppingCondition.notifySearchStarted();
        assertThrows(IllegalArgumentException.class, () -> stoppingCondition.notifyFitnessEvaluations(-20));
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
    public void testSearchMustStop_BeforeLimit() {
        MaxFitnessEvaluations condition = new MaxFitnessEvaluations(100);
        condition.notifySearchStarted();
        condition.notifyFitnessEvaluations(99);
        assertFalse(condition.searchMustStop());
    }

    @Test
    public void testSearchMustStop_AtLimit() {
        MaxFitnessEvaluations condition = new MaxFitnessEvaluations(100);
        condition.notifySearchStarted();
        condition.notifyFitnessEvaluations(100);
        assertTrue(condition.searchMustStop());
    }

    @Test
    public void testSearchMustStop_AfterLimit() {
        MaxFitnessEvaluations condition = new MaxFitnessEvaluations(100);
        condition.notifySearchStarted();
        condition.notifyFitnessEvaluations(101);
        assertTrue(condition.searchMustStop());
    }

    @Test
    public void testToString() {
        MaxFitnessEvaluations condition = new MaxFitnessEvaluations(100);
        assertEquals("MaxFitnessEvaluations(100)", condition.toString());
    }

    @Test
    public void testGetProgress() {
        stoppingCondition.notifySearchStarted();
        for (int i = 0; i < 5; i++) {
            stoppingCondition.notifyFitnessEvaluation();
        }
        assertEquals(0.5, stoppingCondition.getProgress(), 0.001);
    }

    @Test
    public void testNotifyFitnessEvaluations () {
        stoppingCondition.notifySearchStarted();
        stoppingCondition.notifyFitnessEvaluations(11);
        assertTrue(stoppingCondition.searchMustStop());
    }
}
