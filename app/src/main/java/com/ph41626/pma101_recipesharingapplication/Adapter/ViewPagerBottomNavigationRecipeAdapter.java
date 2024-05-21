package com.ph41626.pma101_recipesharingapplication.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.ph41626.pma101_recipesharingapplication.Fragment.AllRecipesFragment;
import com.ph41626.pma101_recipesharingapplication.Fragment.SharedRecipesFragment;
import com.ph41626.pma101_recipesharingapplication.Fragment.UnsharedRecipesFragment;

public class ViewPagerBottomNavigationRecipeAdapter extends FragmentStateAdapter {

    public ViewPagerBottomNavigationRecipeAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new AllRecipesFragment();
            case 1: return new SharedRecipesFragment();
            case 2: return new UnsharedRecipesFragment();
            default:break;
        }
        return new AllRecipesFragment();
    }
    @Override
    public int getItemCount() {
        return 3;
    }
}
