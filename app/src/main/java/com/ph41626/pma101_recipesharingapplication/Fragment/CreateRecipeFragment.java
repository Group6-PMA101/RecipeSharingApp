package com.ph41626.pma101_recipesharingapplication.Fragment;

import static android.app.Activity.RESULT_OK;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_INGREDIENTS;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_INSTRUCTIONS;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_MEDIAS;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_RECIPES;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.STORAGE_MEDIAS;
import static com.ph41626.pma101_recipesharingapplication.Services.Services.CreateNewIngredient;
import static com.ph41626.pma101_recipesharingapplication.Services.Services.CreateNewInstruction;
import static com.ph41626.pma101_recipesharingapplication.Services.Services.RandomID;
import static com.ph41626.pma101_recipesharingapplication.Services.Services.findObjectById;

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

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ph41626.pma101_recipesharingapplication.Adapter.RecyclerViewIngredientAdapter;
import com.ph41626.pma101_recipesharingapplication.Adapter.RecyclerViewInstructionAdapter;
import com.ph41626.pma101_recipesharingapplication.Model.Ingredient;
import com.ph41626.pma101_recipesharingapplication.Model.Instruction;
import com.ph41626.pma101_recipesharingapplication.Model.Media;
import com.ph41626.pma101_recipesharingapplication.Model.Recipe;
import com.ph41626.pma101_recipesharingapplication.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateRecipeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateRecipeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CreateRecipeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateRecipeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateRecipeFragment newInstance(String param1, String param2) {
        CreateRecipeFragment fragment = new CreateRecipeFragment();
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
    private static final int PICK_MEDIA_REQUEST = 1;
    private static final long MAX_VIDEO_SIZE_MB = 250;
    private View view;
    private Button
            btn_add_ingredient,
            btn_add_instruction,
            btn_create_recipes;
    private ImageView img_thumbnail_recipe;
    private TextView tv_cook_time,tv_serves;
    private EditText edt_name;
    private LinearLayout btn_choose_media,btn_play,btn_cook_time,btn_serves;
    private RecyclerView rcv_ingredient,rcv_instruction;

    private ArrayList<Ingredient> ingredientList;
    private ArrayList<Instruction> instructionList;
    public ArrayList<Media> mediaList;

    private RecyclerViewIngredientAdapter ingredient_adapter;
    private RecyclerViewInstructionAdapter instruction_adapter;

    private Recipe newRecipe = new Recipe();
    private Instruction current_instruction;
    private ProgressDialog progressDialog;


    private int current_instruction_pos = -1;
    private boolean isThumbnailRecipeChosen = false;
    private String specialCharacters = "[^a-zA-Z0-9 ,.;-]";

    private StorageReference storageReference;
    private DatabaseReference
            databaseReferenceMedias,
            databaseReferenceIngredients,
            databaseReferenceInstructions,
            databaseReferenceRecipes;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_create_recipe, container, false);

        initUI();
        newRecipe.setId(RandomID());
        SetupButtonListeners();
        RecyclerManager();

        return view;
    }
    private void ResetData() {
        mediaList.clear();
        ingredientList.clear();
        ingredientList.add(CreateNewIngredient());
        ingredient_adapter.Reset(ingredientList);
        instructionList.clear();
        instructionList.add(CreateNewInstruction());
        instruction_adapter.Reset(instructionList);

        tv_serves.setText("0");
        tv_cook_time.setText("0 min");
        edt_name.setText("");
        img_thumbnail_recipe.setImageResource(R.color.white_1C1);
        newRecipe = new Recipe();
        newRecipe.setId(RandomID());
        current_instruction = null;
        current_instruction_pos = -1;
        isThumbnailRecipeChosen = false;
    }
    private void SetupButtonListeners() {
        btn_play.setVisibility(View.GONE);
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (view.hasFocus()) view.clearFocus();
                ShowVideoAlertDialog(findObjectById(mediaList,newRecipe.getMediaId()).getUrl());
            }
        });
        btn_choose_media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isThumbnailRecipeChosen = true;
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                if (view.hasFocus()) view.clearFocus();
                String name = edt_name.getText().toString().trim();
                Date newDate = new Date();
                if (ValidateRecipe(name)) {
                    newRecipe.setName(name);
                    newRecipe.setCreationDate(newDate);
                    newRecipe.setLastUpdateDate(newDate);
                    newRecipe.setTotalReviews(0);
                    newRecipe.setAverageRating(0);
                    //newRecipe.setUserId();
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
                if (view.hasFocus()) view.clearFocus();
                ingredientList.add(CreateNewIngredient());
                ingredient_adapter.UpdateData(true, ingredientList, ingredientList.size());
            }
        });
        btn_add_instruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (view.hasFocus()) view.clearFocus();
                Instruction newInstruction = CreateNewInstruction();
                newInstruction.setRecipeId(newRecipe.getId());
                instructionList.add(newInstruction);
                instruction_adapter.UpdateData(true, instructionList, instructionList.size());
            }
        });
    }
//    private void DeleteFile() {
//        storageReference.child("1715517066797.jpg").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void unused) {
//                Toast.makeText(getContext(), "File deleted successfully", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
    private void SaveDataToFirebase() {
        ArrayList<UploadTask> uploadTaskMedias = new ArrayList<>();
        ArrayList<Task<Void>> uploadTaskIngredients = new ArrayList<>();
        ArrayList<Task<Void>> uploadTaskInstructions = new ArrayList<>();
        Task<Void> uploadRecipe = databaseReferenceRecipes.child(newRecipe.getId()).setValue(newRecipe);

        for (Media media:mediaList) {
            Uri uri = Uri.parse(media.getUrl());
            String fileName = System.currentTimeMillis() + "." + GetFileExtension(uri);
            media.setName(fileName);
            StorageReference storageRef = storageReference.child(fileName);
            uploadTaskMedias.add((UploadTask) storageRef.putFile(uri)
                    .addOnSuccessListener(taskSnapshot -> {
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                media.setUrl(uri.toString());
                                databaseReferenceMedias.child(media.getId()).setValue(media);
                            }
                        });
                    })
                    .addOnFailureListener(exception -> {
                        Toast.makeText(getContext(), "Upload failed for " + media.getName() + ": " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }));
        }
        for (Ingredient ingredient:ingredientList) {
            ingredient.setId(RandomID());
            DatabaseReference databaseReferenceRef = databaseReferenceIngredients.child(ingredient.getId());
            uploadTaskIngredients.add(databaseReferenceRef.setValue(ingredient));
        }
        for (Instruction instruction:instructionList) {
            instruction.setId(RandomID());
            DatabaseReference databaseReferenceRef = databaseReferenceInstructions.child(instruction.getId());
            uploadTaskInstructions.add(databaseReferenceRef.setValue(instruction));
        }
//        Tasks.whenAll(uploadTaskMedias).addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {

//            } else {
//                Toast.makeText(getContext(), "Failure!", Toast.LENGTH_SHORT).show();
//            }
//        });
        ArrayList<CompletableFuture<Void>> futures = new ArrayList<>();
        for (UploadTask uploadTask : uploadTaskMedias) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            uploadTask.addOnSuccessListener(taskSnapshot -> future.complete(null))
                    .addOnFailureListener(e -> future.completeExceptionally(e));
            futures.add(future);
        }
        for (Task<Void> task : uploadTaskIngredients) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            task.addOnSuccessListener(aVoid -> future.complete(null))
                    .addOnFailureListener(e -> future.completeExceptionally(e));
            futures.add(future);
        }
        for (Task<Void> task : uploadTaskInstructions) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            task.addOnSuccessListener(aVoid -> future.complete(null))
                    .addOnFailureListener(e -> future.completeExceptionally(e));
            futures.add(future);
        }
        CompletableFuture<Void> future = new CompletableFuture<>();
        uploadRecipe.addOnSuccessListener(aVoid -> future.complete(null))
                .addOnFailureListener(e -> future.completeExceptionally(e));
        futures.add(future);

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        allOf.thenRun(() -> {
            //Toast.makeText(getContext(), "All tasks completed.", Toast.LENGTH_SHORT).show();
            ResetData();
            progressDialog.dismiss();
        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
    }
    private String GetFileExtension(Uri uri){
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mine = MimeTypeMap.getSingleton();
        return uri != null ? mine.getExtensionFromMimeType(contentResolver.getType(uri)):null;
    }
    private boolean ValidateRecipe(String name) {
        if (!ValidateMedia()) return false;
        if (!ValidateIngredientsAndInstructions()) return false;
        if (!ValidateName(name)) return false;
        if (!ValidateIngredientsList()) return false;
        if (!ValidateInstructionsList()) return false;
        return true;
    }
    private boolean ValidateMedia() {
        if (newRecipe.getMediaId() == null || newRecipe.getMediaId().trim().isEmpty()) {
            Toast.makeText(getContext(), "Please select an image or video for the recipe thumbnail", Toast.LENGTH_SHORT).show();
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
    private boolean ValidateName(String name) {
        if (name.isEmpty()) {
            edt_name.setError("Name cannot be empty!");
            return false;
        }

        if (name.matches(".*" + specialCharacters + ".*")) {
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
            if (ingredient.getName().matches(".*" + specialCharacters + ".*")) {
                isCheck = false;
                break;
            }
        }
        if (!isCheck) {
            Toast.makeText(getContext(), "Name cannot contain special characters!", Toast.LENGTH_SHORT).show();
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
            if (instruction.getContent().matches(".*" + specialCharacters + ".*")) {
                isCheck = false;
                break;
            }
        }
        if (!isCheck) {
            Toast.makeText(getContext(), "Name cannot contain special characters!", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
    private void ShowDialogWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
    private void ShowInputDialog(boolean type,String title, final int inputType) {
        // Type = true -> Serves; Type = False -> Cook time
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);
        final EditText input = new EditText(getContext());
        input.setInputType(inputType);
        builder.setView(input);

        Drawable iconDrawable;

        if (type) iconDrawable = getResources().getDrawable(R.drawable.ic_profile);
        else iconDrawable = getResources().getDrawable(R.drawable.ic_clock);

        Drawable icon = iconDrawable.mutate();
        icon.setColorFilter(ContextCompat.getColor(getContext(), R.color.red_E00), PorterDuff.Mode.SRC_IN);
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
                    newRecipe.setServings(Integer.parseInt(value));
                    tv_serves.setText(value);
                } else {
                    newRecipe.setCookTime(Integer.parseInt(value));
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
    private void ShowVideoAlertDialog(String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Watch Video");
        VideoView videoView = new VideoView(getContext());
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
    private boolean isValidInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public void ChooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_MEDIA_REQUEST);
    }
    public void ChooseImage(Instruction instruction, int pos) {
        if (instruction != null && pos != -1) {
            if (instructionList != null && instructionList.size() == 3) {
                Toast.makeText(getContext(), "You can only add up to 3 images for each step!", Toast.LENGTH_SHORT).show();
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
            String mimeType = getContext().getContentResolver().getType(selectedImageUri);
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
                        if (newRecipe.getMediaId() != null && !newRecipe.getMediaId().trim().isEmpty()) {
                            mediaList.remove(findObjectById(mediaList,newRecipe.getMediaId()));
                        }
                        newRecipe.setMediaId(media.getId());
                        Glide.with(getContext()).load(media.getUrl()).error(R.drawable.add_image).into(img_thumbnail_recipe);
                        isThumbnailRecipeChosen = false;
                    }
                }
            } else if (mimeType != null && mimeType.startsWith("video/")) {
                if (checkVideoSize(selectedImageUri)) {
                    if (isThumbnailRecipeChosen) {
                        btn_play.setVisibility(View.VISIBLE);
                        if (newRecipe.getMediaId() != null && !newRecipe.getMediaId().trim().isEmpty()) {
                            mediaList.remove(findObjectById(mediaList,newRecipe.getMediaId()));
                        }
                        newRecipe.setMediaId(media.getId());
                        Glide.with(getContext()).asBitmap().load(media.getUrl()).error(R.drawable.add_image).into(img_thumbnail_recipe);
                        isThumbnailRecipeChosen = false;
                    }

                } else {
                    Toast.makeText(getContext(), "Video is too large. Please select a smaller video.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "File type not supported.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void RecyclerManager() {
        ingredientList.add(CreateNewIngredient());
        instructionList.add(CreateNewInstruction());

        ingredient_adapter = new RecyclerViewIngredientAdapter(
                getContext(),
                ingredientList,
                this);
        instruction_adapter = new RecyclerViewInstructionAdapter(
                getContext(),
                instructionList,
                this
        );
        rcv_ingredient.setLayoutManager(new GridLayoutManager(getContext(),1));
        rcv_ingredient.setAdapter(ingredient_adapter);
        rcv_ingredient.setNestedScrollingEnabled(false);

        rcv_instruction.setLayoutManager(new GridLayoutManager(getContext(),1));
        rcv_instruction.setAdapter(instruction_adapter);
        rcv_instruction.setNestedScrollingEnabled(false);
    }
    public void RemoveItemThumbnail(String mediaId,Instruction instruction, int instructionPos) {
        if (view.hasFocus()) view.clearFocus();
        mediaList.remove(findObjectById(mediaList,mediaId));
        ArrayList<String> mediaIds = instruction.getMediaIds();
        mediaIds.remove(mediaId);
        instruction.setMediaIds(mediaIds);
        instruction_adapter.UpdateData(false, instructionList,instructionPos);
    }
    public void RemoveItemIngredient(int pos) {
        if (view.hasFocus()) view.clearFocus();
        ingredientList.remove(pos);
        ingredient_adapter.UpdateData(false, ingredientList,pos);
    }
    public void RemoveItemInstruction(int pos, Instruction instruction) {
        if (view.hasFocus()) view.clearFocus();
        if (instruction.getMediaIds() != null && !instruction.getMediaIds().isEmpty()) {
            for (String mediaId:instruction.getMediaIds()) {
                mediaList.remove(findObjectById(mediaList,mediaId));
            }
        }
        instructionList.remove(pos);
        instruction_adapter.UpdateData(false, instructionList,pos);
    }
    public void ShowRemoveInstructionDialog (int pos, Instruction instruction) {
        SpannableString spannableString = new SpannableString("Delete");
        spannableString.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.red_E00)), 0, spannableString.length(), 0);

        AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
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
        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            String videoPath = cursor.getString(columnIndex);
            cursor.close();
            return videoPath;
        }
        return uri.getPath();
    }
    private void initUI() {
        storageReference = FirebaseStorage.getInstance().getReference(STORAGE_MEDIAS);
        databaseReferenceMedias = FirebaseDatabase.getInstance().getReference(REALTIME_MEDIAS);
        databaseReferenceIngredients = FirebaseDatabase.getInstance().getReference(REALTIME_INGREDIENTS);
        databaseReferenceInstructions = FirebaseDatabase.getInstance().getReference(REALTIME_INSTRUCTIONS);
        databaseReferenceRecipes = FirebaseDatabase.getInstance().getReference(REALTIME_RECIPES);
        progressDialog = new ProgressDialog(getContext(),R.style.AppCompatAlertDialogStyle);
        ingredientList = new ArrayList<>();
        instructionList = new ArrayList<>();
        mediaList = new ArrayList<>();
        tv_cook_time = view.findViewById(R.id.tv_cook_time);
        tv_serves = view.findViewById(R.id.tv_serves);
        tv_serves = view.findViewById(R.id.tv_serves);
        btn_add_ingredient = view.findViewById(R.id.btn_add_ingredient);
        btn_add_instruction = view.findViewById(R.id.btn_add_instruction);
        btn_create_recipes = view.findViewById(R.id.btn_create_recipes);
        btn_choose_media = view.findViewById(R.id.btn_choose_media);
        btn_play = view.findViewById(R.id.btn_play);
        btn_cook_time = view.findViewById(R.id.btn_cook_time);
        btn_serves = view.findViewById(R.id.btn_serves);
        img_thumbnail_recipe = view.findViewById(R.id.img_thumbnail_recipe);
        edt_name = view.findViewById(R.id.edt_name);
        rcv_ingredient = view.findViewById(R.id.rcv_ingredient);
        rcv_instruction = view.findViewById(R.id.rcv_instruction);
    }
}