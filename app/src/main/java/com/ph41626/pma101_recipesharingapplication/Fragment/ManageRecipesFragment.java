package com.ph41626.pma101_recipesharingapplication.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.ph41626.pma101_recipesharingapplication.Adapter.RecyclerViewRecipeAdminAdapter;
import com.ph41626.pma101_recipesharingapplication.Model.Recipe;
import com.ph41626.pma101_recipesharingapplication.Model.ViewModel;
import com.ph41626.pma101_recipesharingapplication.R;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ManageRecipesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManageRecipesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ManageRecipesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ManageRecipesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ManageRecipesFragment newInstance(String param1, String param2) {
        ManageRecipesFragment fragment = new ManageRecipesFragment();
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
    private RecyclerView rcv_recipe_admin;
    private RecyclerViewRecipeAdminAdapter recipeAdminAdapter;
    private ViewModel viewModel;
    private AdminFragment adminFragment;
    private RecyclerView rcv_admin;
    private int sortItemIndex = 0;
    private Button btn_sort;
    private TextView tv_noti;
    private ArrayList<Recipe> getRecipes = new ArrayList<>();
    private ArrayList<Recipe> currentRecipes = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manage_recipes, container, false);

        initUI(view);
        SetUpRecyclerView();
        viewModel.getManageRecipe().observe(getViewLifecycleOwner(), new Observer<ArrayList<Recipe>>() {
            @Override
            public void onChanged(ArrayList<Recipe> recipes) {
                getRecipes = recipes;
                recipeAdminAdapter.Update(recipes);
            }
        });
        btn_sort.setOnClickListener(v -> {
            ShowSortDialog();
        });


        return view;
    }

    private void Noti() {
        if (currentRecipes.isEmpty())
            tv_noti.setVisibility(View.VISIBLE);
        else
            tv_noti.setVisibility(View.GONE);
    }
    private void ShowSortDialog() {
        String[] items = {"All Recipes", "Locked Recipes", "Unlocked Recipes", "Newest Creation Date", "Oldest Creation Date"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.CustomAlertDialogTheme);
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
                        AllRecipes();
                        break;
                    case 1:
                        LockedRecipes();
                        break;
                    case 2:
                        UnlockRecipes();
                        break;
                    case 3:
                        NewestCreationDate();
                        break;
                    case 4:
                        OldestCreationDate();
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
    private void AllRecipes() {
        currentRecipes = this.getRecipes;
        recipeAdminAdapter.Update(currentRecipes);
    }
    private void LockedRecipes() {
        currentRecipes = (ArrayList<Recipe>) getRecipes.stream()
                .filter(Recipe::isStatus)
                .collect(Collectors.toList());
        Noti();
        recipeAdminAdapter.Update(currentRecipes);
    }
    private void UnlockRecipes() {
        currentRecipes = (ArrayList<Recipe>) getRecipes.stream()
                .filter(recipe -> !recipe.isStatus())
                .collect(Collectors.toList());
        Noti();
        recipeAdminAdapter.Update(currentRecipes);
    }
    private void NewestCreationDate() {
        currentRecipes = (ArrayList<Recipe>) getRecipes.stream()
                .filter(recipe -> !recipe.isStatus())
                .sorted((r1, r2) -> r2.getCreationDate().compareTo(r1.getCreationDate()))
                .collect(Collectors.toList());
        Noti();
        recipeAdminAdapter.Update(currentRecipes);
    }

    private void OldestCreationDate() {
        currentRecipes = (ArrayList<Recipe>) getRecipes.stream()
                .filter(recipe -> !recipe.isStatus())
                .sorted((r1, r2) -> r1.getCreationDate().compareTo(r2.getCreationDate()))
                .collect(Collectors.toList());
        Noti();
        recipeAdminAdapter.Update(currentRecipes);
    }


    private void SetUpRecyclerView() {
        recipeAdminAdapter = new RecyclerViewRecipeAdminAdapter(getContext(),new ArrayList<>(),adminFragment);
        rcv_recipe_admin.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        rcv_recipe_admin.setAdapter(recipeAdminAdapter);
    }
    private void initUI(View view) {
        tv_noti = view.findViewById(R.id.tv_noti);
        btn_sort = view.findViewById(R.id.btn_sort);
        rcv_recipe_admin = view.findViewById(R.id.rcv_recipe_admin);
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
        List<Fragment> fragments = getActivity().getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof AdminFragment) {
                adminFragment = (AdminFragment) fragment;
                break;
            }
        }
    }
}