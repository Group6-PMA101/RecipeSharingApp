package com.ph41626.pma101_recipesharingapplication.Activity;

import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_USERS;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ph41626.pma101_recipesharingapplication.Model.User;
import com.ph41626.pma101_recipesharingapplication.R;
import com.ph41626.pma101_recipesharingapplication.Services.FirebaseUtils;

public class SignInActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private LinearLayout btn_goToSignUp;
    private CheckBox rememberMeCheckBox;
    private Button btn_login;
    private TextView forgot_password;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;

    private static final String PREFS_NAME = "prefs";
    private static final String PREF_EMAIL = "email";
    private static final String PREF_PASSWORD = "password";
    private static final String PREF_REMEMBER_ME = "remember_me";

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initUI();
        loadPreferences();
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSignInClick();
            }
        });
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onForgotPasswordClick();
            }
        });
        btn_goToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextPage();
            }
        });

    }

    private void initUI() {
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        rememberMeCheckBox = findViewById(R.id.remember_me);
        btn_goToSignUp = findViewById(R.id.btn_goToSignUp);
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        btn_login = findViewById(R.id.sign_in);
        forgot_password = findViewById(R.id.forgot_password);
    }

    private void nextPage(){
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    private void loadPreferences() {
        boolean rememberMe = sharedPreferences.getBoolean(PREF_REMEMBER_ME, false);
        rememberMeCheckBox.setChecked(rememberMe);

        if (rememberMe) {
            String email = sharedPreferences.getString(PREF_EMAIL, "");
            String password = sharedPreferences.getString(PREF_PASSWORD, "");
            emailEditText.setText(email);
            passwordEditText.setText(password);
        }
    }

    private void savePreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (rememberMeCheckBox.isChecked()) {
            editor.putString(PREF_EMAIL, emailEditText.getText().toString().trim());
            editor.putString(PREF_PASSWORD, passwordEditText.getText().toString().trim());
            editor.putBoolean(PREF_REMEMBER_ME, true);
        } else {
            editor.putBoolean(PREF_REMEMBER_ME, false);
            editor.remove(PREF_EMAIL);
            editor.remove(PREF_PASSWORD);
        }

        editor.apply();
    }

    public void onSignInClick() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (!ValidateForm(email,password)) return;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            savePreferences();

                            new FirebaseUtils().getUserByEmail(REALTIME_USERS, email, new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    User user = snapshot.getValue(User.class);
                                    Toast.makeText(SignInActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                    intent.putExtra("user", user);
                                    startActivity(intent);
                                    finish();
                                    progressDialog.dismiss();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(SignInActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            });
                        } else {
                            Toast.makeText(SignInActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }
    private boolean ValidateForm(String email, String password) {
        if (!ValidateEmail(email)) return false;
        if (!ValidatePassword(password)) return false;
        return true;
    }
    private boolean ValidateEmail(String email) {
        if (email.isEmpty()) {
            emailEditText.setError("Email cannot be empty!");
            return false;
        }
        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}";

        if (!email.matches(emailPattern)) {
            emailEditText.setError("Invalid email address");
            return false;
        }
        return true;
    }
    private boolean ValidatePassword(String password) {
        if (password.isEmpty()) {
            passwordEditText.setError("Password cannot be empty!");
            return false;
        }

        if (password.length() < 8) {
            passwordEditText.setError("Password must be at least 8 characters long!");
            return false;
        }

        if (!password.matches(".*[A-Z].*")) {
            passwordEditText.setError("Password must contain at least one uppercase letter!");
            return false;
        }

        if (!password.matches(".*[a-z].*")) {
            passwordEditText.setError("Password must contain at least one lowercase letter!");
            return false;
        }

        return true;
    }
    public void onForgotPasswordClick() {
        String email = emailEditText.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập email của bạn", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignInActivity.this, "Email đặt lại mật khẩu đã được gửi", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignInActivity.this, "Gửi email thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}