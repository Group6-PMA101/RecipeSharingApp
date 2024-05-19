package com.ph41626.pma101_recipesharingapplication.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.ph41626.pma101_recipesharingapplication.R;

public class SignUpScreen extends AppCompatActivity {

    private EditText nameEditText, emailEditText, passwordEditText;
    private Spinner roleSpinner;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        nameEditText = findViewById(R.id.name);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        roleSpinner = findViewById(R.id.chef_spinner);

        mAuth = FirebaseAuth.getInstance();

        // Thiết lập dữ liệu cho Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.roles_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        findViewById(R.id.sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
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
        Intent intent = new Intent(SignUpScreen.this,SignInScreen.class);
        startActivity(intent);
    }
    private void registerUser() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String role = roleSpinner.getSelectedItem().toString();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        } else {
            // Thực hiện đăng ký tài khoản với Firebase
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Đăng ký thành công, cập nhật UI hoặc thông báo cho người dùng
                                Toast.makeText(SignUpScreen.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(SignUpScreen.this, SignInScreen.class);
                                startActivity(i);
                            } else {
                                // Nếu đăng ký thất bại, hiển thị thông báo cho người dùng.
                                Toast.makeText(SignUpScreen.this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public void onLoginClick(View view) {
        // Xử lý khi người dùng nhấn vào "Đăng nhập"
        Toast.makeText(this, "Đăng nhập", Toast.LENGTH_SHORT).show();
    }
}
