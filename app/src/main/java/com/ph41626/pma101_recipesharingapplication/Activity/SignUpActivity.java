package com.ph41626.pma101_recipesharingapplication.Activity;

import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_MEDIAS;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_USERS;
import static com.ph41626.pma101_recipesharingapplication.Services.Services.RandomID;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ph41626.pma101_recipesharingapplication.Adapter.AccountTypeAdapter;
import com.ph41626.pma101_recipesharingapplication.Model.User;
import com.ph41626.pma101_recipesharingapplication.R;

public class SignUpActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, passwordEditText;
    private LinearLayout btn_goToLogin;
    private Button btn_signUp;
    private Spinner roleSpinner;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initUI();
        databaseReference = FirebaseDatabase.getInstance().getReference(REALTIME_USERS);

        AccountTypeAdapter accountTypeAdapter = new AccountTypeAdapter(this,R.layout.item_account_type,getResources().getTextArray(R.array.roles_array));
        roleSpinner.setAdapter(accountTypeAdapter);
        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
        btn_goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void registerUser() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        int role = roleSpinner.getSelectedItemPosition();

        if (!ValidateForm(name,email,password)) return;

        User newUser = new User(RandomID(),name,email,password,role);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        databaseReference.child(newUser.getId()).setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignUpActivity.this, "Create account successful!", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(SignUpActivity.this, SignInActivity.class);
                                    startActivity(i);
                                } else {
                                    Toast.makeText(SignUpActivity.this, "Failed to create account!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                progressDialog.dismiss();
                            }
                        });
                    }
                });
    }
    private boolean ValidateForm(String name, String email, String password) {
        if (!ValidateField(name, "Name", nameEditText)) return false;
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

        if (!password.matches(".*\\d.*")) {
            passwordEditText.setError("Password must contain at least one digit!");
            return false;
        }

        return true;
    }
    private boolean ValidateField(String fieldValue, String fieldName, EditText editText) {
        if (fieldValue.isEmpty()) {
            editText.setError(fieldName + " cannot be empty!");
            return false;
        }
        return true;
    }
    private void initUI() {
        nameEditText = findViewById(R.id.name);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        roleSpinner = findViewById(R.id.chef_spinner);
        btn_goToLogin = findViewById(R.id.btn_goToLogin);
        btn_signUp = findViewById(R.id.sign_up);
        mAuth = FirebaseAuth.getInstance();

    }
}
