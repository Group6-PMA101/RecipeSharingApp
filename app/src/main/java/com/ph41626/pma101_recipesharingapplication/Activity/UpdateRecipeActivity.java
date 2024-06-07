package com.ph41626.pma101_recipesharingapplication.Activity;

import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_INGREDIENTS;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_INSTRUCTIONS;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_MEDIAS;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_RECIPES;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.STORAGE_MEDIAS;
import static com.ph41626.pma101_recipesharingapplication.Services.Services.CreateNewIngredient;
import static com.ph41626.pma101_recipesharingapplication.Services.Services.CreateNewInstruction;
import static com.ph41626.pma101_recipesharingapplication.Services.Services.RandomID;
import static com.ph41626.pma101_recipesharingapplication.Services.Services.findObjectById;
import static com.ph41626.pma101_recipesharingapplication.Services.Services.isVideo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ph41626.pma101_recipesharingapplication.Adapter.RecyclerViewIngredientAdapter;
import com.ph41626.pma101_recipesharingapplication.Adapter.RecyclerViewInstructionAdapter;
import com.ph41626.pma101_recipesharingapplication.Model.Ingredient;
import com.ph41626.pma101_recipesharingapplication.Model.Instruction;
import com.ph41626.pma101_recipesharingapplication.Model.Media;
import com.ph41626.pma101_recipesharingapplication.Model.Recipe;
import com.ph41626.pma101_recipesharingapplication.R;
import com.ph41626.pma101_recipesharingapplication.Services.ImageDialogUtil;
import com.ph41626.pma101_recipesharingapplication.Services.OnItemIngredientListener;
import com.ph41626.pma101_recipesharingapplication.Services.OnItemInstructionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UpdateRecipeActivity extends AppCompatActivity {
    private static final int PICK_MEDIA_REQUEST = 1;
    private static final long MAX_VIDEO_SIZE_MB = 250;
    private Button
            btn_add_ingredient,
            btn_add_instruction,
            btn_create_recipes,
            btn_back;
    private ImageView img_thumbnail_recipe;
    private TextView tv_cook_time,tv_serves;
    private EditText edt_name;
    private LinearLayout btn_choose_media,btn_play,btn_cook_time,btn_serves;
    private RecyclerView rcv_ingredient,rcv_instruction;

    private ArrayList<Ingredient> ingredientList;
    private ArrayList<Instruction> instructionList;
    private ArrayList<Ingredient> ingredientListRemove = new ArrayList<>();
    private ArrayList<Instruction> instructionListRemove = new ArrayList<>();
    private ArrayList<Media> mediaListRemove = new ArrayList<>();
    public ArrayList<Media> mediaList;

    private RecyclerViewIngredientAdapter ingredient_adapter;
    private RecyclerViewInstructionAdapter instruction_adapter;

    private Recipe updateRecipe = new Recipe();
    private Media recipeMedia = new Media();
    private Instruction current_instruction;
    private ProgressDialog progressDialog;


    private int current_instruction_pos = -1;
    private boolean isThumbnailRecipeChosen = false;
    private String specialCharacters = "[^\"]+";

    private StorageReference storageReference;
    private DatabaseReference
            databaseReferenceMedias,
            databaseReferenceIngredients,
            databaseReferenceInstructions,
            databaseReferenceRecipes;

    private List<CompletableFuture<Void>> futures = new ArrayList<>();

    @Override
    protected void onStop() {
        super.onStop();
        setResult(RESULT_CANCELED);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_recipe);

        initUI();
        SetupButtonListeners();
        SetupRecyclerView();

        GetDataFromProfile();

    }
    private void SetupButtonListeners() {
        btn_play.setVisibility(View.GONE);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View currentFocus = getCurrentFocus();
                if (currentFocus != null && currentFocus.hasFocus()) currentFocus.clearFocus();
                ShowVideoAlertDialog(findObjectById(mediaList,updateRecipe.getMediaId()).getUrl());
            }
        });
        btn_choose_media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isThumbnailRecipeChosen = true;
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateRecipeActivity.this);
                builder.setTitle("Choose an option");
                builder.setItems(new CharSequence[]{"Choose Image", "Choose Video"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                ChooseImage(null,-1);
                                break;
                            case 1:
                                ChooseVideo();
                                break;
                            default: break;
                        }
                    }
                });
                builder.show();
            }
        });
        btn_create_recipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View currentFocus = getCurrentFocus();
                if (currentFocus != null && currentFocus.hasFocus()) currentFocus.clearFocus();
                String name = edt_name.getText().toString().trim();
                Date newDate = new Date();
                if (ValidateRecipe(name)) {
                    updateRecipe.setName(name);
                    updateRecipe.setLastUpdateDate(newDate);
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    SaveDataToFirebase();
                }
            }
        });
        btn_serves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowInputDialog(true,"Enter Serves", InputType.TYPE_CLASS_NUMBER);
            }
        });
        btn_cook_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowInputDialog(false,"Enter Cook Time", InputType.TYPE_CLASS_NUMBER);
            }
        });
        btn_add_ingredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View currentFocus = getCurrentFocus();
                if (currentFocus != null && currentFocus.hasFocus()) currentFocus.clearFocus();
                Ingredient newIngredient = CreateNewIngredient();
                newIngredient.setRecipeId(updateRecipe.getId());
                ingredientList.add(newIngredient);
                ingredient_adapter.UpdateData(true, ingredientList, ingredientList.size());
            }
        });
        btn_add_instruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View currentFocus = getCurrentFocus();
                if (currentFocus != null && currentFocus.hasFocus()) currentFocus.clearFocus();
                Instruction newInstruction = CreateNewInstruction();
                newInstruction.setRecipeId(updateRecipe.getId());
                instructionList.add(newInstruction);
                instruction_adapter.UpdateData(true, instructionList, instructionList.size());
            }
        });
    }
    private void SaveDataToFirebase() {
        if (!mediaListRemove.isEmpty()) {
            if (!recipeMedia.getId().equals(updateRecipe.getMediaId())) {
                mediaListRemove.add(recipeMedia);
            }
            for (Media media:mediaListRemove) {
                if (!media.isUpload()) continue;
                CompletableFuture<Void> future = new CompletableFuture<>();
                futures.add(future);
                storageReference.child(media.getName()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        databaseReferenceMedias.child(media.getId()).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                future.complete(null);
                            }
                        });
                    }
                });
            }
        }
        if (!ingredientListRemove.isEmpty()) {
            for (Ingredient ingredient:ingredientListRemove) {
                CompletableFuture<Void> future = new CompletableFuture<>();
                futures.add(future);

                DatabaseReference databaseReferenceRef = databaseReferenceIngredients.child(ingredient.getId());
                databaseReferenceRef.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        future.complete(null);
                    }
                });
            }
        }
        if (!instructionListRemove.isEmpty()) {
            for (Instruction instruction:instructionListRemove) {
                CompletableFuture<Void> future = new CompletableFuture<>();
                futures.add(future);
                DatabaseReference databaseReferenceRef = databaseReferenceInstructions.child(instruction.getId());
                databaseReferenceRef.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        future.complete(null);
                    }
                });
            }
        }

        CompletableFuture<Void> futureRecipe = new CompletableFuture<>();
        futures.add(futureRecipe);
        databaseReferenceRecipes.child(updateRecipe.getId()).setValue(updateRecipe).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                futureRecipe.complete(null);
            }
        });

        for (Media media:mediaList) {
            if (media.isUpload()) continue;
            CompletableFuture<Void> future = new CompletableFuture<>();
            futures.add(future);

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
                                databaseReferenceMedias.child(media.getId()).setValue(media);
                                future.complete(null);
                            }
                        });
                    })
                    .addOnFailureListener(exception -> {
                        Toast.makeText(this, "Upload failed for " + media.getName() + ": " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
        for (Ingredient ingredient:ingredientList) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            futures.add(future);

            DatabaseReference databaseReferenceRef = databaseReferenceIngredients.child(ingredient.getId());
            databaseReferenceRef.setValue(ingredient).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    future.complete(null);
                }
            });
        }
        for (Instruction instruction:instructionList) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            futures.add(future);

            DatabaseReference databaseReferenceRef = databaseReferenceInstructions.child(instruction.getId());
            databaseReferenceRef.setValue(instruction).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    future.complete(null);
                }
            });
        }

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allOf.thenRun(() -> {
            Toast.makeText(this, "Update Completed.", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            Intent resultIntent = new Intent();
            setResult(RESULT_OK, resultIntent);
            finish();
        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
    }
    private String GetFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mine = MimeTypeMap.getSingleton();
        return uri != null ? mine.getExtensionFromMimeType(contentResolver.getType(uri)):null;
    }
    private void ShowVideoAlertDialog(String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Watch Video");
        VideoView videoView = new VideoView(this);
        Uri videoUri = Uri.parse(url);
        videoView.setVideoURI(videoUri);

        builder.setView(videoView);
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                videoView.stopPlayback();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        videoView.start();
    }
    private void ShowInputDialog(boolean type,String title, final int inputType) {
        // Type = true -> Serves; Type = False -> Cook time
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        final EditText input = new EditText(this);
        input.setInputType(inputType);
        builder.setView(input);

        Drawable iconDrawable;

        if (type) iconDrawable = getResources().getDrawable(R.drawable.ic_profile);
        else iconDrawable = getResources().getDrawable(R.drawable.ic_clock);

        Drawable icon = iconDrawable.mutate();
        icon.setColorFilter(ContextCompat.getColor(this, R.color.red_E00), PorterDuff.Mode.SRC_IN);
        builder.setIcon(icon);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value = s.toString();
                if (isValidInteger(value)) {
                    input.setError(null);
                } else {
                    input.setError("Invalid value!");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = input.getText().toString();
                if (!isValidInteger(value)) return;

                if (type) {
                    updateRecipe.setServings(Integer.parseInt(value));
                    tv_serves.setText(value);
                } else {
                    updateRecipe.setCookTime(Integer.parseInt(value));
                    tv_cook_time.setText(value + " min");
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    private boolean isValidInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    private void SetupRecyclerView() {
        ingredient_adapter = new RecyclerViewIngredientAdapter(
                this,
                ingredientList,
                new OnItemIngredientListener() {
                    @Override
                    public void removeItemIngredient(Ingredient ingredient,int position) {
                        RemoveItemIngredient(ingredient,position);
                    }
                });
        instruction_adapter = new RecyclerViewInstructionAdapter(
                this,
                instructionList,
                new OnItemInstructionListener() {
                    @Override
                    public void removeItemInstruction(int position, Instruction instruction) {
                        ShowRemoveInstructionDialog(position,instruction);
                    }

                    @Override
                    public void removeItemMedia(String mediaId, Instruction instruction, int pos) {
                        RemoveItemThumbnail(mediaId,instruction,pos);
                    }

                    @Override
                    public void chooseImage(Instruction instruction, int pos) {
                        ChooseImage(instruction,pos);
                    }

                    @Override
                    public void imageViewDetail(String url) {
                        new ImageDialogUtil().ShowVideoDialog(UpdateRecipeActivity.this,url);
                    }

                    @Override
                    public ArrayList<Media> getMedias() {
                        return mediaList;
                    }
                }
        );
        rcv_ingredient.setLayoutManager(new GridLayoutManager(this,1));
        rcv_ingredient.setAdapter(ingredient_adapter);
        rcv_ingredient.setNestedScrollingEnabled(false);

        rcv_instruction.setLayoutManager(new GridLayoutManager(this,1));
        rcv_instruction.setAdapter(instruction_adapter);
        rcv_instruction.setNestedScrollingEnabled(false);
    }
    public void RemoveItemThumbnail(String mediaId,Instruction instruction, int instructionPos) {
        View currentFocus = getCurrentFocus();
        if (currentFocus != null && currentFocus.hasFocus()) currentFocus.clearFocus();

        Media media = findObjectById(mediaList,mediaId);
        mediaListRemove.add(media);
        mediaList.remove(media);

        ArrayList<String> mediaIds = instruction.getMediaIds();
        mediaIds.remove(mediaId);
        instruction.setMediaIds(mediaIds);

        instruction_adapter.notifyItemChanged(instructionPos,instruction);
    }
    public void ShowRemoveInstructionDialog (int pos, Instruction instruction) {
        SpannableString spannableString = new SpannableString("Delete");
        spannableString.setSpan(new ForegroundColorSpan(this.getResources().getColor(R.color.red_E00)), 0, spannableString.length(), 0);

        AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this instruction?");
        builder.setPositiveButton(spannableString, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RemoveItemInstruction(pos,instruction);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
    public void RemoveItemInstruction(int pos, Instruction instruction) {
        View currentFocus = getCurrentFocus();
        if (currentFocus != null && currentFocus.hasFocus()) currentFocus.clearFocus();
        if (instruction.getMediaIds() != null && !instruction.getMediaIds().isEmpty()) {
            for (String mediaId:instruction.getMediaIds()) {
                Media media = findObjectById(mediaList,mediaId);
                mediaListRemove.add(media);
                mediaList.remove(media);
            }
        }
        instructionListRemove.add(instruction);
        instructionList.remove(pos);
        instruction_adapter.UpdateData(false, instructionList,pos);
    }
    public void RemoveItemIngredient(Ingredient ingredient,int pos) {
        View currentFocus = getCurrentFocus();
        if (currentFocus != null && currentFocus.hasFocus()) currentFocus.clearFocus();
        ingredientListRemove.add(ingredient);
        ingredientList.remove(ingredient);
        ingredient_adapter.UpdateData(false, ingredientList,pos);
    }
    private void GetDataFromProfile() {
        Intent intent = getIntent();
        updateRecipe = (Recipe) intent.getSerializableExtra("recipe");

        Media media = (Media) intent.getSerializableExtra("recipeMedia");
        mediaList.add(media);
        recipeMedia = media;

        ingredientList = (ArrayList<Ingredient>) intent.getSerializableExtra("ingredients");
        instructionList = (ArrayList<Instruction>) intent.getSerializableExtra("instructions");
        Collections.sort(instructionList, new Comparator<Instruction>() {
            @Override
            public int compare(Instruction i1, Instruction i2) {
                return Integer.compare(i1.getOrder(), i2.getOrder());
            }
        });
        mediaList.addAll((ArrayList<Media>)intent.getSerializableExtra("instructionMedias"));
        UpdateUi();
    }
    private void UpdateUi() {
        edt_name.setText(updateRecipe.getName());
        tv_serves.setText(String.valueOf(updateRecipe.getServings()));
        tv_cook_time.setText(String.valueOf(updateRecipe.getCookTime()));
        if (isVideo(recipeMedia.getUrl())) {
            btn_play.setVisibility(View.VISIBLE);
        } else {
            btn_play.setVisibility(View.GONE);
        }
        Glide.with(this)
                .asBitmap()
                .load(findObjectById(mediaList,updateRecipe.getMediaId()).getUrl())
                .error(R.drawable.add_image)
                .into(img_thumbnail_recipe);
        ingredient_adapter.Reset(ingredientList);
        instruction_adapter.Reset(instructionList);
    }
    public void ChooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_MEDIA_REQUEST);
    }
    public void ChooseImage(Instruction instruction, int pos) {
        if (instruction != null && pos != -1) {
            if (instruction != null && instruction.getMediaIds() != null && instruction.getMediaIds().size() == 3) {
                Toast.makeText(this, "You can only add up to 3 images for each step!", Toast.LENGTH_SHORT).show();
                return;
            }
            current_instruction = instruction;
            current_instruction_pos = pos;
        } else {
            current_instruction = null;
            current_instruction_pos = -1;
        }
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_MEDIA_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_MEDIA_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            Uri selectedImageUri = data.getData();
            String mimeType = this.getContentResolver().getType(selectedImageUri);
            Media media = new Media();
            media.setId(RandomID());
            media.setName("IMAGE");
            media.setUrl(selectedImageUri.toString());
            mediaList.add(media);

            if (mimeType != null && mimeType.startsWith("image/")) {
                if (current_instruction != null && current_instruction_pos != -1) {
                    ArrayList<String> getMediaIDs = current_instruction.getMediaIds();
                    getMediaIDs.add(media.getId());
                    current_instruction.setMediaIds(getMediaIDs);
                    instruction_adapter.notifyItemChanged(current_instruction_pos,current_instruction);
                } else {
                    if (isThumbnailRecipeChosen) {
                        if (btn_play.getVisibility() == View.VISIBLE) {
                            btn_play.setVisibility(View.GONE);
                        }
                        if (updateRecipe.getMediaId() != null && !updateRecipe.getMediaId().trim().isEmpty()) {
                            mediaList.remove(findObjectById(mediaList,updateRecipe.getMediaId()));
                        }
                        updateRecipe.setMediaId(media.getId());
                        Glide.with(this).load(media.getUrl()).error(R.drawable.add_image).into(img_thumbnail_recipe);
                        isThumbnailRecipeChosen = false;
                    }
                }
            } else if (mimeType != null && mimeType.startsWith("video/")) {
                if (checkVideoSize(selectedImageUri)) {
                    if (isThumbnailRecipeChosen) {
                        btn_play.setVisibility(View.VISIBLE);
                        if (updateRecipe.getMediaId() != null && !updateRecipe.getMediaId().trim().isEmpty()) {
                            mediaList.remove(findObjectById(mediaList,updateRecipe.getMediaId()));
                        }
                        updateRecipe.setMediaId(media.getId());
                        Glide.with(this).asBitmap().load(media.getUrl()).error(R.drawable.add_image).into(img_thumbnail_recipe);
                        isThumbnailRecipeChosen = false;
                    }

                } else {
                    Toast.makeText(this, "Video is too large. Please select a smaller video.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "File type not supported.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private boolean checkVideoSize(Uri videoUri) {
        try {
            String videoPath = getVideoPathFromUri(videoUri);
            File videoFile = new File(videoPath);
            long videoSizeBytes = videoFile.length();
            long videoSizeMB = videoSizeBytes / (1024 * 1024);
            return videoSizeMB <= MAX_VIDEO_SIZE_MB;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    private String getVideoPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            String videoPath = cursor.getString(columnIndex);
            cursor.close();
            return videoPath;
        }
        return uri.getPath();
    }
    private boolean ValidateRecipe(String name) {
        if (!ValidateMedia()) return false;
        if (!ValidateName(name)) return false;
        if (!ValidateIngredientsAndInstructions()) return false;
        if (!ValidateIngredientsList()) return false;
        if (!ValidateInstructionsList()) return false;
        return true;
    }
    private boolean ValidateMedia() {
        if (updateRecipe.getMediaId() == null || updateRecipe.getMediaId().trim().isEmpty()) {
            Toast.makeText(this, "Please select an image or video for the recipe thumbnail", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private boolean ValidateIngredientsAndInstructions() {
        if (instructionList == null || instructionList.isEmpty()
                || ingredientList == null || ingredientList.isEmpty()) {
            ShowDialogWarning();
            return false;
        }
        return true;
    }
    private void ShowDialogWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setMessage("Please add at least one ingredient and one step before saving the recipe.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
    private boolean ValidateName(String name) {
        if (name.isEmpty()) {
            edt_name.setError("Name cannot be empty!");
            return false;
        }

        if (!name.matches(".*" + specialCharacters + ".*")) {
            edt_name.setError("Name cannot contain special characters!");
            return false;
        }
        return true;
    }
    private boolean ValidateIngredientsList() {
        boolean isCheck = true;
        for (int i = 0; i < ingredientList.size(); i++) {
            Ingredient ingredient = ingredientList.get(i);
            if (ingredient.getName().trim().isEmpty()) {
                ingredient_adapter.notifyItemChanged(i);
                return false;
            }
            if (!ingredient.getName().matches(".*" + specialCharacters + ".*")) {
                isCheck = false;
                break;
            }
        }
        if (!isCheck) {
            Toast.makeText(this, "Name cannot contain special characters!", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
    private boolean ValidateInstructionsList() {
        boolean isCheck = true;
        for (int i = 0; i < instructionList.size(); i++) {
            Instruction instruction = instructionList.get(i);
            if (instruction.getContent().trim().isEmpty()) {
                instruction_adapter.notifyItemChanged(i);
                return false;
            }
            if (!instruction.getContent().matches(".*" + specialCharacters + ".*")) {
                isCheck = false;
                break;
            }
        }
        if (!isCheck) {
            Toast.makeText(this, "Name cannot contain special characters!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void initUI() {
        storageReference = FirebaseStorage.getInstance().getReference(STORAGE_MEDIAS);
        databaseReferenceMedias = FirebaseDatabase.getInstance().getReference(REALTIME_MEDIAS);
        databaseReferenceIngredients = FirebaseDatabase.getInstance().getReference(REALTIME_INGREDIENTS);
        databaseReferenceInstructions = FirebaseDatabase.getInstance().getReference(REALTIME_INSTRUCTIONS);
        databaseReferenceRecipes = FirebaseDatabase.getInstance().getReference(REALTIME_RECIPES);
        progressDialog = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
        ingredientList = new ArrayList<>();
        instructionList = new ArrayList<>();
        mediaList = new ArrayList<>();
        tv_cook_time = findViewById(R.id.tv_cook_time);
        tv_serves = findViewById(R.id.tv_serves);
        tv_serves = findViewById(R.id.tv_serves);
        btn_add_ingredient = findViewById(R.id.btn_add_ingredient);
        btn_add_instruction = findViewById(R.id.btn_add_instruction);
        btn_create_recipes = findViewById(R.id.btn_create_recipes);
        btn_choose_media = findViewById(R.id.btn_choose_media);
        btn_play = findViewById(R.id.btn_play);
        btn_cook_time = findViewById(R.id.btn_cook_time);
        btn_serves = findViewById(R.id.btn_serves);
        btn_back = findViewById(R.id.btn_back);
        img_thumbnail_recipe = findViewById(R.id.img_thumbnail_recipe);
        edt_name = findViewById(R.id.edt_name);
        rcv_ingredient = findViewById(R.id.rcv_ingredient);
        rcv_instruction = findViewById(R.id.rcv_instruction);
    }
}