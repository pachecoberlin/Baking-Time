package de.pacheco.bakingapp.activities;

import de.pacheco.bakingapp.R;
import de.pacheco.bakingapp.model.Recipe;
import de.pacheco.bakingapp.model.Step;
import de.pacheco.bakingapp.utils.Utils;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    protected Recipe recipe;
    public Step step;
    private FragmentActivity activity;
    private PlayerView playerView;
    private SimpleExoPlayer player;
    private static boolean playWhenReady = true;
    private static int currentWindow = 0;
    private static long playbackPosition = 0;
    private static String actualUrl = "";
    private String urlString;
    private boolean isVideo = false;
    private PlaybackStateListener playbackStateListener;
    private TextView title;
    private View rootView;

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
            int stepsId = savedInstanceState != null && savedInstanceState.getInt(STEPS_ID) >= 0 ?
                    savedInstanceState.getInt(STEPS_ID) : getArguments().getInt(STEPS_ID);
            activity = this.getActivity();
            if (activity == null) {
                return;
            }
            recipe = activity.getIntent().getParcelableExtra(getString(R.string.recipe));
            if (recipe == null) {
                return;
            }
            saveStepIdInShared(stepsId);
            step = Utils.getStep(recipe.steps, stepsId);
            title = activity.findViewById(R.id.detail_title);
            setTitle();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (step != null) {
            outState.putInt(STEPS_ID, step.id);
        }
    }

    @Override
    public void onDestroy() {
        saveStepIdInShared(step.id);
        super.onDestroy();
    }

    private void saveStepIdInShared(int stepsId) {
        SharedPreferences sp = activity.getSharedPreferences(getString(R.string.recipe), 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(RECIPE_ID, recipe.id);
        editor.putInt(STEPS_ID, stepsId);
        editor.apply();
    }

    private void setTitle() {
        if (title == null) {
            return;
        }
        title.setText(step.shortDescription);
    }

    public void refresh(int increment) {
        if (recipe == null || step == null) {
            return;
        }
        if (increment > 0) {
            step = Utils.getNextStep(step.id, recipe);
        } else {
            step = Utils.getPreviousStep(step.id, recipe);
        }
        currentWindow = 0;
        playbackPosition = 0L;
        player.stop();
        setTitle();
        setStepContents();
    }

    /**
     * TODO @Reviewer can you tell me best practices to enable full screen mode for playerview in
     * landscapemode. Although this solution works, the method i am using is deprecated.
     * https://developer.android.com/training/system-ui/immersive#java
     */
    @SuppressWarnings("deprecation")
    private void hideSystemUiWhenInLandscape() {
        int orientation = getResources().getConfiguration().orientation;
        if (!StepListActivity.mTwoPane && orientation == Configuration.ORIENTATION_LANDSCAPE && playerView != null && playerView.getVisibility() == View.VISIBLE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LOW_PROFILE
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.step_detail, container, false);
        FloatingActionButton nextStep = rootView.findViewById(R.id.next_step);
        nextStep.setOnClickListener(view -> refresh(1));
        FloatingActionButton previousStep = rootView.findViewById(R.id.previous_step);
        previousStep.setOnClickListener(view -> refresh(-1));
        setStepContents();
        return rootView;
    }

    private void setStepContents() {
        if (recipe != null) {
            TextView textView = rootView.findViewById(R.id.item_detail);
            if (step != null) {
                String text = step.id == 0 ? Utils.getIngredients(recipe) : step.description;
                textView.setText(text);
            }
            setupPlayer(rootView);
        }
    }

    private void setupPlayer(View rootView) {
        playerView = rootView.findViewById(R.id.playerView);
        NestedScrollView textView = rootView.findViewById(R.id.item_detail_scrollview);
        ImageView imageView = rootView.findViewById(R.id.step_image);
        if (step == null) {
            return;
        }
        urlString = step.videoURL == null || step.videoURL.isEmpty() ? step.thumbnailURL : step.videoURL;
        if (urlString == null || urlString.isEmpty()) {
            switchToOnlyText(playerView, textView, imageView);
            return;
        }
        playbackStateListener = new PlaybackStateListener();
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

    private void switchToImageOrVideo(PlayerView playerView, NestedScrollView textView, ImageView imageView,
                                      boolean isVideo) {
        if (playerView != null) {
            playerView.setVisibility(isVideo ? View.VISIBLE : View.INVISIBLE);
            hideSystemUiWhenInLandscape();
        }
        if (imageView != null) {
            imageView.setVisibility(isVideo ? View.INVISIBLE : View.VISIBLE);
        }
        if (textView != null && textView.getLayoutParams() != null) {
            textView.getLayoutParams().height = 0;
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
                        switchToImageOrVideo(playerView, textView, imageView, false);
                        Picasso.get().load(urlString).error(R.drawable.ic_food).into(imageView);
                    });
                } else if (contentType.startsWith(VIDEO)) {
                    isVideo = true;
                    if (playerView == null || activity == null) {
                        return;
                    }
                    playerView.post(() -> {
                        initializePlayer();
                        switchToImageOrVideo(playerView, textView, imageView, true);
                    });
                } else {
                    switchToOnlyText(playerView, textView, imageView);
                }
            } catch (Throwable e) {
                Log.e(TAG, "Error while checking URL", e);
                if (textView != null) {
                    textView.post(() -> switchToOnlyText(playerView, textView, imageView));
                }
            }
        };
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void initializePlayer() {
        if (!isVideo) {
            return;
        }
        if (player == null) {
            DefaultTrackSelector trackSelector = new DefaultTrackSelector(activity);
            trackSelector.setParameters(trackSelector.buildUponParameters().setMaxVideoSizeSd());
            player = new SimpleExoPlayer.Builder(activity).setTrackSelector(trackSelector).build();
        }
        playerView.setPlayer(player);
        Uri uri = Uri.parse(urlString);
        MediaSource mediaSource = buildMediaSource(uri);
        if (!actualUrl.equals(urlString)) {
            currentWindow = 0;
            playbackPosition = 0L;
            actualUrl = urlString == null ? "" : urlString;
        }
        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);
        player.addListener(playbackStateListener);
        player.prepare(mediaSource, false, false);
    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.removeListener(playbackStateListener);
            player.release();
            player = null;
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(activity, "bakingStep");
        return new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
    }

    private static class PlaybackStateListener implements Player.EventListener {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady,
                                         int playbackState) {
            String stateString;
            switch (playbackState) {
                case ExoPlayer.STATE_IDLE:
                    stateString = "ExoPlayer.STATE_IDLE      -";
                    break;
                case ExoPlayer.STATE_BUFFERING:
                    stateString = "ExoPlayer.STATE_BUFFERING -";
                    break;
                case ExoPlayer.STATE_READY:
                    stateString = "ExoPlayer.STATE_READY     -";
                    break;
                case ExoPlayer.STATE_ENDED:
                    stateString = "ExoPlayer.STATE_ENDED     -";
                    break;
                default:
                    stateString = "UNKNOWN_STATE             -";
                    break;
            }
            Log.d(TAG, "changed state to " + stateString
                    + " playWhenReady: " + playWhenReady);
        }
    }
}