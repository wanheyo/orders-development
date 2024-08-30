package com.prim.orders.models.api_respond;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.prim.orders.models.Order_Available_Dish;

import java.io.Serializable;
import java.util.ArrayList;

public class API_Resp implements Serializable {
    @SerializedName("response")
    @Expose
    private String response;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
