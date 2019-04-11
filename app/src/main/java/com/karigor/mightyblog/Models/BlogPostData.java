package com.karigor.mightyblog.Models;

import com.google.firebase.database.ServerValue;

public class BlogPostData {

    private String title,description,userId,picture,userPhoto,postKey;
    private Object timestamp;

    public BlogPostData(String title, String description, String userId, String picture, String userPhoto) {
        this.title = title;
        this.description = description;
        this.userId = userId;
        this.picture = picture;
        this.userPhoto = userPhoto;
        this.timestamp = ServerValue.TIMESTAMP;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getPostKey() {
        return postKey;
    }

    public BlogPostData() {
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUserId() {
        return userId;
    }

    public String getPicture() {
        return picture;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public Object getTimestamp() {
        return timestamp;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }
}
