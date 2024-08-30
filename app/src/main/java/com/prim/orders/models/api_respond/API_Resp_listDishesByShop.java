package com.prim.orders.models.api_respond;

import com.google.gson.annotations.SerializedName;
import com.prim.orders.models.Dishes;

import java.util.ArrayList;

public class API_Resp_listDishesByShop {

    @SerializedName("data")
    private ArrayList<Dishes> data;

    @SerializedName("count")
    private ArrayList<Dishes> count;

    public ArrayList<Dishes> getData() {
        return data;
    }

    public ArrayList<Dishes> getCount() {
        return count;
    }
}
