package com.example.moreaboutmoreapp.Models;

import com.google.firebase.database.ServerValue;

public class Comment {
    private String contentComments;
    private String uid;
    private String userName;
    private String nickName;
    private Object timeStamp;
    private String userPhoto;
    private String postKey;
    private String CommentKey;
    private String CommentType;


    public Comment() {
    }

    public Comment(String contentComments, String uid, String userName, String nickName, String userPhoto, String CommentType) {
        this.contentComments = contentComments;
        this.uid = uid;
        this.userName = userName;
        this.nickName = nickName;
        this.timeStamp = ServerValue.TIMESTAMP;
        this.userPhoto = userPhoto;
        this.CommentType = CommentType;
    }

    public Comment(String contentComments, String uid, String userName, String nickName, Object timeStamp, String userPhoto) {
        this.contentComments = contentComments;
        this.uid = uid;
        this.userName = userName;
        this.nickName = nickName;
        this.timeStamp = timeStamp;
        this.userPhoto = userPhoto;
    }

    public String getCommentType() {
        return CommentType;
    }

    public void setCommentType(String commentType) {
        CommentType = commentType;
    }

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getCommentKey() { return CommentKey;}

    public void setCommentKey(String commentKey) {
        CommentKey = commentKey;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getContentComments() {
        return contentComments;
    }

    public void setContentComments(String contentComments) {
        this.contentComments = contentComments;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }
}
