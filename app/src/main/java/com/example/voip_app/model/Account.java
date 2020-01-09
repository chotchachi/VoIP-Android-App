package com.example.voip_app.model;

import android.widget.TextView;

import androidx.databinding.BindingAdapter;

public class Account {
    private int id;
    private String phoneNumber;
    //private String password;
    private String name;

    public Account(){

    }

    public Account(int id, String phoneNumber, /*String password,*/ String name) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        //this.password = password;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @BindingAdapter("phoneNumber")
    public static void setPhoneContact(TextView view, String phoneNumber) {
        view.setText(phoneNumber);
    }
    /*public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }*/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @BindingAdapter("name")
    public static void setNameContact(TextView view, String name) {
        view.setText(name);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", phoneNumber='" + phoneNumber + '\'' +
                /*", password='" + password + '\'' +*/
                ", name='" + name + '\'' +
                '}';
    }
}
