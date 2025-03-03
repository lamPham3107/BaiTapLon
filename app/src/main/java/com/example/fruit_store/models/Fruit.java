package com.example.fruit_store.models;

public class Fruit {
    String Fruit_Name;
    String Fruit_Price;
    int Image_Res;

    public Fruit(String fruit_Name, String fruit_Price, int image_Res) {
        Fruit_Name = fruit_Name;
        Fruit_Price = fruit_Price;
        Image_Res = image_Res;
    }

    public int getImage_Res() {
        return Image_Res;
    }

    public void setImage_Res(int image_Res) {
        Image_Res = image_Res;
    }

    public String getFruit_Name() {
        return Fruit_Name;
    }

    public void setFruit_Name(String fruit_Name) {
        Fruit_Name = fruit_Name;
    }

    public String getFruit_Price() {
        return Fruit_Price;
    }

    public void setFruit_Price(String fruit_Price) {
        Fruit_Price = fruit_Price;
    }
}
