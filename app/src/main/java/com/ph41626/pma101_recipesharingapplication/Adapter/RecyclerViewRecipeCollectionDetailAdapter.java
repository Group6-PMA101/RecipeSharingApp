package com.ph41626.pma101_recipesharingapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ph41626.pma101_recipesharingapplication.Activity.RecipeCollectionActivity;
import com.ph41626.pma101_recipesharingapplication.Model.Recipe;
import com.ph41626.pma101_recipesharingapplication.Model.RecipeCollection;
import com.ph41626.pma101_recipesharingapplication.R;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewRecipeCollectionDetailAdapter extends RecyclerView.Adapter<RecyclerViewRecipeCollectionDetailAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Recipe> recipes;
    private RecipeCollectionActivity recipeCollectionActivity;

    public RecyclerViewRecipeCollectionDetailAdapter(Context context, ArrayList<Recipe> recipes,RecipeCollectionActivity recipeCollectionActivity) {
        this.context = context;
        this.recipes = recipes;
        this.recipeCollectionActivity = recipeCollectionActivity;
        AddLoadingPlaceholders();
    }
    public void AddLoadingPlaceholders() {
        recipes.clear();
        for (int i = 0; i < recipeCollectionActivity.recipeCollection.getNumberOfRecipes(); i++) {
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
    public RecyclerViewRecipeCollectionDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saved_recipe_detail,null,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewRecipeCollectionDetailAdapter.ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        if (recipe != null) {
            holder.btn_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu(view,recipe);
                }
            });
            if (!recipe.isPublic()) {
                holder.tv_recipe_name.setText("This recipe has been hidden or deleted by the user.");
                return;
            }
            holder.tv_recipe_name.setText(recipe.getName());
            holder.tv_recipe_averageRating.setText(String.valueOf(recipe.getAverageRating()));
            holder.tv_cook_time.setText("Cook time: " + recipe.getCookTime() + " min");
            Glide.with(context).
                    asBitmap().
                    load(recipeCollectionActivity.recipeMedia.get(recipe.getId()).getUrl()).
                    error(R.drawable.caption).
                    placeholder(R.drawable.caption).
                    into(holder.img_recipe_thumbnail);
            holder.pb_load_img.setVisibility(View.GONE);

            holder.itemView.setOnClickListener(v -> {
                recipeCollectionActivity.RecipeDetail(recipe);
            });
        }
    }
    private void PopupMenu(View view, Recipe recipe) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        List<String> menuItems = getMenuItems();
        for (String menuItem : menuItems) {
            popupMenu.getMenu().add(menuItem);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                handleMenuItemClick(item, recipe);
                return true;
            }
        });
        popupMenu.show();
    }

    private List<String> getMenuItems() {
        List<String> menuItems = new ArrayList<>();
        menuItems.add("Delete");
        return menuItems;
    }

    private void handleMenuItemClick(MenuItem item, Recipe recipe) {
        if (item.getTitle().equals("Delete")) {
            recipeCollectionActivity.Remove(recipe);
        }
    }
    @Override
    public int getItemCount() {
        return recipes != null ? recipes.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView
                tv_recipe_name,
                tv_recipe_averageRating,
                tv_cook_time;
        ImageView img_recipe_thumbnail;
        ProgressBar pb_load_img;
        RelativeLayout btn_more;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_recipe_name = itemView.findViewById(R.id.tv_recipe_name);
            tv_recipe_averageRating = itemView.findViewById(R.id.tv_recipe_averageRating);
            tv_cook_time = itemView.findViewById(R.id.tv_cook_time);
            img_recipe_thumbnail = itemView.findViewById(R.id.img_recipe_thumbnail);
            pb_load_img = itemView.findViewById(R.id.pb_load_img);
            btn_more = itemView.findViewById(R.id.btn_more);
        }
    }
}
