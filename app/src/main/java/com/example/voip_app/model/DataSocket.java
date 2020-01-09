package com.example.voip_app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataSocket implements Serializable{
    private static final long serialVersionUID = 4L;            //Default serial version uid

    private String action = null;
    private String[] data;
    private ArrayList<String[]> data_arr;
    private Account nguoiGui;
    private List<Account> accountList;
    private boolean accept = false;
    private Account nguoiNhan;

    public DataSocket(){

    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String[] getData() {
        return data;
    }

    public void setData(String[] data) {
        this.data = data;
    }

    public ArrayList<String[]> getData_arr() {
        return data_arr;
    }

    public void setData_arr(ArrayList<String[]> data_arr) {
        this.data_arr = data_arr;
    }

    public boolean isAccept() {
        return accept;
    }

    public void setAccept(boolean accept) {
        this.accept = accept;
    }

    public Account getNguoiGui() {
        return nguoiGui;
    }

    public void setNguoiGui(Account account) {
        this.nguoiGui = account;
    }

    public List<Account> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<Account> accountList) {
        this.accountList = accountList;
    }

    public Account getNguoiNhan() {
        return nguoiNhan;
    }

    public void setNguoiNhan(Account guestAccount) {
        this.nguoiNhan = guestAccount;
    }


}
