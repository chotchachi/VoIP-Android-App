package com.example.voip_app;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;

import static android.content.Context.WIFI_SERVICE;

public class ContactManager {
    private static final int BROADCAST_PORT = 50001; // Socket dùng chung cho các client để lắng nghe các hoạt động
    private static final int BROADCAST_INTERVAL = 10000; // Milliseconds
    private boolean BROADCAST = true;
    private boolean LISTEN = true;
    private HashMap<String, InetAddress> contacts;
    private InetAddress broadcastIP;
    public static ContactManager instance;

    public static ContactManager getInstance(String name, Context context){
        if (instance == null){
            instance = new ContactManager(name, context);
        }
        return instance;
    }

    public ContactManager(String name, Context context) {
        this.contacts = new HashMap<>();
        InetAddress broadcastIP = getBroadcastIp(context);
        this.broadcastIP = broadcastIP;
        broadcastName(name, broadcastIP);
        listenBroadcast();
    }

    private InetAddress getBroadcastIp(Context context) {
        // Function to return the broadcast address, based on the IP address of the device
        try {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
            assert wifiManager != null;
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            String addressString = toBroadcastIp(ipAddress);
            return InetAddress.getByName(addressString);
        }
        catch(UnknownHostException e) {
            Log.e("xxx", "UnknownHostException in getBroadcastIP: " + e);
            return null;
        }
    }
    private String toBroadcastIp(int ip) {
        // Returns converts an IP address in int format to a formatted string
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                "255";
    }

    private void broadcastName(final String name, final InetAddress broadcastIP) {
        Log.i("xxx", "Broadcasting started!");
        Thread broadcastThread = new Thread(() -> {
            try {
                String request = "ADD:"+name;
                byte[] message = request.getBytes();
                DatagramSocket socket = new DatagramSocket();
                socket.setBroadcast(true);
                DatagramPacket packet = new DatagramPacket(message, message.length, broadcastIP, BROADCAST_PORT);
                while(BROADCAST) {
                    socket.send(packet);
                    Thread.sleep(BROADCAST_INTERVAL);
                }
                Log.i("xxx", "Broadcaster ending!");
                socket.disconnect();
                socket.close();
            }
            catch(SocketException e) {
                Log.e("xxx", "SocketException in broadcast: " + e);
            }
            catch(IOException e) {
                Log.e("xxx", "IOException in broadcast: " + e);
            }
            catch(InterruptedException e) {
                Log.e("xxx", "InterruptedException in broadcast: " + e);
            }
        });
        broadcastThread.start();
    }

    public HashMap<String, InetAddress> getContacts() {
        return contacts;
    }

    private void addContact(String name, InetAddress address) {
        if(!contacts.containsKey(name)) {
            Log.i("xxx", "Adding contact: " + name);
            contacts.put(name, address);
            Log.i("xxx", "Contacts: " + contacts.size());
            return;
        }
        Log.i("xxx", "Contact already exists: " + name);
    }

    private void removeContact(String name) {
        if(contacts.containsKey(name)) {
            Log.i("xxx", "Removing contact: " + name);
            contacts.remove(name);
            Log.i("xxx", "#Contacts: " + contacts.size());
            return;
        }
        Log.i("xxx", "Cannot remove contact. " + name + " does not exist.");
    }

    public void bye(final String name) {
        // Sends a Bye notification to other devices
        Thread byeThread = new Thread(() -> {

            try {
                Log.i("xxx", "Attempting to broadcast BYE notification!");
                String notification = "BYE:"+name;
                byte[] message = notification.getBytes();
                DatagramSocket socket = new DatagramSocket();
                socket.setBroadcast(true);
                DatagramPacket packet = new DatagramPacket(message, message.length, broadcastIP, BROADCAST_PORT);
                socket.send(packet);
                Log.i("xxx", "Broadcast BYE notification!");
                socket.disconnect();
                socket.close();
            }
            catch(SocketException e) {

                Log.e("xxx", "SocketException during BYE notification: " + e);
            }
            catch(IOException e) {

                Log.e("xxx", "IOException during BYE notification: " + e);
            }
        });
        byeThread.start();
    }

    public void stopBroadcasting() {
        BROADCAST = false;
    }

    public void stopListening() {
        LISTEN = false;
    }

    private void listenBroadcast() {
        Log.i("xxx", "Listening started!");
        Thread listenThread = new Thread(new Runnable() {
            @Override
            public void run() {
                DatagramSocket socket;

                try {
                    socket = new DatagramSocket(BROADCAST_PORT);
                } catch (SocketException e) {
                    Log.e("xxx", "SocketException: " + e);
                    return;
                }

                byte[] buffer = new byte[1024];

                while(LISTEN) {
                    listen(socket, buffer);
                }

                Log.i("xxx", "Listener ending!");
                socket.disconnect();
                socket.close();
            }

            void listen(DatagramSocket socket, byte[] buffer) {
                try {
                    // Lắng nghe các hoạt động ở Broadcast Port
                    DatagramPacket packet = new DatagramPacket(buffer, 1024);
                    socket.setSoTimeout(15000);
                    socket.receive(packet);
                    String data = new String(buffer, 0, packet.getLength());
                    Log.i("xxx", "Packet received: " + data);
                    String action = data.substring(0, 4);
                    if(action.equals("ADD:")) {
                        // Lắng nghe người dùng mới
                        Log.i("xxx", "Listener received ADD request");
                        addContact(data.substring(4), packet.getAddress());
                    }
                    else if(action.equals("BYE:")) {
                        // Có người đăng xuất
                        Log.i("xxx", "Listener received BYE request");
                        removeContact(data.substring(4));
                    }
                    else {
                        // Không nhận diện được
                        Log.w("xxx", "Listener received invalid request: " + action);
                    }
                }
                catch(SocketTimeoutException e) {
                    Log.i("xxx", "No packet received!");
                    if(LISTEN) {
                        listen(socket, buffer);
                    }
                }
                catch(SocketException e) {
                    Log.e("xxx", "SocketException in listen: " + e);
                }
                catch(IOException e) {
                    Log.e("xxx", "IOException in listen: " + e);
                }
            }
        });
        listenThread.start();
    }
}
