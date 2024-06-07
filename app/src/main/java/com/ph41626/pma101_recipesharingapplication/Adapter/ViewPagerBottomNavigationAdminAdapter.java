package com.ph41626.pma101_recipesharingapplication.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.ph41626.pma101_recipesharingapplication.Fragment.AdminFragment;
import com.ph41626.pma101_recipesharingapplication.Fragment.CreateRecipeFragment;
import com.ph41626.pma101_recipesharingapplication.Fragment.HomeFragment;
import com.ph41626.pma101_recipesharingapplication.Fragment.ManageRecipesFragment;
import com.ph41626.pma101_recipesharingapplication.Fragment.ManageUserFragment;
import com.ph41626.pma101_recipesharingapplication.Fragment.NotificationsFragment;
import com.ph41626.pma101_recipesharingapplication.Fragment.ProfileFragment;
import com.ph41626.pma101_recipesharingapplication.Fragment.SavedRecipesFragment;

public class ViewPagerBottomNavigationAdminAdapter extends FragmentStateAdapter {

    public ViewPagerBottomNavigationAdminAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new ManageRecipesFragment();
            case 1: return new ManageUserFragment();
            default:break;
        }
        return new ManageRecipesFragment();
    }
    @Override
    public int getItemCount() {
        return 2;
    }
}
