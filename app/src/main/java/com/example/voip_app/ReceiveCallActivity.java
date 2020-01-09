package com.example.voip_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import static com.example.voip_app.util.CommonConstants.EXTRA_CONTACT;
import static com.example.voip_app.util.CommonConstants.EXTRA_IP;

public class ReceiveCallActivity extends AppCompatActivity {
    private static final int BROADCAST_PORT = 50002;
    private String contactIp;
    private String contactName;
    private boolean LISTEN = true;
    private boolean IN_CALL = false;
    private AudioCall call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_call);

        Intent intent = getIntent();
        contactName = intent.getStringExtra(EXTRA_CONTACT);
        contactIp = intent.getStringExtra(EXTRA_IP);

        TextView textView = findViewById(R.id.textViewIncomingCall);
        textView.setText(contactName);

        final Button endButton = findViewById(R.id.buttonEndCall1);
        endButton.setVisibility(View.INVISIBLE);

        startListener();

        // ACCEPT BUTTON
        Button acceptButton = findViewById(R.id.buttonAccept);
        acceptButton.setOnClickListener(v -> {
            try {
                // Accepting call. Send a notification and start the call
                sendMessage("ACC:");
                InetAddress address = InetAddress.getByName(contactIp);
                Log.i("xxx", "Calling " + address.toString());
                IN_CALL = true;
                call = new AudioCall(address);
                call.startCall();
                // Hide the buttons as they're not longer required
                Button accept = findViewById(R.id.buttonAccept);
                accept.setEnabled(false);

                Button reject = findViewById(R.id.buttonReject);
                reject.setEnabled(false);

                endButton.setVisibility(View.VISIBLE);
            }
            catch(UnknownHostException e) {
                Log.e("xxx", "UnknownHostException in acceptButton: " + e);
            }
            catch(Exception e) {
                Log.e("xxx", "Exception in acceptButton: " + e);
            }
        });

        // REJECT BUTTON
        Button rejectButton = findViewById(R.id.buttonReject);
        rejectButton.setOnClickListener(v -> {
            sendMessage("REJ:");
            endCall();
        });

        // END BUTTON
        endButton.setOnClickListener(v -> endCall());
    }

    private void endCall() {
        stopListener();
        if(IN_CALL) {
            call.endCall();
        }
        sendMessage("END:");
        finish();
    }

    private void startListener() {
        // Creates the listener thread
        LISTEN = true;
        Thread listenThread = new Thread(() -> {
            try {
                Log.i("xxx", "Listener started!");
                DatagramSocket socket = new DatagramSocket(BROADCAST_PORT);
                socket.setSoTimeout(1500);
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, 1024);

                while(LISTEN) {
                    try {
                        socket.receive(packet);
                        String data = new String(buffer, 0, packet.getLength());
                        Log.i("xxx", "Packet received from "+ packet.getAddress() +" with contents: " + data);
                        String action = data.substring(0, 4);
                        if(action.equals("END:")) {
                            endCall();
                        }
                        else {
                            Log.w("xxx", packet.getAddress() + " sent invalid message: " + data);
                        }
                    }
                    catch(IOException e) {
                        Log.e("xxx", "IOException in Listener " + e);
                    }
                }
                Log.i("xxx", "Listener ending");
                socket.disconnect();
                socket.close();
            }
            catch(SocketException e) {
                Log.e("xxx", "SocketException in Listener " + e);
                endCall();
            }
        });
        listenThread.start();
    }

    private void stopListener() {
        LISTEN = false;
    }

    private void sendMessage(final String message) {
        // Creates a thread for sending notifications
        Thread replyThread = new Thread(() -> {
            try {
                InetAddress address = InetAddress.getByName(contactIp);
                byte[] data = message.getBytes();
                DatagramSocket socket = new DatagramSocket();
                DatagramPacket packet = new DatagramPacket(data, data.length, address, BROADCAST_PORT);
                socket.send(packet);
                Log.i("xxx", "Sent message( " + message + " ) to " + contactIp);
                socket.disconnect();
                socket.close();
            }
            catch(UnknownHostException e) {
                Log.e("xxx", "Failure. UnknownHostException in sendMessage: " + contactIp);
            }
            catch(SocketException e) {

                Log.e("xxx", "Failure. SocketException in sendMessage: " + e);
            }
            catch(IOException e) {

                Log.e("xxx", "Failure. IOException in sendMessage: " + e);
            }
        });
        replyThread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.receive_call, menu);
        return true;
    }

}
