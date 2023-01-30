package com.example.moreaboutmoreapp.Models;

public class NotificationData {
    public String types;
    public String title;
    public String detail;
    public String timer;

    public NotificationData() {}

    public NotificationData(String types, String title, String detail, String timer) {
        this.types = types;
        this.title = title;
        this.detail = detail;
        this.timer = timer;
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
