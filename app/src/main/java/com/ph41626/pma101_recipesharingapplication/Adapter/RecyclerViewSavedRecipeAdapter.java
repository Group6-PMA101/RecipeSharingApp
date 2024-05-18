package com.ph41626.pma101_recipesharingapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ph41626.pma101_recipesharingapplication.Model.RecipeCollection;
import com.ph41626.pma101_recipesharingapplication.R;

import java.util.ArrayList;

public class RecyclerViewSavedRecipeAdapter extends RecyclerView.Adapter<RecyclerViewSavedRecipeAdapter.ViewHolder> {

    private Context context;
    private ArrayList<RecipeCollection> recipeCollections;

    public RecyclerViewSavedRecipeAdapter(Context context, ArrayList<RecipeCollection> recipeCollections) {
        this.context = context;
        this.recipeCollections = recipeCollections;
    }

    public void Update(ArrayList<RecipeCollection> recipeCollections) {
        this.recipeCollections = recipeCollections;
    }
    @NonNull
    @Override
    public RecyclerViewSavedRecipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saved_recipes,null,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewSavedRecipeAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return recipeCollections != null ? recipeCollections.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
