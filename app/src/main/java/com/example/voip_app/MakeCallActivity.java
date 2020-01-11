package com.example.voip_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.voip_app.model.Account;
import com.example.voip_app.service.eventBus.CallAcceptEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import static com.example.voip_app.util.CommonConstants.EXTRA_CONTACT;

public class MakeCallActivity extends AppCompatActivity {
    private static final int BROADCAST_PORT = 50002;
    private Account receiveAccount;

    private String displayName;
    private String contactIp;
    private boolean LISTEN = true;
    private boolean IN_CALL = false;
    private AudioCall call;

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_call);


        Intent intent = getIntent();
        receiveAccount = intent.getParcelableExtra(EXTRA_CONTACT);

        //displayName = intent.getStringExtra(EXTRA_DISPLAYNAME);
        //contactIp = intent.getStringExtra(EXTRA_IP);

        TextView textView = findViewById(R.id.textViewCalling);
        textView.setText(receiveAccount.getName() + "-" + receiveAccount.getPhoneNumber());

        /*startListener();
        makeCall();

        Button endButton = findViewById(R.id.buttonEndCall);
        endButton.setOnClickListener(v -> endCall());*/
    }

    @Subscribe
    public void onCallAcceptEvent(CallAcceptEvent event) {
        Log.d("xxx", event.getDataSocket().getData()[0]+"-"+event.getDataSocket().getData()[1]);
        try {
            call = new AudioCall(InetAddress.getByName(event.getDataSocket().getData()[0]), Integer.parseInt(event.getDataSocket().getData()[1]));
            call.startCall();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void makeCall() {
        sendMessage("CAL:"+displayName, 50003);
    }

    private void endCall() {
        // Ends the chat sessions
        stopListener();
        if(IN_CALL) {

            call.endCall();
        }
        sendMessage("END:", BROADCAST_PORT);
        finish();
    }

    private void startListener() {
        LISTEN = true;
        Thread listenThread = new Thread(() -> {
            try {
                Log.i("xxx", "Listener started!");
                DatagramSocket socket = new DatagramSocket(BROADCAST_PORT);
                socket.setSoTimeout(15000);
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, 1024);
                while(LISTEN) {
                    try {
                        Log.i("xxx", "Listening for packets");
                        socket.receive(packet);
                        String data = new String(buffer, 0, packet.getLength());
                        Log.i("xxx", "Packet received from "+ packet.getAddress() +" with contents: " + data);
                        String action = data.substring(0, 4);
                        switch (action) {
                            case "ACC:":
                                call = new AudioCall(packet.getAddress(), 1);
                                call.startCall();
                                IN_CALL = true;
                                break;
                            case "REJ:":
                            case "END:":
                                endCall();
                                break;
                            default:
                                Log.w("xxx", packet.getAddress() + " sent invalid message: " + data);
                                break;
                        }
                    }
                    catch(SocketTimeoutException e) {
                        if(!IN_CALL) {

                            Log.i("xxx", "No reply from contact. Ending call");
                            endCall();
                            return;
                        }
                    }
                    catch(IOException e) {

                    }
                }
                Log.i("xxx", "Listener ending");
                socket.disconnect();
                socket.close();
                return;
            }
            catch(SocketException e) {

                Log.e("xxx", "SocketException in Listener");
                endCall();
            }
        });
        listenThread.start();
    }

    private void stopListener() {
        // Ends the listener thread
        LISTEN = false;
    }

    private void sendMessage(final String message, final int port) {
        Thread replyThread = new Thread(() -> {
            try {
                InetAddress address = InetAddress.getByName(contactIp);
                byte[] data = message.getBytes();
                DatagramSocket socket = new DatagramSocket();
                DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
                socket.send(packet);
                Log.i("xxx", "Send message( " + message + " ) to " + contactIp);
                socket.disconnect();
                socket.close();
            }
            catch(UnknownHostException e) {
                Log.e("xxx", "UnknownHostException " + contactIp);
            }
            catch(SocketException e) {
                Log.e("xxx", "SocketException " + e);
            }
            catch(IOException e) {
                Log.e("xxx", "IOException " + e);
            }
        });
        replyThread.start();
    }
}