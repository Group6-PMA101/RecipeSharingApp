package com.ph41626.pma101_recipesharingapplication.Model;

import static com.ph41626.pma101_recipesharingapplication.Services.Services.RandomID;

import java.util.Date;

public class UserFollower {
    private String id;
    private String userId;
    private String chefId;
    private Date followDate;

    public UserFollower() {
        this.id = RandomID();
    }

    public UserFollower(String userId, String chefId, Date followDate) {
        this.id = RandomID();
        this.userId = userId;
        this.chefId = chefId;
        this.followDate = followDate;
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

    public String getChefId() {
        return chefId;
    }

    public void setChefId(String chefId) {
        this.chefId = chefId;
    }

    public Date getFollowDate() {
        return followDate;
    }

    public void setFollowDate(Date followDate) {
        this.followDate = followDate;
    }
}
