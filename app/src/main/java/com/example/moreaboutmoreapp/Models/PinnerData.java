package com.example.moreaboutmoreapp.Models;

public class PinnerData {
    public String uid;
    public String postId;
    public String token;

    public PinnerData() {}

    public PinnerData(String uid, String postId, String token) {
        this.uid = uid;
        this.postId = postId;
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
