package com.example.voip_app.util.retrofit;

import com.example.voip_app.util.CommonConstants;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiFunc {
    @FormUrlEncoded
    @POST(CommonConstants.URL_LOGIN)
    Call<ResponseBody> loginAccount (@Field("phone_number") String phoneNumber, @Field("password") String password);

    @FormUrlEncoded
    @POST(CommonConstants.URL_REGISTER)
    Call<ResponseBody> registerAccount (@Field("phone_number") String username, @Field("password") String password,@Field("name") String name);

    @FormUrlEncoded
    @POST(CommonConstants.URL_LOAD_CONTACT)
    Call<ResponseBody> loadContacts (@Field("phone_number") String phoneNumber);
}
