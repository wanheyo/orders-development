package com.prim.orders.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Order_Available_Dish implements Serializable {
    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("quantity")
    @Expose
    private int quantity;

    @SerializedName("totalprice")
    @Expose
    private double totalprice;

    @SerializedName("delivery_status")
    @Expose
    private String delivery_status;

    @SerializedName("delivery_proof_pic")
    @Expose
    private String delivery_proof_pic;

    @SerializedName("order_desc")
    @Expose
    private String order_desc;

    @SerializedName("order_available_id")
    @Expose
    private int order_available_id;

    @SerializedName("order_cart_id")
    @Expose
    private int order_cart_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(double totalprice) {
        this.totalprice = totalprice;
    }

    public String getDelivery_status() {
        return delivery_status;
    }

    public void setDelivery_status(String delivery_status) {
        this.delivery_status = delivery_status;
    }

    public String getDelivery_proof_pic() {
        return delivery_proof_pic;
    }

    public void setDelivery_proof_pic(String delivery_proof_pic) {
        this.delivery_proof_pic = delivery_proof_pic;
    }

    public String getOrder_desc() {
        return order_desc;
    }

    public void setOrder_desc(String order_desc) {
        this.order_desc = order_desc;
    }

    public int getOrder_available_id() {
        return order_available_id;
    }

    public void setOrder_available_id(int order_available_id) {
        this.order_available_id = order_available_id;
    }

    public int getOrder_cart_id() {
        return order_cart_id;
    }

    public void setOrder_cart_id(int order_cart_id) {
        this.order_cart_id = order_cart_id;
    }
}
