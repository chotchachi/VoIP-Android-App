package com.example.voip_app.viewModel;

import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.voip_app.Repository.LoginRepository;
import com.example.voip_app.util.retrofit.LoginListener;

public class LoginViewModel extends ViewModel {

    public MutableLiveData<String> phoneNumber = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();

    private MutableLiveData<LoginRepository> userMutableLiveData;

    public MutableLiveData<LoginRepository> getUser() {
        if (userMutableLiveData == null) {
            userMutableLiveData = new MutableLiveData<>();
        }
        return userMutableLiveData;
    }

    public void onClick(View view) {
        LoginRepository loginRepository = new LoginRepository(phoneNumber.getValue(), password.getValue());
        userMutableLiveData.setValue(loginRepository);
    }

    public void login(LoginListener loginListener) {
        userMutableLiveData.getValue().login(loginListener);
    }
}