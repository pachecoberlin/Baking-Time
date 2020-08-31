package de.pacheco.bakingapp.activities;

import de.pacheco.bakingapp.R;
import de.pacheco.bakingapp.model.Recipe;
import de.pacheco.bakingapp.model.RecipesViewModel;
import de.pacheco.bakingapp.utils.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

/**
 * TODO props to         //        <div>Icon made from <a href="http://www.onlinewebfonts.com/icon">Icon Fonts</a> is licensed by CC BY 3.0</div>
 */
public class RecipeListActivity extends AppCompatActivity {

    private RecipeRecyclerViewAdapter recipeRecyclerViewAdapter;
    private List<Recipe> recipes = Collections.emptyList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Context context = view.getContext();
            Intent intent = new Intent(context, ItemListActivity.class);
            context.startActivity(intent);
        });

        View recyclerView = findViewById(R.id.recipe_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
        setupViewModel();
    }
    //TODO onSave und onLoad
    private void setupViewModel() {
        RecipesViewModel recipesViewModel = new ViewModelProvider(this).get(RecipesViewModel.class);
        recipesViewModel.getRecipes().observe(this, list -> {
            recipes = list;
            recipeRecyclerViewAdapter.setRecipes(recipes);
            recipeRecyclerViewAdapter.notifyDataSetChanged();
        });
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new GridLayoutManager(this, Utils.calculateNoOfColumns(this),
                GridLayoutManager.VERTICAL, false));
        recipeRecyclerViewAdapter = new RecipeRecyclerViewAdapter(recipes, this);
        recyclerView.setAdapter(recipeRecyclerViewAdapter);
    }

    public static class RecipeRecyclerViewAdapter
            extends RecyclerView.Adapter<RecipeRecyclerViewAdapter.ViewHolder> {
        private final RecipeListActivity recipeListActivity;
        private final View.OnClickListener mOnClickListener = view -> {
            Context context = view.getContext();
            Intent intent = new Intent(context, ItemListActivity.class);
            intent.putExtra(context.getString(R.string.recipe), (Recipe) view.getTag());
            context.startActivity(intent);
        };
        private List<Recipe> mValues;

        RecipeRecyclerViewAdapter(List<Recipe> items, RecipeListActivity recipeListActivity) {
            mValues = items;
            this.recipeListActivity = recipeListActivity;
        }

        @NonNull
        @Override
        public RecipeRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recipe_list_item, parent, false);
            return new RecipeRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecipeRecyclerViewAdapter.ViewHolder holder, int position) {
            Recipe recipe = mValues.get(position);
            String url = recipe.image.isEmpty() ? "empty" : recipe.image;
            Picasso.get().load(url).placeholder(R.drawable.ic_food).error(R.drawable.ic_food).into(
                    holder.recipeImage);
            holder.recipeTitle.setText(recipe.name);
            holder.recipeSubtitle.setText(recipeListActivity.getString(R.string.servings,
                    recipe.servings));
            holder.itemView.setTag(recipe);
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public void setRecipes(List<Recipe> recipes) {
            mValues = recipes;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final MaterialCardView mIdView;
            final ImageView recipeImage;
            final TextView recipeTitle;
            final TextView recipeSubtitle;

            ViewHolder(View view) {
                super(view);
                mIdView = view.findViewById(R.id.recipe_list_item_card);
                recipeImage = view.findViewById(R.id.recipe_image);
                recipeTitle = view.findViewById(R.id.recipe_title);
                recipeSubtitle = view.findViewById(R.id.recipe_subtitle);
            }
        }
    }
}