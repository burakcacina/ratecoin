package com.example.burak.ratecoindeneme.models;

import java.util.List;

public class PromotionModel {
    private int id;
    private int cost;
    private String costPK;
    private String brand;
    private String description;
    private String image;


    public int getid() {return id;}
    public void getid(int id) {
        this.id = id;
    }

    public int getcost() {
        return cost;
    }
    public void setcost(int cost) {
        this.cost = cost;
    }

    public String getcostPK() {
        return costPK;
    }
    public void setcostPK(String costPK) {
        this.costPK = costPK;
    }

    public String getBrand() {
        return brand;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

}

