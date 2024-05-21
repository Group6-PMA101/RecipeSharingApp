package com.ph41626.pma101_recipesharingapplication.Fragment;

import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_MEDIAS;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_RECIPES;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ph41626.pma101_recipesharingapplication.Activity.MainActivity;
import com.ph41626.pma101_recipesharingapplication.Adapter.ViewPagerBottomNavigationRecipeAdapter;
import com.ph41626.pma101_recipesharingapplication.Model.Media;
import com.ph41626.pma101_recipesharingapplication.Model.Recipe;
import com.ph41626.pma101_recipesharingapplication.Model.User;
import com.ph41626.pma101_recipesharingapplication.Model.ViewModel;
import com.ph41626.pma101_recipesharingapplication.R;
import com.ph41626.pma101_recipesharingapplication.Services.FirebaseUtils;

import java.util.ArrayList;

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
    private ArrayList<Recipe> recipes = new ArrayList<>();
    private User currentUser = new User();
    private Media currentMedia = new Media();
    private ViewPager2 viewPager2_recipe;
    private BottomNavigationView bottomNavigationView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initUI(view);

        UpdateUiWhenDataChange();
        GetRecipeByUser();
        BottomNavigationManager();

        return view;
    }
    private void BottomNavigationManager() {
        ViewPagerBottomNavigationRecipeAdapter bottomNavigationAdapter = new ViewPagerBottomNavigationRecipeAdapter(getActivity());
        viewPager2_recipe.setAdapter(bottomNavigationAdapter);
//        viewPager2_recipe.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageSelected(int position) {
//                super.onPageSelected(position);
//                switch (position) {
//                    case 0: bottomNavigationView.getMenu().findItem(R.id.allRecipe).setChecked(true);
//                        break;
//                    case 1: bottomNavigationView.getMenu().findItem(R.id.sharedRecipe).setChecked(true);
//                        break;
//                    case 2: bottomNavigationView.getMenu().findItem(R.id.unsharedRecipe).setChecked(true);
//                        break;
//                }
//            }
//        });
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
    private void GetRecipeByUser() {
        if (currentUser.getAccountType() == 0) return;
        new FirebaseUtils().getAllDataByKey(REALTIME_RECIPES, "userId", currentUser.getId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipes.clear();
                for (DataSnapshot recipeSnapshot:snapshot.getChildren()) {
                    Recipe recipe = recipeSnapshot.getValue(Recipe.class);
                    recipes.add(recipe);
                }
                tv_recipes_count_user.setText(String.valueOf(recipes.size()));
                viewModel.changeAllRecipeByChef(recipes);
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