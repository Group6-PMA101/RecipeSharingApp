package com.ph41626.pma101_recipesharingapplication.Model;

import static com.ph41626.pma101_recipesharingapplication.Services.Services.RandomID;

import java.io.Serializable;

public class Recipe_RecipeCollection implements Serializable {
    private String id;
    private String recipeCollectionId;
    private String recipeId;

    public Recipe_RecipeCollection() {
        this.id = RandomID();
    }

    public Recipe_RecipeCollection(String recipeCollectionId, String recipeId) {
        this.id = RandomID();
        this.recipeCollectionId = recipeCollectionId;
        this.recipeId = recipeId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecipeCollectionId() {
        return recipeCollectionId;
    }

    public void setRecipeCollectionId(String recipeCollectionId) {
        this.recipeCollectionId = recipeCollectionId;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    @Override
    public String toString() {
        return "Recipe_RecipeCollection{" +
                "id='" + id + '\'' +
                ", recipeCollectionId='" + recipeCollectionId + '\'' +
                ", recipeId='" + recipeId + '\'' +
                '}';
    }
}
