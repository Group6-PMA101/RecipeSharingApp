package com.ph41626.pma101_recipesharingapplication.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ph41626.pma101_recipesharingapplication.Adapter.ViewPagerBottomNavigationMainAdapter;
import com.ph41626.pma101_recipesharingapplication.Model.Media;
import com.ph41626.pma101_recipesharingapplication.Model.Recipe;
import com.ph41626.pma101_recipesharingapplication.Model.User;
import com.ph41626.pma101_recipesharingapplication.Model.ViewModel;
import com.ph41626.pma101_recipesharingapplication.R;
import com.ph41626.pma101_recipesharingapplication.Services.FirebaseUtils;

import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {
    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_SAVED_RECIPES = 1;
    private static final int FRAGMENT_CREATE_RECIPE = 2;
    private static final int FRAGMENT_NOTIFICATIONS = 3;
    private static final int FRAGMENT_PROFILE = 4;
    private int current_fragment = FRAGMENT_HOME;


    public static final String STORAGE_MEDIAS = "STORAGE_MEDIAS";
    public static final String REALTIME_MEDIAS = "REALTIME_MEDIAS";
    public static final String REALTIME_INGREDIENTS = "REALTIME_INGREDIENTS";
    public static final String REALTIME_INSTRUCTIONS = "REALTIME_INSTRUCTIONS";
    public static final String REALTIME_RECIPES = "REALTIME_RECIPES";
    public static final String REALTIME_USERS = "REALTIME_USERS";

    private MeowBottomNavigation bottom_navigation_main;
    private ViewPagerBottomNavigationMainAdapter bottom_navigation_main_adapter;
    private ViewPager2 view_pager_main;

    private User currentUser = new User();

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    private FirebaseUtils firebaseUtils;
    private ViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        GetUser();
        BottomNavigationManager();
//        GetDataFromFireBase();
    }

    private void GetUser() {
        Intent intent = getIntent();
        currentUser = (User) intent.getSerializableExtra("user");
        viewModel.changeCurrentUser(currentUser);
    }

    private void GetDataFromFireBase() {
        FirebaseUtils firebaseUtils = new FirebaseUtils();

        GetRecipes(firebaseUtils,REALTIME_RECIPES);
        GetMedias(firebaseUtils,REALTIME_MEDIAS);
    }

    private void GetMedias(FirebaseUtils firebaseUtils,String path) {
        firebaseUtils.getDataFromFirebase(path, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void GetRecipes(FirebaseUtils firebaseUtils,String path) {
        firebaseUtils.getDataFromFirebase(path, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void BottomNavigationManager() {
        view_pager_main.setAdapter(bottom_navigation_main_adapter);
        view_pager_main.setUserInputEnabled(false);
        bottom_navigation_main.add(new MeowBottomNavigation.Model(0, R.drawable.ic_home));
        bottom_navigation_main.add(new MeowBottomNavigation.Model(1, R.drawable.ic_bookmark));
        if (currentUser.getAccountType() != 0) bottom_navigation_main.add(new MeowBottomNavigation.Model(2, R.drawable.ic_add));
        bottom_navigation_main.add(new MeowBottomNavigation.Model(3, R.drawable.ic_notification));
        bottom_navigation_main.add(new MeowBottomNavigation.Model(4, R.drawable.ic_user));
        bottom_navigation_main.show(0,true);

        bottom_navigation_main.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                switch (model.getId()) {
                    case 0:
                        if (current_fragment != FRAGMENT_HOME) {
                            view_pager_main.setCurrentItem(0,false);
                            current_fragment = FRAGMENT_HOME;
                        }
                        break;
                    case 1:
                        if (current_fragment != FRAGMENT_SAVED_RECIPES) {
                            view_pager_main.setCurrentItem(1,false);
                            current_fragment = FRAGMENT_SAVED_RECIPES;
                        }
                        break;
                    case 2:
                        if (current_fragment != FRAGMENT_CREATE_RECIPE) {
                            view_pager_main.setCurrentItem(2,false);
                            current_fragment = FRAGMENT_CREATE_RECIPE;
                        }
                        break;
                    case 3:
                        if (current_fragment != FRAGMENT_NOTIFICATIONS) {
                            view_pager_main.setCurrentItem(3,false);
                            current_fragment = FRAGMENT_NOTIFICATIONS;
                        }
                        break;
                    case 4:
                        if (current_fragment != FRAGMENT_PROFILE) {
                            view_pager_main.setCurrentItem(4,false);
                            current_fragment = FRAGMENT_PROFILE;
                        }
                        break;
                    default: break;
                }
                return null;
            }
        });
    }

    private void initUI() {
        bottom_navigation_main = findViewById(R.id.bottomNavigationMain);
        view_pager_main = findViewById(R.id.viewPagerMain);
        bottom_navigation_main_adapter = new ViewPagerBottomNavigationMainAdapter(this);
        firebaseUtils = new FirebaseUtils();
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
    }
}
