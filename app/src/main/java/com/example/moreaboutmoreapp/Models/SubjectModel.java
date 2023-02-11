package com.example.moreaboutmoreapp.Models;

public class SubjectModel {
    private String id;
    private String faculty;
    private String branch;
    private String passcode;
    private String name;
    private String source;

    public SubjectModel(String id, String faculty, String branch, String passcode, String name, String source) {
        this.id = id;
        this.faculty = faculty;
        this.branch = branch;
        this.passcode = passcode;
        this.name = name;
        this.source = source;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
