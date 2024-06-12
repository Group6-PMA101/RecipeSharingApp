package com.ph41626.pma101_recipesharingapplication.Activity;

import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_INGREDIENTS;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ph41626.pma101_recipesharingapplication.Adapter.RecyclerViewRecipeSearchAdapter;
import com.ph41626.pma101_recipesharingapplication.Model.Ingredient;
import com.ph41626.pma101_recipesharingapplication.Model.Recipe;
import com.ph41626.pma101_recipesharingapplication.Model.User;
import com.ph41626.pma101_recipesharingapplication.R;
import com.ph41626.pma101_recipesharingapplication.Services.FirebaseUtils;
import com.ph41626.pma101_recipesharingapplication.Services.RecipeDetailEventListener;
import com.ph41626.pma101_recipesharingapplication.Services.RecipeEventListener;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class SearchActivity extends AppCompatActivity implements RecipeDetailEventListener, RecipeEventListener {
    private EditText edt_search;
    private Button btn_sort;
    private RecyclerView rcv_search;
    ArrayList<Recipe> uniqueRecipes = new ArrayList<>();
    private ArrayList<Recipe> recipes = new ArrayList<>();
    private HashMap<String, Recipe> recipeByIngredients = new HashMap<>();
    private RecyclerViewRecipeSearchAdapter recyclerViewRecipeSearchAdapter;
    private int sortItemIndex = 0;

    private Handler handler = new Handler();
    private Runnable searchRunnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);

        initUI();
        edt_search.requestFocus();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        recyclerViewRecipeSearchAdapter = new RecyclerViewRecipeSearchAdapter(this,new ArrayList<>(),this);
        rcv_search.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        rcv_search.setAdapter(recyclerViewRecipeSearchAdapter);
        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String value = editable.toString().trim();
                if (value.isEmpty()) return;

                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }

                searchRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (value.isEmpty()) return;
                        searchRecipe(value);
//                        searchRecipeByIngredientName(value);
//                        search(value);
                    }
                };
                handler.postDelayed(searchRunnable, 500);
            }
        });
        btn_sort.setOnClickListener(v -> {
            ShowSortDialog();
        });
    }
    public void RecipeDetail(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        RecipeDetailActivity.setRecipeEventListener(this);
        RecipeDetailActivity.setRecipeDetailEventListener(this);
        intent.putExtra("recipe",recipe);
//        intent.putExtra("recipeMedia",.get(recipe.getId()));
//        intent.putExtra("recipeOwner",recipeUsers.get(recipe.getUserId()));
        startActivity(intent);
    }
    private void ShowSortDialog() {
        String[] items = {"Newest Creation Date", "Oldest Creation Date","Cook Time Ascending", "Cook Time Descending", "Highest Ratings"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.CustomAlertDialogTheme);
        builder.setTitle("Sort");

        builder.setSingleChoiceItems(items, sortItemIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sortItemIndex = which;
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (sortItemIndex) {
                    case 0:
                        NewestCreationDate();
                        break;
                    case 1:
                        OldestCreationDate();
                        break;
                    case 2:
                        CookTimeAscending();
                        break;
                    case 3:
                        CookTimeDescending();
                        break;
                    case 4:
                        HighestRatings();
                        break;
                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void HighestRatings() {
        uniqueRecipes = (ArrayList<Recipe>) uniqueRecipes.stream()
                .sorted(Comparator.comparingDouble(Recipe::getAverageRating).reversed())
                .collect(Collectors.toList());
        recyclerViewRecipeSearchAdapter.UpdateRecipe(uniqueRecipes);
    }
    private void CookTimeAscending() {
        uniqueRecipes = (ArrayList<Recipe>) uniqueRecipes.stream()
                .sorted(Comparator.comparingInt(Recipe::getCookTime))
                .collect(Collectors.toList());
        recyclerViewRecipeSearchAdapter.UpdateRecipe(uniqueRecipes);
    }
    private void CookTimeDescending() {
        uniqueRecipes = (ArrayList<Recipe>) uniqueRecipes.stream()
                .sorted(Comparator.comparingInt(Recipe::getCookTime).reversed())
                .collect(Collectors.toList());
        recyclerViewRecipeSearchAdapter.UpdateRecipe(uniqueRecipes);
    }
    private void NewestCreationDate() {
        uniqueRecipes = (ArrayList<Recipe>) uniqueRecipes.stream()
                .filter(recipe -> !recipe.isStatus())
                .sorted((r1, r2) -> r2.getCreationDate().compareTo(r1.getCreationDate()))
                .collect(Collectors.toList());
        recyclerViewRecipeSearchAdapter.UpdateRecipe(uniqueRecipes);
    }
    private void OldestCreationDate() {
        uniqueRecipes = (ArrayList<Recipe>) uniqueRecipes.stream()
                .filter(recipe -> !recipe.isStatus())
                .sorted((r1, r2) -> r1.getCreationDate().compareTo(r2.getCreationDate()))
                .collect(Collectors.toList());
        recyclerViewRecipeSearchAdapter.UpdateRecipe(uniqueRecipes);
    }
    public void searchRecipe(String name) {
        String lowerCaseName = name.toLowerCase();

        CompletableFuture<Void> recipesFuture = new CompletableFuture<>();
        CompletableFuture<Void> recipesByIngredientFuture = new CompletableFuture<>();

        searchRecipesByName(lowerCaseName,recipesFuture);
        searchRecipesByIngredients(lowerCaseName,recipesByIngredientFuture);

        CompletableFuture<Void> allOf = CompletableFuture.allOf(
                recipesFuture, recipesByIngredientFuture
        );
        allOf.thenRun(() -> {
            mergeAndDisplayRecipes();

        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
    }
    private void searchRecipesByName(String lowerCaseName, CompletableFuture<Void> recipesFuture) {
        FirebaseDatabase.getInstance().getReference(MainActivity.REALTIME_RECIPES)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        recipes.clear();

                        for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                            Recipe recipe = recipeSnapshot.getValue(Recipe.class);
                            if (recipe != null && recipe.isPublic() && !recipe.isStatus()) {
                                String recipeName = recipe.getName().toLowerCase();

                                boolean containsAllKeywords = false;
                                String[] keywords = lowerCaseName.split("[,\\s]+");

//
//                                if (recipeName.contains(lowerCaseName)) {
//                                    recipes.add(recipe);
//                                }
                                for (String keyword : keywords) {
                                    if (recipeName.contains(keyword)) {
                                        containsAllKeywords = true;
                                        break;
                                    }
                                }
                                if (containsAllKeywords) {
                                    recipes.add(recipe);
                                }
                            }
                        }
                        recipesFuture.complete(null);
//                        recyclerViewRecipeSearchAdapter.UpdateRecipe(recipes);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }
    private void searchRecipesByIngredients(String lowerCaseName, CompletableFuture<Void> recipesByIngredientFuture) {
        FirebaseDatabase.getInstance().getReference(REALTIME_INGREDIENTS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        recipeByIngredients.clear();

                        Set<String> recipeIds = new HashSet<>();
                        for (DataSnapshot ingredientSnapshot : snapshot.getChildren()) {
                            Ingredient ingredient = ingredientSnapshot.getValue(Ingredient.class);
                            if (ingredient != null) {
                                String ingredientName = ingredient.getName().toLowerCase();
                                boolean containsAllKeywords = false;
                                String[] keywords = lowerCaseName.split("[,\\s]+");
//                                if (ingredientName.contains(lowerCaseName)) {
//                                    recipeIds.add(ingredient.getRecipeId());
//                                }
                                for (String keyword : keywords) {
                                    if (ingredientName.contains(keyword)) {
                                        containsAllKeywords = true;
                                        break;
                                    }
                                }
                                if (containsAllKeywords) {
                                    recipeIds.add(ingredient.getRecipeId());
                                }
                            }
                        }
                        List<CompletableFuture<Void>> allFutures = new ArrayList<>();
                        for (String recipeId : recipeIds) {
                            if (recipeByIngredients.containsKey(recipeId)) continue;
                            CompletableFuture<Void> future = new CompletableFuture<>();
                            allFutures.add(future);
                            fetchRecipeById(recipeId,future);
                        }

                        CompletableFuture<Void> allOf = CompletableFuture.allOf(allFutures.toArray(new CompletableFuture[0]));
                        allOf.thenRun(() -> {
                            recipesByIngredientFuture.complete(null);
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
    private void fetchRecipeById(String recipeId, CompletableFuture<Void> future) {
        new FirebaseUtils().getDataFromFirebaseById(MainActivity.REALTIME_RECIPES, recipeId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Recipe recipe = snapshot.getValue(Recipe.class);
                recipeByIngredients.put(recipeId,recipe);
                future.complete(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void mergeAndDisplayRecipes() {
        Set<String> uniqueRecipeIds = new HashSet<>();
        uniqueRecipes = new ArrayList<>();

        for (Recipe recipe : recipes) {
            if (uniqueRecipeIds.add(recipe.getId())) {
                uniqueRecipes.add(recipe);
            }
        }

        for (Recipe recipe : recipeByIngredients.values()) {
            if (uniqueRecipeIds.add(recipe.getId())) {
                uniqueRecipes.add(recipe);
            }
        }
        recyclerViewRecipeSearchAdapter.UpdateRecipe(uniqueRecipes);
    }
    private String normalize(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
    }
    private void initUI() {
        edt_search = findViewById(R.id.edt_search);

        rcv_search = findViewById(R.id.rcv_search);

        btn_sort = findViewById(R.id.btn_sort);
    }

    @Override
    public void onFollowEvent(String userId, User user) {

    }

    @Override
    public void onDataChange(Recipe recipe) {

    }
}