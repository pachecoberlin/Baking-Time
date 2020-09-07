package de.pacheco.bakingapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;

public class Recipe implements Parcelable {

    @SerializedName("id")
    @Expose
    public int id;

    @SerializedName("ingredients")
    @Expose
    public List<Ingredient> ingredients;

    @SerializedName("steps")
    @Expose
    public List<Step> steps;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("servings")
    @Expose
    public int servings;

    @SerializedName("image")
    @Expose
    public String image;

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    protected Recipe(Parcel in) {
        id = in.readInt();
        name = in.readString();
        servings = in.readInt();
        image = in.readString();
        ingredients = new LinkedList<>();
        in.readList(ingredients, Ingredient.class.getClassLoader());
        steps = new LinkedList<>();
        in.readList(steps, Step.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeInt(servings);
        parcel.writeString(image);
        parcel.writeList(ingredients);
        parcel.writeList(steps);
    }
}
