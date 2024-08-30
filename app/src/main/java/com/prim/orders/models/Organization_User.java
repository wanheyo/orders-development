package com.prim.orders.models;

import java.io.Serializable;

public class Organization_User implements Serializable {
    private int id;
    private int organization_id;
    private int user_id;
    private int role_id;
    private int status;
    private String fees_status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrganization_id() {
        return organization_id;
    }

    public void setOrganization_id(int organization_id) {
        this.organization_id = organization_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getRole_id() {
        return role_id;
    }

    public void setRole_id(int role_id) {
        this.role_id = role_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getFees_status() {
        return fees_status;
    }

    public void setFees_status(String fees_status) {
        this.fees_status = fees_status;
    }
}
