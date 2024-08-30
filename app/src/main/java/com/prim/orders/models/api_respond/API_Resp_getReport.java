package com.prim.orders.models.api_respond;

import com.google.gson.annotations.SerializedName;
import com.prim.orders.models.Order_Available_Dish;
import com.prim.orders.models.Order_Cart;

import java.util.ArrayList;

public class API_Resp_getReport {
    @SerializedName("dish_name")
    private String dishName;

    @SerializedName("profit")
    private double profit;

    @SerializedName("quantity")
    private int quantity;

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
