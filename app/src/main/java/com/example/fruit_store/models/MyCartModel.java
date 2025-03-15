package com.example.fruit_store.models;

import java.io.Serializable;

public class MyCartModel implements Serializable {
    private String fruitName;
    private String fruitPrice;
    private String currentDate;
    private String currentTime;
    private String totalQuantity;
    private Double totalPrice;
    private String doucumentId;

    public MyCartModel() {
    }

    public MyCartModel(String fruitName, String fruitPrice, String currentDate, String currentTime, String totalQuantity, Double totalPrice) {
        this.fruitName = fruitName;
        this.fruitPrice = fruitPrice;
        this.currentDate = currentDate;
        this.currentTime = currentTime;
        this.totalQuantity = totalQuantity;
        this.totalPrice = totalPrice;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getFruitPrice() {
        return fruitPrice;
    }

    public void setFruitPrice(String fruitPrice) {
        this.fruitPrice = fruitPrice;
    }

    public String getFruitName() {
        return fruitName;
    }

    public void setFruitName(String fruitName) {
        this.fruitName = fruitName;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(String totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getDoucumentId() {
        return doucumentId;
    }

    public void setDoucumentId(String doucumentId) {
        this.doucumentId = doucumentId;
    }
}
