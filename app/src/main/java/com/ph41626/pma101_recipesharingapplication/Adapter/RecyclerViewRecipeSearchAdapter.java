package com.ph41626.pma101_recipesharingapplication.Adapter;

import static android.view.View.GONE;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ph41626.pma101_recipesharingapplication.Activity.MainActivity;
import com.ph41626.pma101_recipesharingapplication.Model.Media;
import com.ph41626.pma101_recipesharingapplication.Model.Recipe;
import com.ph41626.pma101_recipesharingapplication.R;
import com.ph41626.pma101_recipesharingapplication.Activity.SearchActivity;
import com.ph41626.pma101_recipesharingapplication.Services.FirebaseUtils;

import java.util.ArrayList;

public class RecyclerViewRecipeSearchAdapter extends RecyclerView.Adapter<RecyclerViewRecipeSearchAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Recipe> recipes;
    private SearchActivity searchActivity;

    public RecyclerViewRecipeSearchAdapter(Context context, ArrayList<Recipe> recipes, SearchActivity searchActivity) {
        this.context = context;
        this.recipes = recipes;
        this.searchActivity = searchActivity;
    }

    public void UpdateRecipe(ArrayList<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerViewRecipeSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saved_recipe_detail,null,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewRecipeSearchAdapter.ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        if (recipe != null) {
            holder.tv_recipe_name.setText(recipe.getName());
            holder.tv_recipe_averageRating.setText(String.valueOf(recipe.getAverageRating()));
            holder.tv_cook_time.setText("Cook time: " + recipe.getCookTime() + " min");
            new FirebaseUtils().getDataFromFirebaseById(MainActivity.REALTIME_MEDIAS, recipe.getMediaId(), new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Media media = snapshot.getValue(Media.class);
                    Glide.with(context).
                            asBitmap().
                            load(media.getUrl()).
                            error(R.drawable.caption).
                            placeholder(R.drawable.caption).
                            into(holder.img_recipe_thumbnail);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            holder.pb_load_img.setVisibility(GONE);

            ImageView img = holder.btn_more.findViewById(R.id.icon_more);
            img.setBackgroundResource(R.drawable.ic_bookmark);
//            holder.btn_more.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    seeAllRecipeActivity.SaveRecipe(recipe);
//                }
//            });
            holder.itemView.setOnClickListener(v -> {
                searchActivity.RecipeDetail(recipe);
            });
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
            layout_hide.setVisibility(GONE);
        }

    }
}
