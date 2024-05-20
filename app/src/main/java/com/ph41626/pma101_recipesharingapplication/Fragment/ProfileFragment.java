package com.ph41626.pma101_recipesharingapplication.Fragment;

import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_MEDIAS;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ph41626.pma101_recipesharingapplication.Activity.MainActivity;
import com.ph41626.pma101_recipesharingapplication.Model.Media;
import com.ph41626.pma101_recipesharingapplication.Model.User;
import com.ph41626.pma101_recipesharingapplication.Model.ViewModel;
import com.ph41626.pma101_recipesharingapplication.R;
import com.ph41626.pma101_recipesharingapplication.Services.FirebaseUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
    private ImageView img_avatar_user;
    private TextView tv_name_user, tv_recipes_count_user, tv_follower_count_user, tv_following_count_user;
    private MainActivity mainActivity;
    private ViewModel viewModel;

    private static User currentUser = new User();
    private Media currentMedia = new Media();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initUI(view);

        UpdateUiWhenDataChange();

        return view;
    }

    private void UpdateUiWhenDataChange() {
        viewModel.getChangeDateCurrentUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                currentUser = user;
                new FirebaseUtils().getDataFromFirebaseById(REALTIME_MEDIAS, user.getMediaId(), new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        currentMedia = snapshot.getValue(Media.class);
                        UpdateUi(currentUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    private void UpdateUi(User user) {
        if (user.getMediaId() == null) {
            Glide.with(getContext()).
                    load(currentMedia.getUrl()).
                    error(R.drawable.default_avatar).
                    placeholder(R.drawable.default_avatar).
                    into(img_avatar_user);
        } else {
            img_avatar_user.setImageResource(R.drawable.default_avatar);
        }
        tv_name_user.setText(user.getName());
        tv_follower_count_user.setText(String.valueOf(user.getFollowersCount()));
        tv_following_count_user.setText(String.valueOf(user.getFollowingCount()));
    }

    private void initUI(View view) {
        img_avatar_user = view.findViewById(R.id.img_avatar_user);
        tv_name_user = view.findViewById(R.id.tv_name_user);
        tv_recipes_count_user = view.findViewById(R.id.tv_recipes_count_user);
        tv_follower_count_user = view.findViewById(R.id.tv_follower_count_user);
        tv_following_count_user = view.findViewById(R.id.tv_following_count_user);
        mainActivity = (MainActivity) getActivity();
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
    }
}