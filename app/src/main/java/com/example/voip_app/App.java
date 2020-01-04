package com.example.voip_app;

import android.app.Application;

import com.example.voip_app.util.shared.Prefs;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Prefs.getInstance().init(getApplicationContext());

    }
}
