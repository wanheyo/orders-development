package com.prim.orders.models.api_respond;

import com.google.gson.annotations.SerializedName;
import com.prim.orders.models.Order_Available_Dish;
import com.prim.orders.models.Order_Cart;

import java.util.ArrayList;

public class API_Resp_getOrderCart {
    @SerializedName("order_available_dish")
    private ArrayList<Order_Available_Dish> orderAvailableDish;

    @SerializedName("cart")
    private Order_Cart orderCart;

    public ArrayList<Order_Available_Dish> getOrderAvailableDish() {
        return orderAvailableDish;
    }

    public Order_Cart getOrderCart() {
        return orderCart;
    }
}
