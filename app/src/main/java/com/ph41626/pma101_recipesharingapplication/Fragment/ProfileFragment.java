package com.ph41626.pma101_recipesharingapplication.Fragment;

import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_INGREDIENTS;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_INSTRUCTIONS;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_MEDIAS;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_RECIPES;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
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

import java.util.ArrayList;
import java.util.HashMap;

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

    private static final int FRAGMENT_ALL_RECIPES = 0;
    private static final int FRAGMENT_SHARED_RECIPES = 1;
    private static final int FRAGMENT_UNSHARED_RECIPES = 2;
    private int mCurrentFragment = FRAGMENT_ALL_RECIPES;
    private ImageView img_avatar_user;
    private TextView tv_name_user, tv_recipes_count_user, tv_follower_count_user, tv_following_count_user;
    private MainActivity mainActivity;
    private ViewModel viewModel;
    public ArrayList<Recipe> recipes = new ArrayList<>();
    private User currentUser = new User();
    private Media currentMedia = new Media();
    private ViewPager2 viewPager2_recipe;
    private BottomNavigationView bottomNavigationView;
    public HashMap<String, Media> recipeMedias = new HashMap<>();
    public HashMap<String, ArrayList<Ingredient>> recipeIngredients = new HashMap<>();
    public HashMap<String, ArrayList<Instruction>> recipeInstructions = new HashMap<>();
    public HashMap<String, ArrayList<Media>> instructionMedias = new HashMap<>();

    private int totalTasks = 0;
    private int completedTasks = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initUI(view);
//        GetRecipeByUser();
        UpdateUiWhenDataChange();
        BottomNavigationManager();
        viewModel.getAllRecipeByChef().observe(getViewLifecycleOwner(), new Observer<ArrayList<Recipe>>() {
            @Override
            public void onChanged(ArrayList<Recipe> recipes) {

            }
        });

        return view;
    }
    private void BottomNavigationManager() {
        ViewPagerBottomNavigationRecipeAdapter bottomNavigationAdapter = new ViewPagerBottomNavigationRecipeAdapter(getActivity());
        viewPager2_recipe.setAdapter(bottomNavigationAdapter);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.allRecipe) {
                    if (mCurrentFragment != FRAGMENT_ALL_RECIPES) {
                        viewPager2_recipe.setCurrentItem(0);
                        mCurrentFragment = FRAGMENT_ALL_RECIPES;
                    }
                } else if(item.getItemId() == R.id.sharedRecipe) {
                    if (mCurrentFragment != FRAGMENT_SHARED_RECIPES) {
                        viewPager2_recipe.setCurrentItem(1);
                        mCurrentFragment = FRAGMENT_SHARED_RECIPES;
                    }
                } else if (item.getItemId() == R.id.unsharedRecipe) {
                    if (mCurrentFragment != FRAGMENT_UNSHARED_RECIPES) {
                        viewPager2_recipe.setCurrentItem(2);
                        mCurrentFragment = FRAGMENT_UNSHARED_RECIPES;
                    }
                }
                return true;
            }
        });
    }
    public void UpdateSharedRecipe(Recipe recipe) {
        new FirebaseUtils().UpdateRecipeShare(REALTIME_RECIPES, recipe.getId(), recipe.isPublic(), new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                if (recipe.isPublic()) {
                    Toast.makeText(mainActivity, "Share successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mainActivity, "Share canceled!", Toast.LENGTH_SHORT).show();
                }
//                GetRecipeByUser();
            }
        });
    }
    private void GetRecipeByUser() {
        if (currentUser.getAccountType() == 0) return;
        new FirebaseUtils().getAllDataByKeyRealTime(REALTIME_RECIPES, "userId", currentUser.getId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipes.clear();
                int index = 0;
                for (DataSnapshot recipeSnapshot:snapshot.getChildren()) {
                    Recipe recipe = recipeSnapshot.getValue(Recipe.class);
                    recipes.add(recipe);
                    totalTasks++;
                    fetchMediaForRecipe(recipe,index);
                    index++;
                }
                tv_recipes_count_user.setText(String.valueOf(recipes.size()));
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
    }
    private void fetchMediaForRecipe(Recipe recipe,int pos) {
//        if (recipeMedias.containsKey(recipe.getId()) &&
//        recipeMedias.get(recipe.getId()) != null) {
//            fetchIngredientForRecipe(recipe,pos);
//            return;
//        }
        new FirebaseUtils().getDataFromFirebaseById(MainActivity.REALTIME_MEDIAS, recipe.getMediaId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Media media = snapshot.getValue(Media.class);
                recipeMedias.put(recipe.getId(),media);
                completedTasks++;
                totalTasks++;
                fetchIngredientForRecipe(recipe,pos);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void fetchIngredientForRecipe(Recipe recipe,int pos) {
//        if (recipeIngredients.containsKey(recipe.getId()) &&
//        recipeIngredients.get(recipe.getId()) != null) {
//            fetchInstructionForRecipe(recipe);
//            return;
//        }
        new FirebaseUtils().getAllDataByKey(REALTIME_INGREDIENTS, "recipeId", recipe.getId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Ingredient> ingredients = new ArrayList<>();
                for (DataSnapshot child:snapshot.getChildren()) {
                    Ingredient ingredient = child.getValue(Ingredient.class);
                    ingredients.add(ingredient);
                }
                recipeIngredients.put(recipe.getId(),ingredients);
//                allRecipeAdapter.notifyItemChanged(pos);
                completedTasks++;
                totalTasks++;
                fetchInstructionForRecipe(recipe);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void fetchInstructionForRecipe(Recipe recipe) {
//        if (recipeInstructions.containsKey(recipe.getId()) &&
//        recipeInstructions.get(recipe.getId()) != null) {
//
//        }
        new FirebaseUtils().getAllDataByKey(REALTIME_INSTRUCTIONS, "recipeId", recipe.getId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Instruction> instructions = new ArrayList<>();
                for (DataSnapshot child:snapshot.getChildren()) {
                    Instruction instruction = child.getValue(Instruction.class);
                    instructions.add(instruction);
                }
                completedTasks++;
                totalTasks++;
                recipeInstructions.put(recipe.getId(),instructions);
                fetchMediaForInstruction(recipe);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void fetchMediaForInstruction(Recipe recipe) {
        for (int i = 0; i < recipeInstructions.get(recipe.getId()).size(); i++) {
            Instruction instruction = recipeInstructions.get(recipe.getId()).get(i);
            new FirebaseUtils().getDataFromFirebaseById(REALTIME_MEDIAS, instruction.getId(), new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<Media> medias = new ArrayList<>();
                    for (DataSnapshot child:snapshot.getChildren()) {
                        Media media = child.getValue(Media.class);
                        medias.add(media);
                    }
                    completedTasks++;
                    instructionMedias.put(instruction.getId(),medias);
                    CheckAllTaskCompleted();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
    private void CheckAllTaskCompleted() {
        Toast.makeText(mainActivity, totalTasks + " - " + completedTasks, Toast.LENGTH_SHORT).show();
        if (totalTasks == completedTasks) {
            viewModel.changeAllRecipeByChef(recipes);
            totalTasks = 0;
            completedTasks = 0;
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