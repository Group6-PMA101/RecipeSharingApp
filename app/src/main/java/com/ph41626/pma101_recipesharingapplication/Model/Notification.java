package com.ph41626.pma101_recipesharingapplication.Model;

import static com.ph41626.pma101_recipesharingapplication.Services.Services.RandomID;

import java.util.Date;
import java.util.Random;

public class Notification {
    private String id;
    private String userId;
    private String title;
    private String content;
    private boolean isStatus;
    private Date date;

    public Notification() {
        this.id = RandomID();
        this.isStatus = false;
        this.date = new Date();
    }

    public Notification(String userId,String title, String content) {
        this.id = RandomID();
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.isStatus = false;
        this.date = new Date();
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isStatus() {
        return isStatus;
    }

    public void setStatus(boolean status) {
        isStatus = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
