package com.ph41626.pma101_recipesharingapplication.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.ph41626.pma101_recipesharingapplication.Fragment.AllNotificationFragment;
import com.ph41626.pma101_recipesharingapplication.Fragment.ReadNotificationFragment;
import com.ph41626.pma101_recipesharingapplication.Fragment.UnReadNotificationFragment;

public class ViewPagerBottomNavigationNotificationAdapter extends FragmentStateAdapter {

    public ViewPagerBottomNavigationNotificationAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new AllNotificationFragment();
            case 1: return new UnReadNotificationFragment();
            case 2: return new ReadNotificationFragment();

            default:break;
        }
        return new AllNotificationFragment();
    }
    @Override
    public int getItemCount() {
        return 3;
    }
}
