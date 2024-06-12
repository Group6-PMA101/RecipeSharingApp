package com.ph41626.pma101_recipesharingapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ph41626.pma101_recipesharingapplication.Activity.MainActivity;
import com.ph41626.pma101_recipesharingapplication.Adapter.RecyclerViewRecipeSearchAdapter;
import com.ph41626.pma101_recipesharingapplication.Model.Ingredient;
import com.ph41626.pma101_recipesharingapplication.Model.Recipe;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SearchActivity extends AppCompatActivity {
    private EditText edt_search;
    private Button btn_sort;
    private RecyclerView rcv_search;
    private ArrayList<Recipe> recipes = new ArrayList<>();
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
        edt_search.setFocusable(true);
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
                        searchRecipeByName(value);
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
        recipes = (ArrayList<Recipe>) recipes.stream()
                .sorted(Comparator.comparingDouble(Recipe::getAverageRating).reversed())
                .collect(Collectors.toList());
        recyclerViewRecipeSearchAdapter.UpdateRecipe(recipes);
    }
    private void CookTimeAscending() {
        recipes = (ArrayList<Recipe>) recipes.stream()
                .sorted(Comparator.comparingInt(Recipe::getCookTime))
                .collect(Collectors.toList());
        recyclerViewRecipeSearchAdapter.UpdateRecipe(recipes);
    }
    private void CookTimeDescending() {
        recipes = (ArrayList<Recipe>) recipes.stream()
                .sorted(Comparator.comparingInt(Recipe::getCookTime).reversed())
                .collect(Collectors.toList());
        recyclerViewRecipeSearchAdapter.UpdateRecipe(recipes);
    }
    private void NewestCreationDate() {
        recipes = (ArrayList<Recipe>) recipes.stream()
                .filter(recipe -> !recipe.isStatus())
                .sorted((r1, r2) -> r2.getCreationDate().compareTo(r1.getCreationDate()))
                .collect(Collectors.toList());
        recyclerViewRecipeSearchAdapter.UpdateRecipe(recipes);
    }

    private void OldestCreationDate() {
        recipes = (ArrayList<Recipe>) recipes.stream()
                .filter(recipe -> !recipe.isStatus())
                .sorted((r1, r2) -> r1.getCreationDate().compareTo(r2.getCreationDate()))
                .collect(Collectors.toList());
        recyclerViewRecipeSearchAdapter.UpdateRecipe(recipes);
    }

    public void searchRecipeByName(String name) {
        Query query = FirebaseDatabase.getInstance().getReference(MainActivity.REALTIME_RECIPES);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recipes.clear();
                String lowerCaseName = name.toLowerCase();
                for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                    Recipe recipe = recipeSnapshot.getValue(Recipe.class);
                    if (recipe != null && recipe.isPublic() && !recipe.isStatus()) {
                        String recipeName = recipe.getName().toLowerCase();
                        if (recipeName.contains(lowerCaseName)) {
                            recipes.add(recipe);
                        }
                    }
                }

//                String lowerCaseIngredientName = name.toLowerCase();
                String lowerCaseIngredientName = normalize(name);

                Query ingredientQuery = FirebaseDatabase.getInstance().getReference("REALTIME_INGREDIENTS")
                        .orderByChild("name").startAt(lowerCaseIngredientName).endAt(lowerCaseIngredientName + "\uf8ff");

                ingredientQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot ingredientSnapshot) {
                        Set<String> recipeIds = new HashSet<>();
                        for (DataSnapshot ingredientData : ingredientSnapshot.getChildren()) {
                            Ingredient ingredient = ingredientData.getValue(Ingredient.class);
                            if (ingredient != null) {
                                recipeIds.add(ingredient.getRecipeId());
                            }
                        }
                        Toast.makeText(SearchActivity.this, ingredientSnapshot.getChildrenCount() + "", Toast.LENGTH_SHORT).show();

                        if (!recipeIds.isEmpty()) {
                            Toast.makeText(SearchActivity.this, "Check A", Toast.LENGTH_SHORT).show();
                        }
//                        else {
//                            Toast.makeText(SearchActivity.this, "Check", Toast.LENGTH_SHORT).show();
//                            recyclerViewRecipeSearchAdapter.UpdateRecipe(new ArrayList<Recipe>());
//                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Xử lý lỗi nếu có
                    }
                });


                recyclerViewRecipeSearchAdapter.UpdateRecipe(recipes);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }









//    public void searchRecipeByIngredientName(String ingredientName) {
//        String lowerCaseIngredientName = ingredientName.toLowerCase();
//
//        // Tìm kiếm nguyên liệu chứa từ khóa tìm kiếm
//        Query ingredientQuery = FirebaseDatabase.getInstance().getReference("REALTIME_INGREDIENTS")
//                .orderByChild("name").startAt(lowerCaseIngredientName).endAt(lowerCaseIngredientName + "\uf8ff");
//
//        ingredientQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot ingredientSnapshot) {
//                Set<String> recipeIds = new HashSet<>();
//                for (DataSnapshot ingredientData : ingredientSnapshot.getChildren()) {
//                    Ingredient ingredient = ingredientData.getValue(Ingredient.class);
//                    if (ingredient != null) {
//                        recipeIds.add(ingredient.getRecipeId());
//                    }
//                }
//
//                if (!recipeIds.isEmpty()) {
//                    fetchRecipesByIds(recipeIds);
//                } else {
//                    Toast.makeText(SearchActivity.this, "Check", Toast.LENGTH_SHORT).show();
//                    recyclerViewRecipeSearchAdapter.UpdateRecipe(new ArrayList<Recipe>());
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Xử lý lỗi nếu có
//            }
//        });
//    }
//    private void fetchRecipesByIds(Set<String> recipeIds) {
//        Query recipeQuery = FirebaseDatabase.getInstance().getReference("REALTIME_RECIPES");
//        recipeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot recipeSnapshot) {
//                ArrayList<Recipe> recipes = new ArrayList<>();
//                for (DataSnapshot recipeData : recipeSnapshot.getChildren()) {
//                    Recipe recipe = recipeData.getValue(Recipe.class);
//                    if (recipe != null && recipeIds.contains(recipe.getId())) {
//                        recipes.add(recipe);
//                    }
//                }
//                recyclerViewRecipeSearchAdapter.UpdateRecipe(recipes);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Xử lý lỗi nếu có
//            }
//        });
//    }
    private String normalize(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
    }
    private void initUI() {
        edt_search = findViewById(R.id.edt_search);

        rcv_search = findViewById(R.id.rcv_search);

        btn_sort = findViewById(R.id.btn_sort);
    }
}