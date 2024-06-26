package com.ph41626.pma101_recipesharingapplication.Fragment;

import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_MEDIAS;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_RECIPES;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_RECIPE_COLLECTIONS;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_RECIPE_RECIPECOLLECTIONS;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_USERS;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ph41626.pma101_recipesharingapplication.Activity.MainActivity;
import com.ph41626.pma101_recipesharingapplication.Activity.RecipeDetailActivity;
import com.ph41626.pma101_recipesharingapplication.Adapter.RecyclerViewPopularCreatorsAdapter;
import com.ph41626.pma101_recipesharingapplication.Adapter.RecyclerViewRecipeTrendingAdapter;
import com.ph41626.pma101_recipesharingapplication.Adapter.RecyclerViewTop100RecipeAdapter;
import com.ph41626.pma101_recipesharingapplication.Model.Media;
import com.ph41626.pma101_recipesharingapplication.Model.Recipe;
import com.ph41626.pma101_recipesharingapplication.Model.RecipeCollection;
import com.ph41626.pma101_recipesharingapplication.Model.Recipe_RecipeCollection;
import com.ph41626.pma101_recipesharingapplication.Model.User;
import com.ph41626.pma101_recipesharingapplication.Model.ViewModel;
import com.ph41626.pma101_recipesharingapplication.R;
import com.ph41626.pma101_recipesharingapplication.Activity.SearchActivity;
import com.ph41626.pma101_recipesharingapplication.Activity.SeeAllRecipeActivity;
import com.ph41626.pma101_recipesharingapplication.Services.FirebaseUtils;
import com.ph41626.pma101_recipesharingapplication.Services.MainActivityEventListener;
import com.ph41626.pma101_recipesharingapplication.Services.RecipeDetailEventListener;
import com.ph41626.pma101_recipesharingapplication.Services.RecipeEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements RecipeDetailEventListener, RecipeEventListener, MainActivityEventListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private EditText edt_search;
    private Button btn_see_all_popular_creator,btn_see_all_trending,btn_see_all_top100;
    private RecyclerView rcv_trending,rcv_top_100_recipe,rcv_popular_creators;
    private RecyclerViewRecipeTrendingAdapter recipeTrendingAdapter;
    private RecyclerViewTop100RecipeAdapter top100RecipeAdapter;
    private RecyclerViewPopularCreatorsAdapter popularCreatorsAdapter;
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<Recipe> recipes = new ArrayList<>();
    private ArrayList<Recipe> trendingRecipes = new ArrayList<>();
    private ArrayList<Recipe> top100Recipes = new ArrayList<>();
    public HashMap<String,Media> recipeMedias = new HashMap<>();
    public HashMap<String,User> recipeUsers = new HashMap<>();
    public HashMap<String,Media> userMedias = new HashMap<>();
    private ViewModel viewModel;
    private MainActivity mainActivity;
    private FirebaseUtils firebaseUtils;
    private List<CompletableFuture<Void>> recipeFutures = new ArrayList<>();
    private List<CompletableFuture<Void>> userFutures = new ArrayList<>();
    private List<CompletableFuture<Void>> saveRecipeFutures = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        initUI(view);
        RecyclerViewManager();
        GetDataFromFirebase();
        UpdateUiWhenDataChange();
        SetUpButton();

        return view;
    }

    private void SetUpButton() {
        edt_search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    edt_search.clearFocus();
                    startActivity(new Intent(getContext(), SearchActivity.class));
                }
            }
        });
        btn_see_all_trending.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SeeAllRecipeActivity.class);
            intent.putExtra("recipes",trendingRecipes);
            intent.putExtra("mediaForRecipes",recipeMedias);
            intent.putExtra("users",recipeUsers);
            intent.putExtra("mediaForUsers",userMedias);
            SeeAllRecipeActivity.setMainActivityEventListener(this);
            startActivity(intent);
        });
        btn_see_all_top100.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SeeAllRecipeActivity.class);
            intent.putExtra("recipes",top100Recipes);
            intent.putExtra("mediaForRecipes",recipeMedias);
            intent.putExtra("users",recipeUsers);
            intent.putExtra("mediaForUsers",userMedias);
            SeeAllRecipeActivity.setMainActivityEventListener(this);
            startActivity(intent);
        });
//        btn_see_all_popular_creator.setOnClickListener(v -> {
//
//        });

    }

    private void UpdateUiWhenDataChange() {
        viewModel.getChangeDataRecipes().observe(getViewLifecycleOwner(), new Observer<ArrayList<Recipe>>() {
            @Override
            public void onChanged(ArrayList<Recipe> recipes) {
                UpdateTrendingRecipes(recipes);
                UpdateTop100Recipes(recipes);
            }
        });
        viewModel.getChangeDateUsers().observe(getViewLifecycleOwner(), new Observer<ArrayList<User>>() {
            @Override
            public void onChanged(ArrayList<User> users) {
                Collections.sort(users, new Comparator<User>() {
                    @Override
                    public int compare(User u1, User u2) {
                        return Integer.compare(u2.getFollowersCount(), u1.getFollowersCount());
                    }
                });
                popularCreatorsAdapter.Update(users);
            }
        });
    }
    private void UpdateTrendingRecipes(ArrayList<Recipe> recipes) {
        ArrayList<Recipe> trending = new ArrayList<>(recipes);
        Collections.sort(trending, new Comparator<Recipe>() {
            @Override
            public int compare(Recipe r1, Recipe r2) {
                return Long.compare(r2.getCreationDate().getTime(), r1.getCreationDate().getTime());
            }
        });
//        if (trending.size() > 10) {
//            trending = new ArrayList<>(trending.subList(0, 10));
//        }
        trendingRecipes = trending;
        recipeTrendingAdapter.Update(trendingRecipes);
    }
    private void UpdateTop100Recipes(ArrayList<Recipe> recipes) {
        ArrayList<Recipe> top100 = new ArrayList<>(recipes);
        Collections.sort(top100, new Comparator<Recipe>() {
            @Override
            public int compare(Recipe r1, Recipe r2) {
                return Double.compare(r2.getAverageRating(), r1.getAverageRating());
            }
        });
//        if (top100.size() > 100) {
//            top100 = new ArrayList<>(top100.subList(0, 100));
//        }
        top100Recipes = top100;
        top100RecipeAdapter.Update(top100Recipes);
    }

    private void fetchMediaForRecipe(Recipe recipe) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        recipeFutures.add(future);
        if (recipeMedias.containsKey(recipe.getId()) && recipeMedias.get(recipe.getId()) != null) {
            future.complete(null);
            return;
        }

        new FirebaseUtils().getDataFromFirebaseById(REALTIME_MEDIAS, recipe.getMediaId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Media media = snapshot.getValue(Media.class);
                recipeMedias.put(recipe.getId(),media);
                future.complete(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void fetchUserForRecipe(Recipe recipe) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        recipeFutures.add(future);
        if (recipeUsers.containsKey(recipe.getUserId()) && recipeUsers.get(recipe.getUserId()) != null) {
            future.complete(null);
            return;
        }
        new FirebaseUtils().getDataFromFirebaseById(REALTIME_USERS, recipe.getUserId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                recipeUsers.put(recipe.getUserId(),user);
                fetchMediaForUser(future,user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void fetchMediaForUser(CompletableFuture<Void> future,User user) {
        if (user.getMediaId() == null || user.getMediaId().isEmpty() ||
                (userMedias.containsKey(user.getId()) && userMedias.get(user.getId()) != null)) {
            future.complete(null);
            return;
        }
        firebaseUtils.getDataFromFirebaseById(REALTIME_MEDIAS, user.getMediaId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Media media = snapshot.getValue(Media.class);
                userMedias.put(user.getId(),media);
                future.complete(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void GetDataFromFirebase() {
        firebaseUtils.getDataFromFirebase(REALTIME_RECIPES, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipes.clear();
                for (DataSnapshot recipeSnapshot:snapshot.getChildren()) {
                    Recipe recipe = recipeSnapshot.getValue(Recipe.class);
                    if (!recipe.isPublic() || recipe.isStatus()) continue;
                    recipes.add(recipe);
                    fetchMediaForRecipe(recipe);
                    fetchUserForRecipe(recipe);
                }

                CompletableFuture<Void> allOf = CompletableFuture.allOf(recipeFutures.toArray(new CompletableFuture[0]));
                allOf.thenRun(() -> {
                    viewModel.changeDateRecipes(recipes);
                }).exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        firebaseUtils.getDataFromFirebase(REALTIME_USERS, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot userSnapshot:snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user.getAccountType() != 1) continue;
                    users.add(user);
                    CompletableFuture<Void> future = new CompletableFuture<>();
                    userFutures.add(future);
                    fetchMediaForUser(future,user);
                }
                CompletableFuture<Void> allOf = CompletableFuture.allOf(userFutures.toArray(new CompletableFuture[0]));
                allOf.thenRun(() -> {
                    viewModel.changeUsers(users);
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
    public void RecipeDetail(Recipe recipe) {
        Intent intent = new Intent(getContext(), RecipeDetailActivity.class);
        RecipeDetailActivity.setRecipeEventListener(this);
        RecipeDetailActivity.setRecipeDetailEventListener(this);
        intent.putExtra("recipe",recipe);
        intent.putExtra("recipeMedia",recipeMedias.get(recipe.getId()));
        intent.putExtra("recipeOwner",recipeUsers.get(recipe.getUserId()));
        startActivity(intent);
    }
    public boolean recipeExists(String recipeId, List<Recipe_RecipeCollection> recipeRecipeCollections) {
        if (recipeRecipeCollections == null) {
            return false;
        }
        return recipeRecipeCollections.stream()
                .anyMatch(rrCollection -> rrCollection.getRecipeId().equals(recipeId));
    }
    public void SaveRecipe(Context context, Recipe recipe) {
        if (!mainActivity.recipeCollectionFuture.isDone()) {
            Toast.makeText(context, "You are performing actions too quickly. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mainActivity.recipeCollections == null || mainActivity.recipeCollections.isEmpty()) {
            Toast.makeText(context, "You should create a list of recipes before saving a recipe.", Toast.LENGTH_SHORT).show();
            return;
        }
        String[] collectionNames = new String[mainActivity.recipeCollections.size()];
        boolean[] checkedItems = new boolean[mainActivity.recipeCollections.size()];

        for (int i = 0; i < mainActivity.recipeCollections.size(); i++) {
            RecipeCollection recipeCollection = mainActivity.recipeCollections.get(i);
            collectionNames[i] = recipeCollection.getName();
            if (recipeExists(recipe.getId(),mainActivity.recipeRecipeCollectionHashMap.get(recipeCollection.getId()))) {
                checkedItems[i] = true;
            } else {
                checkedItems[i] = false;
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Recipe Collection")
                .setMultiChoiceItems(collectionNames, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkedItems[which] = isChecked;
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < collectionNames.length; i++) {
                            RecipeCollection recipeCollection = mainActivity.recipeCollections.get(i);
                            CompletableFuture<Void> changeFuture = new CompletableFuture<>();
                            saveRecipeFutures.add(changeFuture);
                            if (checkedItems[i]) {
                                if (recipeExists(recipe.getId(),mainActivity.recipeRecipeCollectionHashMap.get(mainActivity.recipeCollections.get(i).getId()))) {
                                    changeFuture.complete(null);
                                    continue;
                                }
                                CompletableFuture<Void> saveFuture = new CompletableFuture<>();
                                saveRecipeFutures.add(saveFuture);

                                recipeCollection.setNumberOfRecipes(recipeCollection.getNumberOfRecipes() + 1);

                                Recipe_RecipeCollection recipeRecipeCollection = new Recipe_RecipeCollection();
                                recipeRecipeCollection.setRecipeId(recipe.getId());
                                recipeRecipeCollection.setRecipeCollectionId(recipeCollection.getId());

                                FirebaseDatabase
                                        .getInstance()
                                        .getReference(REALTIME_RECIPE_RECIPECOLLECTIONS)
                                        .child(recipeRecipeCollection.getId())
                                        .setValue(recipeRecipeCollection)
                                        .addOnCompleteListener(task -> {
                                            saveFuture.complete(null);
                                        });
                                FirebaseDatabase
                                        .getInstance()
                                        .getReference(REALTIME_RECIPE_COLLECTIONS)
                                        .child(recipeCollection.getId())
                                        .setValue(recipeCollection)
                                        .addOnCompleteListener(task -> {
                                            changeFuture.complete(null);
                                        });
                            } else {
                                if (recipeExists(recipe.getId(),mainActivity.recipeRecipeCollectionHashMap.get(mainActivity.recipeCollections.get(i).getId()))) {
                                    CompletableFuture<Void> deleteFuture = new CompletableFuture<>();
                                    saveRecipeFutures.add(deleteFuture);

                                    recipeCollection.setNumberOfRecipes(recipeCollection.getNumberOfRecipes() - 1);

                                    Recipe_RecipeCollection recipeRecipeCollection = mainActivity.recipeRecipeCollectionHashMap.get(recipeCollection.getId()).stream()
                                            .filter(rrCollection -> rrCollection.getRecipeId().equals(recipe.getId()))
                                            .findFirst()
                                            .orElse(null);

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
                                                changeFuture.complete(null);
                                            });
                                } else {
                                    changeFuture.complete(null);
                                }
                            }
                        }

                        CompletableFuture<Void> allOf = CompletableFuture.allOf(saveRecipeFutures.toArray(new CompletableFuture[0]));
                        allOf.thenRun(() -> {
                            Toast.makeText(mainActivity, "Saved successfully.", Toast.LENGTH_SHORT).show();
                        }).exceptionally(e -> {
                            e.printStackTrace();
                            return null;
                        });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void RecyclerViewManager() {
        recipeTrendingAdapter = new RecyclerViewRecipeTrendingAdapter(getContext(),recipes,this);
        rcv_trending.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false));
        rcv_trending.setAdapter(recipeTrendingAdapter);

        top100RecipeAdapter = new RecyclerViewTop100RecipeAdapter(getContext(),recipes,this);
        rcv_top_100_recipe.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false));
        rcv_top_100_recipe.setAdapter(top100RecipeAdapter);

        popularCreatorsAdapter = new RecyclerViewPopularCreatorsAdapter(getContext(),users,this);
        rcv_popular_creators.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false));
        rcv_popular_creators.setAdapter(popularCreatorsAdapter);
    }

    private void initUI(View view) {
        btn_see_all_top100 = view.findViewById(R.id.btn_see_all_top100);
        btn_see_all_trending = view.findViewById(R.id.btn_see_all_trending);
//        btn_see_all_popular_creator = view.findViewById(R.id.btn_see_all_popular_creator);

        rcv_trending = view.findViewById(R.id.rcv_trending);
        rcv_top_100_recipe = view.findViewById(R.id.rcv_top_100_recipe);
        rcv_popular_creators = view.findViewById(R.id.rcv_popular_creators);

        edt_search = view.findViewById(R.id.edt_search);

        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
        firebaseUtils = new FirebaseUtils();
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public void onFollowEvent(String userId,User user) {
        recipeUsers.put(userId,user);
    }

    @Override
    public void onDataChange(Recipe recipe) {
        recipeTrendingAdapter.Update(recipes);
        top100RecipeAdapter.Update(recipes);
        for (int i = 0; i < recipes.size(); i++) {
            if (recipes.get(i).getId().equals(recipe.getId())) {
                recipes.set(i, recipe);
                break;
            }
        }
    }

    @Override
    public void onSavedRecipe(Context context,Recipe recipe) {
        SaveRecipe(context,recipe);
    }
}