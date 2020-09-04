package de.pacheco.bakingapp.activities;

import de.pacheco.bakingapp.R;
import de.pacheco.bakingapp.model.Recipe;
import de.pacheco.bakingapp.model.Steps;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link StepDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class StepListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private static final String BUNDLE_LAYOUT = "BUNDLE_LAYOUT";

    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (findViewById(R.id.item_detail_container) != null) {
            mTwoPane = true;
        }
        Recipe recipe = getIntent().getParcelableExtra(this.getString(R.string.recipe));
        RecyclerView recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (recipe == null) {
            SharedPreferences sp = getSharedPreferences(getString(R.string.recipe), 0);
            int recipeNumber = sp.getInt(StepDetailFragment.RECIPE_ID, -1);
            recipe = RecipeListActivity.recipes.get(recipeNumber - 1);
            if (recipe == null) {
                return;
            }
            int stepId = sp.getInt(StepDetailFragment.STEPS_ID, 0);
            int scrollPosition = Math.min(stepId, recipe.steps.size() - 1);
            layoutManager.scrollToPosition(scrollPosition);
        }

        setTitle(recipe.name);
        setupRecyclerView(recyclerView, recipe);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_LAYOUT,
                layoutManager.onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            super.onRestoreInstanceState(savedInstanceState);
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_LAYOUT);
            layoutManager.onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpTo(this, new Intent(this, RecipeListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, Recipe recipe) {
        recyclerView.setAdapter(
                new SimpleItemRecyclerViewAdapter(this, recipe, mTwoPane));
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {
        private final StepListActivity mParentActivity;
        private final List<Steps> mValues;
        private final boolean mTwoPane;
        private final Recipe recipe;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Steps steps = (Steps) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putInt(StepDetailFragment.STEPS_ID, steps.id);
                    arguments.putParcelable(view.getContext().getString(R.string.recipe),
                            recipe);
                    StepDetailFragment fragment = new StepDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, StepDetailActivity.class);
                    intent.putExtra(StepDetailFragment.STEPS_ID, steps.id);
                    intent.putExtra(context.getString(R.string.recipe), recipe);
                    context.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapter(StepListActivity parent, Recipe recipe, boolean twoPane) {
            mValues = recipe.steps;
            this.recipe = recipe;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.step_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mIdView.setText(String.valueOf(mValues.get(position).id));
            holder.mContentView.setText(mValues.get(position).shortDescription);
            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;

            ViewHolder(View view) {
                super(view);
                mIdView = view.findViewById(R.id.item_list_item_header);
                mContentView = view.findViewById(R.id.item_list_item_content);
            }
        }
    }
}