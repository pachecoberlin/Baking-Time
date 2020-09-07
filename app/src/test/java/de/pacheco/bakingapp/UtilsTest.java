package de.pacheco.bakingapp;

import de.pacheco.bakingapp.model.Recipe;
import de.pacheco.bakingapp.model.Step;
import de.pacheco.bakingapp.utils.Utils;

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class UtilsTest {
    private List<Step> steps;
    private Recipe recipe;

    @Before
    public void init() {
        steps = new LinkedList<>();
        for (int i = 1; i < 5; i++) {
            steps.add(new Step(i));
        }
        for (int i = 8; i < 10; i++) {
            steps.add(new Step(i));
        }
        recipe = new Recipe(steps);
    }

    @Test
    public void getStep() {
        for (int i = 1; i < 5; i++) {
            Step step = Utils.getStep(steps, i);
            assertEquals(i, step.id);
        }
        Step step = Utils.getStep(steps, 23);
        assertNull(step);
    }

    @Test
    public void getNextStep() {
        Step step = Utils.getNextStep(1, recipe);
        assertEquals(2, step.id);
        step = Utils.getNextStep(5, recipe);
        assertEquals(8, step.id);
        step = Utils.getNextStep(10, recipe);
        assertEquals(1, step.id);
    }

    @Test
    public void getPreviousStep() {
        Step step = Utils.getPreviousStep(1, recipe);
        assertEquals(1, step.id);
        step = Utils.getPreviousStep(8, recipe);
        assertEquals(4, step.id);
        step = Utils.getPreviousStep(9, recipe);
        assertEquals(8, step.id);
    }
}