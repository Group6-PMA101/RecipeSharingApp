package com.ph41626.pma101_recipesharingapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ph41626.pma101_recipesharingapplication.Fragment.ProfileFragment;
import com.ph41626.pma101_recipesharingapplication.Model.Recipe;
import com.ph41626.pma101_recipesharingapplication.R;

import java.util.ArrayList;
import java.util.List;

public class LinearLayoutAdapter {
    private Context context;
    private ArrayList<Recipe> recipes;
    private LinearLayout linearLayout;
    private ProfileFragment profileFragment;

    public LinearLayoutAdapter(Context context, ArrayList<Recipe> recipes, LinearLayout linearLayout, ProfileFragment profileFragment) {
        this.context = context;
        this.recipes = recipes;
        this.linearLayout = linearLayout;
        this.profileFragment = profileFragment;
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

    public void notifyDataSetChanged() {
        linearLayout.removeAllViews();
        for (Recipe recipe: recipes) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_all_recipe, linearLayout, false);

            if (recipe != null) {
                TextView
                        tv_recipe_name = view.findViewById(R.id.tv_recipe_name),
                        tv_recipe_averageRating = view.findViewById(R.id.tv_recipe_averageRating),
                        tv_recipe_ingredients_cook_time = view.findViewById(R.id.tv_recipe_ingredients_cook_time);
                ImageView img_recipe_thumbnail = view.findViewById(R.id.img_recipe_thumbnail);
                ProgressBar pb_load_img = view.findViewById(R.id.pb_load_img);
                RelativeLayout btn_more = view.findViewById(R.id.btn_more);

                tv_recipe_name.setText(recipe.getName());
                tv_recipe_averageRating.setText(String.valueOf(recipe.getAverageRating()));
                if (profileFragment.recipeIngredients.containsKey(recipe.getId())
                        && profileFragment.recipeIngredients.get(recipe.getId()) != null) {
                    tv_recipe_ingredients_cook_time.setText(
                            profileFragment.recipeIngredients.get(recipe.getId()).size() + " Ingredients | " +
                                    recipe.getCookTime() + " min");
                }
                if (profileFragment.recipeMedias.containsKey(recipe.getId())
                        && profileFragment.recipeMedias.get(recipe.getId()) != null) {
                    Glide.with(context).
                            asBitmap().
                            load(profileFragment.recipeMedias.get(recipe.getId()).getUrl()).
                            error(R.drawable.caption).
                            placeholder(R.drawable.caption).
                            into(img_recipe_thumbnail);
                }
                pb_load_img.setVisibility(View.GONE);
                btn_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu(view,recipe);
                    }
                });
            }
            linearLayout.addView(view);
        }
        profileFragment.adjustViewPagerHeight(profileFragment.viewPager2_recipe);
    }
    private void PopupMenu(View view, Recipe recipe) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        List<String> menuItems = getMenuItems(recipe);
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
    private List<String> getMenuItems(Recipe recipe) {
        List<String> menuItems = new ArrayList<>();
        if (recipe.isPublic()) {
            menuItems.add("Unshared");
        } else {
            menuItems.add("Shared");
        }
        menuItems.add("Update");
        menuItems.add("Delete");
        return menuItems;
    }
    private void handleMenuItemClick(MenuItem item, Recipe recipe) {
        if (item.getTitle().equals("Shared")) {
            recipe.setPublic(true);
            profileFragment.SharedRecipe(recipe);
        } else if (item.getTitle().equals("Unshared")) {
            recipe.setPublic(false);
            profileFragment.SharedRecipe(recipe);
        } else if (item.getTitle().equals("Delete")) {
            profileFragment.DeleteRecipe(recipe);
        } else if (item.getTitle().equals("Update")) {
            profileFragment.UpdateRecipe(recipe);
        }
    }
}
