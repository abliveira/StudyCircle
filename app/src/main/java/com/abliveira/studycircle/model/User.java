package com.abliveira.studycircle.model;

import androidx.annotation.Nullable;

public class User {

    private String uid;
    private String username;
    private Boolean userType;
    @Nullable private String urlPicture;

    public User() { }

    public User(String uid, String username, @Nullable String urlPicture) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.userType = false;
    }

    public String getUid() { return uid; }
    public String getUsername() { return username; }
    public String getUrlPicture() { return urlPicture; }
    public Boolean getUserType() { return userType; }

    public void setUsername(String username) { this.username = username; }
    public void setUid(String uid) { this.uid = uid; }
    public void setUrlPicture(String urlPicture) { this.urlPicture = urlPicture; }
    public void setUserType(Boolean userType) { this.userType = userType; }
}