package com.ph41626.pma101_recipesharingapplication.Adapter;

import static android.view.View.GONE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
            if (!recipe.isPublic() || recipe.isStatus()) {
                HideItem(holder);
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
            holder.pb_load_img.setVisibility(GONE);
            holder.btn_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu(view,holder.getAdapterPosition());
                }
            });
            holder.itemView.setOnClickListener(v -> {
                recipeCollectionActivity.RecipeDetail(recipe);
            });
        } else {
           HideItem(holder);
        }
    }
    private void HideItem(ViewHolder holder) {
        holder.layout_hide.setVisibility(View.VISIBLE);
        holder.tv_recipe_unshare.setText("This recipe has been hidden or deleted by the user.");
        holder.layout_main.setVisibility(GONE);
        holder.btn_remove.setOnClickListener(v -> {
            PopupMenu(v,holder.getAdapterPosition());
        });
    }
    private void PopupMenu(View view, int pos) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        List<String> menuItems = getMenuItems();
        for (String menuItem : menuItems) {
            popupMenu.getMenu().add(menuItem);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                handleMenuItemClick(item, pos);
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

    private void handleMenuItemClick(MenuItem item, int pos) {
        if (item.getTitle().equals("Delete")) {
            recipeCollectionActivity.Remove(pos);
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
                tv_cook_time,
                tv_recipe_unshare;
        ImageView img_recipe_thumbnail;
        ProgressBar pb_load_img;
        RelativeLayout btn_more,layout_main,btn_remove;
        LinearLayout layout_hide;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_recipe_name = itemView.findViewById(R.id.tv_recipe_name);
            tv_recipe_averageRating = itemView.findViewById(R.id.tv_recipe_averageRating);
            tv_cook_time = itemView.findViewById(R.id.tv_cook_time);
            tv_recipe_unshare = itemView.findViewById(R.id.tv_recipe_unshare);
            img_recipe_thumbnail = itemView.findViewById(R.id.img_recipe_thumbnail);
            pb_load_img = itemView.findViewById(R.id.pb_load_img);
            btn_more = itemView.findViewById(R.id.btn_more);
            btn_remove = itemView.findViewById(R.id.btn_remove);
            layout_main = itemView.findViewById(R.id.layout_main);
            layout_hide = itemView.findViewById(R.id.layout_hide);
        }
    }
}
