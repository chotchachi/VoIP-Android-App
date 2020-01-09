package com.example.voip_app.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.voip_app.App;
import com.example.voip_app.model.Account;
import com.example.voip_app.util.retrofit.ApiFunc;
import com.example.voip_app.util.retrofit.RetrofitConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactRepository {
    private MutableLiveData<List<Account>> mutableLiveData = new MutableLiveData<>();

    public MutableLiveData<List<Account>> getContacts(GetContactListener listener){
        List<Account> accountList = new ArrayList<>();
        ApiFunc apiFunc = RetrofitConfig.getRetrofit().create(ApiFunc.class);
        Call<ResponseBody> call = apiFunc.loadContacts(App.getAccount().getPhoneNumber());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        for(int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject object = jsonArray.getJSONObject(i);
                            accountList.add(new Account(object.getInt("id"), object.getString("phone_number"), object.getString("name")));
                        }
                        mutableLiveData.setValue(accountList);
                    } catch (JSONException | IOException e) {
                        listener.onGetContactError(e.getMessage());
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                listener.onGetContactError(t.getMessage());
            }
        });

        return mutableLiveData;
    }

    public interface GetContactListener{
        void onGetContactError(String exception);
    }
}
