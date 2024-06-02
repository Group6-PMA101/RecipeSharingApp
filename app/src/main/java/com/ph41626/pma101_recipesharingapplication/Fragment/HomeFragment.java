package com.ph41626.pma101_recipesharingapplication.Fragment;

import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_MEDIAS;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_RECIPES;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_RECIPE_COLLECTIONS;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_RECIPE_RECIPECOLLECTIONS;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_USERS;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ph41626.pma101_recipesharingapplication.Activity.MainActivity;
import com.ph41626.pma101_recipesharingapplication.Activity.RecipeDetailActivity;
import com.ph41626.pma101_recipesharingapplication.Adapter.RecyclerViewAllRecipeAdapter;
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
import com.ph41626.pma101_recipesharingapplication.Services.FirebaseUtils;
import com.ph41626.pma101_recipesharingapplication.Services.RecipeDetailEventListener;
import com.ph41626.pma101_recipesharingapplication.Services.RecipeEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements RecipeDetailEventListener, RecipeEventListener {

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

    private RecyclerView rcv_trending,rcv_top_100_recipe,rcv_popular_creators;
    private RecyclerViewRecipeTrendingAdapter recipeTrendingAdapter;
    private RecyclerViewTop100RecipeAdapter top100RecipeAdapter;
    private RecyclerViewPopularCreatorsAdapter popularCreatorsAdapter;
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<Recipe> recipes = new ArrayList<>();
    public HashMap<String,Media> recipeMedias = new HashMap<>();
    public HashMap<String,User> recipeUsers = new HashMap<>();
    public HashMap<String,Media> userMedias = new HashMap<>();
    private ViewModel viewModel;
    private MainActivity mainActivity;
    private FirebaseUtils firebaseUtils;
    private List<CompletableFuture<Void>> recipeFutures = new ArrayList<>();
    private List<CompletableFuture<Void>> userFutures = new ArrayList<>();
    private List<CompletableFuture<Void>> saveRecipeFutures = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerViewAllRecipeAdapter recipeAdapter;
    private ArrayList<Recipe> recipeList;
    private DatabaseReference databaseReference;
    private EditText searchEditText;
    private ProfileFragment profileFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initUI(view);
        RecyclerViewManager();
        GetDataFromFirebase();
        UpdateUiWhenDataChange();

        recyclerView = view.findViewById(R.id.rcv_search);
        searchEditText = view.findViewById(R.id.searchEditText);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        search();


        return view;
    }


    private void search(){
        profileFragment = new ProfileFragment();
        recipeList = new ArrayList<>();
        recipeAdapter = new RecyclerViewAllRecipeAdapter(getContext(), recipeList, profileFragment);
        recyclerView.setAdapter(recipeAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("REALTIME_RECIPES");
        recipeAdapter.Update(new ArrayList<>());
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString();
                if (searchText.isEmpty()) {
                    recipeList.clear();
                    recipeAdapter.Update(recipeList);
                } else {
                    searchInFirebase(searchText);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void searchInFirebase(String searchText) {
        databaseReference.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        recipeList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Recipe recipe = snapshot.getValue(Recipe.class);
                            recipeList.add(recipe);
                        }
                        recipeAdapter.Update(recipeList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle possible errors
                    }
                });
    }


    private void UpdateUiWhenDataChange() {
        viewModel.getChangeDataRecipes().observe(getViewLifecycleOwner(), new Observer<ArrayList<Recipe>>() {
            @Override
            public void onChanged(ArrayList<Recipe> recipes) {
                recipeTrendingAdapter.Update(recipes);
                top100RecipeAdapter.Update(recipes);
            }
        });
        viewModel.getChangeDateUsers().observe(getViewLifecycleOwner(), new Observer<ArrayList<User>>() {
            @Override
            public void onChanged(ArrayList<User> users) {
                popularCreatorsAdapter.Update(users);
            }
        });
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
                future.complete(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void fetchMediaForUser(User user) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        userFutures.add(future);

        if (user.getMediaId() == null ||
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
                    if (!recipe.isPublic()) continue;
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
                    fetchMediaForUser(user);
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
    public void SaveRecipe(Recipe recipe) {
        if (!mainActivity.recipeCollectionFuture.isDone()) {
            Toast.makeText(mainActivity, "You are performing actions too quickly. Please try again.", Toast.LENGTH_SHORT).show();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
        rcv_trending = view.findViewById(R.id.rcv_trending);
        rcv_top_100_recipe = view.findViewById(R.id.rcv_top_100_recipe);
        rcv_popular_creators = view.findViewById(R.id.rcv_popular_creators);

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
}