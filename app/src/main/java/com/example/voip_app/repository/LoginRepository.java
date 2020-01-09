package com.example.voip_app.repository;

import androidx.annotation.NonNull;

import com.example.voip_app.model.Account;
import com.example.voip_app.util.retrofit.ApiFunc;
import com.example.voip_app.util.retrofit.LoginListener;
import com.example.voip_app.util.retrofit.RegisterListener;
import com.example.voip_app.util.retrofit.RetrofitConfig;
import com.example.voip_app.util.shared.Prefs;
import com.example.voip_app.util.shared.PrefsKey;
import com.google.gson.Gson;

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
        ApiFunc apiFunc = RetrofitConfig.getRetrofit().create(ApiFunc.class);
        Call<ResponseBody> call = apiFunc.loginAccount(phoneNumber, password);
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

    public void register(String name, RegisterListener listener) {
        ApiFunc apiFunc = RetrofitConfig.getRetrofit().create(ApiFunc.class);
        Call<ResponseBody> call = apiFunc.registerAccount(phoneNumber, password, name);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        int status = jsonObject.getInt("status");
                        if (status == 1) {
                            Account account = new Account();
                            account.setId(jsonObject.getInt("id"));
                            account.setPhoneNumber(jsonObject.getString("phone"));
                            account.setName(jsonObject.getString("name"));
                            listener.onRegisterSuccess(account);
                        } else if (status == 0){
                            listener.onRegisterFailed();
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

    public void storeLoginSession(Account account){
        Gson gson = new Gson();
        String json = gson.toJson(account);
        Prefs.getInstance().put(PrefsKey.SESSION_ACCOUNT, json);
    }
}
