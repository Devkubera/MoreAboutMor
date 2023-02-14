package com.example.moreaboutmoreapp.Models;

public class upSubjectToFirebase {
    private String name;
    private String code;
    private String uid;
    private String docKey;
    private String faculty;
    private String branch;

    public upSubjectToFirebase(String name, String code, String uid, String docKey, String faculty, String branch) {
       this.name = name;
       this.code = code;
       this.uid = uid;
       this.docKey = docKey;
       this.faculty = faculty;
       this.branch = branch;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDocKey() {
        return docKey;
    }

    public void setDocKey(String docKey) {
        this.docKey = docKey;
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
}
