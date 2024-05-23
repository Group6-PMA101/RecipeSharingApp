package com.ph41626.pma101_recipesharingapplication.Model;

import java.io.Serializable;

public class Media implements Serializable {
    private String id;
    private String name;
    private String url;
    private boolean isUpload;

    public Media() {
    }

    public Media(String id, String name, String url) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.isUpload = false;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isUpload() {
        return isUpload;
    }

    public void setUpload(boolean upload) {
        isUpload = upload;
    }

    @Override
    public String toString() {
        return "Media{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", isUpload=" + isUpload +
                '}';
    }
}
