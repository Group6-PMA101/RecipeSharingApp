package com.ph41626.pma101_recipesharingapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ph41626.pma101_recipesharingapplication.Fragment.AdminFragment;
import com.ph41626.pma101_recipesharingapplication.Model.User;
import com.ph41626.pma101_recipesharingapplication.R;

import java.util.ArrayList;

public class RecyclerViewManageUserAdapter extends RecyclerView.Adapter<RecyclerViewManageUserAdapter.ViewHolder> {
    private Context context;
    private ArrayList<User> users;
    private AdminFragment adminFragment;

    public RecyclerViewManageUserAdapter(Context context, ArrayList<User> users, AdminFragment adminFragment) {
        this.context = context;
        this.users = users;
        this.adminFragment = adminFragment;
    }
    public void Update(ArrayList<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerViewManageUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manage_user,null,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewManageUserAdapter.ViewHolder holder, int position) {
        User user = users.get(position);
        if (user != null) {
            holder.tv_rank.setText(String.valueOf(position + 1));
            holder.tv_user_name.setText("Name: " + user.getName());
            holder.tv_email.setText("Email: " + user.getEmail());
            if (user.getAccountType() == 0) {
                holder.tv_account_type.setText("Regular");
            } else {
                holder.tv_account_type.setText("Chef");
            }
            if (user.isStatus()) {
                holder.tv_status.setText("Locked");
                holder.tv_status.setTextColor(context.getResources().getColor(R.color.red_E00));
            } else {
                holder.tv_status.setText("Active");
                holder.tv_status.setTextColor(context.getResources().getColor(R.color.green_F56));
            }
            holder.btn_more.setOnClickListener(v -> {
                adminFragment.PopupMenuManageUser(v,user);
            });
        }
    }

    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_rank,tv_user_name,tv_email,tv_account_type,tv_status;
        ImageView img_user_avatar;
        ImageButton btn_more;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_rank = itemView.findViewById(R.id.tv_rank);
            tv_user_name = itemView.findViewById(R.id.tv_user_name);
            tv_email = itemView.findViewById(R.id.tv_email);
            tv_account_type = itemView.findViewById(R.id.tv_account_type);
            tv_status = itemView.findViewById(R.id.tv_status);
            img_user_avatar = itemView.findViewById(R.id.img_user_avatar);
            btn_more = itemView.findViewById(R.id.btn_more);
        }
    }
}
