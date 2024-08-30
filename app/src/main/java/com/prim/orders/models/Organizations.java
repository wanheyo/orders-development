package com.prim.orders.models;

import java.io.Serializable;
import java.sql.Timestamp;

public class Organizations implements Serializable {
    private int id;
    private String code;
    private String email;
    private String nama;
    private String telno;
    private String address;
    private String postcode;
    private String state;
    private String fixed_charges;
    private String remember_token;
    private Timestamp created_at;
    private Timestamp updated_at;
    private int type_org;
    private Timestamp deleted_at;
    private String seller_id;
    private String district;
    private String city;
    private String organization_picture;
    private String description;
    private int parent_org;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getTelno() {
        return telno;
    }

    public void setTelno(String telno) {
        this.telno = telno;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getFixed_charges() {
        return fixed_charges;
    }

    public void setFixed_charges(String fixed_charges) {
        this.fixed_charges = fixed_charges;
    }

    public String getRemember_token() {
        return remember_token;
    }

    public void setRemember_token(String remember_token) {
        this.remember_token = remember_token;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public Timestamp getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Timestamp updated_at) {
        this.updated_at = updated_at;
    }

    public int getType_org() {
        return type_org;
    }

    public void setType_org(int type_org) {
        this.type_org = type_org;
    }

    public Timestamp getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(Timestamp deleted_at) {
        this.deleted_at = deleted_at;
    }

    public String getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getOrganization_picture() {
        return organization_picture;
    }

    public void setOrganization_picture(String organization_picture) {
        this.organization_picture = organization_picture;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getParent_org() {
        return parent_org;
    }

    public void setParent_org(int parent_org) {
        this.parent_org = parent_org;
    }
}
