package com.ph41626.pma101_recipesharingapplication.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ph41626.pma101_recipesharingapplication.Adapter.RecyclerViewManageUserAdapter;
import com.ph41626.pma101_recipesharingapplication.Model.User;
import com.ph41626.pma101_recipesharingapplication.Model.ViewModel;
import com.ph41626.pma101_recipesharingapplication.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ManageUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManageUserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ManageUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ManageUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ManageUserFragment newInstance(String param1, String param2) {
        ManageUserFragment fragment = new ManageUserFragment();
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
    private ViewModel viewModel;
    private AdminFragment adminFragment;
    private RecyclerView rcv_manage_user;
    private RecyclerViewManageUserAdapter manageUserAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manage_user, container, false);

        initUI(view);
        SetUpRecyclerView();
        viewModel.getManageUser().observe(getViewLifecycleOwner(), new Observer<HashMap<String, User>>() {
            @Override
            public void onChanged(HashMap<String, User> stringUserHashMap) {
                ArrayList<User> users = new ArrayList<>(stringUserHashMap.values());
                manageUserAdapter.Update(users);
            }
        });

        return view;
    }

    private void SetUpRecyclerView() {
        manageUserAdapter = new RecyclerViewManageUserAdapter(getContext(),new ArrayList<>(),adminFragment);
        rcv_manage_user.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        rcv_manage_user.setAdapter(manageUserAdapter);
    }

    private void initUI(View view) {
        rcv_manage_user = view.findViewById(R.id.rcv_manage_user);
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