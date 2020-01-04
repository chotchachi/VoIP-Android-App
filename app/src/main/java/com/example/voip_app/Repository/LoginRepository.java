package com.example.voip_app.Repository;

import androidx.annotation.NonNull;

import com.example.voip_app.model.Account;
import com.example.voip_app.util.retrofit.LoginAccountApi;
import com.example.voip_app.util.retrofit.LoginListener;
import com.example.voip_app.util.retrofit.RetrofitConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginRepository {
    private String phoneNumber;
    private String password;

    public LoginRepository(String phoneNumber, String password) {
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isPasswordLengthGreaterThan5() {
        return getPassword().length() > 5;
    }

    public void login(LoginListener listener){
        LoginAccountApi loginAccountApi = RetrofitConfig.getRetrofit().create(LoginAccountApi.class);
        Call<ResponseBody> call = loginAccountApi.loginAccount(phoneNumber, password);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        int status = jsonObject.getInt("status");
                        if (status == 2) {
                            Account account = new Account();
                            account.setId(jsonObject.getInt("id"));
                            account.setPhoneNumber(jsonObject.getString("phone"));
                            account.setName(jsonObject.getString("name"));
                            listener.onLoginSuccess(account);
                        } else if (status == 1) {
                            listener.onUserNotRegister();
                        } else if (status == 0){
                            listener.onPhoneOrPassWrong();
                        }
                    } catch (JSONException | IOException e) {
                        listener.getMessageError(e.getMessage());
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call,@NonNull Throwable t) {
                listener.getMessageError(t.getMessage());
            }
        });
    }
}
