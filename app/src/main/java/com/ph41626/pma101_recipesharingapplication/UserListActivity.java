package com.ph41626.pma101_recipesharingapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ph41626.pma101_recipesharingapplication.Activity.MainActivity;
import com.ph41626.pma101_recipesharingapplication.Adapter.RecyclerViewUserListAdapter;
import com.ph41626.pma101_recipesharingapplication.Model.User;
import com.ph41626.pma101_recipesharingapplication.Model.UserFollower;
import com.ph41626.pma101_recipesharingapplication.Services.FirebaseUtils;
import com.ph41626.pma101_recipesharingapplication.Services.UserPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UserListActivity extends AppCompatActivity {

    private ArrayList<UserFollower> userFollowers = new ArrayList<>();
    public HashMap<String, User> userForUserFollow = new HashMap<>();
    private RecyclerView rcv_user_list;
    private RecyclerViewUserListAdapter userListAdapter;
    public boolean TYPE = true;
    private TextView tv_type_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_list);

        Intent intent = getIntent();
        TYPE = intent.getBooleanExtra("type",true);

        initUI();
        if (TYPE) tv_type_name.setText("Follower");
        else tv_type_name.setText("Following");
        SetUpRecyclerView();
        fetchUserFollowForUser();

    }

    private void SetUpRecyclerView() {
        userListAdapter = new RecyclerViewUserListAdapter(this,new ArrayList<>(),this);
        rcv_user_list.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        rcv_user_list.setAdapter(userListAdapter);
    }


    private void fetchUserFollowForUser() {
        String key = "";
        if (TYPE) {
            key = "chefId";
        } else {
            key = "userId";
        }
        new FirebaseUtils().getAllDataByKey(MainActivity.REALTIME_FOLLOWERS, key, UserPreferences.GetUser(this).getId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userFollowers.clear();
                List<CompletableFuture<Void>> futures = new ArrayList<>();
                for (DataSnapshot child: snapshot.getChildren()) {
                    CompletableFuture<Void> future = new CompletableFuture<>();
                    futures.add(future);
                    UserFollower userFollower = child.getValue(UserFollower.class);
                    fetchUser(userFollower,future);
                    userFollowers.add(userFollower);
                }

                CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
                allOf.thenRun(() -> {
                    userListAdapter.Update(userFollowers);
                }).exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void UnFollow(UserFollower userFollower) {
        User user = UserPreferences.GetUser(this);
        user.setFollowingCount(user.getFollowingCount() - 1);

        User getChef = userForUserFollow.get(userFollower.getChefId());
        getChef.setFollowersCount(getChef.getFollowersCount() - 1);
        UserPreferences.ClearUser(this);
        UserPreferences.SaveUser(this,user);
        FirebaseDatabase.getInstance().getReference(MainActivity.REALTIME_USERS)
                .child(getChef.getId())
                .setValue(getChef)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });

        FirebaseDatabase.getInstance().getReference(MainActivity.REALTIME_USERS)
                .child(user.getId())
                .setValue(user)
                .addOnCompleteListener(task -> {
                    Intent intent = new Intent("com.example.USER_DATA_CHANGED");
                    sendBroadcast(intent);
                });
        FirebaseDatabase.getInstance().getReference(MainActivity.REALTIME_FOLLOWERS)
                .child(userFollower.getId())
                .setValue(null)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        userFollowers.remove(userFollower);
                        userListAdapter.Update(userFollowers);
                    }
                });
    }
    public void PopUpMenu(View view, UserFollower userFollower) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        List<String> menuItems = getMenuItems();
        for (String menuItem : menuItems) {
            popupMenu.getMenu().add(menuItem);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                handleMenuItemClick(item,userFollower);
                return true;
            }
        });
        popupMenu.show();
    }
    private List<String> getMenuItems() {
        List<String> menuItems = new ArrayList<>();
        menuItems.add("Remove this follower");
        return menuItems;
    }

    private void handleMenuItemClick(MenuItem item,UserFollower userFollower) {
        if (item.getTitle().equals("Remove this follower")) {
            User user = UserPreferences.GetUser(this);
            user.setFollowersCount(user.getFollowersCount() - 1);

            User getUserFollower = userForUserFollow.get(userFollower.getUserId());
            getUserFollower.setFollowingCount(getUserFollower.getFollowingCount() - 1);
            UserPreferences.ClearUser(this);
            UserPreferences.SaveUser(this,user);
            FirebaseDatabase.getInstance().getReference(MainActivity.REALTIME_USERS)
                    .child(getUserFollower.getId())
                    .setValue(getUserFollower)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });

            FirebaseDatabase.getInstance().getReference(MainActivity.REALTIME_USERS)
                    .child(user.getId())
                    .setValue(user)
                    .addOnCompleteListener(task -> {
                        Intent intent = new Intent("com.example.USER_DATA_CHANGED");
                        sendBroadcast(intent);
                    });
            FirebaseDatabase.getInstance().getReference(MainActivity.REALTIME_FOLLOWERS)
                    .child(userFollower.getId())
                    .setValue(null)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            userFollowers.remove(userFollower);
                            userListAdapter.Update(userFollowers);
                        }
                    });
        }
    }
    private void fetchUser(UserFollower userFollower,CompletableFuture<Void> future) {
        String id = "";
        if (TYPE) {
            id = userFollower.getUserId();
        } else {
            id = userFollower.getChefId();
        }
        new FirebaseUtils().getDataFromFirebaseById(MainActivity.REALTIME_USERS, id, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                userForUserFollow.put(user.getId(),user);
                future.complete(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initUI() {
        tv_type_name = findViewById(R.id.tv_type_name);
        rcv_user_list = findViewById(R.id.rcv_user);
    }
}