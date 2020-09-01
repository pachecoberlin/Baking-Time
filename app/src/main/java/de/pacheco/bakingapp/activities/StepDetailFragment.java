package de.pacheco.bakingapp.activities;

import de.pacheco.bakingapp.R;
import de.pacheco.bakingapp.model.Recipe;
import de.pacheco.bakingapp.model.Steps;
import de.pacheco.bakingapp.utils.Utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link StepListActivity}
 * in two-pane mode (on tablets) or a {@link StepDetailActivity}
 * on handsets.
 */
public class StepDetailFragment extends Fragment {
    /**
     * The fragment argument representing the Step ID that this fragment
     * represents.
     */
    public static final String STEPS_ID = "stepsID";
    public static final String RECIPE_ID = "recipeID";

    /**
     * The Recipe and Step number this fragment is presenting.
     */
    private Recipe recipe;
    private Steps step;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StepDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(STEPS_ID)) {
            int stepsId = getArguments().getInt(STEPS_ID);
            Activity activity = this.getActivity();
            if (activity == null) {
                return;
            }
            recipe = activity.getIntent().getParcelableExtra(getString(R.string.recipe));
            if (recipe == null) {
                return;
            }
            step = Utils.getStep(recipe.steps, stepsId);
            TextView title = activity.findViewById(R.id.detail_title);
            title.setText(this.step.shortDescription);
            SharedPreferences sp = activity.getSharedPreferences(getString(R.string.recipe), 0);
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(RECIPE_ID, recipe.id);
            editor.putInt(STEPS_ID, stepsId);
            editor.apply();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.step_detail, container, false);
        // TODO Show the recipe content as text in a TextView. and the movie in the exoplayer
        if (recipe != null) {
            ((TextView) rootView.findViewById(R.id.item_detail)).setText(
                    step.description);
        }
        return rootView;
    }
}