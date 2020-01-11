package com.example.voip_app.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.voip_app.MakeCallActivity;
import com.example.voip_app.model.Account;
import com.example.voip_app.service.eventBus.CallRequestEvent;
import com.example.voip_app.util.CommonConstants;
import com.example.voip_app.util.NetUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Objects;

import Model.DataSocket;

import static com.example.voip_app.util.CommonConstants.EXTRA_CONTACT;

public class CallService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        Log.d("xxx", "Call Service start");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onCallRequestEvent(CallRequestEvent event) {
        new Thread(() -> {
            try {
                OutputStream outputStream = ConnectSever.getSocket().getOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

                DataSocket dtsk = new DataSocket();
                dtsk.setAction("request_call");
                dtsk.setNguoiGui(event.getNguoiGui());
                dtsk.setNguoiNhan(event.getNguoiNhan());
                String[] data = new String[2];
                data[0] = NetUtils.getIPAddress(getApplicationContext());
                data[1] = String.valueOf(CommonConstants.MY_PORT);
                dtsk.setData(data);

                objectOutputStream.writeObject(dtsk);

                Intent intent = new Intent(this, MakeCallActivity.class);
                intent.putExtra(EXTRA_CONTACT, new Account(event.getNguoiNhan().getId(), event.getNguoiNhan().getPhoneNumber(), event.getNguoiNhan().getName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } catch (IOException e){
                Log.d("xxx", Objects.requireNonNull(e.getMessage()));
            }
        }).start();
    }
}
