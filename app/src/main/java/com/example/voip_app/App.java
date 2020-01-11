package com.example.voip_app;

import android.app.Application;

import com.example.voip_app.model.Account;
import com.example.voip_app.util.shared.Prefs;
import com.example.voip_app.util.shared.PrefsKey;
import com.google.gson.Gson;

public class App extends Application {
    public static boolean CALLING = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Prefs.getInstance().init(getApplicationContext());

    }

    public static Account getAccount(){
        return new Gson().fromJson(Prefs.getInstance().get(PrefsKey.SESSION_ACCOUNT, String.class), Account.class);
    }
}
