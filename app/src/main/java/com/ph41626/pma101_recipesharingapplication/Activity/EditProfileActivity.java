package com.ph41626.pma101_recipesharingapplication.Activity;

import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_MEDIAS;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_USERS;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.STORAGE_MEDIAS;
import static com.ph41626.pma101_recipesharingapplication.Services.Services.RandomID;
import static com.ph41626.pma101_recipesharingapplication.Services.UserPreferences.GetUser;
import static com.ph41626.pma101_recipesharingapplication.Services.UserPreferences.SaveUser;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ph41626.pma101_recipesharingapplication.Model.Instruction;
import com.ph41626.pma101_recipesharingapplication.Model.Media;
import com.ph41626.pma101_recipesharingapplication.Model.User;
import com.ph41626.pma101_recipesharingapplication.R;
import com.ph41626.pma101_recipesharingapplication.Services.EditProfileEventListener;
import com.ph41626.pma101_recipesharingapplication.Services.FirebaseUtils;
import com.ph41626.pma101_recipesharingapplication.Services.RecipeDetailEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_MEDIA_REQUEST = 1;
    private EditText edt_name,edt_email,edt_password;
    private ImageView img_user_avatar;
    private Button btn_save,btn_back;
    private Media media = new Media();
    private User user = new User();
    private StorageReference storageReference;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mUser = mAuth.getCurrentUser();
    private boolean isMedia = false; //True: Selected Avatar -> Update
    private boolean isUpdate = false; //True: Update Avatar; False -> Add New Avatar
    private static EditProfileEventListener eventListener;
    private ProgressDialog progressDialog;
    private List<CompletableFuture<Void>> futures = new ArrayList<>();
    public static void setEditProfileEventListener(EditProfileEventListener listener) {
        eventListener = listener;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        user = GetUser(this);
        initUI();
        SetUpButton();
        SetUpUI();
    }

    private void SetUpUI() {
        edt_name.setText(user.getName());
        edt_email.setText(user.getEmail());
        edt_password.setText(user.getPassword());

        if (user.getMediaId() == null || user.getMediaId().isEmpty()) {
            media.setId(RandomID());
            isUpdate = false;
            img_user_avatar.setImageResource(R.drawable.default_avatar);
        } else {
            isUpdate = true;
            new FirebaseUtils().getDataFromFirebaseById(MainActivity.REALTIME_MEDIAS, GetUser(this).getMediaId(), new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    media = snapshot.getValue(Media.class);
                    Glide.with(EditProfileActivity.this).
                            load(media.getUrl()).
                            error(R.drawable.default_avatar).
                            placeholder(R.drawable.default_avatar).
                            into(img_user_avatar);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    private void SetUpButton() {
        img_user_avatar.setOnClickListener(v -> {
            ChooseImage();
        });
        btn_save.setOnClickListener(v -> {
            String name = edt_name.getText().toString().trim();
            String password = edt_password.getText().toString().trim();

            if(!ValidateForm(name,password)) return;
            progressDialog.setMessage("Please wait ...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            user.setName(name);
            user.setPassword(password);

            CompletableFuture<Void> future = new CompletableFuture<>();
            futures.add(future);
            if (isMedia) {
                if (isUpdate) {
                    storageReference
                            .child(media.getName())
                            .delete()
                            .addOnSuccessListener(storageTask -> {
                                UpdateAvatar(future);
                            });
                } else {
                    user.setMediaId(media.getId());
                    UpdateAvatar(future);
                }
            } else {
                UpdateUser(future);
            }
            CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

            allOf.thenRun(() -> {
                mUser.updatePassword(password)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(this, "Update Completed.", Toast.LENGTH_SHORT).show();
                                        SaveUser(this,user);
                                        progressDialog.dismiss();
                                        eventListener.changeData();
                                        finish();
                                    }
                                });
            }).exceptionally(e -> {
                e.printStackTrace();
                return null;
            });
        });
        btn_back.setOnClickListener(v -> {
            finish();
        });
    }

    private void UpdateAvatar(CompletableFuture<Void> future) {
        Uri uri = Uri.parse(media.getUrl());
        String fileName = System.currentTimeMillis() + "." + GetFileExtension(uri);
        media.setName(fileName);

        StorageReference storageRef = storageReference.child(fileName);
        storageRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            media.setUrl(uri.toString());
                            media.setUpload(true);
                            FirebaseDatabase.getInstance()
                                    .getReference(REALTIME_MEDIAS)
                                    .child(media.getId())
                                    .setValue(media)
                                    .addOnCompleteListener(task -> {
                                        UpdateUser(future);
                                    });
                        }
                    });
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(this, "Upload failed for " + media.getName() + ": " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void UpdateUser(CompletableFuture<Void> future) {
        FirebaseDatabase.getInstance()
                .getReference(REALTIME_USERS)
                .child(user.getId())
                .setValue(user)
                .addOnCompleteListener(task -> {
                    future.complete(null);
                });
    }

    private String GetFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mine = MimeTypeMap.getSingleton();
        return uri != null ? mine.getExtensionFromMimeType(contentResolver.getType(uri)):null;
    }
    public void ChooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_MEDIA_REQUEST);
    }
    private boolean ValidateForm(String name, String password) {
        if (!ValidateField(name, "Name", edt_name)) return false;
        if (!ValidatePassword(password)) return false;
        return true;
    }
    private boolean ValidatePassword(String password) {
        if (password.isEmpty()) {
            edt_password.setError("Password cannot be empty!");
            return false;
        }

        if (password.length() < 8) {
            edt_password.setError("Password must be at least 8 characters long!");
            return false;
        }

        if (!password.matches(".*[A-Z].*")) {
            edt_password.setError("Password must contain at least one uppercase letter!");
            return false;
        }

        if (!password.matches(".*\\d.*")) {
            edt_password.setError("Password must contain at least one digit!");
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_MEDIA_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            isMedia = true;
            Uri selectedImageUri = data.getData();
            media.setUrl(selectedImageUri.toString());

            Glide.with(this)
                    .load(media.getUrl())
                    .error(R.drawable.default_avatar)
                    .placeholder(R.drawable.default_avatar)
                    .into(img_user_avatar);

        }
    }

    private void initUI() {
        storageReference = FirebaseStorage.getInstance().getReference(STORAGE_MEDIAS);
        edt_name = findViewById(R.id.edt_name);
        edt_email = findViewById(R.id.edt_email);
        edt_password = findViewById(R.id.edt_password);

        img_user_avatar = findViewById(R.id.img_user_avatar);

        btn_save = findViewById(R.id.btn_save);
        btn_back = findViewById(R.id.btn_back);

        progressDialog = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
    }
}