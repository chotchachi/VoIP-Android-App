package com.example.voip_app.util.retrofit;

import com.example.voip_app.model.Account;

public interface LoginListener {
    void getDataSuccess(Account account);
    void getMessageError(Exception e);
}
