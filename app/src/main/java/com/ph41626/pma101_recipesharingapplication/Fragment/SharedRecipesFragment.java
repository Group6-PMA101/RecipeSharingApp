package com.ph41626.pma101_recipesharingapplication.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ph41626.pma101_recipesharingapplication.Adapter.RecyclerViewAllRecipeAdapter;
import com.ph41626.pma101_recipesharingapplication.Model.Recipe;
import com.ph41626.pma101_recipesharingapplication.Model.ViewModel;
import com.ph41626.pma101_recipesharingapplication.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SharedRecipesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SharedRecipesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SharedRecipesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SharedRecipesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SharedRecipesFragment newInstance(String param1, String param2) {
        SharedRecipesFragment fragment = new SharedRecipesFragment();
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
    private ArrayList<Recipe> recipes = new ArrayList<>();
    private RecyclerView rcv_shared_recipes;
    private ViewModel viewModel;
    private RecyclerViewAllRecipeAdapter allRecipeAdapter;
    public ProfileFragment profileFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shared_recipes, container, false);
        initUI(view);
        RecyclerViewManager();
        viewModel.getAllRecipeByChef().observe(getViewLifecycleOwner(), new Observer<ArrayList<Recipe>>() {
            @Override
            public void onChanged(ArrayList<Recipe> recipes) {
                ArrayList<Recipe> newRecipes = new ArrayList<>();
                for(Recipe recipe:recipes) {
                    if (recipe.isPublic()) {
                        newRecipes.add(recipe);
                    }
                }
                UpdateUI(newRecipes);
            }
        });
//        viewModel.getSharedRecipes().observe(getViewLifecycleOwner(), new Observer<ArrayList<Recipe>>() {
//            @Override
//            public void onChanged(ArrayList<Recipe> recipes) {
//                ArrayList<Recipe> newRecipes = new ArrayList<>();
//                for(Recipe recipe:recipes) {
//                    if (recipe.isPublic()) {
//                        newRecipes.add(recipe);
//                    }
//                }
//                UpdateUI(newRecipes);
//            }
//        });
        return view;
    }
    private void UpdateUI(ArrayList<Recipe> recipes) {
        this.recipes = recipes;
        allRecipeAdapter.Update(recipes);
    }

    private void RecyclerViewManager() {
        allRecipeAdapter = new RecyclerViewAllRecipeAdapter(getContext(),new ArrayList<>(),profileFragment);
        rcv_shared_recipes.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        rcv_shared_recipes.setAdapter(allRecipeAdapter);
        rcv_shared_recipes.setNestedScrollingEnabled(true);
    }

    private void initUI(View view) {
        rcv_shared_recipes = view.findViewById(R.id.rcv_shared_recipes);
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
        List<Fragment> fragments = getActivity().getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof ProfileFragment) {
                profileFragment = (ProfileFragment) fragment;
                break;
            }
        }
    }
}