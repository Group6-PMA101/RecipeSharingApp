package com.ph41626.pma101_recipesharingapplication.Fragment;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
import com.ph41626.pma101_recipesharingapplication.Activity.MainActivity;
import com.ph41626.pma101_recipesharingapplication.Model.Notification;
import com.ph41626.pma101_recipesharingapplication.Model.ViewModel;
import com.ph41626.pma101_recipesharingapplication.R;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllNotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllNotificationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AllNotificationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AllNotificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AllNotificationFragment newInstance(String param1, String param2) {
        AllNotificationFragment fragment = new AllNotificationFragment();
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
    private TextView tv_empty_notification,tv_no_notification;
    private LinearLayout layout_notification_today,layout_notification;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_notification, container, false);

        initUI(view);
        viewModel.getChangeNotification().observe(getViewLifecycleOwner(), new Observer<ArrayList<Notification>>() {
            @Override
            public void onChanged(ArrayList<Notification> notifications) {
                if (notifications == null || notifications.isEmpty()) {
                    tv_empty_notification.setVisibility(View.VISIBLE);
                } else {
                    layout_notification_today.removeAllViews();
                    layout_notification.removeAllViews();
                    tv_empty_notification.setVisibility(View.GONE);

                    for (Notification notification:notifications) {
                        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_notification,null,false);
                        TextView tv_title = view.findViewById(R.id.tv_title),
                                tv_content = view.findViewById(R.id.tv_content);
                        ImageView img_status = view.findViewById(R.id.img_status);

                        if (notification.isStatus()) {
                            img_status.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.green_F56)));
                        }
                        view.setOnClickListener(v -> {
                            notification.setStatus(true);
                            FirebaseDatabase
                                    .getInstance()
                                    .getReference(MainActivity.REALTIME_NOTIFICATIONS)
                                    .child(notification.getId())
                                    .setValue(notification);
                            showDetailsDialog(notification);
                        });
                        tv_title.setText(notification.getTitle());
                        tv_content.setText(notification.getContent());
                        if (isToday(notification.getDate())) {
                            layout_notification_today.addView(view);
                        } else {
                            layout_notification.addView(view);
                        }
                    }
                    if (layout_notification_today.getChildCount() == 0) {
                        tv_empty_notification.setVisibility(View.VISIBLE);
                    } else {
                        tv_empty_notification.setVisibility(View.GONE);
                    }
                    if (layout_notification.getChildCount() == 0) {
                        tv_no_notification.setVisibility(View.VISIBLE);
                    } else {
                        tv_no_notification.setVisibility(View.GONE);
                    }
                }
            }
        });

        return view;
    }

    private void showDetailsDialog(Notification notification) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(notification.getTitle());
        builder.setMessage(notification.getContent());
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    public boolean isToday(Date createDate) {
        LocalDate today = LocalDate.now();
        LocalDate createDateLocal = createDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return today.equals(createDateLocal);
    }
    private void initUI(View view) {
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
        tv_empty_notification = view.findViewById(R.id.tv_empty_notification);
        tv_no_notification = view.findViewById(R.id.tv_no_notifications);

        layout_notification_today = view.findViewById(R.id.layout_notification_today);
        layout_notification = view.findViewById(R.id.layout_notification);
    }
}