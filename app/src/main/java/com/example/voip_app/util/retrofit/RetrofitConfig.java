package com.example.voip_app.util.retrofit;

import com.example.voip_app.util.CommonConstants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitConfig {
    private static Retrofit retrofit;

    public static Retrofit getRetrofit() {
        if (retrofit == null)
            return new Retrofit.Builder()
                    .baseUrl(CommonConstants.URL_BASE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        else return retrofit;
    }
}
