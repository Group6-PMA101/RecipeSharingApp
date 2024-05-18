package com.ph41626.pma101_recipesharingapplication.Model;

public class RecipeCollection {
    private String id;
    private String userId;
    private String name;
    private int numberOfRecipes;

    public RecipeCollection() {
    }

    public RecipeCollection(String id, String userId, String name, int numberOfRecipes) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.numberOfRecipes = numberOfRecipes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfRecipes() {
        return numberOfRecipes;
    }

    public void setNumberOfRecipes(int numberOfRecipes) {
        this.numberOfRecipes = numberOfRecipes;
    }
}
