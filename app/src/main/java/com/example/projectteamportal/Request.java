package com.example.projectteamportal;

public class Request {

    private String sender, status, senderName,imageuri,projectID,projectName, date;
    public Request() {
    }

    public Request(String sender, String senderName, String projectID, String projectName, String status,String date, String imageuri) {
        this.sender = sender;
        this.projectID = projectID;
        this.projectName = projectName;
        this.status = status;
        this.senderName = senderName;
        this.imageuri = imageuri;
        this.date = date;
    }

    public String getImageuri() {
        return imageuri;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getProjectID() {
        return projectID;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getSender() {
        return sender;
    }

    public String getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }
}
