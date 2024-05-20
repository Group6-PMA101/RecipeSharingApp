package com.ph41626.pma101_recipesharingapplication.Model;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String name;
    private String email;
    private String password;
    private boolean isStatus; //True = The account is locked; False = The account is active
    private int accountType; //0 = Normal user, 1 = Chef of cook
    private String mediaId;
    private int followersCount; //The number of followers for the user
    private int followingCount; //The number of users that the user is following
    public User() {
    }

    public User(String id, String name, String email, String password, int accountType) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.isStatus = false;
        this.accountType = accountType;
        this.mediaId = "";
        this.followersCount = 0;
        this.followingCount = 0;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isStatus() {
        return isStatus;
    }

    public void setStatus(boolean status) {
        isStatus = status;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", isStatus=" + isStatus +
                ", accountType=" + accountType +
                ", mediaId='" + mediaId + '\'' +
                ", followersCount=" + followersCount +
                ", followingCount=" + followingCount +
                '}';
    }
}
