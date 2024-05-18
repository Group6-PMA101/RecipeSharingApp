package com.ph41626.pma101_recipesharingapplication.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.ph41626.pma101_recipesharingapplication.Fragment.CreateRecipeFragment;
import com.ph41626.pma101_recipesharingapplication.Fragment.HomeFragment;
import com.ph41626.pma101_recipesharingapplication.Fragment.NotificationsFragment;
import com.ph41626.pma101_recipesharingapplication.Fragment.ProfileFragment;
import com.ph41626.pma101_recipesharingapplication.Fragment.SavedRecipesFragment;

public class ViewPagerBottomNavigationMainAdapter extends FragmentStateAdapter {

    public ViewPagerBottomNavigationMainAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new HomeFragment();
            case 1: return new SavedRecipesFragment();
            case 2: return new CreateRecipeFragment();
            case 3: return new NotificationsFragment();
            case 4: return new ProfileFragment();
            default:break;
        }
        return new HomeFragment();
    }
    @Override
    public int getItemCount() {
        return 5;
    }
}
