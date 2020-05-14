package com.example.projectteamportal;

import java.util.Date;
import java.util.Map;

public class Post {
    private String title, description, startdate, creatorid, creatorname, createdate;
    private Map createdDate;

    public Post() {

    }

    public Post(String creator, String creatorname, String title, String description, String start_date, String createdate, Map createdDate) {
        this.creatorid = creator;
        this.creatorname = creatorname;
        this.title = title;
        this.description = description;
        this.createdate = createdate;
        this.startdate = start_date;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStartdate() {
        return startdate;
    }

    public String getCreatorid() {
        return creatorid;
    }

    public String getCreatorname() {
        return creatorname;
    }

    public String getCreatedate() {
        return createdate;
    }

    public Map getCreatedDate() { return createdDate; }
}
