package com.karigor.mightyblog.Models;

import com.google.firebase.database.ServerValue;

public class commentsData {

    private Object timeStamp;
    private String comment,userId,userImage,userName;

    public commentsData(String comment, String userId, String userImage, String userName) {
        this.timeStamp = timeStamp;
        this.comment = comment;
        this.userId = userId;
        this.userImage = userImage;
        this.userName = userName;
        this.timeStamp= ServerValue.TIMESTAMP;
    }


    public commentsData() {
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
