package com.example.moreaboutmoreapp.Models;

import com.google.firebase.database.ServerValue;

public class Post {

    private String postKey;
    private String detailComments;
    private String userId;
    private String userName;
    private String nickName;
    private String selectTag;
    private Object timeStamp;
    private String userPhoto;

    /*public Post(String detailComments, String userId, String userName, String selectTag) {
        this.detailComments = detailComments;
        this.userId = userId;
        this.userName = userName;
        this.selectTag = selectTag;
        this.userPhoto = userPhoto;
        this.timeStamp = ServerValue.TIMESTAMP;
    }*/

    public Post(String detailComments, String userId, String userName, String nickName, String selectTag, String userPhoto) {
        this.detailComments = detailComments;
        this.userId = userId;
        this.userName = userName;
        this.nickName = nickName;
        this.selectTag = selectTag;
        this.timeStamp = ServerValue.TIMESTAMP;
        this.userPhoto = userPhoto;
    }

    public Post() {

    }



    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getDetailComments() {
        return detailComments;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSelectTag() { return selectTag;}



    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setDetailComments(String detailComments) {
        this.detailComments = detailComments;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setSelectTag(String selectTag) { this.selectTag = selectTag; }


    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }
}
