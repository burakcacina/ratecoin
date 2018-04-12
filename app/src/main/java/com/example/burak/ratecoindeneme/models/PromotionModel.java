package com.example.burak.ratecoindeneme.models;

import java.util.List;

public class PromotionModel {
    private int promotion_id;
    private int value;
    private String brand;
    private String description;
    private String image;


    public int getPromotion_id() {return promotion_id;}
    public void setPromotion_id(int id) {
        this.promotion_id = id;
    }


    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
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

