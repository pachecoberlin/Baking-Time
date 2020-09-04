package de.pacheco.bakingapp.activities;

import de.pacheco.bakingapp.R;
import de.pacheco.bakingapp.model.Recipe;
import de.pacheco.bakingapp.model.Steps;
import de.pacheco.bakingapp.utils.Utils;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.net.URLConnection;

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
    public static final String IMAGE = "image";
    public static final String VIDEO = "video";
    public static final String TAG = StepDetailFragment.class.getSimpleName();

    /**
     * The Recipe and Step number this fragment is presenting.
     */
    private Recipe recipe;
    private Steps step;
    private FragmentActivity activity;
    private PlayerView playerView;

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
        if (recipe != null) {
            TextView textView = (TextView) rootView.findViewById(R.id.item_detail);
            textView.setText(step.description);
            setupPlayer(rootView);
        }
        return rootView;
    }

    private void setupPlayer(View rootView) {
        //TODO full video screen on rotating to landscape as well as zeitpunkt vom play
        // weiterführen und nicht neu anfangen und so
        playerView = rootView.findViewById(R.id.playerView);
        NestedScrollView textView = rootView.findViewById(R.id.item_detail_scrollview);
        ImageView imageView = rootView.findViewById(R.id.step_image);
        String urlString = step.videoURL == null || step.videoURL.isEmpty() ? step.thumbnailURL : step.videoURL;
        if (urlString == null || urlString.isEmpty()) {
            switchToOnlyText(playerView, textView, imageView);
            return;
        }
        Thread thread = new Thread(setPlayerOrImageContent(playerView, textView, imageView,
                urlString));
        thread.start();
    }

    private void switchToOnlyText(PlayerView playerView, NestedScrollView textView, ImageView imageView) {
        if (playerView != null) {
            playerView.setVisibility(View.INVISIBLE);
        }
        if (imageView != null) {
            imageView.setVisibility(View.INVISIBLE);
        }
        if (textView != null && textView.getLayoutParams() != null) {
            textView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        }
    }

    private void switchToImageOrVideo(PlayerView playerView, ImageView imageView, boolean isVideo) {
        if (playerView != null) {
            playerView.setVisibility(isVideo ? View.VISIBLE : View.INVISIBLE);
        }
        if (imageView != null) {
            imageView.setVisibility(isVideo ? View.INVISIBLE : View.VISIBLE);
        }
    }

    private Runnable setPlayerOrImageContent(PlayerView playerView, NestedScrollView textView, ImageView imageView, String urlString) {
        return () -> {
            try {
                URL url = new URL(urlString);
                URLConnection urlConnection = url.openConnection();
                String contentType = urlConnection == null ? "" : urlConnection.getContentType();
                if (contentType.startsWith(IMAGE)) {
                    if (imageView == null) {
                        return;
                    }
                    imageView.post(() -> {
                        switchToImageOrVideo(playerView, imageView, false);
                        Picasso.get().load(urlString).error(R.drawable.ic_food).into(imageView);
                    });
                } else if (contentType.startsWith(VIDEO)) {
                    if (playerView == null) {
                        return;
                    }
                    if (activity == null) {
                        return;
                    }
                    SimpleExoPlayer player = new SimpleExoPlayer.Builder(activity).build();
                    Uri uri = Uri.parse(urlString);
                    DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                            activity, "bakingStep");
                    MediaSource mediaSource = new ProgressiveMediaSource.Factory(
                            dataSourceFactory).createMediaSource(uri);
                    playerView.post(() -> {
                        playerView.setPlayer(player);
                        player.setPlayWhenReady(true);
                        player.prepare(mediaSource, false, false);
                        switchToImageOrVideo(playerView, imageView, true);
                    });
                } else {
                    switchToOnlyText(playerView, textView, imageView);
                }
            } catch (Throwable e) {
                Log.e(TAG, "Error while checking URL", e);
                if (textView != null) {
                    textView.post(() -> {
                        switchToOnlyText(playerView, textView, imageView);
                        Toast.makeText(activity, "ERROR", Toast.LENGTH_LONG).show();
                    });
                }
            }
        };
    }
}