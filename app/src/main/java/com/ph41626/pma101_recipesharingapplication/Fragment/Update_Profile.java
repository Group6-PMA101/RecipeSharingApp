package com.ph41626.pma101_recipesharingapplication.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ph41626.pma101_recipesharingapplication.R;

import java.util.HashMap;
import java.util.Map;

public class Update_Profile extends AppCompatActivity {

    private ImageButton btnBack, btnChangePicture;
    private ImageView imgProfile;
    private EditText etName, etEmail, etPassword;
    private Button btnSave, btnudpass;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        // Initialize views
        btnBack = findViewById(R.id.btn_back);
        btnChangePicture = findViewById(R.id.btn_change_picture);
        imgProfile = findViewById(R.id.img_profile);
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnSave = findViewById(R.id.btn_save);
        btnudpass=findViewById(R.id.btn_ud_pass);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("REALTIME_USERS");

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // User is signed in
            String email = user.getEmail();
            String uid = user.getUid();

            // Set email
            etEmail.setText(email);

            // Retrieve and set user's name
            retrieveUserNameByEmail(email);
        } else {
            // No user is signed in
            // Handle the error
            Toast.makeText(this, "No user is signed in", Toast.LENGTH_SHORT).show();
        }

        // Set onClick listeners
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnChangePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle change picture
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle save changes
                updateUserInfo();
            }
        });



        btnudpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdatePasswordDialog();
            }
        });
    }

    private void showUpdatePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_update_password, null);
        builder.setView(dialogView);

        final EditText etOldPassword = dialogView.findViewById(R.id.et_old_password);
        final EditText etNewPassword = dialogView.findViewById(R.id.et_new_password);
        Button btnUpdatePassword = dialogView.findViewById(R.id.btn_update_password);

        final AlertDialog dialog = builder.create();

        btnUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = etOldPassword.getText().toString().trim();
                String newPassword = etNewPassword.getText().toString().trim();

                if (!oldPassword.isEmpty() && !newPassword.isEmpty()) {
                    updatePassword(oldPassword, newPassword);
                    dialog.dismiss();
                } else {
                    // Show error message
                }
            }
        });

        dialog.show();
    }

    private void updatePassword(String oldPassword, String newPassword) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);

            // Xác thực lại người dùng
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Cập nhật mật khẩu
                    user.updatePassword(newPassword).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            // Cập nhật mật khẩu thành công

                            etPassword.setText(newPassword);
                            Toast.makeText(Update_Profile.this, "Cập nhật mật khẩu thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            // Cập nhật mật khẩu thất bại
                            Toast.makeText(Update_Profile.this, "Cập nhật mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Xác thực thất bại
                    Toast.makeText(Update_Profile.this, "Xác thực thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void retrieveUserNameByEmail(String email) {
        Query query = mDatabase.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String name = userSnapshot.child("name").getValue(String.class);

                         id = userSnapshot.child("id").getValue(String.class);

                        etName.setText(name);
                        break; // Assuming email is unique, break after the first match
                    }
                } else {
                    // No user found
                    Toast.makeText(Update_Profile.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(Update_Profile.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void updateUserInfo() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {

            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            Map<String, Object> userUpdates = new HashMap<>();
            userUpdates.put("name", name);
            userUpdates.put("email", email);
            userUpdates.put("password", pass);

            mDatabase.child(id).updateChildren(userUpdates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(Update_Profile.this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Update_Profile.this, "Cập nhật thông tin thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
