package com.example.voip_app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.example.voip_app.databinding.ActivityMakeCallBinding;
import com.example.voip_app.service.CallService;
import com.example.voip_app.service.eventBus.CallEvent;
import com.example.voip_app.viewModel.MakeCallViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import Model.Account;
import Model.DataSocket;

import static com.example.voip_app.service.eventBus.CallEvent.BAN_DONG_Y;
import static com.example.voip_app.service.eventBus.CallEvent.KET_THUC_VIEW;
import static com.example.voip_app.service.eventBus.CallEvent.NGUOI_GUI_END;
import static com.example.voip_app.service.eventBus.CallEvent.TU_CHOI;
import static com.example.voip_app.util.CommonConstants.EXTRA_DATA_SOCKET;

public class MakeCallActivity extends AppCompatActivity {
    private ActivityMakeCallBinding binding;
    private MakeCallViewModel viewModel;

    private Account receiveAccount;
    private DataSocket dataSocket;
    private int typeCall;

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initBinding();

        Bundle bundle = getIntent().getExtras();
        dataSocket = (DataSocket) bundle.getSerializable(EXTRA_DATA_SOCKET);
        receiveAccount = dataSocket.getNguoiNhan();
        typeCall = bundle.getInt(CallService.TYPE_CALL);

        binding.setAccount(receiveAccount);
        viewModel.setDataSocket(dataSocket);
    }

    private void initBinding() {
        viewModel = ViewModelProviders.of(this).get(MakeCallViewModel.class);
        binding = DataBindingUtil.setContentView(MakeCallActivity.this, R.layout.activity_make_call);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
    }

    @Subscribe
    public void onCallEvent(CallEvent event) {
        switch (event.getAction()){
            case TU_CHOI:
            case NGUOI_GUI_END:
            case KET_THUC_VIEW:
                finish();
                break;
            case BAN_DONG_Y:
                if (typeCall == CallService.VIDEO_CALL){
                    Intent intent = new Intent(this, VideoCallActivity.class);
                    Bundle bundle = new Bundle();

                    bundle.putSerializable(EXTRA_DATA_SOCKET, event.getDataSocket());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /*private void makeCall() {
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
    }*/
}