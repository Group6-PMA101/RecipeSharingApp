package com.ph41626.pma101_recipesharingapplication.Activity;

import static android.view.View.GONE;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_COMMENTS;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_FOLLOWERS;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_INGREDIENTS;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_INSTRUCTIONS;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_MEDIAS;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_RECIPES;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_USERS;
import static com.ph41626.pma101_recipesharingapplication.Services.Services.findObjectById;
import static com.ph41626.pma101_recipesharingapplication.Services.Services.isVideo;
import static com.ph41626.pma101_recipesharingapplication.Services.UserPreferences.GetUser;
import static com.ph41626.pma101_recipesharingapplication.Services.UserPreferences.SaveUser;
import static com.ph41626.pma101_recipesharingapplication.Services.VideoDialogUtil.ShowVideoDialog;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentCallbacks;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ph41626.pma101_recipesharingapplication.Model.Comment;
import com.ph41626.pma101_recipesharingapplication.Model.Ingredient;
import com.ph41626.pma101_recipesharingapplication.Model.Instruction;
import com.ph41626.pma101_recipesharingapplication.Model.Media;
import com.ph41626.pma101_recipesharingapplication.Model.Recipe;
import com.ph41626.pma101_recipesharingapplication.Model.User;
import com.ph41626.pma101_recipesharingapplication.Model.UserFollower;
import com.ph41626.pma101_recipesharingapplication.R;
import com.ph41626.pma101_recipesharingapplication.Services.FirebaseUtils;
import com.ph41626.pma101_recipesharingapplication.Services.ImageDialogUtil;
import com.ph41626.pma101_recipesharingapplication.Services.RecipeDetailEventListener;
import com.ph41626.pma101_recipesharingapplication.Services.RecipeEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RecipeDetailActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_ACTIVITY_BACK = 1;
    private LinearLayout
            layout_ingredients,
            layout_instructions,
            layout_comments,
            btn_play,
            btn_send_comment,
            btn_rate_recipe,
            btn_serves,
            btn_cook_time;
    private TextView
            tv_cook_time,
            tv_serves,
            tv_recipe_name,
            tv_averageRating,
            tv_review_count,
            tv_user_name,
            tv_user_follower,
            tv_ingredient_count,
            tv_instruction_count,
            tv_comment_status,
            tv_creation_date,
            tv_most_recent_update;
    private Button btn_follow,btn_show_more;
    private EditText edt_input_comment;
    private ImageView img_thumbnail_recipe,img_user_avatar,img_my_avatar;
    private Recipe recipe = new Recipe();
    private Media recipeMedia = new Media();
    private User recipeOwner = new User();
    private Media ownerMedia = new Media();
    private ArrayList<Ingredient> ingredients = new ArrayList<>();
    private ArrayList<Instruction> instructions = new ArrayList<>();
    private HashMap<String, User> commentUser = new HashMap<>();
    private HashMap<String,Media> userMedia = new HashMap<>();
    private ArrayList<Comment> comments = new ArrayList<>();
    private ArrayList<Media> instructionMedias = new ArrayList<>();
    private List<CompletableFuture<Void>> instructionFutures = new ArrayList<>();
    private List<CompletableFuture<Void>> commentFutures = new ArrayList<>();
    private UserFollower userFollower = new UserFollower();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private ProgressDialog progressDialog;
    private List<CompletableFuture<Void>> futures = new ArrayList<>();
    private DatabaseReference databaseReferenceComment;
    private SimpleExoPlayer player;
    private static RecipeDetailEventListener eventListener;
    private static RecipeEventListener recipeEventListener;
    private boolean isShow = true; //True = Show; False = Hide;
    private boolean isFollow = false; //True = UnFollow; False = Follow;
    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_detail);

        initUI();
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        GetData();
        SetUpUi();
        SetUpButton();
    }

    private void SetUpButton() {
        btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recipeOwner.getId().equals(GetUser(RecipeDetailActivity.this).getId())) {
                    Toast.makeText(RecipeDetailActivity.this, "You cannot follow yourself!", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog.show();
                User user = GetUser(RecipeDetailActivity.this);
                CompletableFuture<Void> addFollower = new CompletableFuture<>();
                futures.add(addFollower);
                if (!isFollow) {
                    userFollower = new UserFollower();
                    userFollower.setUserId(GetUser(RecipeDetailActivity.this).getId());
                    userFollower.setChefId(recipeOwner.getId());
                    userFollower.setFollowDate(new Date());

                    recipeOwner.setFollowersCount(recipeOwner.getFollowersCount() + 1);
                    user.setFollowingCount(user.getFollowingCount() + 1);
                    FirebaseDatabase
                            .getInstance()
                            .getReference(REALTIME_FOLLOWERS)
                            .child(userFollower.getId())
                            .setValue(userFollower)
                            .addOnCompleteListener(task -> {
                                addFollower.complete(null);
                            });
                } else {
                    recipeOwner.setFollowersCount(recipeOwner.getFollowersCount() - 1);
                    user.setFollowingCount(user.getFollowingCount() - 1);
                    FirebaseDatabase
                            .getInstance()
                            .getReference(REALTIME_FOLLOWERS)
                            .child(userFollower.getId())
                            .setValue(null)
                            .addOnCompleteListener(task -> {
                                addFollower.complete(null);
                            });
                }
                CompletableFuture<Void> changeChef = new CompletableFuture<>();
                futures.add(changeChef);
                FirebaseDatabase
                        .getInstance()
                        .getReference(REALTIME_USERS)
                        .child(recipeOwner.getId())
                        .child("followersCount")
                        .setValue(recipeOwner.getFollowersCount())
                        .addOnCompleteListener(task -> {
                            changeChef.complete(null);
                        });
                CompletableFuture<Void> changeUser = new CompletableFuture<>();
                futures.add(changeChef);
                FirebaseDatabase
                        .getInstance()
                        .getReference(REALTIME_USERS)
                        .child(user.getId())
                        .child("followingCount")
                        .setValue(recipeOwner.getFollowersCount())
                        .addOnCompleteListener(task -> {
                            SaveUser(RecipeDetailActivity.this,user);
                            changeUser.complete(null);
                        });
                CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
                allOf.thenRun(() -> {
                    SaveUser(RecipeDetailActivity.this,user);
                    tv_user_follower.setText("Follower " + recipeOwner.getFollowersCount());
                    eventListener.onFollowEvent(recipe.getUserId(),recipeOwner);
                    checkChefFollower(progressDialog);
                }).exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
            }
        });
        btn_play.setOnClickListener(v -> {
            ShowVideoDialog(this,recipeMedia.getUrl());
        });
        btn_rate_recipe.setOnClickListener(v -> {
            if (GetUser(this).getId().equals(recipeOwner.getId())) {
                Toast.makeText(this, "You cannot evaluate your own recipe.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, RateRecipeActivity.class);
            intent.putExtra("recipe",recipe);
            startActivityForResult(intent,REQUEST_CODE_ACTIVITY_BACK);
        });
        btn_send_comment.setOnClickListener(v -> {
            String content = edt_input_comment.getText().toString().trim();
            if (!content.isEmpty()) {
                Comment comment = new Comment();
                comment.setRecipeId(recipe.getId());
                comment.setUserId(GetUser(this).getId());
                comment.setContent(content);
                databaseReferenceComment
                        .child(comment.getId())
                        .setValue(comment)
                        .addOnCompleteListener(task -> {

                });

            } else {
                Toast.makeText(this, "Please enter your comment content!", Toast.LENGTH_SHORT).show();
            }
        });
        btn_show_more.setOnClickListener(v -> {
            if (isShow) {
                isShow = false;
                btn_show_more.setText("Show more...");
            } else {
                isShow = true;
                btn_show_more.setText("Hide");
            }
            ResizeLayoutComment();
        });
    }
    public void ShowImageDetail(String url) {
        new ImageDialogUtil().ShowVideoDialog(this,url);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ACTIVITY_BACK) {
            if (resultCode == RESULT_OK) {
                new FirebaseUtils().getDataFromFirebaseById(REALTIME_RECIPES, recipe.getId(), new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        recipe = snapshot.getValue(Recipe.class);
                        recipeEventListener.onDataChange(recipe);
                        if (recipe.getTotalReviews() == 0) {
                            tv_review_count.setText("(No ratings for this recipe yet)");
                        } else {
                            tv_review_count.setText("(" + recipe.getTotalReviews() + " Reviews)");
                        }
                        tv_averageRating.setText(String.valueOf(recipe.getAverageRating()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        }
    }

    private void ResizeLayoutComment() {
        int totalHeight = 0;
        if (isShow) {
            totalHeight =  LinearLayout.LayoutParams.WRAP_CONTENT;
        } else {
            for (int i = 0; i < 3; i++) {
                View child = layout_comments.getChildAt(i);
                if (child != null) {
                    totalHeight += child.getHeight();
                }
            }
        }
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layout_comments.getLayoutParams();
        layoutParams.height = totalHeight;
        layout_comments.setLayoutParams(layoutParams);
    }
    private void SetUpUi() {
        tv_recipe_name.setText(recipe.getName());
        String creation_date = dateFormat.format(recipe.getCreationDate());
        tv_creation_date.setText("Creation date: " + creation_date);
        String most_recent_update = dateFormat.format(recipe.getLastUpdateDate());
        tv_most_recent_update.setText("Most Recent Update: " + most_recent_update);

        if (recipe.getServings() > 0) {
            tv_serves.setText(String.valueOf(recipe.getServings()));
        } else
            btn_serves.setVisibility(View.GONE);
        if (recipe.getCookTime() > 0) {
            tv_cook_time.setText(String.valueOf(recipe.getCookTime()) + "min");
        } else
            btn_cook_time.setVisibility(View.GONE);

        if (recipeMedia == null) {
            new FirebaseUtils().getDataFromFirebaseById(REALTIME_MEDIAS, recipe.getMediaId(), new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Media media = snapshot.getValue(Media.class);
                    recipeMedia = media;
                    if (isVideo(recipeMedia.getUrl())) btn_play.setVisibility(View.VISIBLE);
                    else btn_play.setVisibility(View.GONE);
                    Glide.with(RecipeDetailActivity.this)
                            .asBitmap()
                            .load(recipeMedia.getUrl())
                            .error(R.drawable.caption)
                            .into(img_thumbnail_recipe);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            if (isVideo(recipeMedia.getUrl())) btn_play.setVisibility(View.VISIBLE);
            else btn_play.setVisibility(View.GONE);
            Glide.with(this)
                    .asBitmap()
                    .load(recipeMedia.getUrl())
                    .error(R.drawable.caption)
                    .into(img_thumbnail_recipe);
        }


        tv_averageRating.setText(String.valueOf(recipe.getAverageRating()));
        if (recipe.getTotalReviews() == 0) {
            tv_review_count.setText("(No ratings for this recipe yet)");
        } else {
            tv_review_count.setText("(" + recipe.getTotalReviews() + " Reviews)");
        }

        if (recipeOwner == null) {
            fetchRecipeOwner();
        } else {
            tv_user_name.setText(recipeOwner.getName());
            tv_user_follower.setText("Follower " + recipeOwner.getFollowersCount());
            if (ownerMedia != null) {
                Glide.with(this)
                        .asBitmap()
                        .load(ownerMedia.getUrl())
                        .error(R.drawable.default_avatar)
                        .into(img_user_avatar);
            } else {
                img_user_avatar.setImageResource(R.drawable.default_avatar);
            }
            if (recipeOwner.getMediaId() != null && !recipeOwner.getMediaId().isEmpty() && ownerMedia == null) {
                fetchMediaForOwner();
            }
        }

        if (ingredients == null || ingredients.isEmpty()) {
            ingredients = new ArrayList<>();
            fetchIngredientForRecipe();
        }

        if (instructions == null || instructions.isEmpty()) {
            instructions = new ArrayList<>();
            if (instructionMedias == null) instructionMedias = new ArrayList<>();
            fetchInstructionForRecipe(recipe);
        }
        CompletableFuture<Void> allOfInstruction = CompletableFuture.allOf(instructionFutures.toArray(new CompletableFuture[0]));
        allOfInstruction.thenRun(() -> {
            tv_instruction_count.setText(instructions.size() + " Items");
            Collections.sort(instructions, new Comparator<Instruction>() {
                @Override
                public int compare(Instruction i1, Instruction i2) {
                    return Integer.compare(i1.getOrder(), i2.getOrder());
                }
            });
            for (Instruction instruction:instructions) {

                View view = LayoutInflater.from(RecipeDetailActivity.this).inflate(R.layout.item_instruction,null,false);
                TextView tv_order = view.findViewById(R.id.tv_order);
                EditText edt_content = view.findViewById(R.id.edt_content);
                Button btn_add_media = view.findViewById(R.id.btn_add_media);
                Button btn_remove_item_instruction = view.findViewById(R.id.btn_remove_item_instruction);
                LinearLayout layout_image = view.findViewById(R.id.layout_image);


                tv_order.setText(String.valueOf(instruction.getOrder()));
                edt_content.setText(instruction.getContent());
                edt_content.setFocusable(false);
                btn_add_media.setVisibility(GONE);
                btn_remove_item_instruction.setVisibility(GONE);

                if (instruction.getMediaIds() != null && !instruction.getMediaIds().isEmpty()) {

                    for (String string:instruction.getMediaIds()) {
                        Media media = findObjectById(instructionMedias,string);
                        View viewThumbnail = LayoutInflater.from(RecipeDetailActivity.this).inflate(R.layout.item_instruction_thumbnail,null,false);
                        ImageView img = viewThumbnail.findViewById(R.id.img_thumbnail);
                        LinearLayout btn_remove_thumbnail = viewThumbnail.findViewById(R.id.btn_remove_thumbnail);
                        btn_remove_thumbnail.setVisibility(GONE);
                        Glide.with(RecipeDetailActivity.this)
                                .asBitmap()
                                .load(media.getUrl())
                                .placeholder(R.drawable.caption)
                                .error(R.drawable.caption)
                                .into(img);
                        img.setOnClickListener(v -> {
                            ShowImageDetail(media.getUrl());
                        });
                        layout_image.addView(viewThumbnail);
                    }
                }

                layout_instructions.addView(view);
            }
            progressDialog.dismiss();
        }).exceptionally(e -> {
            progressDialog.dismiss();
            e.printStackTrace();
            return null;
        });

        fetchCommentForRecipe(recipe);
        checkChefFollower(null);
    }
    private void checkChefFollower(ProgressDialog progressDialog) {
        new FirebaseUtils().getAllDataByKey(REALTIME_FOLLOWERS, "userId", GetUser(this).getId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() != 0) {
                    for (DataSnapshot child:snapshot.getChildren()) {
                        UserFollower user = child.getValue(UserFollower.class);
                        if(user.getUserId().equals(GetUser(RecipeDetailActivity.this).getId())) {
                            isFollow = true;
                            userFollower = user;
                            break;
                        } else {
                            isFollow = false;
                            userFollower = null;
                        }
                    }
                } else {
                    isFollow = false;
                    userFollower = null;
                }


                if (isFollow) {
                    btn_follow.setText("Unfollow");
                } else {
                    btn_follow.setText("Follow");
                }
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void fetchIngredientForRecipe() {
        new FirebaseUtils().getAllDataByKey(REALTIME_INGREDIENTS, "recipeId", recipe.getId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child:snapshot.getChildren()) {
                    Ingredient ingredient = child.getValue(Ingredient.class);
                    ingredients.add(ingredient);

                    View view = LayoutInflater.from(RecipeDetailActivity.this).inflate(R.layout.item_recipe_detail_ingredient,null,false);
                    TextView
                            tv_ingredient_name = view.findViewById(R.id.tv_ingredient_name),
                            tv_ingredient_mass = view.findViewById(R.id.tv_ingredient_mass);
                    tv_ingredient_name.setText(ingredient.getName());
                    if (ingredient.getMass() == 0) {
                        tv_ingredient_mass.setVisibility(GONE);
                    } else {
                        tv_ingredient_mass.setText(ingredient.getMass() + "gram");
                    }
                    layout_ingredients.addView(view);
                }
                tv_ingredient_count.setText(ingredients.size() + " Items");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void fetchInstructionForRecipe(Recipe recipe) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        instructionFutures.add(future);
        new FirebaseUtils().getAllDataByKey(REALTIME_INSTRUCTIONS, "recipeId", recipe.getId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<CompletableFuture<Void>> mediaFutures = new ArrayList<>();
                for (DataSnapshot child:snapshot.getChildren()) {
                    Instruction instruction = child.getValue(Instruction.class);
                    instructions.add(instruction);
                    if (instruction.getMediaIds() != null && !instruction.getMediaIds().isEmpty()) {
                        CompletableFuture<Void> future = new CompletableFuture<>();
                        instructionFutures.add(future);
                        mediaFutures.add(fetchMediaForInstruction(instruction));
                    }
                }
                CompletableFuture<Void> allMediaFutures = CompletableFuture.allOf(mediaFutures.toArray(new CompletableFuture[0]));
                allMediaFutures.thenRun(() -> future.complete(null));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private CompletableFuture<Void> fetchMediaForInstruction(Instruction instruction) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (String mediaId:instruction.getMediaIds()) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            futures.add(future);
            new FirebaseUtils().getDataFromFirebaseById(REALTIME_MEDIAS, mediaId, new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Media media = snapshot.getValue(Media.class);
                    instructionMedias.add(media);
                    future.complete(null);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
    private void fetchCommentForRecipe(Recipe recipe) {
        new FirebaseUtils().getAllDataByKeyRealTime(REALTIME_COMMENTS, "recipeId", recipe.getId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comments.clear();
                for (DataSnapshot child:snapshot.getChildren()) {
                    CompletableFuture<Void> future = new CompletableFuture<>();
                    commentFutures.add(future);
                    Comment comment = child.getValue(Comment.class);
                    comments.add(comment);
                    fetchUserForComment(future,comment);
                }
                CompletableFuture<Void> allOfComment = CompletableFuture.allOf(commentFutures.toArray(new CompletableFuture[0]));
                allOfComment.thenRun(() -> {
                    if (comments == null || comments.isEmpty()) {
                        tv_comment_status.setVisibility(View.VISIBLE);
                        btn_show_more.setVisibility(GONE);
                        return;
                    } else {
                        btn_show_more.setVisibility(View.VISIBLE);
                        tv_comment_status.setVisibility(GONE);
                    }
                    layout_comments.removeAllViews();
                    for (Comment comment:comments) {
                        View view = LayoutInflater
                                .from(RecipeDetailActivity.this)
                                .inflate(R.layout.item_comment,null);
                        ImageView img_user_avatar = view.findViewById(R.id.img_user_avatar);
                        TextView tv_user_name = view.findViewById(R.id.tv_user_name),
                                tv_comment_content = view.findViewById(R.id.tv_comment_content);
                        User user = commentUser.get(comment.getId());
                        if (user.getMediaId() != null && !user.getMediaId().isEmpty()) {
                            Media media = userMedia.get(commentUser.get(comment.getId()).getId());
                            Glide.with(RecipeDetailActivity.this)
                                    .asBitmap()
                                    .load(userMedia.get(commentUser.get(comment.getId()).getId()).getUrl())
                                    .error(R.drawable.default_avatar)
                                    .placeholder(R.drawable.default_avatar)
                                    .into(img_user_avatar);
                        } else {
                            img_user_avatar.setImageResource(R.drawable.default_avatar);
                        }
                        tv_user_name.setText(commentUser.get(comment.getId()).getName());
                        tv_comment_content.setText(comment.getContent());
                        layout_comments.addView(view);
                    }
                    if (comments.size() >= 3) {
                        btn_show_more.setVisibility(View.VISIBLE);
                        btn_show_more.callOnClick();
                    } else btn_show_more.setVisibility(GONE);
                }).exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void fetchUserForComment(CompletableFuture<Void> future,Comment comment) {
        if (commentUser.containsKey(comment.getId()) && commentUser.get(comment.getId()) != null) {
            fetchMediaForUser(future,commentUser.get(comment.getId()));
            return;
        }
        new FirebaseUtils().getDataFromFirebaseById(REALTIME_USERS, comment.getUserId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                commentUser.put(comment.getId(),user);
                fetchMediaForUser(future,user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void fetchMediaForUser(CompletableFuture<Void> future,User user) {
        if (user.getMediaId() == null || user.getMediaId().isEmpty()) {
            future.complete(null);
            return;
        }
        if (userMedia.containsKey(user.getId())) {
            future.complete(null);
            return;
        }
        new FirebaseUtils().getDataFromFirebaseById(REALTIME_MEDIAS, user.getMediaId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Media media = snapshot.getValue(Media.class);
                userMedia.put(user.getId(),media);
                future.complete(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void fetchRecipeOwner() {
        new FirebaseUtils().getDataFromFirebaseById(REALTIME_USERS, recipe.getUserId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipeOwner = snapshot.getValue(User.class);
                tv_user_name.setText(recipeOwner.getName());
                tv_user_follower.setText("Follower " + recipeOwner.getFollowersCount());
                if (recipeOwner.getMediaId() != null && !recipeOwner.getMediaId().isEmpty()) {
                    fetchMediaForOwner();
                } else {
                    img_user_avatar.setImageResource(R.drawable.default_avatar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void fetchMediaForOwner() {
        new FirebaseUtils().getDataFromFirebaseById(REALTIME_MEDIAS, recipeOwner.getMediaId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ownerMedia = snapshot.getValue(Media.class);
                Glide.with(RecipeDetailActivity.this)
                        .asBitmap()
                        .load(ownerMedia.getUrl())
                        .error(R.drawable.default_avatar)
                        .into(img_user_avatar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void GetData() {
        Intent intent = getIntent();
        recipe = (Recipe) intent.getSerializableExtra("recipe");
        recipeMedia = (Media) intent.getSerializableExtra("recipeMedia");
        recipeOwner = (User) intent.getSerializableExtra("recipeOwner");
        ownerMedia = (Media) intent.getSerializableExtra("userMedia");
        ingredients = (ArrayList<Ingredient>) intent.getSerializableExtra("ingredients");
        instructions = (ArrayList<Instruction>) intent.getSerializableExtra("instructions");
        instructionMedias = (ArrayList<Media>) intent.getSerializableExtra("instructionMedias");
        if (GetUser(this).getMediaId() != null && !GetUser(this).getMediaId().isEmpty()) {
            new FirebaseUtils().getDataFromFirebaseById(REALTIME_MEDIAS, GetUser(this).getMediaId(), new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Media media = snapshot.getValue(Media.class);
                    Glide.with(RecipeDetailActivity.this)
                            .asBitmap()
                            .load(media.getUrl())
                            .error(R.drawable.default_avatar)
                            .placeholder(R.drawable.default_avatar)
                            .into(img_my_avatar);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
    public static void setRecipeDetailEventListener(RecipeDetailEventListener listener) {
        eventListener = listener;
    }
    public static void setRecipeEventListener(RecipeEventListener listener) {
        recipeEventListener = listener;
    }
    private void initUI() {
        databaseReferenceComment = FirebaseDatabase.getInstance().getReference(REALTIME_COMMENTS);

        layout_ingredients = findViewById(R.id.layout_ingredients);
        layout_instructions = findViewById(R.id.layout_instructions);
        layout_comments = findViewById(R.id.layout_comments);

        tv_recipe_name = findViewById(R.id.tv_recipe_name);
        tv_averageRating = findViewById(R.id.tv_averageRating);
        tv_review_count = findViewById(R.id.tv_review_count);
        tv_user_name = findViewById(R.id.tv_user_name);
        tv_user_follower = findViewById(R.id.tv_user_follower);
        tv_ingredient_count = findViewById(R.id.tv_ingredient_count);
        tv_instruction_count = findViewById(R.id.tv_instruction_count);
        tv_comment_status = findViewById(R.id.tv_comment_status);
        tv_most_recent_update = findViewById(R.id.tv_most_recent_update);
        tv_creation_date = findViewById(R.id.tv_creation_date);
        tv_serves = findViewById(R.id.tv_serves);
        tv_cook_time = findViewById(R.id.tv_cook_time);

        img_thumbnail_recipe = findViewById(R.id.img_thumbnail_recipe);
        img_user_avatar = findViewById(R.id.img_user_avatar);
        img_my_avatar = findViewById(R.id.img_my_avatar);

        btn_cook_time = findViewById(R.id.btn_cook_time);
        btn_serves = findViewById(R.id.btn_serves);
        btn_follow = findViewById(R.id.btn_follow);
        btn_play = findViewById(R.id.btn_play);
        btn_send_comment = findViewById(R.id.btn_send_comment);
        btn_show_more = findViewById(R.id.btn_show_more);
        btn_rate_recipe = findViewById(R.id.btn_rate_recipe);

        edt_input_comment = findViewById(R.id.edt_input_comment);

        progressDialog = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
    }
}