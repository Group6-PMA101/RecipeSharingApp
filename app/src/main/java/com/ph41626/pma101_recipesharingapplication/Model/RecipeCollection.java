package com.ph41626.pma101_recipesharingapplication.Model;

import static com.ph41626.pma101_recipesharingapplication.Services.Services.RandomID;

import java.io.Serializable;

public class RecipeCollection implements Serializable {
    private String id;
    private String userId;
    private String name;
    private int numberOfRecipes;

    public RecipeCollection() {
        this.id = RandomID();
        this.numberOfRecipes = 0;
    }

    public RecipeCollection(String userId, String name) {
        this.id = RandomID();
        this.userId = userId;
        this.name = name;
        this.numberOfRecipes = 0;
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

    @Override
    public String toString() {
        return "RecipeCollection{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", numberOfRecipes=" + numberOfRecipes +
                '}';
    }
}
