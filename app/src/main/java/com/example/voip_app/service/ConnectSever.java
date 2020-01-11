package com.example.voip_app.service;

import android.util.Log;

import com.example.voip_app.App;
import com.example.voip_app.service.eventBus.CallAcceptEvent;
import com.example.voip_app.util.CommonConstants;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;

import Model.DataSocket;

public class ConnectSever extends Thread {
    private static Socket socket;
    private ObjectInputStream ois = null;
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

                ois = new ObjectInputStream(socket.getInputStream());
                while (true) {
                    respon = (DataSocket) ois.readObject();
                    System.out.println(respon.getAction());
                    switch (respon.getAction()) {
                        case "respon_call":
                            responCall(respon);
                            break;
                        case "request_call":
                            requestCall(respon);
                            break;
                        case "endcall":
                            endCall(respon);
                            break;
                        default:
                            System.out.println("Unknown action");
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
        System.out.println("Đã nhận yêu cầu cuộc gọi");

    }

    private void responCall(DataSocket data) {
        Log.d("xxx", data.getNguoiGui().getPhoneNumber()+" đã gửi phản hồi yêu cầu");

        // Từ chối
        if (!data.isAccept()) {

            // Đồng ý
        } else {
            EventBus.getDefault().post(new CallAcceptEvent(data));
        }
    }

    private void endCall(DataSocket dataSocket){

    }
}
