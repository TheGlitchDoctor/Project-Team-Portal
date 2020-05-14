package com.example.projectteamportal;

public class User {
    User(){

    }
    private String name, email, college, profilePic,about,skills;
    User(String Name, String Email, String College){
        this.name = Name;
        this.email = Email;
        this.college = College;
    }


    public User(String name, String email, String college, String profilePic) {
        this.name = name;
        this.email = email;
        this.college = college;
        this.profilePic = profilePic;
    }

    User(String name, String email, String college, String profilePic, String about , String skills) {
        this.name = name;
        this.email = email;
        this.college = college;
        this.profilePic = profilePic;
        this.about = about;
        this.skills = skills;

    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public String getAbout() {
        return about;
    }

    public String getSkills() {
        return skills;
    }

    public String getCollege() {
        return college;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}

