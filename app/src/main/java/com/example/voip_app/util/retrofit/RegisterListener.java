package com.example.voip_app.util.retrofit;

import com.example.voip_app.model.Account;

public interface RegisterListener {
    void onRegisterSuccess(Account account);
    void onRegisterFailed();
    void getMessageError(String e);
}
