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
import com.ph41626.pma101_recipesharingapplication.Fragment.AdminFragment;
import com.ph41626.pma101_recipesharingapplication.Model.Media;
import com.ph41626.pma101_recipesharingapplication.Model.Recipe;
import com.ph41626.pma101_recipesharingapplication.Model.User;
import com.ph41626.pma101_recipesharingapplication.R;

import java.util.ArrayList;

public class RecyclerViewRecipeAdminAdapter extends RecyclerView.Adapter<RecyclerViewRecipeAdminAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Recipe> recipes;
    private AdminFragment adminFragment;

    public RecyclerViewRecipeAdminAdapter(Context context, ArrayList<Recipe> recipes,AdminFragment adminFragment) {
        this.context = context;
        this.recipes = recipes;
        this.adminFragment = adminFragment;
        AddLoadingPlaceholders();
    }
    public void Update(ArrayList<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerViewRecipeAdminAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe_admin,null,false);
        return new ViewHolder(view);

    }
    public void AddLoadingPlaceholders() {
        recipes.clear();
        for (int i = 0; i < 10; i++) {
            recipes.add(null);
        }
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewRecipeAdminAdapter.ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        if (recipe != null) {
            Media recipeMedia = new Media();

            holder.tv_recipe_name.setText(recipe.getName());
            holder.tv_recipe_averageRating.setText(String.valueOf(recipe.getAverageRating()));
            if (adminFragment.mediaForRecipeHashMap.get(recipe.getId()) != null) {
                recipeMedia = adminFragment.mediaForRecipeHashMap.get(recipe.getId());
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
            User user = adminFragment.userForRecipeHashMap.get(recipe.getUserId());
            if (user != null) {
                if (user.getMediaId() != null && !user.getMediaId().isEmpty()) {
                    Glide.with(context).
                            load(adminFragment.mediaForUserHashMap.get(user.getMediaId()).getUrl()).
                            error(R.drawable.default_avatar).
                            placeholder(R.drawable.default_avatar).
                            into(holder.img_user_avatar);
                } else {
                    holder.img_user_avatar.setImageResource(R.drawable.default_avatar);
                }
            }
            if (adminFragment.userForRecipeHashMap.containsKey(recipe.getUserId()) && adminFragment.userForRecipeHashMap.get(recipe.getUserId()) != null) {
                holder.tv_recipe_owner.setText(adminFragment.userForRecipeHashMap.get(recipe.getUserId()).getName());
            }
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    adminFragment.RecipeDetail(recipe);
//                }
//            });
            holder.btn_more.setOnClickListener(v -> {
                adminFragment.PopupMenu(v,recipe);
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
        RelativeLayout btn_more;
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
            btn_more = itemView.findViewById(R.id.btn_more);
        }
    }
}
