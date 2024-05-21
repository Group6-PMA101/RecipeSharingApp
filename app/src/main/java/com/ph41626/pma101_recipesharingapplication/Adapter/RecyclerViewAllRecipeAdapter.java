package com.ph41626.pma101_recipesharingapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ph41626.pma101_recipesharingapplication.Fragment.AllRecipesFragment;
import com.ph41626.pma101_recipesharingapplication.Model.Recipe;
import com.ph41626.pma101_recipesharingapplication.R;

import java.util.ArrayList;

public class RecyclerViewAllRecipeAdapter extends RecyclerView.Adapter<RecyclerViewAllRecipeAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Recipe> recipes;
    private AllRecipesFragment allRecipesFragment;
    public RecyclerViewAllRecipeAdapter(Context context, ArrayList<Recipe> recipes, AllRecipesFragment allRecipesFragment) {
        this.context = context;
        this.recipes = recipes;
        this.allRecipesFragment = allRecipesFragment;
        AddLoadingPlaceholders();
    }
    public void AddLoadingPlaceholders() {
        recipes.clear();
        for (int i = 0; i < 2; i++) {
            recipes.add(null);
        }
        notifyDataSetChanged();
    }
    public void Update(ArrayList<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public RecyclerViewAllRecipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saved_recipe_detail,null,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAllRecipeAdapter.ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        if (recipe != null) {
            holder.tv_recipe_name.setText(recipe.getName());
            holder.tv_recipe_averageRating.setText(String.valueOf(recipe.getAverageRating()));
            if (allRecipesFragment.recipeIngredients.containsKey(recipe.getId())
                && allRecipesFragment.recipeIngredients.get(recipe.getId()) != null) {
                holder.tv_recipe_ingredients_cook_time.setText(
                        allRecipesFragment.recipeIngredients.get(recipe.getId()).size() + " Ingredients | " +
                        recipe.getCookTime() + " min");
            }
        }
    }

    @Override
    public int getItemCount() {
        return recipes != null ? recipes.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_recipe_name,tv_recipe_averageRating,tv_recipe_ingredients_cook_time;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_recipe_name = itemView.findViewById(R.id.tv_recipe_name);
            tv_recipe_averageRating = itemView.findViewById(R.id.tv_recipe_averageRating);
            tv_recipe_ingredients_cook_time = itemView.findViewById(R.id.tv_recipe_ingredients_cook_time);
        }
    }
}
