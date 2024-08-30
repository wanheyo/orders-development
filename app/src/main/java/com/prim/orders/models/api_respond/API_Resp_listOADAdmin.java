package com.prim.orders.models.api_respond;

import com.google.gson.annotations.SerializedName;
import com.prim.orders.models.Dishes;
import com.prim.orders.models.Order_Available_Dish;
import com.prim.orders.models.Order_Cart;
import com.prim.orders.models.Users;

import java.util.ArrayList;

public class API_Resp_listOADAdmin {
    @SerializedName("order_available_dish")
    private ArrayList<Order_Available_Dish> orderAvailableDishList;

    @SerializedName("users")
    private ArrayList<Users> usersList;

    @SerializedName("order_cart")
    private ArrayList<Order_Cart> orderCartList;

    public ArrayList<Order_Available_Dish> getOrderAvailableDishList() {
        return orderAvailableDishList;
    }

    public void setOrderAvailableDishList(ArrayList<Order_Available_Dish> orderAvailableDishList) {
        this.orderAvailableDishList = orderAvailableDishList;
    }

    public ArrayList<Users> getUsersList() {
        return usersList;
    }

    public void setUsersList(ArrayList<Users> usersList) {
        this.usersList = usersList;
    }

    public ArrayList<Order_Cart> getOrderCartList() {
        return orderCartList;
    }

    public void setOrderCartList(ArrayList<Order_Cart> orderCartList) {
        this.orderCartList = orderCartList;
    }
}
