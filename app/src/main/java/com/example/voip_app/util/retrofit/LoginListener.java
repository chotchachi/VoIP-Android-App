package com.example.voip_app.util.retrofit;

import com.example.voip_app.model.Account;

public interface LoginListener {
    void onLoginSuccess(Account account);
    void onPhoneOrPassWrong();
    void onUserNotRegister();
    void getMessageError(String e);
}
