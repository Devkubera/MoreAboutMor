package com.example.moreaboutmoreapp.Models;

public class ModelFacultyData
{
    private String id;
    private String Major;
    private String StudyPlan;
    private String CourseDescription;
    private String Faculty;
    private String Website;

    public String getId() {
        return id;
    }

    public ModelFacultyData() {

    }

    public ModelFacultyData(String id, String major, String studyPlan, String courseDescription, String faculty, String website) {
        this.id = id;
        Major = major;
        StudyPlan = studyPlan;
        CourseDescription = courseDescription;
        Faculty = faculty;
        Website = website;
    }

    public String getMajor() {
        return Major;
    }

    public String getStudyPlan() {
        return StudyPlan;
    }

    public String getCourseDescription() {
        return CourseDescription;
    }

    public String getFaculty() {
        return Faculty;
    }

    public String getWebsite() {
        return Website;
    }
}
