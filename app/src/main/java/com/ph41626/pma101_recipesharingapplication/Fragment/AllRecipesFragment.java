package com.ph41626.pma101_recipesharingapplication.Fragment;

import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_INGREDIENTS;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_INSTRUCTIONS;

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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ph41626.pma101_recipesharingapplication.Activity.MainActivity;
import com.ph41626.pma101_recipesharingapplication.Adapter.RecyclerViewAllRecipeAdapter;
import com.ph41626.pma101_recipesharingapplication.Model.Ingredient;
import com.ph41626.pma101_recipesharingapplication.Model.Instruction;
import com.ph41626.pma101_recipesharingapplication.Model.Media;
import com.ph41626.pma101_recipesharingapplication.Model.Recipe;
import com.ph41626.pma101_recipesharingapplication.Model.ViewModel;
import com.ph41626.pma101_recipesharingapplication.R;
import com.ph41626.pma101_recipesharingapplication.Services.FirebaseUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllRecipesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllRecipesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AllRecipesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AllRecipesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AllRecipesFragment newInstance(String param1, String param2) {
        AllRecipesFragment fragment = new AllRecipesFragment();
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
    private RecyclerView rcv_all_recipes;
    private ViewModel viewModel;
    private RecyclerViewAllRecipeAdapter allRecipeAdapter;

    public HashMap<String, Media> recipeMedias = new HashMap<>();
    public HashMap<String, ArrayList<Ingredient>> recipeIngredients = new HashMap<>();
    public HashMap<String, ArrayList<Instruction>> recipeInstructions = new HashMap<>();
    public HashMap<String, ArrayList<Media>> instructionMedias = new HashMap<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_recipes, container, false);

        initUI(view);
        RecyclerViewManager();
        viewModel.getAllRecipeByChef().observe(getViewLifecycleOwner(), new Observer<ArrayList<Recipe>>() {
            @Override
            public void onChanged(ArrayList<Recipe> recipes) {
                UpdateUI(recipes);

                for (int i = 0; i < recipes.size(); i++) {
                    fetchMediaForRecipe(recipes.get(i),i);
                }
            }
        });

        return view;
    }

    private void fetchMediaForRecipe(Recipe recipe,int pos) {
        new FirebaseUtils().getDataFromFirebaseById(MainActivity.REALTIME_MEDIAS, recipe.getMediaId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Media media = snapshot.getValue(Media.class);
                recipeMedias.put(recipe.getId(),media);
                fetchIngredientForRecipe(recipe,pos);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchIngredientForRecipe(Recipe recipe,int pos) {
        new FirebaseUtils().getAllDataByKey(REALTIME_INGREDIENTS, "recipeId", recipe.getId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Ingredient> ingredients = new ArrayList<>();
                for (DataSnapshot child:snapshot.getChildren()) {
                    Ingredient ingredient = child.getValue(Ingredient.class);
                    ingredients.add(ingredient);
                }
                recipeIngredients.put(recipe.getId(),ingredients);
                allRecipeAdapter.notifyItemChanged(pos);
//                fetchInstructionForRecipe(recipe);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchInstructionForRecipe(Recipe recipe) {
        new FirebaseUtils().getAllDataByKey(REALTIME_INSTRUCTIONS, "recipeId", recipe.getId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Instruction> instructions = new ArrayList<>();
                for (DataSnapshot child:snapshot.getChildren()) {
                    Instruction instruction = child.getValue(Instruction.class);
                    instructions.add(instruction);
                }
                recipeInstructions.put(recipe.getId(),instructions);
                fetchMediaForInstruction(recipe);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchMediaForInstruction(Recipe recipe) {

    }

    private void UpdateUI(ArrayList<Recipe> recipes) {
        this.recipes = recipes;
        allRecipeAdapter.Update(recipes);
    }

    private void RecyclerViewManager() {
        allRecipeAdapter = new RecyclerViewAllRecipeAdapter(getContext(),recipes,this);
        rcv_all_recipes.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        rcv_all_recipes.setAdapter(allRecipeAdapter);
        rcv_all_recipes.setNestedScrollingEnabled(true);
    }

    private void initUI(View view) {
        rcv_all_recipes = view.findViewById(R.id.rcv_all_recipes);
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
    }
}