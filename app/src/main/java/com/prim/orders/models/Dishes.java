package com.prim.orders.models;

import java.io.Serializable;
import java.sql.Timestamp;

public class Dishes implements Serializable {
    private int id;
    private String name;
    private double price;
    private String dish_image;
    private Timestamp created_at;
    private Timestamp updated_at;
    private int organ_id;
    private int dish_type;
    private String o_nama;

    //Dishes - Dish_Available
    private int totalOrderAvailable;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDish_image() {
        return dish_image;
    }

    public void setDish_image(String dish_image) {
        this.dish_image = dish_image;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public Timestamp getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Timestamp updated_at) {
        this.updated_at = updated_at;
    }

    public int getOrgan_id() {
        return organ_id;
    }

    public void setOrgan_id(int organ_id) {
        this.organ_id = organ_id;
    }

    public int getDish_type() {
        return dish_type;
    }

    public void setDish_type(int dish_type) {
        this.dish_type = dish_type;
    }

    public String getO_nama() {
        return o_nama;
    }

    public void setO_nama(String o_nama) {
        this.o_nama = o_nama;
    }

    public int getTotalOrderAvailable() {
        return totalOrderAvailable;
    }

    public void setTotalOrderAvailable(int totalOrderAvailable) {
        this.totalOrderAvailable = totalOrderAvailable;
    }
}
