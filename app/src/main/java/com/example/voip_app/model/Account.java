package com.example.voip_app.model;

import android.util.Log;

import com.example.voip_app.util.retrofit.LoginAccountApi;
import com.example.voip_app.util.retrofit.RetrofitConfig;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Account implements Serializable {
    @SerializedName("id")
    private int id;
    @SerializedName("phone_number")
    private String phoneNumber;
    @SerializedName("password")
    private String password;
    @SerializedName("name")
    private String name;
    @SerializedName("online")
    private int status;

    Account(){

    }

    public Account(String phoneNumber, String password) {
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public Account(int id, String phoneNumber, String password, String name, int status) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

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

    public boolean isPasswordLengthGreaterThan5() {
        return getPassword().length() > 5;
    }

    public void login(){
        LoginAccountApi loginAccountApi = RetrofitConfig.getRetrofit().create(LoginAccountApi.class);
        Call<ResponseBody> call = loginAccountApi.loginAccount(phoneNumber, password);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        int status = jsonObject.getInt("status");
                        if (status == 1) {
                            /*Account account = new Account();
                            account.setUserName(jsonObject.getString("user_name"));
                            account.setEmail(jsonObject.getString("email"));
                            listener.getDataSuccess(account);*/
                            Log.d("xxx", jsonObject.getString("phone_number"));
                        } else {
                            Log.d("xxx", "onResponse: "+jsonObject.toString());
                        }

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("xxx", Objects.requireNonNull(t.getMessage()));
            }
        });
    }
}
