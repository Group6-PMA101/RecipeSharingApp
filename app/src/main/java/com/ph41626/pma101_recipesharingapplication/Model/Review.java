package com.ph41626.pma101_recipesharingapplication.Model;

import static com.ph41626.pma101_recipesharingapplication.Services.Services.RandomID;

public class Review {
    private String id;
    private String recipeId;
    private String userId;
    private int ratingValue;
    private String content;

    public Review() {
        this.id = RandomID();
    }

    public Review(String recipeId, String userId, int ratingValue, String content) {
        this.id = RandomID();
        this.recipeId = recipeId;
        this.userId = userId;
        this.ratingValue = ratingValue;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(int ratingValue) {
        this.ratingValue = ratingValue;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
