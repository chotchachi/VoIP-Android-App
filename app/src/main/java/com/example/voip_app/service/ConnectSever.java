package com.example.voip_app.service;

import android.util.Log;

import com.example.voip_app.App;
import com.example.voip_app.service.eventBus.CallEvent;
import com.example.voip_app.util.CommonConstants;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;

import Model.DataSocket;

import static com.example.voip_app.service.eventBus.CallEvent.BAN_DONG_Y;
import static com.example.voip_app.service.eventBus.CallEvent.BAN_KET_THUC;
import static com.example.voip_app.service.eventBus.CallEvent.NHAN;
import static com.example.voip_app.service.eventBus.CallEvent.TU_CHOI;

public class ConnectSever extends Thread {
    private static Socket socket;
    private DataSocket respon = null;

    @Override
    public void run() {
        super.run();
        try {
            socket = new Socket(CommonConstants.SERVER_IP, CommonConstants.SERVER_PORT);
            try {
                OutputStream outputStream = socket.getOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

                DataSocket dtsk = new DataSocket();
                dtsk.setAction("androidLoginOk");
                String[] data = new String[1];
                data[0] = App.getAccount().getPhoneNumber();
                dtsk.setData(data);

                objectOutputStream.writeObject(dtsk);

                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                while (true) {
                    respon = (DataSocket) ois.readObject();
                    Log.d("xxx", respon.getAction());
                    switch (respon.getAction()) {
                        case "respon_call":
                            responCall(respon);
                            break;
                        case "request_call":
                            requestCall(respon);
                            break;
                        case "endcall":
                            endCall();
                            break;
                        default:
                            Log.d("xxx", "Unknown action");
                    }
                }
            } catch (IOException | ClassNotFoundException e){
                Log.d("xxx", Objects.requireNonNull(e.getMessage()));
            }
        } catch (IOException e) {
            Log.d("xxx", Objects.requireNonNull(e.getMessage()));
        }
    }

    static Socket getSocket(){
        return socket;
    }

    private void requestCall(DataSocket data) {
        Log.d("xxx", "Đã nhận yêu cầu cuộc gọi");

        if (App.CALLING){
            DataSocket dtsk = new DataSocket();
            dtsk.setAction("respon_call");
            dtsk.setNguoiGui(data.getNguoiNhan());
            dtsk.setNguoiNhan(data.getNguoiGui());
            dtsk.setAccept(false);
            try {
                ObjectOutputStream dout = new ObjectOutputStream(socket.getOutputStream());
                dout.writeObject(dtsk);
                Log.d("xxx", "Đã gửi phản hồi: từ chối");
            } catch (IOException e) {
                Log.d("xxx", Objects.requireNonNull(e.getMessage()));
            }
        } else {
            EventBus.getDefault().post(new CallEvent(NHAN, data));
        }
    }

    private void responCall(DataSocket data) {
        Log.d("xxx", data.getNguoiGui().getPhoneNumber()+" đã gửi phản hồi yêu cầu");

        // Từ chối
        if (!data.isAccept()) {
            App.CALLING = false;
            EventBus.getDefault().post(new CallEvent(TU_CHOI, null));
            // Đồng ý
        } else {
            EventBus.getDefault().post(new CallEvent(BAN_DONG_Y, data));
        }
    }

    private void endCall(){
        App.CALLING = false;
        EventBus.getDefault().post(new CallEvent(BAN_KET_THUC, null));
    }
}
