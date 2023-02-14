package com.example.moreaboutmoreapp.Models;

public class NotificationData {
    public String types;
    public String title;
    public String detail;
    public String timer;
    public String uidPusher;
    public String uidReceiver;
    public String topic;
    public String id;
    public String postKey;

    public NotificationData() {}

    public NotificationData(String types, String title, String detail, String timer, String uidPusher, String uidReceiver, String topic, String id, String postKey) {
        this.types = types;
        this.title = title;
        this.detail = detail;
        this.timer = timer;
        this.uidPusher = uidPusher;
        this.uidReceiver = uidReceiver;
        this.topic = topic;
        this.id = id;
        this.postKey = postKey;
    }

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getUidPusher() {
        return uidPusher;
    }

    public void setUidPusher(String uidPusher) {
        this.uidPusher = uidPusher;
    }

    public String getUidReceiver() {
        return uidReceiver;
    }

    public void setUidReceiver(String uidReceiver) {
        this.uidReceiver = uidReceiver;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }
}
