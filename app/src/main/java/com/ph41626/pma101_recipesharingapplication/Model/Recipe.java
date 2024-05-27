package com.ph41626.pma101_recipesharingapplication.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Recipe implements Serializable {
    private String id;
    private String name;
    private String mediaId;
    private String userId;
    private int servings;
    private int cookTime;
    private Date creationDate;
    private Date lastUpdateDate;
    private boolean isPublic;
    private boolean isStatus;
    private int totalReviews;
    private double averageRating;
    private ArrayList<String> ingredientIds;
    private ArrayList<String> instructionIds;
    private ArrayList<String> commentIds;
    private ArrayList<String> ratingIds;

    public Recipe() {
    }

    public Recipe(String id, String name, String mediaId, String userId, int servings, int cookTime, boolean isPublic, boolean isStatus, ArrayList<String> ingredientIds, ArrayList<String> instructionIds, ArrayList<String> commentIds, ArrayList<String> ratingIds) {
        this.id = id;
        this.name = name;
        this.mediaId = mediaId;
        this.userId = userId;
        this.servings = servings;
        this.cookTime = cookTime;
        this.creationDate = new Date();
        this.lastUpdateDate = null;
        this.isPublic = isPublic;
        this.isStatus = isStatus;
        this.totalReviews = 0;
        this.averageRating = 0;
        this.ingredientIds = ingredientIds;
        this.instructionIds = instructionIds;
        this.commentIds = commentIds;
        this.ratingIds = ratingIds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public int getCookTime() {
        return cookTime;
    }

    public void setCookTime(int cookTime) {
        this.cookTime = cookTime;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public boolean isStatus() {
        return isStatus;
    }

    public void setStatus(boolean status) {
        isStatus = status;
    }

    public int getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(int totalReviews) {
        this.totalReviews = totalReviews;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public ArrayList<String> getIngredientIds() {
        return ingredientIds;
    }

    public void setIngredientIds(ArrayList<String> ingredientIds) {
        this.ingredientIds = ingredientIds;
    }

    public ArrayList<String> getInstructionIds() {
        return instructionIds;
    }

    public void setInstructionIds(ArrayList<String> instructionIds) {
        this.instructionIds = instructionIds;
    }

    public ArrayList<String> getCommentIds() {
        return commentIds;
    }

    public void setCommentIds(ArrayList<String> commentIds) {
        this.commentIds = commentIds;
    }

    public ArrayList<String> getRatingIds() {
        return ratingIds;
    }

    public void setRatingIds(ArrayList<String> ratingIds) {
        this.ratingIds = ratingIds;
    }


    @Override
    public String toString() {
        return "Recipe{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", mediaId='" + mediaId + '\'' +
                ", userId='" + userId + '\'' +
                ", servings=" + servings +
                ", cookTime=" + cookTime +
                ", creationDate=" + creationDate +
                ", lastUpdateDate=" + lastUpdateDate +
                ", isPublic=" + isPublic +
                ", isStatus=" + isStatus +
                ", totalReviews=" + totalReviews +
                ", averageRating=" + averageRating +
                ", ingredientIds=" + ingredientIds +
                ", instructionIds=" + instructionIds +
                ", commentIds=" + commentIds +
                ", ratingIds=" + ratingIds +
                '}';
    }
}
