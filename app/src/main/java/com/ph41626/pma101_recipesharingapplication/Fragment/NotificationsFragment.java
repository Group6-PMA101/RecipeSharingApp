package com.ph41626.pma101_recipesharingapplication.Fragment;

import static com.ph41626.pma101_recipesharingapplication.Services.UserPreferences.GetUser;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ph41626.pma101_recipesharingapplication.Activity.MainActivity;
import com.ph41626.pma101_recipesharingapplication.Adapter.ViewPagerBottomNavigationAdminAdapter;
import com.ph41626.pma101_recipesharingapplication.Adapter.ViewPagerBottomNavigationNotificationAdapter;
import com.ph41626.pma101_recipesharingapplication.Model.Notification;
import com.ph41626.pma101_recipesharingapplication.Model.ViewModel;
import com.ph41626.pma101_recipesharingapplication.R;
import com.ph41626.pma101_recipesharingapplication.Services.FirebaseUtils;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationsFragment newInstance(String param1, String param2) {
        NotificationsFragment fragment = new NotificationsFragment();
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
    public ViewPager2 viewPager2_recipe;
    private ViewModel viewModel;
    private BottomNavigationView bottomNavigationView;
    private ArrayList<Notification> notifications = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        initUI(view);
        BottomNavigationManager();
        fetNotificationForUser();

        return view;
    }

    private void fetNotificationForUser() {
        new FirebaseUtils().getAllDataByKeyRealTime(MainActivity.REALTIME_NOTIFICATIONS, "userId", GetUser(getContext()).getId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notifications.clear();
                for (DataSnapshot child:snapshot.getChildren()) {
                    Notification notification = child.getValue(Notification.class);
                    notifications.add(notification);
                }

                viewModel.changeNotificationForUser(notifications);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void BottomNavigationManager() {
        ViewPagerBottomNavigationNotificationAdapter bottomNavigationAdapter = new ViewPagerBottomNavigationNotificationAdapter(getActivity());
        viewPager2_recipe.setAdapter(bottomNavigationAdapter);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.allNotification) {
                    viewPager2_recipe.setCurrentItem(0);
                } else if(item.getItemId() == R.id.unreadNotification) {
                    viewPager2_recipe.setCurrentItem(1);
                } else if(item.getItemId() == R.id.readNotification) {
                    viewPager2_recipe.setCurrentItem(2);
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
                        bottomNavigationView.setSelectedItemId(R.id.allNotification);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.unreadNotification);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.readNotification);
                        break;
                }
            }
        });
    }
    private void initUI(View view) {
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
        viewPager2_recipe = view.findViewById(R.id.viewPager2_recipe);
        bottomNavigationView = view.findViewById(R.id.bottomNavigationViewNotification);
    }
}