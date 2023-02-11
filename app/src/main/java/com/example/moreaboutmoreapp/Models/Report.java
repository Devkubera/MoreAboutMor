package com.example.moreaboutmoreapp.Models;

import com.google.firebase.database.ServerValue;

public class Report {

    private String postKey;
    private String detailComments;
    private String userId;
    private String userName;
    private String nickName;
    private String selectTag;
    private Object timeStamp;
    private String userPhoto;

    private String userReport;
    private String tagReport;
    private Object timeStampReport;

    public Report(String postKey, String detailComments, String userId, String userName, String nickName, String selectTag, Object timeStamp, String userPhoto, String userReport, String tagReport) {
        this.postKey = postKey;
        this.detailComments = detailComments;
        this.userId = userId;
        this.userName = userName;
        this.nickName = nickName;
        this.selectTag = selectTag;
        this.timeStamp = timeStamp;
        this.userPhoto = userPhoto;
        this.userReport = userReport;
        this.tagReport = tagReport;
        this.timeStampReport = ServerValue.TIMESTAMP;
    }

    public Report() {
    }

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getDetailComments() {
        return detailComments;
    }

    public void setDetailComments(String detailComments) {
        this.detailComments = detailComments;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getSelectTag() {
        return selectTag;
    }

    public void setSelectTag(String selectTag) {
        this.selectTag = selectTag;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getUserReport() {
        return userReport;
    }

    public void setUserReport(String userReport) {
        this.userReport = userReport;
    }

    public String getTagReport() {
        return tagReport;
    }

    public void setTagReport(String tagReport) {
        this.tagReport = tagReport;
    }

    public Object getTimeStampReport() {
        return timeStampReport;
    }

    public void setTimeStampReport(Object timeStampReport) {
        this.timeStampReport = timeStampReport;
    }

}
