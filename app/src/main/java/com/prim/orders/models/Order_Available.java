package com.prim.orders.models;

import java.io.Serializable;
import java.util.Date;

public class Order_Available implements Serializable, Comparable<Order_Available> {
    private int id;
    private Date open_date;
    private Date close_date;
    private Date delivery_date;
    private String delivery_address;
    private int quantity;
    private double discount;
    private int dish_id;
    private double latitude;
    private double longitude;
    private String delivery_place_pic;

    //Quantity order available dish
    private int quantity_order;
    private double totalPrice_order;
    private int totalOrderDishAvailable;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getOpen_date() {
        return open_date;
    }

    public void setOpen_date(Date open_date) {
        this.open_date = open_date;
    }

    public Date getClose_date() {
        return close_date;
    }

    public void setClose_date(Date close_date) {
        this.close_date = close_date;
    }

    public Date getDelivery_date() {
        return delivery_date;
    }

    public void setDelivery_date(Date delivery_date) {
        this.delivery_date = delivery_date;
    }

    public String getDelivery_address() {
        return delivery_address;
    }

    public void setDelivery_address(String delivery_address) {
        this.delivery_address = delivery_address;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public int getDish_id() {
        return dish_id;
    }

    public void setDish_id(int dish_id) {
        this.dish_id = dish_id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDelivery_place_pic() {
        return delivery_place_pic;
    }

    public void setDelivery_place_pic(String delivery_place_pic) {
        this.delivery_place_pic = delivery_place_pic;
    }

    public int getQuantity_order() {
        return quantity_order;
    }

    public void setQuantity_order(int quantity_order) {
        this.quantity_order = quantity_order;
    }

    public double getTotalPrice_order() {
        return totalPrice_order;
    }

    public void setTotalPrice_order(double totalPrice_order) {
        this.totalPrice_order = totalPrice_order;
    }

    public int getTotalOrderDishAvailable() {
        return totalOrderDishAvailable;
    }

    public void setTotalOrderDishAvailable(int totalOrderDishAvailable) {
        this.totalOrderDishAvailable = totalOrderDishAvailable;
    }

    @Override
    public int compareTo(Order_Available orderAvailable) {
        if (getDelivery_date() == null || orderAvailable.getDelivery_date() == null)
            return 0;
        return getDelivery_date().compareTo(orderAvailable.getDelivery_date());
    }
}
