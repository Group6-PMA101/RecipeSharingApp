package com.ph41626.pma101_recipesharingapplication.Activity;

import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_MEDIAS;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_RECIPES;
import static com.ph41626.pma101_recipesharingapplication.Activity.MainActivity.REALTIME_REVIEWS;
import static com.ph41626.pma101_recipesharingapplication.Services.UserPreferences.GetUser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ph41626.pma101_recipesharingapplication.Model.Media;
import com.ph41626.pma101_recipesharingapplication.Model.Recipe;
import com.ph41626.pma101_recipesharingapplication.Model.Review;
import com.ph41626.pma101_recipesharingapplication.Model.User;
import com.ph41626.pma101_recipesharingapplication.R;
import com.ph41626.pma101_recipesharingapplication.Services.FirebaseUtils;

import java.util.ArrayList;

public class RateRecipeActivity extends AppCompatActivity {
    private TextView tv_user_name;
    private ImageView img_user_avatar;
    private EditText edt_content;
    private Button btn_post;
    private LinearLayout layout_rating;
    private User user = new User();
    private Media media = new Media();
    private Review review = new Review();
    private Recipe recipe = new Recipe();
    private ArrayList<Review> reviews = new ArrayList<>();
    private int ratingValue = 0;
    private boolean isUpdate = false;
    private ProgressDialog progressDialog;
    @Override
    protected void onStop() {
        super.onStop();
        setResult(RESULT_CANCELED);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rate_recipe);

        initUI();
        Intent intent = getIntent();
        recipe = (Recipe) intent.getSerializableExtra("recipe");
        if (recipe.getTotalReviews() > 0) {
            fetchReviewForRecipe();
        }

        SetUpButton();
        SetUpUI();
    }

    private void SetUpButton() {
        btn_post.setOnClickListener(v -> {
            String content = edt_content.getText().toString().trim();

            if (ratingValue == 0) {
                Toast.makeText(this, "Please select a rating value first.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (review.getUserId() != null && !review.getUserId().isEmpty() && review.getUserId().equals(user.getId())){
                isUpdate = true;
            } else {
                review.setRecipeId(recipe.getId());
                review.setUserId(user.getId());
            }

            review.setRatingValue(ratingValue);
            review.setContent(content);
            FirebaseDatabase
                    .getInstance()
                    .getReference(REALTIME_REVIEWS)
                    .child(review.getId())
                    .setValue(review)
                    .addOnCompleteListener(task -> {
                        double total = 0;

                        if (!isUpdate) {
                            total = ratingValue;
                            recipe.setTotalReviews(recipe.getTotalReviews() + 1);
                        }

                        for (int i = 0; i < reviews.size(); i++) {
                            Review review = reviews.get(i);
                            total += review.getRatingValue();
                        }
                        double averageRating = total / recipe.getTotalReviews();
                        double roundedNumber = Math.round(averageRating * 10) / 10.0;
                        recipe.setAverageRating(roundedNumber);

                        FirebaseDatabase
                                .getInstance()
                                .getReference(REALTIME_RECIPES)
                                .child(recipe.getId())
                                .setValue(recipe).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                });
                    });
        });
        for (int i = 0; i < layout_rating.getChildCount(); i++) {
            View view = layout_rating.getChildAt(i);
            int value = i + 1;
            if (view instanceof ImageButton) {
                ImageButton imageButton = (ImageButton) view;
                imageButton.setOnClickListener(v -> {
                    ratingValue = value;
                    UpdateStarColor();
                });
            }

        }
    }
    private void fetchReviewForRecipe() {
        new FirebaseUtils().getAllDataByKey(REALTIME_REVIEWS, "recipeId", recipe.getId(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child:snapshot.getChildren()) {
                    Review review = child.getValue(Review.class);
                    reviews.add(review);

                    if (review.getUserId().equals(user.getId())) {
                        MyReview(review);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void MyReview(Review review) {
        this.review = review;
        ratingValue = review.getRatingValue();
        UpdateStarColor();
        if (review.getContent() != null && !review.getContent().isEmpty()) {
            edt_content.setText(review.getContent());
        }
    }

    private void UpdateStarColor() {
        for (int i = 0; i < layout_rating.getChildCount(); i++) {
            ImageButton imageButton = (ImageButton) layout_rating.getChildAt(i);
            if (i < ratingValue) {
                imageButton.setBackground(getResources().getDrawable(R.drawable.ic_star));
            } else {
                imageButton.setBackground(getResources().getDrawable(R.drawable.ic_star_stroke));
            }
        }
    }
    private void SetUpUI() {
        user = GetUser(this);
        tv_user_name.setText(user.getName());

        if (user.getMediaId() != null && !user.getMediaId().isEmpty()) {
            new FirebaseUtils().getDataFromFirebaseById(REALTIME_MEDIAS, user.getId(), new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    media = snapshot.getValue(Media.class);
                    Glide.with(RateRecipeActivity.this)
                            .asBitmap()
                            .load(media.getUrl())
                            .error(R.drawable.default_avatar)
                            .placeholder(R.drawable.default_avatar)
                            .into(img_user_avatar);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            img_user_avatar.setImageResource(R.drawable.default_avatar);
        }
    }

    private void initUI() {
        tv_user_name = findViewById(R.id.tv_user_name);

        img_user_avatar = findViewById(R.id.img_user_avatar);

        btn_post = findViewById(R.id.btn_post);

        layout_rating = findViewById(R.id.layout_rating);

        edt_content  = findViewById(R.id.edt_content);

        progressDialog = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
    }
}