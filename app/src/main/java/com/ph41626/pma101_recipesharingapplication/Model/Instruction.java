package com.ph41626.pma101_recipesharingapplication.Model;

import java.util.ArrayList;

public class Instruction {
    private String id;
    private String recipeId;
    private String content;
    private ArrayList<String> mediaIds;
    private int order;

    public Instruction() {
    }

    public Instruction(String id, String recipeId, String content, ArrayList<String> mediaIds, int order) {
        this.id = id;
        this.recipeId = recipeId;
        this.content = content;
        this.mediaIds = mediaIds;
        this.order = order;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<String> getMediaIds() {
        return mediaIds;
    }

    public void setMediaIds(ArrayList<String> mediaIds) {
        this.mediaIds = mediaIds;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "Instruction{" +
                "id='" + id + '\'' +
                ", recipeId='" + recipeId + '\'' +
                ", content='" + content + '\'' +
                ", mediaList=" + mediaIds +
                ", order=" + order +
                '}';
    }
}
