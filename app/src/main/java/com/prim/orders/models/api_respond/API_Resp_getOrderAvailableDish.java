package com.prim.orders.models.api_respond;

import com.google.gson.annotations.SerializedName;
import com.prim.orders.models.Dishes;
import com.prim.orders.models.Order_Available;
import com.prim.orders.models.Order_Available_Dish;
import com.prim.orders.models.Order_Cart;
import com.prim.orders.models.Organizations;

import java.util.ArrayList;

public class API_Resp_getOrderAvailableDish {
    @SerializedName("order_available_dish")
    private ArrayList<Order_Available_Dish> orderAvailableDish;

    @SerializedName("order_cart")
    private ArrayList<Order_Cart> orderCart;

    @SerializedName("order_available")
    private ArrayList<Order_Available> orderAvailable;

    @SerializedName("dishes")
    private ArrayList<Dishes> dishes;

    @SerializedName("organizations")
    private ArrayList<Organizations> organizations;

    public ArrayList<Order_Available_Dish> getOrderAvailableDish() {
        return orderAvailableDish;
    }

    public ArrayList<Order_Cart> getOrderCart() {
        return orderCart;
    }

    public ArrayList<Order_Available> getOrderAvailable() {
        return orderAvailable;
    }

    public ArrayList<Dishes> getDishes() {
        return dishes;
    }

    public ArrayList<Organizations> getOrganizations() {
        return organizations;
    }
}
