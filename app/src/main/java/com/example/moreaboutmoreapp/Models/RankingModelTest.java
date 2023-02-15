package com.example.moreaboutmoreapp.Models;

import java.util.ArrayList;

public class RankingModelTest
{
    private ArrayList<String> rankingModelTests;
    private ArrayList<ArrayList<String>> MajorModel;
    private String[] array;
    private String name1;
    private String name2;
    private String name3;
    private String name4;
    private String name5;

    public RankingModelTest(){}


    public RankingModelTest(String n1, String n2, String n3, String n4, String n5) {
        this.name1 = n1;
        this.name2 = n2;
        this.name3 = n3;
        this.name4 = n4;
        this.name5 = n5;
        this.rankingModelTests = new ArrayList<>();
        rankingModelTests.add(name1);
        rankingModelTests.add(name2);
        rankingModelTests.add(name3);
        rankingModelTests.add(name4);
        rankingModelTests.add(name5);
        MajorModel.add(rankingModelTests);
    }

    public ArrayList<ArrayList<String>> getMajorModel() {
        return MajorModel;
    }

    public void setMajorModel(ArrayList<ArrayList<String>> majorModel) {
        MajorModel = majorModel;
    }

    public ArrayList<String> getRankingModelTests() {
        return rankingModelTests;
    }

    public void setRankingModelTests(ArrayList<String> rankingModelTests) {
        this.rankingModelTests = rankingModelTests;
    }

    public String[] getArray() {
        return array;
    }

    public void setArray(String[] array) {
        this.array = array;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getName3() {
        return name3;
    }

    public void setName3(String name3) {
        this.name3 = name3;
    }

    public String getName4() {
        return name4;
    }

    public void setName4(String name4) {
        this.name4 = name4;
    }

    public String getName5() {
        return name5;
    }

    public void setName5(String name5) {
        this.name5 = name5;
    }
}
