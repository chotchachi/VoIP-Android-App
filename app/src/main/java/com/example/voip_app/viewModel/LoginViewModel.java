package com.example.voip_app.viewModel;

import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.voip_app.model.Account;

public class LoginViewModel extends ViewModel {

    public MutableLiveData<String> phoneNumber = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();

    private MutableLiveData<Account> userMutableLiveData;

    public MutableLiveData<Account> getUser() {
        if (userMutableLiveData == null) {
            userMutableLiveData = new MutableLiveData<>();
        }
        return userMutableLiveData;

    }

    public void onClick(View view) {
        Account account = new Account(phoneNumber.getValue(), password.getValue());
        userMutableLiveData.setValue(account);
    }

    public void login() {
        userMutableLiveData.getValue().login();
    }
}