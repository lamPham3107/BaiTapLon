package com.example.fruit_store.models;

import java.io.Serializable;

public class FruitModel implements Serializable {
    private String name;
    private String price;
    private String img_url;
    private String description;
    private int quantity;

    private String unit;
    public FruitModel() {
    }

    public FruitModel(String name, String price, String img_url, String description, int quantity,String unit) {
        this.name = name;
        this.price = price;
        this.img_url = img_url;
        this.description = description;
        this.quantity = quantity;
        this.unit = unit;
    }

    public FruitModel(String description, String img_url, String name, String price) {
        this.description = description;
        this.img_url = img_url;
        this.name = name;
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }
}
