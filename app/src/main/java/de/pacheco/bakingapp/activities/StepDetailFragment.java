package de.pacheco.bakingapp.activities;

import de.pacheco.bakingapp.R;
import de.pacheco.bakingapp.model.Recipe;
import de.pacheco.bakingapp.model.Steps;
import de.pacheco.bakingapp.utils.Utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

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
    private Activity activity;

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
            activity = this.getActivity();
            if (activity == null) {
                return;
            }
            recipe = activity.getIntent().getParcelableExtra(getString(R.string.recipe));
            if (recipe == null) {
                return;
            }
            SharedPreferences sp = activity.getSharedPreferences(getString(R.string.recipe), 0);
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(RECIPE_ID, recipe.id);
            editor.putInt(STEPS_ID, stepsId);
            editor.apply();
            step = Utils.getStep(recipe.steps, stepsId);
            TextView title = activity.findViewById(R.id.detail_title);
            if (title == null) {
                return;
            }
            title.setText(this.step.shortDescription);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.step_detail, container, false);
        // TODO depending if there is a video thumbnail url or neither the view must be
        //  different, maybe Framelayout. thumbnail shall be an image, but is in example a video...
        if (recipe != null) {
            TextView textView = (TextView) rootView.findViewById(R.id.item_detail);
            textView.setText(step.description);
            setupPlayer(rootView);
        }
        return rootView;
    }

//TODO test with image   String testimg = "https://upload.wikimedia" +
//            ".org/wikipedia/commons/thumb/f/fc/Banknote_portrait_pattern_%28Intaglio_print%2C_tactile_effect%29.jpg/400px-Banknote_portrait_pattern_%28Intaglio_print%2C_tactile_effect%29.jpg";

    private void setupPlayer(View rootView) {
        //TODO full video screen on rotating to landscape
        PlayerView playerView = (PlayerView) rootView.findViewById(R.id.playerView);
        String videoURL = step.videoURL == null || step.videoURL.isEmpty() ? step.thumbnailURL : step.videoURL;
        if (videoURL == null || videoURL.isEmpty()) {
            playerView.setVisibility(View.INVISIBLE);
            //TODO entweder framelayout oder hier noch die TextView groß machen
            return;
        }
        playerView.setUseArtwork(true);
//        Drawable drawable; //TODO check ob in der URL ein Bild oder Video ist und wenn bild dann
        // ein Drawable daraus machen
//        playerView.setDefaultArtwork(drawable);


        if (activity == null) {
            return;
        }
        SimpleExoPlayer player = new SimpleExoPlayer.Builder(activity).build();
        playerView.setPlayer(player);
        Uri uri = Uri.parse(videoURL);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(activity,
                "bakingStep");
        MediaSource mediaSource =
                new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
        player.setPlayWhenReady(true);
        player.prepare(mediaSource, false, false);
    }
}