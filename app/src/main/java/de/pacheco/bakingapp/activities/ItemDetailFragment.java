package de.pacheco.bakingapp.activities;

import de.pacheco.bakingapp.R;
import de.pacheco.bakingapp.model.Recipe;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.CollapsingToolbarLayout;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the Step ID that this fragment
     * represents.
     */
    public static final String STEPS_ID = "steps_id";

    /**
     * The Recipe and Step number this fragment is presenting.
     */
    private Recipe recipe;
    private int stepsId;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(STEPS_ID)) {
            stepsId = getArguments().getInt(STEPS_ID);
            Activity activity = this.getActivity();
            if (activity == null) {
                return;
            }
            recipe = activity.getIntent().getParcelableExtra(getString(R.string.recipe));
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(
                    R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(recipe.steps.get(stepsId).shortDescription);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);

        // TODO Show the recipe content as text in a TextView. and the movie in the exoplayer
        if (recipe != null) {
            ((TextView) rootView.findViewById(R.id.item_detail)).setText(
                    recipe.steps.get(stepsId).description);
        }

        return rootView;
    }
}