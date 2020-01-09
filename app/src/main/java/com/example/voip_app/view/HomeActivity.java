package com.example.voip_app.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.voip_app.App;
import com.example.voip_app.ContactManager;
import com.example.voip_app.R;
import com.example.voip_app.ReceiveCallActivity;
import com.example.voip_app.model.DataSocket;
import com.example.voip_app.view.ui.dashboard.DashboardFragment;
import com.example.voip_app.view.ui.home.HomeFragment;
import com.example.voip_app.view.ui.notifications.NotificationsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.voip_app.util.CommonConstants.EXTRA_CONTACT;
import static com.example.voip_app.util.CommonConstants.EXTRA_IP;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.nav_view)
    BottomNavigationView bottomNavigationView;

    final Fragment fragment1 = new HomeFragment();
    final Fragment fragment2 = new DashboardFragment();
    final Fragment fragment3 = new NotificationsFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;

    private static final int LISTENER_PORT = 50003;
    private ContactManager contactManager;
    private boolean STARTED = false;
    private boolean IN_CALL = false;
    private boolean LISTEN = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        initFragment();

        contactManager = ContactManager.getInstance(App.getAccount().getPhoneNumber(), this);

        startCallListener();

        new Thread(() -> {
            try {
                Socket socket = new Socket("192.168.1.20", 8888);
                Thread.sleep(5000);

                OutputStream outputStream = socket.getOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

                DataSocket dtsk = new DataSocket();
                String[] data = new String[2];
                dtsk.setAction("login");
                data[0] = "";
                data[1] = "";
                dtsk.setData(data);

                objectOutputStream.writeObject(dtsk);

            } catch (IOException e) {
                Log.d("xxx", e.getMessage());

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Luồng lắng nghe tín hiệu cuộc gọi
    private void startCallListener() {
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
                    try {
                        Log.i("xxx", "Listening for incoming calls");
                        socket.receive(packet);
                        String data = new String(buffer, 0, packet.getLength());
                        Log.i("xxx", "Packet received from "+ packet.getAddress() +" with contents: " + data);
                        String action = data.substring(0, 4);
                        if(action.equals("CAL:")) {
                            String address = packet.getAddress().toString();
                            String name = data.substring(4, packet.getLength());

                            Intent intent = new Intent(HomeActivity.this, ReceiveCallActivity.class);
                            intent.putExtra(EXTRA_CONTACT, name);
                            intent.putExtra(EXTRA_IP, address.substring(1));
                            IN_CALL = true;
                            startActivity(intent);
                        }
                        else {
                            Log.w("xxx", packet.getAddress() + " send error: " + data);
                        }
                    }
                    catch(Exception ignored) {}
                }
                Log.i("xxx", "Call listener end");
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

    private void initFragment() {
        fm.beginTransaction().add(R.id.main_container, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.main_container, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.main_container,fragment1, "1").commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                fm.beginTransaction().hide(active).show(fragment1).commit();
                active = fragment1;
                return true;

            case R.id.navigation_dashboard:
                fm.beginTransaction().hide(active).show(fragment2).commit();
                active = fragment2;
                return true;

            case R.id.navigation_notifications:
                fm.beginTransaction().hide(active).show(fragment3).commit();
                active = fragment3;
                return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(STARTED) {
            contactManager.bye(App.getAccount().getPhoneNumber());
            contactManager.stopBroadcasting();
            contactManager.stopListening();
        }
        stopCallListener();
    }
}
