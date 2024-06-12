package com.ph41626.pma101_recipesharingapplication.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ph41626.pma101_recipesharingapplication.Fragment.SavedRecipesFragment;
import com.ph41626.pma101_recipesharingapplication.Model.Media;
import com.ph41626.pma101_recipesharingapplication.Model.Recipe;
import com.ph41626.pma101_recipesharingapplication.Model.RecipeCollection;
import com.ph41626.pma101_recipesharingapplication.R;

import java.util.ArrayList;

public class RecyclerViewRecipeCollectionAdapter extends RecyclerView.Adapter<RecyclerViewRecipeCollectionAdapter.ViewHolder> {

    private Context context;
    private ArrayList<RecipeCollection> recipeCollections;
    private SavedRecipesFragment savedRecipesFragment;
    private boolean isUpdateThumbnail = false;
    public void Update(ArrayList<RecipeCollection> recipeCollections) {
        this.recipeCollections = recipeCollections;
        isUpdateThumbnail = false;
        notifyDataSetChanged();
    }
    public void UpdateThumbnail() {
        isUpdateThumbnail = true;
        notifyDataSetChanged();
    }
    public RecyclerViewRecipeCollectionAdapter(Context context, ArrayList<RecipeCollection> recipeCollections,SavedRecipesFragment savedRecipesFragment) {
        this.context = context;
        this.recipeCollections = recipeCollections;
        this.savedRecipesFragment = savedRecipesFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe_collection,null,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecipeCollection recipeCollection = recipeCollections.get(position);

        holder.tv_recipe_collection_name.setText(recipeCollection.getName());
        holder.tv_quantity.setText(recipeCollection.getNumberOfRecipes() + " Recipes");
        if (isUpdateThumbnail) {
            if (recipeCollection.getNumberOfRecipes() >= 2) {
                holder.img_recipe_collection_sub.setVisibility(View.VISIBLE);
                ArrayList<Recipe> recipes = savedRecipesFragment.mainActivity.recipeForRecipeCollection.get(recipeCollection.getId());
//                Log.e("Check data collection",recipeCollection.toString());

                for(int i = 0;i < 2; i++) {
                    Recipe recipe = recipes.get(i);
                    if (recipe == null){
                        holder.img_recipe_collection_sub.setVisibility(View.GONE);
                        continue;
                    }
                    Media media = savedRecipesFragment.mainActivity.recipeMedia.get(recipe.getId());

                    if (i == 0) {
                        Glide.with(context).
                                load(media.getUrl()).
                                error(R.drawable.default_avatar).
                                placeholder(R.drawable.default_avatar).
                                into(holder.img_recipe_collection_main);
                    } else {
                        Glide.with(context).
                                load(media.getUrl()).
                                error(R.drawable.default_avatar).
                                placeholder(R.drawable.default_avatar).
                                into(holder.img_recipe_collection_sub);
                    }
                }

            } else {
                if (recipeCollection.getNumberOfRecipes() == 1) {
                    ArrayList<Recipe> recipes = savedRecipesFragment.mainActivity.recipeForRecipeCollection.get(recipeCollection.getId());
                    Recipe recipe = recipes.get(0);
                    Media media = savedRecipesFragment.mainActivity.recipeMedia.get(recipe.getId());
                    Glide.with(context).
                            load(media.getUrl()).
                            error(R.drawable.default_avatar).
                            placeholder(R.drawable.default_avatar).
                            into(holder.img_recipe_collection_main);
                } else {
                    holder.img_recipe_collection_main.setImageResource(R.drawable.caption);
                }
                holder.img_recipe_collection_sub.setVisibility(View.GONE);
            }
        } else {
            if (recipeCollection.getNumberOfRecipes() == 0) {
                holder.img_recipe_collection_main.setImageResource(R.drawable.caption);
            }
        }
        holder.itemView.setOnClickListener(v -> {
            savedRecipesFragment.RecipeCollectionDetail(recipeCollection);
        });
    }

    @Override
    public int getItemCount() {
        return recipeCollections != null ? recipeCollections.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_recipe_collection_name,tv_quantity;
        ImageView img_recipe_collection_sub,img_recipe_collection_main;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_recipe_collection_name = itemView.findViewById(R.id.tv_recipe_collection_name);
            tv_quantity = itemView.findViewById(R.id.tv_quantity);

            img_recipe_collection_sub = itemView.findViewById(R.id.img_recipe_collection_sub);
            img_recipe_collection_main = itemView.findViewById(R.id.img_recipe_collection_main);
        }
    }
}
