package com.prim.orders.utilities;

public class ApiSetup {

    //127.0.0.1
    //192.168.0.106
    //http://10.0.2.2:8000
//    private String baseURL = "http://10.131.77.189:8000";
    private String baseURL = "https://prim.my";

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public String getBaseURL() {
        return baseURL;
    }
}
