package com.ph41626.pma101_recipesharingapplication.Model;

public class Comment {
    private String id;
    private String recipeId;
    private String userId;
    private String content;

    public Comment() {
    }

    public Comment(String id, String recipeId, String userId, String content) {
        this.id = id;
        this.recipeId = recipeId;
        this.userId = userId;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
