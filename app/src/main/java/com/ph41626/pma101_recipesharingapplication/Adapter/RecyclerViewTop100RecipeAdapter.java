package com.ph41626.pma101_recipesharingapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ph41626.pma101_recipesharingapplication.Fragment.HomeFragment;
import com.ph41626.pma101_recipesharingapplication.Model.Recipe;
import com.ph41626.pma101_recipesharingapplication.R;

import java.util.ArrayList;

public class RecyclerViewTop100RecipeAdapter extends RecyclerView.Adapter<RecyclerViewTop100RecipeAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Recipe> recipes;
    private HomeFragment homeFragment;
    public RecyclerViewTop100RecipeAdapter(Context context, ArrayList<Recipe> recipes,HomeFragment homeFragment) {
        this.context = context;
        this.recipes = recipes;
        this.homeFragment = homeFragment;
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
    public RecyclerViewTop100RecipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_100_recipe,null,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewTop100RecipeAdapter.ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        if (recipe != null) {
            holder.tv_recipe_name.setText(recipe.getName());
            if (homeFragment.recipeMedias.containsKey(recipe.getId())) {
                Glide.with(context).
                        load(homeFragment.recipeMedias.get(recipe.getId()).getUrl()).
                        error(R.drawable.default_avatar).
                        placeholder(R.drawable.default_avatar).
                        into(holder.img_recipe_thumbnail);
            }
            if (homeFragment.recipeUsers.containsKey(recipe.getId())) {
                holder.tv_recipe_owner.setText(homeFragment.recipeUsers.get(recipe.getId()).getName());
            }
            holder.pb_load_img.setVisibility(View.GONE);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    homeFragment.RecipeDetail(recipe);
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return recipes != null ? recipes.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_recipe_name,tv_recipe_owner;
        ImageView img_recipe_thumbnail;
        ProgressBar pb_load_img;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_recipe_name = itemView.findViewById(R.id.tv_recipe_name);
            tv_recipe_owner = itemView.findViewById(R.id.tv_recipe_owner);
            img_recipe_thumbnail = itemView.findViewById(R.id.img_recipe_thumbnail);
            pb_load_img = itemView.findViewById(R.id.pb_load_img);
        }
    }
}
