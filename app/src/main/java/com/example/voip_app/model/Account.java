package com.example.voip_app.model;

public class Account {
    private int id;
    private int phoneNumber;
    private String password;
    private String nane;
    private int status;

    Account(){

    }

    public Account(int id, int phoneNumber, String password, String nane, int status) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.nane = nane;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNane() {
        return nane;
    }

    public void setNane(String nane) {
        this.nane = nane;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
