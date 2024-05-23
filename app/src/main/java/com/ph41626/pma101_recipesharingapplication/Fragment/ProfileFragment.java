package com.ph41626.pma101_recipesharingapplication.Fragment;

import static android.app.Activity.RESULT_OK;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_INGREDIENTS;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_INSTRUCTIONS;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_MEDIAS;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_RECIPES;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.STORAGE_MEDIAS;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ph41626.pma101_recipesharingapplication.Activity.MainActivity;
import com.ph41626.pma101_recipesharingapplication.Adapter.ViewPagerBottomNavigationRecipeAdapter;
import com.ph41626.pma101_recipesharingapplication.Model.Ingredient;
import com.ph41626.pma101_recipesharingapplication.Model.Instruction;
import com.ph41626.pma101_recipesharingapplication.Model.Media;
import com.ph41626.pma101_recipesharingapplication.Model.Recipe;
import com.ph41626.pma101_recipesharingapplication.Model.User;
import com.ph41626.pma101_recipesharingapplication.Model.ViewModel;
import com.ph41626.pma101_recipesharingapplication.R;
import com.ph41626.pma101_recipesharingapplication.Services.FirebaseUtils;
import com.ph41626.pma101_recipesharingapplication.Activity.UpdateRecipeActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REQUEST_CODE_ACTIVITY_BACK = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
    private ImageView img_avatar_user;
    private TextView tv_name_user, tv_recipes_count_user, tv_follower_count_user, tv_following_count_user;
    private MainActivity mainActivity;
    private ViewModel viewModel;
    public ArrayList<Recipe> recipes = new ArrayList<>();
    private User currentUser = new User();
    private Media currentMedia = new Media();
    public ViewPager2 viewPager2_recipe;
    private BottomNavigationView bottomNavigationView;
    public HashMap<String, Media> recipeMedias = new HashMap<>();
    public HashMap<String, ArrayList<Ingredient>> recipeIngredients = new HashMap<>();
    public HashMap<String, ArrayList<Instruction>> recipeInstructions = new HashMap<>();
    public HashMap<String, Media> instructionMedias = new HashMap<>();
    private List<CompletableFuture<Void>> futures = new ArrayList<>();
    private List<CompletableFuture<Void>> deleteRecipeFutures = new ArrayList<>();
    private StorageReference storageReference;
    private DatabaseReference
            databaseReferenceMedias,
            databaseReferenceIngredients,
            databaseReferenceInstructions,
            databaseReferenceRecipes;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initUI(view);
        UpdateUiWhenDataChange();
        BottomNavigationManager();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ACTIVITY_BACK) {
            if (resultCode == RESULT_OK) {
                GetRecipeByUser();
            }
        }
    }
    private void BottomNavigationManager() {
        ViewPagerBottomNavigationRecipeAdapter bottomNavigationAdapter = new ViewPagerBottomNavigationRecipeAdapter(getActivity());
        viewPager2_recipe.setAdapter(bottomNavigationAdapter);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.allRecipe) {
                    viewPager2_recipe.setCurrentItem(0);
                } else if(item.getItemId() == R.id.sharedRecipe) {
                    viewPager2_recipe.setCurrentItem(1);
                } else if (item.getItemId() == R.id.unsharedRecipe) {
                    viewPager2_recipe.setCurrentItem(2);
                }
                return true;
            }
        });
        viewPager2_recipe.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.allRecipe);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.sharedRecipe);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.unsharedRecipe);
                        break;
                }
            }
        });
        viewPager2_recipe.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                adjustViewPagerHeight(viewPager2_recipe);
                viewPager2_recipe.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }
    public void adjustViewPagerHeight(ViewPager2 viewPager2) {
        viewPager2.post(new Runnable() {
            @Override
            public void run() {
                View currentView = ((ViewGroup) viewPager2.getChildAt(0)).getChildAt(viewPager2.getCurrentItem());
                if (currentView != null) {
                    ViewGroup.LayoutParams layoutParams = viewPager2.getLayoutParams();
                    layoutParams.height = getCurrentPageHeight(viewPager2);
                    viewPager2.setLayoutParams(layoutParams);
                }
            }
        });
    }
    private int getCurrentPageHeight(ViewPager2 viewPager2) {
        View currentView = ((ViewGroup) viewPager2.getChildAt(0)).getChildAt(viewPager2.getCurrentItem());
        if (currentView == null) {
            return 0;
        }
        currentView.measure(View.MeasureSpec.makeMeasureSpec(currentView.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        return currentView.getMeasuredHeight();
    }
    public void SharedRecipe(Recipe recipe) {
        new FirebaseUtils().UpdateRecipeShare(REALTIME_RECIPES, recipe.getId(), recipe.isPublic(), new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                if (recipe.isPublic()) {
                    Toast.makeText(mainActivity, "Share successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mainActivity, "Share canceled!", Toast.LENGTH_SHORT).show();
                }
                GetRecipeByUser();
            }
        });
    }
    public void UpdateRecipe(Recipe recipe) {
        ArrayList<Media> medias = new ArrayList<>();
        for (Instruction instruction:recipeInstructions.get(recipe.getId())) {
            if (instruction.getMediaIds() == null || instruction.getMediaIds().isEmpty()) {
                instruction.setMediaIds(new ArrayList<>());
                continue;
            }
            for (String string: instruction.getMediaIds()) {
                medias.add(instructionMedias.get(string));
            }
        }

        Intent intent = new Intent(getActivity(), UpdateRecipeActivity.class);
        intent.putExtra("recipe",recipe);
        intent.putExtra("recipeMedia",recipeMedias.get(recipe.getId()));
        intent.putExtra("ingredients",recipeIngredients.get(recipe.getId()));
        intent.putExtra("instructions",recipeInstructions.get(recipe.getId()));
        intent.putExtra("instructionMedias",medias);
        startActivityForResult(intent,REQUEST_CODE_ACTIVITY_BACK);
    }
    public void DeleteRecipe(Recipe recipe) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Warning");
        builder.setMessage("You are about to delete a recipe from your account. Please note that this action is irreversible and the recipe cannot be recovered once deleted.\n\nAre you sure you want to proceed with deleting this recipe?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DeleteRecipeFromDatabase(recipe);
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
    private void DeleteRecipeFromDatabase(Recipe recipe) {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        CompletableFuture<Void> futureRecipe = new CompletableFuture<>();
        deleteRecipeFutures.add(futureRecipe);
        databaseReferenceRecipes
                .child(recipe.getId())
                .setValue(null)
                .addOnCompleteListener(task -> futureRecipe.complete(null));

        CompletableFuture<Void> futureRecipeMedia = new CompletableFuture<>();
        deleteRecipeFutures.add(futureRecipeMedia);
        Media recipeMedia = recipeMedias.get(recipe.getId());
        storageReference
                .child(recipeMedia.getName())
                .delete()
                .addOnSuccessListener(storageTask -> {
                    databaseReferenceMedias
                            .child(recipeMedia.getId())
                            .setValue(null)
                            .addOnCompleteListener(task -> futureRecipeMedia.complete(null));
                });

        for (Instruction instruction:recipeInstructions.get(recipe.getId())) {
            CompletableFuture<Void> futureInstruction = new CompletableFuture<>();
            deleteRecipeFutures.add(futureInstruction);
            DatabaseReference databaseReferenceRef = databaseReferenceInstructions.child(instruction.getId());
            databaseReferenceRef
                    .setValue(null)
                    .addOnCompleteListener(task -> futureInstruction.complete(null));

            if (instruction.getMediaIds() == null || instruction.getMediaIds().isEmpty()) continue;

            for (String mediaId:instruction.getMediaIds()) {
                Media media = instructionMedias.get(mediaId);
                CompletableFuture<Void> future = new CompletableFuture<>();
                deleteRecipeFutures.add(future);
                storageReference
                        .child(media.getName())
                        .delete()
                        .addOnSuccessListener(storageTask -> {
                            databaseReferenceMedias
                                    .child(media.getId())
                                    .setValue(null)
                                    .addOnCompleteListener(task -> futureRecipeMedia.complete(null));future.complete(null);
                        });
            }
        }
        for (Ingredient ingredient:recipeIngredients.get(recipe.getId())) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            deleteRecipeFutures.add(future);

            DatabaseReference databaseReferenceRef = databaseReferenceIngredients.child(ingredient.getId());
            databaseReferenceRef.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    future.complete(null);
                }
            });
        }
        CompletableFuture<Void> allOf = CompletableFuture.allOf(deleteRecipeFutures.toArray(new CompletableFuture[0]));
        allOf.thenRun(() -> {
            Toast.makeText(getContext(), "Delete Completed.", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            GetRecipeByUser();
        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
    }
    private void GetRecipeByUser() {
        if (currentUser.getAccountType() == 0) return;
        new FirebaseUtils().getAllDataByKey(REALTIME_RECIPES, "userId", currentUser.getId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipes.clear();
                for (DataSnapshot recipeSnapshot:snapshot.getChildren()) {
                    Recipe recipe = recipeSnapshot.getValue(Recipe.class);
                    recipes.add(recipe);

                    fetchMediaForRecipe(recipe);
                    fetchIngredientForRecipe(recipe);
                    fetchInstructionForRecipe(recipe);
                }
                tv_recipes_count_user.setText(String.valueOf(recipes.size()));
//                viewModel.changeAllRecipeByChef(recipes);

                CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
                allOf.thenRun(() -> {
//                    Toast.makeText(getContext(), "Completed.", Toast.LENGTH_SHORT).show();
                    viewModel.changeAllRecipeByChef(recipes);
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
    private void UpdateUiWhenDataChange() {
        viewModel.getChangeDateCurrentUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                currentUser = user;
                UpdateUi(currentUser);
            }
        });
        viewModel.getRecipe().observe(getViewLifecycleOwner(), new Observer<Recipe>() {
            @Override
            public void onChanged(Recipe recipe) {
                GetRecipeByUser();
            }
        });
    }
    private void fetchMediaForRecipe(Recipe recipe) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        futures.add(future);
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
    private void fetchIngredientForRecipe(Recipe recipe) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        futures.add(future);
        new FirebaseUtils().getAllDataByKey(REALTIME_INGREDIENTS, "recipeId", recipe.getId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Ingredient> ingredients = new ArrayList<>();
                for (DataSnapshot child:snapshot.getChildren()) {
                    Ingredient ingredient = child.getValue(Ingredient.class);
                    ingredients.add(ingredient);
                }
                recipeIngredients.put(recipe.getId(),ingredients);
                future.complete(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void fetchInstructionForRecipe(Recipe recipe) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        futures.add(future);
        new FirebaseUtils().getAllDataByKey(REALTIME_INSTRUCTIONS, "recipeId", recipe.getId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Instruction> instructions = new ArrayList<>();
                for (DataSnapshot child:snapshot.getChildren()) {
                    Instruction instruction = child.getValue(Instruction.class);
                    instructions.add(instruction);
                    fetchMediaForInstruction(instruction);
                }
                recipeInstructions.put(recipe.getId(),instructions);
                future.complete(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void fetchMediaForInstruction(Instruction instruction) {
        if (instruction.getMediaIds() == null || instruction.getMediaIds().isEmpty()) return;
        for (String mediaId:instruction.getMediaIds()) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            futures.add(future);
            new FirebaseUtils().getDataFromFirebaseById(REALTIME_MEDIAS, mediaId, new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Media media = snapshot.getValue(Media.class);
                    instructionMedias.put(media.getId(),media);
                    future.complete(null);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }
    private void UpdateUi(User user) {
        if (user.getMediaId() == null) {
            img_avatar_user.setImageResource(R.drawable.default_avatar);
        } else {
            new FirebaseUtils().getDataFromFirebaseById(REALTIME_MEDIAS, user.getMediaId(), new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    currentMedia = snapshot.getValue(Media.class);
                    Glide.with(getContext()).
                            load(currentMedia.getUrl()).
                            error(R.drawable.default_avatar).
                            placeholder(R.drawable.default_avatar).
                            into(img_avatar_user);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        tv_name_user.setText(user.getName());
        tv_follower_count_user.setText(String.valueOf(user.getFollowersCount()));
        tv_following_count_user.setText(String.valueOf(user.getFollowingCount()));

        if (currentUser.getAccountType() == 0) {
            bottomNavigationView.setVisibility(View.GONE);
            viewPager2_recipe.setVisibility(View.GONE);
        } else {
            GetRecipeByUser();
        }
    }
    private void initUI(View view) {
        storageReference = FirebaseStorage.getInstance().getReference(STORAGE_MEDIAS);
        databaseReferenceMedias = FirebaseDatabase.getInstance().getReference(REALTIME_MEDIAS);
        databaseReferenceIngredients = FirebaseDatabase.getInstance().getReference(REALTIME_INGREDIENTS);
        databaseReferenceInstructions = FirebaseDatabase.getInstance().getReference(REALTIME_INSTRUCTIONS);
        databaseReferenceRecipes = FirebaseDatabase.getInstance().getReference(REALTIME_RECIPES);
        img_avatar_user = view.findViewById(R.id.img_avatar_user);
        tv_name_user = view.findViewById(R.id.tv_name_user);
        tv_recipes_count_user = view.findViewById(R.id.tv_recipes_count_user);
        tv_follower_count_user = view.findViewById(R.id.tv_follower_count_user);
        tv_following_count_user = view.findViewById(R.id.tv_following_count_user);
        mainActivity = (MainActivity) getActivity();
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
        viewPager2_recipe = view.findViewById(R.id.viewPager2_recipe);
        bottomNavigationView = view.findViewById(R.id.bottomNavigationViewInventory);
    }
}