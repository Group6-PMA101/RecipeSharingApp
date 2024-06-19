package com.ph41626.pma101_recipesharingapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ph41626.pma101_recipesharingapplication.Activity.MainActivity;
import com.ph41626.pma101_recipesharingapplication.Model.Media;
import com.ph41626.pma101_recipesharingapplication.Model.User;
import com.ph41626.pma101_recipesharingapplication.Model.UserFollower;
import com.ph41626.pma101_recipesharingapplication.R;
import com.ph41626.pma101_recipesharingapplication.Services.FirebaseUtils;
import com.ph41626.pma101_recipesharingapplication.UserListActivity;

import java.util.ArrayList;

public class RecyclerViewUserListAdapter extends RecyclerView.Adapter<RecyclerViewUserListAdapter.ViewHolder> {
    private Context context;
    private ArrayList<UserFollower> userFollowers;
    private UserListActivity userListActivity;
    public RecyclerViewUserListAdapter(Context context, ArrayList<UserFollower> userFollowers,UserListActivity userListActivity) {
        this.context = context;
        this.userFollowers = userFollowers;
        this.userListActivity = userListActivity;
    }
    public void Update(ArrayList<UserFollower> userFollowers) {
        this.userFollowers = userFollowers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerViewUserListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_list,null,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewUserListAdapter.ViewHolder holder, int position) {
        UserFollower userFollower = userFollowers.get(position);
        User user = new User();
        if (userListActivity.TYPE) {
            user = userListActivity.userForUserFollow.get(userFollower.getUserId());
        } else {
            user = userListActivity.userForUserFollow.get(userFollower.getChefId());
        }
        holder.tv_user_name.setText(user.getName());

        if (user.getMediaId() != null && !user.getMediaId().isEmpty()) {
            new FirebaseUtils().getDataFromFirebaseById(MainActivity.REALTIME_MEDIAS, user.getMediaId(), new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Media media = snapshot.getValue(Media.class);
                    Glide.with(context).
                            load(media.getUrl()).
                            error(R.drawable.caption).
                            placeholder(R.drawable.caption).
                            into(holder.img_user_avatar);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            holder.img_user_avatar.setImageResource(R.drawable.default_avatar);
        }

        if (userListActivity.TYPE) {
            holder.btn_more.setOnClickListener(v -> {
                userListActivity.PopUpMenu(v,userFollower);
            });
        } else {
            holder.btn_more.setVisibility(View.GONE);

            holder.btn_unfollow.setVisibility(View.VISIBLE);
            holder.btn_unfollow.setOnClickListener(v -> {
                userListActivity.UnFollow(userFollower);
            });
        }
    }

    @Override
    public int getItemCount() {
        return userFollowers != null ? userFollowers.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_user_name;
        ImageButton btn_more;
        Button btn_unfollow;
        ImageView img_user_avatar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_user_name = itemView.findViewById(R.id.tv_user_name);
            btn_more = itemView.findViewById(R.id.btn_more);
            btn_unfollow = itemView.findViewById(R.id.btn_unfollow);
            img_user_avatar = itemView.findViewById(R.id.img_user_avatar);
        }
    }
}
