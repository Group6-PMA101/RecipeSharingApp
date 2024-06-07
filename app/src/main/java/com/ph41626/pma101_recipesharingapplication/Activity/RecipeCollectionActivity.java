package com.ph41626.pma101_recipesharingapplication.Activity;

import static android.widget.LinearLayout.VERTICAL;

import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_RECIPE_COLLECTIONS;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_RECIPE_RECIPECOLLECTIONS;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;
import com.ph41626.pma101_recipesharingapplication.Adapter.RecyclerViewRecipeCollectionDetailAdapter;
import com.ph41626.pma101_recipesharingapplication.Fragment.HomeFragment;
import com.ph41626.pma101_recipesharingapplication.Model.Media;
import com.ph41626.pma101_recipesharingapplication.Model.Recipe;
import com.ph41626.pma101_recipesharingapplication.Model.RecipeCollection;
import com.ph41626.pma101_recipesharingapplication.Model.Recipe_RecipeCollection;
import com.ph41626.pma101_recipesharingapplication.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RecipeCollectionActivity extends AppCompatActivity {

    private TextView tv_recipe_collection_name;
    private RecyclerView rcv_recipe_collection;
    private Button btn_more;
    private RecyclerViewRecipeCollectionDetailAdapter recipeCollectionDetailAdapter;

    public RecipeCollection recipeCollection = new RecipeCollection();
    private ArrayList<Recipe> recipes = new ArrayList<>();
    private ArrayList<Recipe_RecipeCollection> recipeRecipeCollections = new ArrayList<>();
    public HashMap<String, Media> recipeMedia = new HashMap<>();
    private ProgressDialog progressDialog;

    private List<CompletableFuture<Void>> futures = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_collection);

        initUI();
        SetUpRecyclerView();
        SetUpButton();
        GetData();
        SetUpUI();
    }

    private void SetUpButton() {
        btn_more.setOnClickListener(v -> {
            PopupMenu(v);
        });
    }

    private void PopupMenu(View view ) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        List<String> menuItems = getMenuItems();
        for (String menuItem : menuItems) {
            popupMenu.getMenu().add(menuItem);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                handleMenuItemClick(item);
                return true;
            }
        });
        popupMenu.show();
    }

    private List<String> getMenuItems() {
        List<String> menuItems = new ArrayList<>();
        menuItems.add("Delete");
        menuItems.add("Rename");
        return menuItems;
    }

    private void handleMenuItemClick(MenuItem item) {
        if (item.getTitle().equals("Delete")) {
            DeleteRecipeCollection();
        } else if (item.getTitle().equals("Rename")) {
            RenameRecipeCollection();
        }
    }

    private void DeleteRecipeCollection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setMessage("You are about to delete a recipe collection from your account. Please note that this action is irreversible, and the recipe collection cannot be recovered once deleted. All saved recipes will be lost.\n" +
                "\n" +
                "Are you sure you want to proceed with deleting this recipe collection?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DeleteRecipeCollectionFromDatabase();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void DeleteRecipeCollectionFromDatabase() {
        progressDialog.setMessage("Please wait ...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        if (recipeRecipeCollections != null && !recipeRecipeCollections.isEmpty()) {
            for (Recipe_RecipeCollection recipeRecipeCollection:recipeRecipeCollections) {
                CompletableFuture<Void> future = new CompletableFuture<>();
                futures.add(future);
                FirebaseDatabase
                        .getInstance()
                        .getReference(REALTIME_RECIPE_COLLECTIONS)
                        .child(recipeRecipeCollection.getId())
                        .setValue(null)
                        .addOnCompleteListener(task -> {
                            future.complete(null);
                        });
            }
        }
        CompletableFuture<Void> future = new CompletableFuture<>();
        futures.add(future);
        FirebaseDatabase
                .getInstance()
                .getReference(REALTIME_RECIPE_COLLECTIONS)
                .child(recipeCollection.getId())
                .setValue(null)
                .addOnCompleteListener(task -> {
                    future.complete(null);
                });

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allOf.thenRun(() -> {
            Toast.makeText(this, "Delete Completed.", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            finish();
        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
    }

    private void RenameRecipeCollection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rename Recipe Collection");
        final EditText input = new EditText(this);
        builder.setView(input);
        input.setText(recipeCollection.getName());
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value = s.toString().trim();
                if (value.isEmpty()) {
                    input.setError("Name cannot be empty!");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = input.getText().toString().trim();

                if (value.isEmpty()) return;

                recipeCollection.setName(value);
                
                FirebaseDatabase
                        .getInstance()
                        .getReference(REALTIME_RECIPE_COLLECTIONS)
                        .child(recipeCollection.getId())
                        .setValue(recipeCollection)
                        .addOnCompleteListener(task -> {
                            Toast.makeText(RecipeCollectionActivity.this, "Renamed successfully.", Toast.LENGTH_SHORT).show();
                            tv_recipe_collection_name.setText(recipeCollection.getName());
                        });
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void SetUpRecyclerView() {
        recipeCollectionDetailAdapter = new RecyclerViewRecipeCollectionDetailAdapter(this,new ArrayList<>(),this);
        rcv_recipe_collection.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));
        rcv_recipe_collection.setAdapter(recipeCollectionDetailAdapter);
    }

    private void SetUpUI() {
        tv_recipe_collection_name.setText(recipeCollection.getName());

        recipeCollectionDetailAdapter.Update(recipes);
    }
    public void RecipeDetail(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra("recipe",recipe);
        intent.putExtra("recipeMedia",recipeMedia.get(recipe.getId()));
        startActivity(intent);
    }
    private void GetData() {
        Intent intent = getIntent();
        recipeCollection = (RecipeCollection) intent.getSerializableExtra("recipeCollection");
        recipes = (ArrayList<Recipe>) intent.getSerializableExtra("recipes");
        recipeMedia = (HashMap<String, Media>) intent.getSerializableExtra("recipeMedia");
        recipeRecipeCollections = (ArrayList<Recipe_RecipeCollection>) intent.getSerializableExtra("recipeRecipeCollection");
        if (recipes == null) recipes = new ArrayList<>();
        if (recipeMedia == null) recipeMedia = new HashMap<>();
        if (recipeRecipeCollections == null) recipeRecipeCollections = new ArrayList<>();
    }
    public void Remove(int pos) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        CompletableFuture<Void> deleteFuture = new CompletableFuture<>();
        futures.add(deleteFuture);
        CompletableFuture<Void> updateFuture = new CompletableFuture<>();
        futures.add(updateFuture);

        recipeCollection.setNumberOfRecipes(recipeCollection.getNumberOfRecipes() - 1);

        Recipe_RecipeCollection recipeRecipeCollection = recipeRecipeCollections.get(pos);


        FirebaseDatabase
                .getInstance()
                .getReference(REALTIME_RECIPE_RECIPECOLLECTIONS)
                .child(recipeRecipeCollection.getId())
                .setValue(null)
                .addOnCompleteListener(task -> {
                    deleteFuture.complete(null);
                });
        FirebaseDatabase
                .getInstance()
                .getReference(REALTIME_RECIPE_COLLECTIONS)
                .child(recipeCollection.getId())
                .setValue(recipeCollection)
                .addOnCompleteListener(task -> {
                    updateFuture.complete(null);
                });

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allOf.thenRun(() -> {
            recipes.remove(pos);
            recipeRecipeCollections.remove(recipeRecipeCollection);
            recipeCollectionDetailAdapter.Update(recipes);
        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });

    }
    private void initUI() {
        tv_recipe_collection_name = findViewById(R.id.tv_recipe_collection_name);
        rcv_recipe_collection = findViewById(R.id.rcv_recipe_collection);
        btn_more = findViewById(R.id.btn_more);

        progressDialog = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
    }
}