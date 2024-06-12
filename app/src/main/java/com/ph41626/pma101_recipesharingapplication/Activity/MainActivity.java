package com.ph41626.pma101_recipesharingapplication.Activity;

import static com.ph41626.pma101_recipesharingapplication.Services.Services.RandomID;
import static com.ph41626.pma101_recipesharingapplication.Services.UserPreferences.ClearUser;
import static com.ph41626.pma101_recipesharingapplication.Services.UserPreferences.GetUser;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ph41626.pma101_recipesharingapplication.Adapter.ViewPagerBottomNavigationMainAdapter;
import com.ph41626.pma101_recipesharingapplication.Model.Media;
import com.ph41626.pma101_recipesharingapplication.Model.Notification;
import com.ph41626.pma101_recipesharingapplication.Model.Recipe;
import com.ph41626.pma101_recipesharingapplication.Model.RecipeCollection;
import com.ph41626.pma101_recipesharingapplication.Model.Recipe_RecipeCollection;
import com.ph41626.pma101_recipesharingapplication.Model.User;
import com.ph41626.pma101_recipesharingapplication.Model.ViewModel;
import com.ph41626.pma101_recipesharingapplication.R;
import com.ph41626.pma101_recipesharingapplication.Services.FirebaseUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {
    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_SAVED_RECIPES = 1;
    private static final int FRAGMENT_CREATE_RECIPE = 2;
    private static final int FRAGMENT_NOTIFICATIONS = 3;
    private static final int FRAGMENT_PROFILE = 4;
    private static final int FRAGMENT_ADMIN = 5;
    private int current_fragment = FRAGMENT_HOME;


    public static final String STORAGE_MEDIAS = "STORAGE_MEDIAS";
    public static final String REALTIME_MEDIAS = "REALTIME_MEDIAS";
    public static final String REALTIME_INGREDIENTS = "REALTIME_INGREDIENTS";
    public static final String REALTIME_INSTRUCTIONS = "REALTIME_INSTRUCTIONS";
    public static final String REALTIME_RECIPES = "REALTIME_RECIPES";
    public static final String REALTIME_USERS = "REALTIME_USERS";
    public static final String REALTIME_COMMENTS = "REALTIME_COMMENTS";
    public static final String REALTIME_REVIEWS = "REALTIME_REVIEWS";
    public static final String REALTIME_FOLLOWERS = "REALTIME_FOLLOWERS";
    public static final String REALTIME_RECIPE_COLLECTIONS = "REALTIME_RECIPE_COLLECTIONS";
    public static final String REALTIME_RECIPE_RECIPECOLLECTIONS = "REALTIME_RECIPE_RECIPECOLLECTIONS";
    public static final String REALTIME_NOTIFICATIONS = "REALTIME_NOTIFICATIONS";

    private MeowBottomNavigation bottom_navigation_main;
    private ViewPagerBottomNavigationMainAdapter bottom_navigation_main_adapter;
    private ViewPager2 view_pager_main;

//    private User currentUser = new User();
//    public User getCurrentUser() {
//        return currentUser;
//    }
//    public void setCurrentUser(User currentUser) {
//        this.currentUser = currentUser;
//    }

    private FirebaseUtils firebaseUtils;
    private ViewModel viewModel;
    private ArrayList<Notification> notifications = new ArrayList<>();
    public ArrayList<RecipeCollection> recipeCollections = new ArrayList<>();
    public HashMap<String,ArrayList<Recipe_RecipeCollection>> recipeRecipeCollectionHashMap = new HashMap<>();
    public HashMap<String,ArrayList<Recipe>> recipeForRecipeCollection = new HashMap<>();
    public HashMap<String,Media> recipeMedia = new HashMap<>();
    public CompletableFuture<Void> recipeCollectionFuture = new CompletableFuture<>();

    public List<CompletableFuture<Void>> recipeCollectionFutures = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        viewModel.changeCurrentUser(GetUser(this));
        BottomNavigationManager();
//        GetDataFromFireBase();
        fetchRecipeCollectionForUser();
        LockAccount();
        fetchNotificationForUser();

        recipeCollectionFuture.thenAccept(result -> {
//            if (recipeCollections != null && !recipeCollections.isEmpty()) {
//                for (RecipeCollection recipeCollection:recipeCollections) {
//
//                }
//            }
        });
    }
    public boolean LockAccount() {
        if (GetUser(this).isStatus()) {
            view_pager_main.setCurrentItem(3);
            bottom_navigation_main.show(3,true);
            bottom_navigation_main.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
                @Override
                public Unit invoke(MeowBottomNavigation.Model model) {
                    return null;
                }
            });
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Account Locked");
            builder.setMessage("Your account has been locked. Please log in again.");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
//                    ClearUser(MainActivity.this);
//                    Intent intent = new Intent(MainActivity.this, SignInActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
//                    finish();
                }
            });
            AlertDialog alertDialog = builder.create();
//            alertDialog.setCancelable(false);
//            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
            return true;
        }
        return false;
    }
    private void fetchRecipeCollectionForUser() {
        new FirebaseUtils().getAllDataByKeyRealTime(REALTIME_RECIPE_COLLECTIONS, "userId", GetUser(this).getId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GetRecipeForRecipeCollection(snapshot);

                CompletableFuture<Void> allOf = CompletableFuture.allOf(recipeCollectionFutures.toArray(new CompletableFuture[0]));
                allOf.thenRun(() -> {
                    viewModel.changeRecipeForRecipeCollection(recipeForRecipeCollection);
                    for (RecipeCollection recipeCollection:recipeCollections) {
                        int index1 = recipeRecipeCollectionHashMap.get(recipeCollection.getId()).size();
                        int index2 = recipeForRecipeCollection.get(recipeCollection.getId()).size();

                    }
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
    private void GetRecipeForRecipeCollection(DataSnapshot snapshot) {
        recipeRecipeCollectionHashMap.clear();
        recipeForRecipeCollection.clear();
        recipeMedia.clear();
        recipeCollectionFutures.clear();

        for (DataSnapshot child:snapshot.getChildren()) {
            RecipeCollection recipeCollection = child.getValue(RecipeCollection.class);
            recipeCollections.add(recipeCollection);
            if (recipeCollection != null && recipeCollection.getNumberOfRecipes() > 0) {
                CompletableFuture<Void> future = new CompletableFuture<>();
                recipeCollectionFutures.add(future);
                fetchRecipeRecipeCollection(future,recipeCollection);
            }
        }
        recipeCollectionFuture.complete(null);
        viewModel.changeRecipeCollectionForUser(recipeCollections);
    }
    private void fetchNotificationForUser() {
        new FirebaseUtils().getAllDataByKeyRealTime(MainActivity.REALTIME_NOTIFICATIONS, "userId", GetUser(this).getId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notifications.clear();
                int newNotiCount = 0;
                for (DataSnapshot child:snapshot.getChildren()) {
                    Notification notification = child.getValue(Notification.class);
                    if (!notification.isStatus()) newNotiCount++;
                    notifications.add(notification);
                }

                if (newNotiCount != 0) {
                    bottom_navigation_main.setCount(3, String.valueOf(newNotiCount));
                } else {
                    bottom_navigation_main.clearCount(3);
                }
                viewModel.changeNotificationForUser(notifications);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void fetchRecipeRecipeCollection(CompletableFuture<Void> future,RecipeCollection recipeCollection) {
        new FirebaseUtils().getAllDataByKey(REALTIME_RECIPE_RECIPECOLLECTIONS, "recipeCollectionId", recipeCollection.getId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Recipe_RecipeCollection> recipeRecipeCollections = new ArrayList<>();
                for (DataSnapshot child:snapshot.getChildren()) {
                    Recipe_RecipeCollection recipeRecipeCollection = child.getValue(Recipe_RecipeCollection.class);
                    recipeRecipeCollections.add(recipeRecipeCollection);
                }
                recipeRecipeCollectionHashMap.put(recipeCollection.getId(),recipeRecipeCollections);
                Log.e("Check app recipe RECIPE_RECIPECOLLECTIONS",recipeRecipeCollections.size() + "");
                List<CompletableFuture<Void>> mediaFutures = new ArrayList<>();
                for (Recipe_RecipeCollection recipeRecipeCollection:recipeRecipeCollections) {
                    CompletableFuture<Void> mediaFuture = new CompletableFuture<>();
                    mediaFutures.add(mediaFuture);
                    fetchRecipeForRecipeCollection(mediaFuture,recipeCollection,recipeRecipeCollection);
                }
                CompletableFuture.allOf(mediaFutures.toArray(new CompletableFuture[0]))
                        .thenRun(() -> future.complete(null))
                        .exceptionally(e -> {
                            e.printStackTrace();
                            future.complete(null);
                            return null;
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchRecipeForRecipeCollection(CompletableFuture<Void> future,RecipeCollection recipeCollection,Recipe_RecipeCollection recipeRecipeCollection) {
        new FirebaseUtils().getDataFromFirebaseById(REALTIME_RECIPES, recipeRecipeCollection.getRecipeId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Recipe recipe = snapshot.getValue(Recipe.class);
//                ArrayList<Recipe> newArr = new ArrayList<>();
//                if (recipeForRecipeCollection.containsKey(recipeCollection.getId())
//                        && recipeForRecipeCollection.get(recipeCollection.getId()) != null
//                        && !recipeForRecipeCollection.get(recipeCollection.getId()).isEmpty()) {
//                    newArr = recipeForRecipeCollection.get(recipeCollection.getId());
//                }
                ArrayList<Recipe> newArr = recipeForRecipeCollection.computeIfAbsent(recipeCollection.getId(), k -> new ArrayList<>());
                newArr.add(recipe);
                recipeForRecipeCollection.put(recipeCollection.getId(),newArr);
//                Toast.makeText(MainActivity.this, recipeCollection.getName() + newArr.size(), Toast.LENGTH_SHORT).show();
                fetchMediaForRecipe(future,recipe);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchMediaForRecipe(CompletableFuture<Void> future, Recipe recipe) {
        if (recipe == null) {
            future.complete(null);
            return;
        }
        new FirebaseUtils().getDataFromFirebaseById(REALTIME_MEDIAS, recipe.getMediaId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Media media = snapshot.getValue(Media.class);
                recipeMedia.put(recipe.getId(),media);
                future.complete(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
        view_pager_main.setOffscreenPageLimit(5);
        bottom_navigation_main.add(new MeowBottomNavigation.Model(0, R.drawable.ic_home));
        bottom_navigation_main.add(new MeowBottomNavigation.Model(1, R.drawable.ic_bookmark));
        if (GetUser(this).getAccountType() != 0) bottom_navigation_main.add(new MeowBottomNavigation.Model(2, R.drawable.ic_add));
        bottom_navigation_main.add(new MeowBottomNavigation.Model(3, R.drawable.ic_notification));
        bottom_navigation_main.add(new MeowBottomNavigation.Model(4, R.drawable.ic_user));
        new FirebaseUtils().getDataFromFirebaseById("ADMIN", FirebaseAuth.getInstance().getCurrentUser().getUid(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    bottom_navigation_main.add(new MeowBottomNavigation.Model(5, R.drawable.ic_admin_panel_settings));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                    case 5:
                        if (current_fragment != FRAGMENT_ADMIN) {
                            view_pager_main.setCurrentItem(5,false);
                            current_fragment = FRAGMENT_ADMIN;
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
