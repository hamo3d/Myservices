package com.example.myservices.Adapter;

public class MostSarvices {
String name;
 private int iamge;
String Job;
String sall;



    public MostSarvices(String name, String job, String sall,int iamge) {
        this.name = name;
        this.iamge = iamge;
        Job = job;
        this.sall = sall;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIamge() {
        return iamge;
    }

    public void setIamge(int iamge) {
        this.iamge = iamge;
    }

    public String getJob() {
        return Job;
    }

    public void setJob(String job) {
        Job = job;
    }

    public String getSall() {
        return sall;
    }

    public void setSall(String sall) {
        this.sall = sall;
    }
}
