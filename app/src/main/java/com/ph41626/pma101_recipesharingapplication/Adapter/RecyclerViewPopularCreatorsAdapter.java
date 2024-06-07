package com.ph41626.pma101_recipesharingapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ph41626.pma101_recipesharingapplication.Fragment.HomeFragment;
import com.ph41626.pma101_recipesharingapplication.Model.Recipe;
import com.ph41626.pma101_recipesharingapplication.Model.User;
import com.ph41626.pma101_recipesharingapplication.R;

import java.util.ArrayList;

public class RecyclerViewPopularCreatorsAdapter extends RecyclerView.Adapter<RecyclerViewPopularCreatorsAdapter.ViewHolder> {
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;
    private Context context;
    private ArrayList<User> users;
    private HomeFragment homeFragment;

    public RecyclerViewPopularCreatorsAdapter(Context context, ArrayList<User> users, HomeFragment homeFragment) {
        this.context = context;
        this.users = users;
        this.homeFragment = homeFragment;
        AddLoadingPlaceholders();
    }
    public void AddLoadingPlaceholders() {
        users.clear();
        for (int i = 0; i < 2; i++) {
            users.add(null);
        }
        notifyDataSetChanged();
    }
    public void Update(ArrayList<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public RecyclerViewPopularCreatorsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_popular_creators,null,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewPopularCreatorsAdapter.ViewHolder holder, int position) {
        User user = users.get(position);
        if (user != null) {
            holder.tv_user_name.setText(user.getName());
            if (user.getMediaId() != null && !user.getMediaId().isEmpty()) {
                Glide.with(context).
                        load(homeFragment.userMedias.get(user.getId()).getUrl()).
                        error(R.drawable.default_avatar).
                        placeholder(R.drawable.default_avatar).
                        into(holder.img_avatar_user);
            } else {
                holder.img_avatar_user.setImageResource(R.drawable.default_avatar);
            }
            holder.pb_load_img.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_user_name;
        ImageView img_avatar_user;
        ProgressBar pb_load_img;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_user_name = itemView.findViewById(R.id.tv_user_name);
            img_avatar_user = itemView.findViewById(R.id.img_avatar_user);
            pb_load_img = itemView.findViewById(R.id.pb_load_img);
        }
    }
}
