package de.pacheco.bakingapp.utils;

import de.pacheco.bakingapp.model.Recipe;
import de.pacheco.bakingapp.model.Steps;

import android.content.Context;
import android.util.DisplayMetrics;

import java.util.List;

public class Utils {

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 200;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        if (noOfColumns < 2)
            noOfColumns = 2;
        return noOfColumns;
    }

    public static Steps getStep(List<Steps> steps, int stepId) {
        for (Steps step :
                steps) {
            if (step.id == stepId) {
                return step;
            }
        }
        return null;
    }

    public static Steps getNextStep(int actualStepId, Recipe recipe) {
        return getStep(actualStepId, recipe, true);
    }

    public static Steps getPreviousStep(int actualStepId, Recipe recipe) {
        return getStep(actualStepId, recipe, false);
    }

    private static Steps getStep(int actualStepId, Recipe recipe, boolean next) {
        if (next) {
            actualStepId++;
        } else {
            actualStepId--;
        }
        actualStepId = actualStepId < 0 || actualStepId >= recipe.steps.size() ? 0 : actualStepId;
        Steps step;
        do {
            step = Utils.getStep(recipe.steps, actualStepId);
            if (next) {
                actualStepId++;
            } else {
                actualStepId--;
            }
            actualStepId = actualStepId < 0 || actualStepId >= recipe.steps.size() ? 0 : actualStepId;
        } while (step == null);
        return step;
    }
}