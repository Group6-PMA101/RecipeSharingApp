package com.ph41626.pma101_recipesharingapplication.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.ph41626.pma101_recipesharingapplication.R;

public class SignInScreen extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private CheckBox rememberMeCheckBox;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;

    private static final String PREFS_NAME = "prefs";
    private static final String PREF_EMAIL = "email";
    private static final String PREF_PASSWORD = "password";
    private static final String PREF_REMEMBER_ME = "remember_me";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_screen);

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        rememberMeCheckBox = findViewById(R.id.remember_me);

        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        loadPreferences();

        findViewById(R.id.sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignInClick();
            }
        });

        findViewById(R.id.forgot_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onForgotPasswordClick();
            }
        });

        findViewById(R.id.textNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextPage();
            }
        });

    }

    private void nextPage(){
        Intent intent = new Intent(SignInScreen.this,SignUpScreen.class);
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

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                savePreferences();
                                Toast.makeText(SignInScreen.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignInScreen.this, MainActivity.class);
                                startActivity(intent);
                                finish(); // Đóng activity hiện tại sau khi đăng nhập thành công
                            } else {
                                Toast.makeText(SignInScreen.this, "Đăng nhập thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
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
                                Toast.makeText(SignInScreen.this, "Email đặt lại mật khẩu đã được gửi", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignInScreen.this, "Gửi email thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public void onSignUpClick(View view) {
        Intent intent = new Intent(this, SignUpScreen.class);
        startActivity(intent);
    }
}
