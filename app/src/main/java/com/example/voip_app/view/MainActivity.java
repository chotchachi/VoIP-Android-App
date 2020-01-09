package com.example.voip_app.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.example.voip_app.App;
import com.example.voip_app.ContactManager;
import com.example.voip_app.MakeCallActivity;
import com.example.voip_app.R;
import com.example.voip_app.ReceiveCallActivity;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;

import static com.example.voip_app.util.CommonConstants.EXTRA_CONTACT;
import static com.example.voip_app.util.CommonConstants.EXTRA_DISPLAYNAME;
import static com.example.voip_app.util.CommonConstants.EXTRA_IP;

public class MainActivity extends AppCompatActivity {
    private static final int LISTENER_PORT = 50003;
    private ContactManager contactManager;
    private String displayName;
    private boolean STARTED = false;
    private boolean IN_CALL = false;
    private boolean LISTEN = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        STARTED = true;

        displayName = App.getAccount().getName();

        contactManager = new ContactManager(displayName, this);
        startCallListener();

        // UPDATE BUTTON
        // Updates the list of reachable devices
        Button btnUpdate = findViewById(R.id.buttonUpdate);
        btnUpdate.setOnClickListener(v -> updateContactList());

        // CALL BUTTON
        // Attempts to initiate an audio chat session with the selected device
        Button btnCall = findViewById(R.id.buttonCall);
        btnCall.setOnClickListener(v -> {
            RadioGroup radioGroup = findViewById(R.id.contactList);
            int selectedButton = radioGroup.getCheckedRadioButtonId();
            if(selectedButton == -1) {
                // If no device was selected, present an error message to the user
                final AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
                alert.setTitle("Oops");
                alert.setMessage("You must select a contact first");
                alert.setButton(-1, "OK", (dialog, which) -> alert.dismiss());
                alert.show();
                return;
            }
            // Collect details about the selected contact
            RadioButton radioButton = findViewById(selectedButton);
            String contact = radioButton.getText().toString();
            InetAddress ip = contactManager.getContacts().get(contact);
            IN_CALL = true;

            // Send this information to the MakeCallActivity and start that activity
            Intent intent = new Intent(MainActivity.this, MakeCallActivity.class);
            intent.putExtra(EXTRA_CONTACT, contact);
            String address = ip.toString();
            address = address.substring(1, address.length());
            intent.putExtra(EXTRA_IP, address);
            intent.putExtra(EXTRA_DISPLAYNAME, displayName);
            startActivity(intent);
        });
    }

    private void updateContactList() {
        // Create a copy of the HashMap used by the ContactManager
        HashMap<String, InetAddress> contacts = contactManager.getContacts();
        // Create a radio button for each contact in the HashMap
        RadioGroup radioGroup = findViewById(R.id.contactList);
        radioGroup.removeAllViews();

        for(String name : contacts.keySet()) {
            RadioButton radioButton = new RadioButton(getBaseContext());
            radioButton.setText(name);
            radioButton.setTextColor(Color.BLACK);
            radioGroup.addView(radioButton);
        }

        radioGroup.clearCheck();
    }

    private void startCallListener() {
        // Tạo thread lắng nghe request cuộc gọi
        LISTEN = true;
        Thread listener = new Thread(() -> {
            try {
                // Thiết lập socket và packet để nhận
                Log.i("xxx", "Incoming call listener started");
                DatagramSocket socket = new DatagramSocket(LISTENER_PORT);
                socket.setSoTimeout(1000);
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, 1024);
                while(LISTEN) {
                    // Lắng nghe yêu cầu request
                    try {
                        Log.i("xxx", "Listening for incoming calls");
                        socket.receive(packet);
                        String data = new String(buffer, 0, packet.getLength());
                        Log.i("xxx", "Packet received from "+ packet.getAddress() +" with contents: " + data);
                        String action = data.substring(0, 4);
                        if(action.equals("CAL:")) {
                            String address = packet.getAddress().toString();
                            String name = data.substring(4, packet.getLength());

                            Intent intent = new Intent(MainActivity.this, ReceiveCallActivity.class);
                            intent.putExtra(EXTRA_CONTACT, name);
                            intent.putExtra(EXTRA_IP, address.substring(1));
                            IN_CALL = true;
                            startActivity(intent);
                        }
                        else {
                            // Nhận dữ liệu không xác định
                            Log.w("xxx", packet.getAddress() + " sent invalid message: " + data);
                        }
                    }
                    catch(Exception ignored) {}
                }
                Log.i("xxx", "Call Listener ending");
                socket.disconnect();
                socket.close();
            }
            catch(SocketException e) {
                Log.e("xxx", "SocketException in listener " + e);
            }
        });
        listener.start();
    }

    private void stopCallListener() {
        LISTEN = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(STARTED) {
            contactManager.bye(displayName);
            contactManager.stopBroadcasting();
            contactManager.stopListening();
            //STARTED = false;
        }
        stopCallListener();
        Log.i("xxx", "App paused!");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("xxx", "App stopped!");
        stopCallListener();
        if(!IN_CALL) {
            finish();
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        Log.i("xxx", "App restarted!");
        IN_CALL = false;
        STARTED = true;
        contactManager = new ContactManager(displayName, this);
        startCallListener();
    }
}

