package com.prim.orders.models;

import java.io.Serializable;
import java.sql.Timestamp;

public class Order_Cart implements Serializable {
    private int id;
    private String order_status;
    private double totalamount;
    private Timestamp created_at;
    private Timestamp updated_at;
    private String cart_desc;
    private int user_id;
    private int organ_id;
    private int transactions_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public double getTotalamount() {
        return totalamount;
    }

    public void setTotalamount(double totalamount) {
        this.totalamount = totalamount;
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

    public String getCart_desc() {
        return cart_desc;
    }

    public void setCart_desc(String cart_desc) {
        this.cart_desc = cart_desc;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getOrgan_id() {
        return organ_id;
    }

    public void setOrgan_id(int organ_id) {
        this.organ_id = organ_id;
    }

    public int getTransactions_id() {
        return transactions_id;
    }

    public void setTransactions_id(int transactions_id) {
        this.transactions_id = transactions_id;
    }
}
