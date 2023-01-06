package com.example.moreaboutmoreapp.Models;

import java.util.ArrayList;

public class ModelStudyPlan {

    ArrayList<data> data;

    public ModelStudyPlan(ArrayList<ModelStudyPlan.data> snapshot) {
        this.data = snapshot;
    }

    public ArrayList<ModelStudyPlan.data> getData() {
        return data;
    }

    public void setData(ArrayList<ModelStudyPlan.data> data) {
        this.data = data;
    }



    public class data {
        String id;
        String Major;
        String StudyPlan;
        String CourseDescription;
        String Faculty;
        String Website;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMajor() {
            return Major;
        }

        public void setMajor(String major) {
            Major = major;
        }

        public String getStudyPlan() {
            return StudyPlan;
        }

        public void setStudyPlan(String studyPlan) {
            StudyPlan = studyPlan;
        }

        public String getCourseDescription() {
            return CourseDescription;
        }

        public void setCourseDescription(String courseDescription) {
            CourseDescription = courseDescription;
        }

        public String getFaculty() {
            return Faculty;
        }

        public void setFaculty(String faculty) {
            Faculty = faculty;
        }

        public String getWebsite() {
            return Website;
        }

        public void setWebsite(String website) {
            Website = website;
        }
    }

}
