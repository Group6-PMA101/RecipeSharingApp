package com.ph41626.pma101_recipesharingapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ph41626.pma101_recipesharingapplication.Activity.RecipeDetailActivity;
import com.ph41626.pma101_recipesharingapplication.Adapter.RecyclerViewAllRecipeAdapter;
import com.ph41626.pma101_recipesharingapplication.Adapter.RecyclerViewSeeAllAdapter;
import com.ph41626.pma101_recipesharingapplication.Model.Media;
import com.ph41626.pma101_recipesharingapplication.Model.Recipe;
import com.ph41626.pma101_recipesharingapplication.Model.User;
import com.ph41626.pma101_recipesharingapplication.Services.MainActivityEventListener;
import com.ph41626.pma101_recipesharingapplication.Services.RecipeDetailEventListener;
import com.ph41626.pma101_recipesharingapplication.Services.RecipeEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class SeeAllRecipeActivity extends AppCompatActivity implements RecipeDetailEventListener,RecipeEventListener {

    private RecyclerView rcv_see_all;
    private ArrayList<Recipe> recipes;
    public HashMap<String, Media> recipeMedias = new HashMap<>();
    public HashMap<String, User> recipeUsers = new HashMap<>();
    public HashMap<String,Media> userMedias = new HashMap<>();
    private Button btn_back;
    private static MainActivityEventListener eventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_see_all_recipe);

        initUI();
        GetData();

        btn_back.setOnClickListener(v -> {
            finish();
        });

    }
    public void RecipeDetail(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        RecipeDetailActivity.setRecipeEventListener(this);
        RecipeDetailActivity.setRecipeDetailEventListener(this);
        intent.putExtra("recipe",recipe);
        intent.putExtra("recipeMedia",recipeMedias.get(recipe.getId()));
        intent.putExtra("recipeOwner",recipeUsers.get(recipe.getUserId()));
        startActivity(intent);
    }
    private void GetData() {
        Intent intent = getIntent();
        recipes = (ArrayList<Recipe>) intent.getSerializableExtra("recipes");
        recipeMedias = (HashMap<String, Media>) intent.getSerializableExtra("mediaForRecipes");
        recipeUsers = (HashMap<String, User>) intent.getSerializableExtra("users");
        userMedias = (HashMap<String, Media>) intent.getSerializableExtra("mediaForUsers");

        RecyclerViewSeeAllAdapter seeAllAdapter = new RecyclerViewSeeAllAdapter(this,new ArrayList<>(),this);
        rcv_see_all.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        rcv_see_all.setAdapter(seeAllAdapter);
        seeAllAdapter.Update(recipes);
    }

    public void SaveRecipe(Recipe recipe) {
        eventListener.onSavedRecipe(this,recipe);
    }
    private void initUI() {
        rcv_see_all = findViewById(R.id.rcv_see_all);
        btn_back = findViewById(R.id.btn_back);
    }
    public static void setMainActivityEventListener(MainActivityEventListener mainActivityEventListener) {
        eventListener = mainActivityEventListener;
    }
    @Override
    public void onDataChange(Recipe recipe) {
//        recipeTrendingAdapter.Update(recipes);
    }

    @Override
    public void onFollowEvent(String userId, User user) {
        recipeUsers.put(userId,user);
    }
}