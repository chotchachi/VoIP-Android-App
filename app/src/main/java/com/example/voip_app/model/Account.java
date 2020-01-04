package com.example.voip_app.model;

public class Account {
    private int id;
    private String phoneNumber;
    //private String password;
    private String name;
    private int status;

    public Account(){

    }

    public Account(int id, String phoneNumber, /*String password,*/ String name, int status) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        //this.password = password;
        this.name = name;
        this.status = status;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", phoneNumber='" + phoneNumber + '\'' +
                /*", password='" + password + '\'' +*/
                ", name='" + name + '\'' +
                ", status=" + status +
                '}';
    }
}
