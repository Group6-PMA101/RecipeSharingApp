package com.ph41626.pma101_recipesharingapplication.Fragment;

import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_MEDIAS;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_RECIPES;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_USERS;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ph41626.pma101_recipesharingapplication.Activity.MainActivity;
import com.ph41626.pma101_recipesharingapplication.Adapter.RecyclerViewPopularCreatorsAdapter;
import com.ph41626.pma101_recipesharingapplication.Adapter.RecyclerViewRecipeTrendingAdapter;
import com.ph41626.pma101_recipesharingapplication.Adapter.RecyclerViewTop100RecipeAdapter;
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
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

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
    private FirebaseUtils firebaseUtils;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initUI(view);
        RecyclerViewManager();
        GetDataFromFirebase();
        UpdateUiWhenDataChange();

        return view;
    }

    private void UpdateUiWhenDataChange() {
        viewModel.getChangeDataRecipes().observe(getViewLifecycleOwner(), new Observer<ArrayList<Recipe>>() {
            @Override
            public void onChanged(ArrayList<Recipe> recipes) {
                recipeTrendingAdapter.Update(recipes);
                top100RecipeAdapter.Update(recipes);

                for (int i = 0; i < recipes.size(); i++) {
                    Recipe recipe = recipes.get(i);
                    fetchMediaForRecipe(recipe,i,recipeTrendingAdapter);
                    fetchMediaForRecipe(recipe,i,top100RecipeAdapter);
                }
            }
        });
        viewModel.getChangeDateUsers().observe(getViewLifecycleOwner(), new Observer<ArrayList<User>>() {
            @Override
            public void onChanged(ArrayList<User> users) {
                popularCreatorsAdapter.Update(users);

                for (int i = 0; i < users.size(); i++) {
                    fetchMediaForUser(users.get(i),i,popularCreatorsAdapter);
                }
            }
        });
    }

    private void fetchMediaForRecipe(Recipe recipe,int pos,RecyclerView.Adapter adapter) {
        if (recipeMedias.containsKey(recipe.getId()) && recipeMedias.get(recipe.getId()) != null) {
            fetchUserForRecipe(recipe,pos,adapter);
            return;
        }
        new FirebaseUtils().getDataFromFirebaseById(REALTIME_MEDIAS, recipe.getMediaId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Media media = snapshot.getValue(Media.class);

                recipeMedias.put(recipe.getId(),media);
                fetchUserForRecipe(recipe,pos,adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void fetchUserForRecipe(Recipe recipe,int pos,RecyclerView.Adapter adapter) {
        if (recipeUsers.containsKey(recipe.getUserId()) && recipeUsers.get(recipe.getId()) != null) {
            fetchMediaForUser(recipeUsers.get(recipe.getId()),pos,adapter);
            return;
        }
        new FirebaseUtils().getDataFromFirebaseById(REALTIME_USERS, recipe.getUserId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                recipeUsers.put(recipe.getId(),user);
                fetchMediaForUser(user,pos,adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void fetchMediaForUser(User user,int pos,RecyclerView.Adapter adapter) {
        if (user.getMediaId() == null ||
                (userMedias.containsKey(user.getId()) && userMedias.get(user.getId()) != null)) {
            adapter.notifyItemChanged(pos);
            return;
        }
        firebaseUtils.getDataFromFirebaseById(REALTIME_MEDIAS, user.getMediaId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Media media = snapshot.getValue(Media.class);
                userMedias.put(user.getId(),media);
                adapter.notifyItemChanged(pos);
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
                    recipes.add(recipe);
                }
                viewModel.changeDateRecipes(recipes);
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
                    users.add(user);
                }
                viewModel.changeUsers(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
    }
}