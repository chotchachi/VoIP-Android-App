package com.example.voip_app.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.voip_app.App;
import com.example.voip_app.AudioCall;
import com.example.voip_app.MakeCallActivity;
import com.example.voip_app.ReceiveCallActivity;
import com.example.voip_app.service.eventBus.CallEvent;
import com.example.voip_app.util.CommonConstants;
import com.example.voip_app.util.NetUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

import Model.DataSocket;

import static com.example.voip_app.service.eventBus.CallEvent.BAN_DONG_Y;
import static com.example.voip_app.service.eventBus.CallEvent.BAN_KET_THUC;
import static com.example.voip_app.service.eventBus.CallEvent.GUI;
import static com.example.voip_app.service.eventBus.CallEvent.KET_THUC_VIEW;
import static com.example.voip_app.service.eventBus.CallEvent.NGUOI_GUI_END;
import static com.example.voip_app.service.eventBus.CallEvent.NGUOI_NHAN_END;
import static com.example.voip_app.service.eventBus.CallEvent.NHAN;
import static com.example.voip_app.service.eventBus.CallEvent.TOI_DONG_Y;
import static com.example.voip_app.service.eventBus.CallEvent.TOI_TU_CHOI;
import static com.example.voip_app.util.CommonConstants.EXTRA_CONTACT;
import static com.example.voip_app.util.CommonConstants.EXTRA_DATA_SOCKET;

public class CallService extends Service {
    private AudioCall audioCall;

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
    public void onEvent(CallEvent event) {
        if (event.getAction() == GUI){
            new Thread(() -> {
                try {
                    Log.d("xxx", "Đã gửi yêu cầu cuộc gọi");

                    OutputStream outputStream = ConnectSever.getSocket().getOutputStream();
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

                    DataSocket dtsk = new DataSocket();
                    dtsk.setAction("request_call");
                    dtsk.setNguoiGui(event.getDataSocket().getNguoiGui());
                    dtsk.setNguoiNhan(event.getDataSocket().getNguoiNhan());
                    String[] data = new String[2];
                    data[0] = NetUtils.getIPAddress(getApplicationContext());
                    data[1] = String.valueOf(CommonConstants.AUDIO_PORT);
                    dtsk.setData(data);

                    objectOutputStream.writeObject(dtsk);

                    App.CALLING = true;

                    Intent intent = new Intent(this, MakeCallActivity.class);
                    Bundle bundle = new Bundle();

                    bundle.putSerializable(EXTRA_CONTACT, event.getDataSocket().getNguoiNhan());
                    bundle.putSerializable(EXTRA_DATA_SOCKET, event.getDataSocket());
                    intent.putExtras(bundle);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (IOException e){
                    Log.d("xxx", Objects.requireNonNull(e.getMessage()));
                }
            }).start();
        }
        else if (event.getAction() == NHAN){
            App.CALLING = true;

            Intent intent = new Intent(this, ReceiveCallActivity.class);
            Bundle bundle = new Bundle();

            bundle.putSerializable(EXTRA_CONTACT, event.getDataSocket().getNguoiGui());
            bundle.putSerializable(EXTRA_DATA_SOCKET, event.getDataSocket());
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else if (event.getAction() == TOI_DONG_Y){
            new Thread(() -> {
                DataSocket dtsk = new DataSocket();
                dtsk.setAction("respon_call");
                dtsk.setNguoiGui(event.getDataSocket().getNguoiNhan());
                dtsk.setNguoiNhan(event.getDataSocket().getNguoiGui());
                String[] data = new String[2];
                data[0] = NetUtils.getIPAddress(getApplicationContext());
                data[1] = String.valueOf(CommonConstants.AUDIO_PORT);
                dtsk.setData(data);
                dtsk.setAccept(true);
                try {
                    ObjectOutputStream dout = new ObjectOutputStream(ConnectSever.getSocket().getOutputStream());
                    dout.writeObject(dtsk);
                    Log.d("xxx", "Đã gửi phản hồi: đồng ý");
                } catch (IOException e) {
                    Log.d("xxx", Objects.requireNonNull(e.getMessage()));
                }
            }).start();

            try {
                audioCall = new AudioCall(InetAddress.getByName(event.getDataSocket().getData()[0]), Integer.parseInt(event.getDataSocket().getData()[1]));
                audioCall.startCall();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        else if (event.getAction() == BAN_DONG_Y){
            try {
                audioCall = new AudioCall(InetAddress.getByName(event.getDataSocket().getData()[0]), Integer.parseInt(event.getDataSocket().getData()[1]));
                audioCall.startCall();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        else if (event.getAction() == NGUOI_GUI_END){
            App.CALLING = false;

            if (audioCall != null){
                audioCall.endCall();
            }

            new Thread(() -> {
                DataSocket dtsk = new DataSocket();
                dtsk.setAction("endcall");
                dtsk.setNguoiGui(event.getDataSocket().getNguoiGui());
                dtsk.setNguoiNhan(event.getDataSocket().getNguoiNhan());

                try {
                    ObjectOutputStream dout = new ObjectOutputStream(ConnectSever.getSocket().getOutputStream());
                    dout.writeObject(dtsk);
                } catch (IOException e) {
                    Log.d("xxx", Objects.requireNonNull(e.getMessage()));
                }
            }).start();
        }
        else if (event.getAction() == NGUOI_NHAN_END){
            App.CALLING = false;

            if (audioCall != null){
                audioCall.endCall();
            }

            new Thread(() -> {
                DataSocket dtsk = new DataSocket();
                dtsk.setAction("endcall");
                dtsk.setNguoiGui(event.getDataSocket().getNguoiNhan());
                dtsk.setNguoiNhan(event.getDataSocket().getNguoiGui());

                try {
                    ObjectOutputStream dout = new ObjectOutputStream(ConnectSever.getSocket().getOutputStream());
                    dout.writeObject(dtsk);
                } catch (IOException e) {
                    Log.d("xxx", Objects.requireNonNull(e.getMessage()));
                }
            }).start();

            EventBus.getDefault().post(new CallEvent(KET_THUC_VIEW, null));
        }
        else if (event.getAction() == BAN_KET_THUC){
            App.CALLING = false;
            if (audioCall != null){
                audioCall.endCall();
            }

            EventBus.getDefault().post(new CallEvent(KET_THUC_VIEW, null));
        }
        else if (event.getAction() == TOI_TU_CHOI){
            App.CALLING = false;

            new Thread(() -> {
                DataSocket dtsk = new DataSocket();
                dtsk.setAction("respon_call");
                dtsk.setNguoiGui(event.getDataSocket().getNguoiNhan());
                dtsk.setNguoiNhan(event.getDataSocket().getNguoiGui());
                dtsk.setAccept(false);

                try {
                    ObjectOutputStream dout = new ObjectOutputStream(ConnectSever.getSocket().getOutputStream());
                    dout.writeObject(dtsk);
                    Log.d("xxx", "Đã gửi phản hồi: từ chối");
                } catch (IOException e) {
                    Log.d("xxx", Objects.requireNonNull(e.getMessage()));
                }
            }).start();
        }
    }
}
