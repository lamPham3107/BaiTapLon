package com.example.fruit_store.models;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class BillModel implements Serializable {
    private String id;
    private String name;
    private String phone;
    private String address;
    private Double totalPrice;
    private List<Map<String, Object>> items;

    public BillModel() {
    }

    public BillModel(String id, String name, String phone, String address, Double totalPrice, List<Map<String, Object>> items) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.totalPrice = totalPrice;
        this.items = items;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Map<String, Object>> getItems() {
        return items;
    }

    public void setItems(List<Map<String, Object>> items) {
        this.items = items;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
