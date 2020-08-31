package de.pacheco.bakingapp.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class RecipesViewModel extends AndroidViewModel {
    private static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net";
    public static final GetRecipesAPI SERVICE = getRecipeService();
    private MutableLiveData<List<Recipe>> recipes;

    public RecipesViewModel(@NonNull Application application) {
        super(application);
    }

    private static GetRecipesAPI getRecipeService() {
        Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return restAdapter.create(GetRecipesAPI.class);
    }

    public LiveData<List<Recipe>> getRecipes() {
        if (recipes == null) {
            recipes = new MutableLiveData<>();
            getRecipes(recipes);
        }
        return recipes;
    }

    private void getRecipes(final MutableLiveData<List<Recipe>> liveData) {
        SERVICE.getRecipes().enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call,
                                   Response<List<Recipe>> response) {
                List<Recipe> recipes = response.body();
                liveData.setValue(recipes);
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public interface GetRecipesAPI {
        @GET("/topher/2017/May/59121517_baking/baking.json")
        Call<List<Recipe>> getRecipes();
    }
}