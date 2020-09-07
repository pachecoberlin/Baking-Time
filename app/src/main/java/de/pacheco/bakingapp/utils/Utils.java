package de.pacheco.bakingapp.utils;

import de.pacheco.bakingapp.model.Ingredient;
import de.pacheco.bakingapp.model.Recipe;
import de.pacheco.bakingapp.model.Step;

import android.content.Context;
import android.util.DisplayMetrics;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

    public static Step getStep(List<Step> steps, int stepId) {
        for (Step step : steps) {
            if (step.id == stepId) {
                return step;
            }
        }
        return null;
    }

    public static Step getNextStep(int actualStepId, Recipe recipe) {
        return getStep(actualStepId, recipe, true);
    }

    public static Step getPreviousStep(int actualStepId, Recipe recipe) {
        return getStep(actualStepId, recipe, false);
    }

    private static Step getStep(int actualStepId, Recipe recipe, boolean next) {
//        Step actualStep = getStep(recipe.steps, actualStepId);
////        actualStepId
//        if (next) {
//            actualStepId++;
//        } else {
//            actualStepId--;
//        }
//        actualStepId = actualStepId < 0 || actualStepId >= recipe.steps.size() ? 0 : actualStepId;
        Step step;
        do {
            if (next) {
                actualStepId++;
            } else {
                actualStepId--;
            }
            actualStepId =
                    actualStepId < minStep(recipe.steps) || actualStepId >= maxStep(recipe.steps) ?
                            minStep(recipe.steps) :
                            actualStepId;
            step = Utils.getStep(recipe.steps, actualStepId);
        } while (step == null);
        return step;
    }

    private static int minStep(List<Step> steps) {
        AtomicInteger min = new AtomicInteger(Integer.MAX_VALUE);
        for (Step step : steps) {
            min.set(step.id > min.get() ? min.get() : step.id);
        }
        return min.get();
    }

    private static int maxStep(List<Step> steps) {
        AtomicInteger max = new AtomicInteger(Integer.MIN_VALUE);
        for (Step step : steps) {
            max.set(step.id > max.get() ? step.id : max.get());
        }
        return max.get();
    }

    public static String getIngredients(Recipe recipe) {
        StringBuilder sb = new StringBuilder("Ingredients:\n\n");
        for (Ingredient ingredient : recipe.ingredients) {
            sb.append(ingredient.toString());
        }
        return sb.toString();
    }
}