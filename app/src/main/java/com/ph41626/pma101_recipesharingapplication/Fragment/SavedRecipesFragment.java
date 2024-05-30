package com.ph41626.pma101_recipesharingapplication.Fragment;

import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_RECIPE_COLLECTIONS;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_RECIPE_RECIPECOLLECTIONS;
import static com.ph41626.pma101_recipesharingapplication.Services.UserPreferences.GetUser;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ph41626.pma101_recipesharingapplication.Activity.MainActivity;
import com.ph41626.pma101_recipesharingapplication.Activity.RecipeCollectionActivity;
import com.ph41626.pma101_recipesharingapplication.Activity.RecipeDetailActivity;
import com.ph41626.pma101_recipesharingapplication.Adapter.RecyclerViewRecipeCollectionAdapter;
import com.ph41626.pma101_recipesharingapplication.Model.Recipe;
import com.ph41626.pma101_recipesharingapplication.Model.RecipeCollection;
import com.ph41626.pma101_recipesharingapplication.Model.Recipe_RecipeCollection;
import com.ph41626.pma101_recipesharingapplication.Model.ViewModel;
import com.ph41626.pma101_recipesharingapplication.R;
import com.ph41626.pma101_recipesharingapplication.Services.FirebaseUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SavedRecipesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SavedRecipesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SavedRecipesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SavedRecipesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SavedRecipesFragment newInstance(String param1, String param2) {
        SavedRecipesFragment fragment = new SavedRecipesFragment();
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


    private Button btn_more;
    private RecyclerView rcv_recipe_collection;
    private RecyclerViewRecipeCollectionAdapter recipeCollectionAdapter;
    private ProgressDialog progressDialog;

    private ArrayList<RecipeCollection> recipeCollections = new ArrayList<>();
    private RecipeCollection newRecipeCollection = new RecipeCollection();
    private HashMap<String,ArrayList<Recipe_RecipeCollection>> recipeForRecipeCollection = new HashMap<>();
    private ViewModel viewModel;
    public MainActivity mainActivity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_saved_recipes, container, false);

        initUI(view);
        SetUpRecyclerView();
        SetUpButton();
        viewModel.getRecipeCollectionForUser().observe(getViewLifecycleOwner(), new Observer<ArrayList<RecipeCollection>>() {
            @Override
            public void onChanged(ArrayList<RecipeCollection> recipeCollections) {
                recipeCollectionAdapter.Update(recipeCollections);
            }
        });
        viewModel.getRecipeForRecipeCollection().observe(getViewLifecycleOwner(), new Observer<HashMap<String, ArrayList<Recipe>>>() {
            @Override
            public void onChanged(HashMap<String, ArrayList<Recipe>> stringArrayListHashMap) {
                recipeCollectionAdapter.UpdateThumbnail();
            }
        });
        return view;
    }
    private void CreateNewRecipeCollection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Create new Recipe Collection");
        final EditText input = new EditText(getContext());
        builder.setView(input);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = input.getText().toString().trim();
                if(value.isEmpty()) {
                    Toast.makeText(getContext(), "Name cannot be empty!", Toast.LENGTH_SHORT).show();
                } else {
                    newRecipeCollection.setName(value);
                    newRecipeCollection.setUserId(GetUser(getContext()).getId());

                    progressDialog.setMessage("Please wait ...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    FirebaseDatabase.getInstance()
                            .getReference(REALTIME_RECIPE_COLLECTIONS)
                            .child(newRecipeCollection.getId())
                            .setValue(newRecipeCollection)
                            .addOnCompleteListener(task -> {
                                progressDialog.dismiss();
                            });


                    dialog.cancel();
                }
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
        recipeCollectionAdapter = new RecyclerViewRecipeCollectionAdapter(getContext(),new ArrayList<>(),this);
        rcv_recipe_collection.setLayoutManager(new GridLayoutManager(getContext(),2));
        rcv_recipe_collection.setAdapter(recipeCollectionAdapter);
    }
    public void RecipeCollectionDetail(RecipeCollection recipeCollection) {
        Intent intent = new Intent(getActivity(), RecipeCollectionActivity.class);
        intent.putExtra("recipeCollection",recipeCollection);
        intent.putExtra("recipeRecipeCollection",mainActivity.recipeRecipeCollectionHashMap.get(recipeCollection.getId()));
        intent.putExtra("recipes",mainActivity.recipeForRecipeCollection.get(recipeCollection.getId()));
        intent.putExtra("recipeMedia",mainActivity.recipeMedia);
        startActivity(intent);
    }
    private void SetUpButton() {
        btn_more.setOnClickListener(v -> {
            PopupMenu(v);
        });
    }
    private void PopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
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
        menuItems.add("New Recipe Collection");
        return menuItems;
    }
    private void handleMenuItemClick(MenuItem item) {
        if (item.getTitle().equals("New Recipe Collection")) {
            CreateNewRecipeCollection();
        }
    }
    private void initUI(View view) {
        progressDialog = new ProgressDialog(getContext(),R.style.AppCompatAlertDialogStyle);
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
        btn_more = view.findViewById(R.id.btn_more);

        rcv_recipe_collection = view.findViewById(R.id.rcv_recipe_collection);
        mainActivity = (MainActivity) getActivity();
    }

}