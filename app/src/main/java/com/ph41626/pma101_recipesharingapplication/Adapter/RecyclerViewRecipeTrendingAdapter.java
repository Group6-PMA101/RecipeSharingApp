package com.ph41626.pma101_recipesharingapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ph41626.pma101_recipesharingapplication.Model.Recipe;
import com.ph41626.pma101_recipesharingapplication.R;

import java.util.ArrayList;

public class RecyclerViewRecipeTrendingAdapter extends RecyclerView.Adapter<RecyclerViewRecipeTrendingAdapter.ViewHolder> {
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;
    private Context context;
    private ArrayList<Recipe> recipes;

    public RecyclerViewRecipeTrendingAdapter(Context context, ArrayList<Recipe> recipes) {
        this.context = context;
        this.recipes = recipes;
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
    @Override
    public int getItemViewType(int position) {
        return recipes.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }
    @NonNull
    @Override
    public RecyclerViewRecipeTrendingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == VIEW_TYPE_ITEM) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_trending,null,false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_trending_load,null,false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewRecipeTrendingAdapter.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            Recipe recipe = recipes.get(position);
            holder.tv_recipe_name.setText(recipe.getName());
            holder.tv_recipe_owner.setText(recipe.getUserId());
        }
    }

    @Override
    public int getItemCount() {
        return recipes != null ? recipes.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_recipe_name,tv_recipe_owner;
        ImageView img_recipe_thumbnail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_recipe_owner = itemView.findViewById(R.id.tv_recipe_owner);
            tv_recipe_name = itemView.findViewById(R.id.tv_recipe_name);
        }
    }
}
