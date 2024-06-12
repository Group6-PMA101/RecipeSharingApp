package com.ph41626.pma101_recipesharingapplication.Fragment;

import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_MEDIAS;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_NOTIFICATIONS;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_RECIPES;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_USERS;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ph41626.pma101_recipesharingapplication.Activity.MainActivity;
import com.ph41626.pma101_recipesharingapplication.Adapter.RecyclerViewRecipeAdminAdapter;
import com.ph41626.pma101_recipesharingapplication.Adapter.ViewPagerBottomNavigationAdminAdapter;
import com.ph41626.pma101_recipesharingapplication.Adapter.ViewPagerBottomNavigationRecipeAdapter;
import com.ph41626.pma101_recipesharingapplication.Model.Ingredient;
import com.ph41626.pma101_recipesharingapplication.Model.Instruction;
import com.ph41626.pma101_recipesharingapplication.Model.Media;
import com.ph41626.pma101_recipesharingapplication.Model.Notification;
import com.ph41626.pma101_recipesharingapplication.Model.Recipe;
import com.ph41626.pma101_recipesharingapplication.Model.User;
import com.ph41626.pma101_recipesharingapplication.Model.ViewModel;
import com.ph41626.pma101_recipesharingapplication.R;
import com.ph41626.pma101_recipesharingapplication.Services.FirebaseUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AdminFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminFragment newInstance(String param1, String param2) {
        AdminFragment fragment = new AdminFragment();
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
    public ArrayList<Recipe> recipes = new ArrayList<>();
    public HashMap<String, Media> mediaForRecipeHashMap = new HashMap<>();
    public HashMap<String, User> userForRecipeHashMap = new HashMap<>();
    public HashMap<String, Media> mediaForUserHashMap = new HashMap<>();
    private HashMap<String, ArrayList<Ingredient>> ingredientHashMap = new HashMap<>();
    private HashMap<String, ArrayList<Instruction>> instructionHashMap = new HashMap<>();

    private List<CompletableFuture<Void>> futures = new ArrayList<>();
    private ProgressDialog progressDialog;
    public ViewPager2 viewPager2_recipe;
    private ViewModel viewModel;
    private BottomNavigationView bottomNavigationView;
    private int lockReasonItemIndex = 0;
    private int lockReasonItemIndexUser = 0;
    private String[] items = {
            "Inappropriate Content",
            "Recipe Error",
            "Negative Feedback",
            "Copyright Violation",
            "Platform Policy Violation",
            "User Request",
            "Misleading Information",
            "Spam or Advertisements"};
    private String[] accountBanReasons = {
            "Violation of Terms of Service",
            "Inappropriate Content",
            "Harassment",
            "Copyright Violation",
            "Spam or Fraudulent Activity",
            "Multiple Account Usage",
            "Fake Information",
            "Use of Automated Tools",
            "Illegal Activities",
            "Privacy Policy Violation",
            "Multiple Reports"
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin, container, false);

        initUI(view);
        fetchRecipes();
        fetchUser();
        BottomNavigationManager();

//        SetUpRecyclerView();

        return view;
    }

    private void fetchUser() {
        new FirebaseUtils().getDataFromFirebaseRealtime(REALTIME_USERS, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child:snapshot.getChildren()) {
                    User user = child.getValue(User.class);
                    userForRecipeHashMap.put(user.getId(),user);
                }
                viewModel.changeDataManageUser(userForRecipeHashMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void BottomNavigationManager() {
        ViewPagerBottomNavigationAdminAdapter bottomNavigationAdapter = new ViewPagerBottomNavigationAdminAdapter(getActivity());
        viewPager2_recipe.setAdapter(bottomNavigationAdapter);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.manageRecipe) {
                    viewPager2_recipe.setCurrentItem(0);
                } else if(item.getItemId() == R.id.manageUser) {
                    viewPager2_recipe.setCurrentItem(1);
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
                        bottomNavigationView.setSelectedItemId(R.id.manageRecipe);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.manageUser);
                        break;
                }
            }
        });
    }
    private void ShowLockReasonDialog(Recipe recipe) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Reason for Locking");

        builder.setSingleChoiceItems(items, lockReasonItemIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                lockReasonItemIndex = which;
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showConfirmationDialog(recipe);
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
    private void showConfirmationDialog(Recipe recipe) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to lock this recipe for the selected reason?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.setMessage("Please wait ...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                recipe.setStatus(true);
                FirebaseDatabase
                        .getInstance()
                        .getReference(REALTIME_RECIPES)
                        .child(recipe.getId())
                        .setValue(recipe)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String title = "Recipe Lock Notification";
                                String content = "Dear " + userForRecipeHashMap.get(recipe.getUserId()).getName() + "," +
                                        "\n\nWe regret to inform you that your recipe titled \"" + recipe.getName() + "\" has been locked due to the following reason: " +
                                        items[lockReasonItemIndex] + "\n\n" +
                                        "We encourage you to review our community guidelines and make the necessary adjustments to your recipe. If you believe this action was taken in error, or if you have any questions, please contact our support team at [Group 6].\n\n" +
                                        "Thank you for your understanding and cooperation.\n\n" +
                                        "Best regards.";
                                Notification notification = new Notification(userForRecipeHashMap.get(recipe.getUserId()).getId(),title,content);
                                FirebaseDatabase
                                        .getInstance()
                                        .getReference(REALTIME_NOTIFICATIONS)
                                        .child(notification.getId())
                                        .setValue(notification)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(getContext(), "Recipe has been successfully locked and the user has been notified.", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        });
                            }
                        });
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void PopupMenu(View view, Recipe recipe) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        List<String> menuItems = getMenuItems(recipe);
        for (String menuItem : menuItems) {
            popupMenu.getMenu().add(menuItem);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                handleMenuItemClick(item, recipe);
                return true;
            }
        });
        popupMenu.show();
    }
    public void PopupMenuManageUser(View view, User user) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        List<String> menuItems = getMenuItemsManageUser(user);
        for (String menuItem : menuItems) {
            popupMenu.getMenu().add(menuItem);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                handleMenuItemClickManageUser(item, user);
                return true;
            }
        });
        popupMenu.show();
    }
    private List<String> getMenuItems(Recipe recipe) {
        List<String> menuItems = new ArrayList<>();
        if (recipe.isStatus()) {
            menuItems.add("UnLocked");
        } else {
            menuItems.add("Locked");
        }
        return menuItems;
    }
    private List<String> getMenuItemsManageUser(User user) {
        List<String> menuItems = new ArrayList<>();
        if (user.isStatus()) {
            menuItems.add("UnLocked");
        } else {
            menuItems.add("Locked");
        }
        return menuItems;
    }
    private void handleMenuItemClick(MenuItem item, Recipe recipe) {
        if (item.getTitle().equals("Locked")) {
            ShowLockReasonDialog(recipe);
        } else if (item.getTitle().equals("UnLocked")) {
            ShowUnlockConfirmationDialog(recipe);
        }
    }
    private void handleMenuItemClickManageUser(MenuItem item, User user) {
        if (item.getTitle().equals("Locked")) {
            ShowLockReasonDialogUser(user);
        } else if (item.getTitle().equals("UnLocked")) {
            ShowUnlockConfirmationDialogUser(user);
        }
    }
    private void ShowLockReasonDialogUser(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Reason for Locking");

        builder.setSingleChoiceItems(accountBanReasons, lockReasonItemIndexUser, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                lockReasonItemIndexUser = which;
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showConfirmationDialogUser(user);
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
    private void showConfirmationDialogUser(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to lock this account for the selected reason?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.setMessage("Please wait ...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                user.setStatus(true);
                FirebaseDatabase
                        .getInstance()
                        .getReference(REALTIME_USERS)
                        .child(user.getId())
                        .setValue(user)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String accountBanTitle = "Account Suspension Notification";
                                String accountBanContent = "Dear " + userForRecipeHashMap.get(user.getId()).getName() + "," +
                                        "\n\nWe regret to inform you that your account has been suspended due to the following reason: " +
                                        accountBanReasons[lockReasonItemIndexUser] + "\n\n" +
                                        "We encourage you to review our community guidelines to understand our policies. If you believe this action was taken in error, or if you have any questions, please contact our support team at [Group 6].\n\n" +
                                        "Thank you for your understanding and cooperation.\n\n" +
                                        "Best regards,\n" + "The [Group 6] Team.";
                                Notification notification = new Notification(userForRecipeHashMap.get(user.getId()).getId(),accountBanTitle,accountBanContent);
                                FirebaseDatabase
                                        .getInstance()
                                        .getReference(REALTIME_NOTIFICATIONS)
                                        .child(notification.getId())
                                        .setValue(notification)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(getContext(), "Account has been successfully locked and the user has been notified.", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        });
                            }
                        });
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void ShowUnlockConfirmationDialog(Recipe recipe) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Unlock Recipe");
        builder.setMessage("Are you sure you want to unlock this recipe?\n\nPlease ensure that this action complies with platform policies.");

        builder.setPositiveButton("Unlock", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.setMessage("Please wait ...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                recipe.setStatus(false);
                FirebaseDatabase
                        .getInstance()
                        .getReference(REALTIME_RECIPES)
                        .child(recipe.getId())
                        .setValue(recipe)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String title = "Your Recipe Has Been Unlocked";
                                String content = "Dear " + userForRecipeHashMap.get(recipe.getUserId()).getName() +
                                        "\nYour recipe has been unlocked." +
                                        "\nRecipe Name: " + recipe.getName() +
                                        "\nThank you for your understanding and cooperation." +
                                        "\nBest regards, [Group 6] Team";
                                Notification notification = new Notification(recipe.getUserId(),title,content);
                                FirebaseDatabase
                                        .getInstance()
                                        .getReference(REALTIME_NOTIFICATIONS)
                                        .child(notification.getId())
                                        .setValue(notification)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(getContext(), "Recipe unlocked successfully.", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        });
                            }
                        });

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
    private void ShowUnlockConfirmationDialogUser(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Unlock Recipe");
            builder.setMessage("Are you sure you want to unlock this recipe?\n\nPlease ensure that this action complies with platform policies.");

        builder.setPositiveButton("Unlock", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.setMessage("Please wait ...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                user.setStatus(false);
                FirebaseDatabase
                        .getInstance()
                        .getReference(REALTIME_USERS)
                        .child(user.getId())
                        .setValue(user)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String accountUnlockTitle = "Your Account Has Been Unlocked";
                                String accountUnlockContent = "Dear " + userForRecipeHashMap.get(user.getId()).getName() +
                                        "\n\nWe are pleased to inform you that your account has been unlocked." +
                                        "\n\nThank you for your understanding and cooperation." +
                                        "\n\nBest regards,\n" +
                                        "The [Group 6] Team";
                                Notification notification = new Notification(user.getId(),accountUnlockTitle,accountUnlockContent);
                                FirebaseDatabase
                                        .getInstance()
                                        .getReference(REALTIME_NOTIFICATIONS)
                                        .child(notification.getId())
                                        .setValue(notification)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(getContext(), "Account unlocked successfully.", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        });
                            }
                        });

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
    private void fetchRecipes() {
        new FirebaseUtils().getDataFromFirebaseRealtime(MainActivity.REALTIME_RECIPES, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipes.clear();
                for (DataSnapshot child:snapshot.getChildren()) {
                    Recipe recipe = child.getValue(Recipe.class);
                    if (!recipe.isPublic()) continue;
                    recipes.add(recipe);
                    CompletableFuture<Void> mediaFuture = fetchMediaForRecipe(recipe);
                    CompletableFuture<Void> userFuture = fetchUserForRecipe(recipe);
                    futures.add(CompletableFuture.allOf(mediaFuture, userFuture));
                }

                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenRun(() -> {
//                    Toast.makeText(getContext(), userForRecipeHashMap.size() + " - " + mediaForUserHashMap.size(), Toast.LENGTH_SHORT).show();
                    viewModel.changeDataManageRecipe(recipes);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private CompletableFuture<Void> fetchUserForRecipe(Recipe recipe) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        if (userForRecipeHashMap.containsKey(recipe.getUserId()) && userForRecipeHashMap.get(recipe.getUserId()) != null) {
            fetchMediaForUser(userForRecipeHashMap.get(recipe.getUserId())).thenRun(() -> future.complete(null));
            return future;
        }
        new FirebaseUtils().getDataFromFirebaseById(MainActivity.REALTIME_USERS, recipe.getUserId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                userForRecipeHashMap.put(recipe.getUserId(),user);
                fetchMediaForUser(user).thenRun(() -> {
                    future.complete(null);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return future;
    }
    private CompletableFuture<Void> fetchMediaForRecipe(Recipe recipe) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        new FirebaseUtils().getDataFromFirebaseById(REALTIME_MEDIAS, recipe.getMediaId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Media media = snapshot.getValue(Media.class);
                mediaForRecipeHashMap.put(recipe.getId(),media);
                future.complete(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return future;
    }
    private CompletableFuture<Void> fetchMediaForUser(User user) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        if (user.getMediaId() == null || user.getMediaId().isEmpty()) {
            future.complete(null);
            return future;
        }
        if (mediaForUserHashMap.containsKey(user.getMediaId()) && mediaForUserHashMap.get(user.getMediaId()) != null) {
            future.complete(null);
            return future;
        }
        new FirebaseUtils().getDataFromFirebaseById(REALTIME_MEDIAS, user.getMediaId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Media media = snapshot.getValue(Media.class);
                mediaForUserHashMap.put(media.getId(),media);
                future.complete(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return future;
    }
    private void initUI(View view) {
        progressDialog = new ProgressDialog(getContext(),R.style.AppCompatAlertDialogStyle);
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
        viewPager2_recipe = view.findViewById(R.id.viewPager2_recipe);
        bottomNavigationView = view.findViewById(R.id.bottomNavigationViewAdmin);
    }
}