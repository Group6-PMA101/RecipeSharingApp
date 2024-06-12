package com.ph41626.pma101_recipesharingapplication.Adapter;

import static com.ph41626.pma101_recipesharingapplication.Services.Services.isVideo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ph41626.pma101_recipesharingapplication.Fragment.HomeFragment;
import com.ph41626.pma101_recipesharingapplication.Model.Media;
import com.ph41626.pma101_recipesharingapplication.Model.Recipe;
import com.ph41626.pma101_recipesharingapplication.Model.User;
import com.ph41626.pma101_recipesharingapplication.R;

import java.util.ArrayList;
import java.util.HashMap;

public class RecyclerViewRecipeTrendingAdapter extends RecyclerView.Adapter<RecyclerViewRecipeTrendingAdapter.ViewHolder> {
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;
    private Context context;
    private HomeFragment homeFragment;
    private ArrayList<Recipe> recipes;
    private HashMap<String,Media> recipeMedias = new HashMap<>();
    private HashMap<String, User> recipeUsers = new HashMap<>();
    private HashMap<String,Media> userMedias = new HashMap<>();
    public RecyclerViewRecipeTrendingAdapter(Context context, ArrayList<Recipe> recipes,HomeFragment homeFragment) {
        this.context = context;
        this.recipes = recipes;
        this.homeFragment = homeFragment;
        AddLoadingPlaceholders();
    }
    public void AddLoadingPlaceholders() {
        recipes.clear();
        for (int i = 0; i < 10; i++) {
            recipes.add(null);
        }
        notifyDataSetChanged();
    }
    public void Update(ArrayList<Recipe> recipes) {
//        this.recipes = recipes;
        if (recipes.size() > 10) {
            this.recipes = new ArrayList<>(recipes.subList(0, 10));
        } else {
            this.recipes = recipes;
        }
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe_trending,null,false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe_trending_load,null,false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewRecipeTrendingAdapter.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            Recipe recipe = recipes.get(position);
            Media recipeMedia = new Media();

            holder.tv_recipe_name.setText(recipe.getName());
            holder.tv_recipe_averageRating.setText(String.valueOf(recipe.getAverageRating()));
            if (homeFragment.recipeMedias.get(recipe.getId()) != null) {
                recipeMedia = homeFragment.recipeMedias.get(recipe.getId());
                Glide.with(context).
                        load(recipeMedia.getUrl()).
                        error(R.drawable.caption).
                        placeholder(R.drawable.caption).
                        into(holder.img_recipe_thumbnail);
                if (isVideo(recipeMedia.getUrl())) {
                    holder.btn_play.setVisibility(View.VISIBLE);
                } else {
                    holder.btn_play.setVisibility(View.GONE);
                }
                holder.pb_load_img.setVisibility(View.GONE);
            }
            User user = homeFragment.recipeUsers.get(recipe.getUserId());
            if (user != null) {
                if (user.getMediaId() != null && !user.getMediaId().isEmpty()) {
                    Glide.with(context).
                            load(homeFragment.userMedias.get(user.getId()).getUrl()).
                            error(R.drawable.default_avatar).
                            placeholder(R.drawable.default_avatar).
                            into(holder.img_user_avatar);
                } else {
                    holder.img_user_avatar.setImageResource(R.drawable.default_avatar);
                }
            }
            if (homeFragment.recipeUsers.containsKey(recipe.getUserId()) && homeFragment.recipeUsers.get(recipe.getUserId()) != null) {
                holder.tv_recipe_owner.setText(homeFragment.recipeUsers.get(recipe.getUserId()).getName());
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    homeFragment.RecipeDetail(recipe);
                }
            });
            holder.btn_save.setOnClickListener(v -> {
                homeFragment.SaveRecipe(context,recipe);
            });
        }
    }

    @Override
    public int getItemCount() {
        return recipes != null ? recipes.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_recipe_name,tv_recipe_owner,tv_recipe_averageRating;
        ImageView img_recipe_thumbnail,img_user_avatar;
        LinearLayout btn_play;
        RelativeLayout btn_save;
        ProgressBar pb_load_img;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_recipe_owner = itemView.findViewById(R.id.tv_recipe_owner);
            tv_recipe_name = itemView.findViewById(R.id.tv_recipe_name);
            tv_recipe_averageRating = itemView.findViewById(R.id.tv_recipe_averageRating);
            img_recipe_thumbnail = itemView.findViewById(R.id.img_recipe_thumbnail);
            img_user_avatar = itemView.findViewById(R.id.img_user_avatar);
            btn_play = itemView.findViewById(R.id.btn_play);
            pb_load_img = itemView.findViewById(R.id.pb_load_img);
            btn_save = itemView.findViewById(R.id.btn_save);
        }
    }
}
