package com.example.burak.ratecoindeneme.models;

import java.util.List;

public class IssueModel {
    private int id;
    private int rateID;
    private int status;
    private String time;
    private String description;
    private String desc_image;
    private String name;
    private String mail;
    private String image;
    private List<created> createdList;

    public int getid() {
        return id;
    }
    public void setid(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }
    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getDesc_image() {
        return desc_image;
    }
    public void setDesc_image(String desc_image) {
        this.desc_image = desc_image;
    }

    public int getRateID() {
        return rateID;
    }
    public void setRateID(int rateID) { this.rateID = rateID; }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    public List<created> getcreatedList() {
        return createdList;
    }
    public void setCreatedList(List<created> createdList) { this.createdList = createdList; }

    public static class created {
        private String options;
        private int optionsID;

        public String getOptions() { return options; }
        public void setOptions(String options) { this.options = options; }

        public int getoptionsID() { return optionsID; }
        public void setoptionsID(int optionsID) { this.optionsID = optionsID; }
    }
}

