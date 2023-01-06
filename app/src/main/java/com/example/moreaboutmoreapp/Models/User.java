package com.example.moreaboutmoreapp.Models;

public class User {

    public String userId;
    public String email;
    public String major;
    public String subMajor;
    public String name;
    public String userPhoto;

    public User() {

    }

    public User(String userId, String email, String major, String subMajor, String name, String userPhoto) {
        this.userId = userId;
        this.email = email;
        this.major = major;
        this.subMajor = subMajor;
        this.name = name;
        this.userPhoto = userPhoto;
    }

    public String getUserId() {
        return userId;
    }

    public void getUserId(String userKey) {
        this.userId = userKey;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getSubMajor() {
        return subMajor;
    }

    public void setSubMajor(String subMajor) {
        this.subMajor = subMajor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }
}
