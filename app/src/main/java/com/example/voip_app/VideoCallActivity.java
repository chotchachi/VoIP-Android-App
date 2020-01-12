package com.example.voip_app;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceView;

import androidx.annotation.Nullable;

import com.example.voip_app.util.CameraManager;
import com.example.voip_app.util.CommonConstants;
import com.example.voip_app.util.base.DecodeManager;
import com.example.voip_app.util.base.EncodeManager;
import com.example.voip_app.util.decode.YuvHardwareDecoder;
import com.example.voip_app.util.encode.X264Encoder;
import com.example.voip_app.util.udp.Message;
import com.example.voip_app.util.udp.NettyClient;
import com.example.voip_app.util.udp.NettyReceiverHandler;
import com.lkl.opengl.MyGLSurfaceView;

import Model.DataSocket;
import butterknife.ButterKnife;

import static com.example.voip_app.util.CommonConstants.EXTRA_DATA_SOCKET;


public class VideoCallActivity extends Activity implements CameraManager.OnFrameCallback, NettyReceiverHandler.FrameResultedCallback {
    private SurfaceView myVideoView;
    private MyGLSurfaceView yourVideoView;

    private CameraManager manager;

    private EncodeManager mEncodeManager;
    private DecodeManager mDecodeManager;
    private NettyClient mNettyClient;
    private boolean isSend = false;
    private String ip ;
    private int port = CommonConstants.CAMERA_PORT;
    private int localPort = CommonConstants.CAMERA_PORT;
    private DataSocket dataSocket;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        dataSocket = (DataSocket) bundle.getSerializable(EXTRA_DATA_SOCKET);
        ip = dataSocket.getData()[0];

        myVideoView = findViewById(R.id.myView);
        manager = new CameraManager(myVideoView);
        manager.setOnFrameCallback(this);

        yourVideoView = findViewById(R.id.yourView);
        yourVideoView.setYuvDataSize(Contants.WIDTH, Contants.HEIGHT);

        mDecodeManager = new YuvHardwareDecoder();
        mDecodeManager.setDecodeCallback(data -> yourVideoView.feedData(data, 1));

        mEncodeManager = new X264Encoder();
        mEncodeManager.setEncodeCallback(data -> mNettyClient.sendData(data, Message.MES_TYPE_VIDEO));

        mNettyClient = new NettyClient.
                Builder().targetIp(ip)
                .targetPort(port)
                .localPort(localPort)
                .frameResultedCallback(this)
                .build();


        findViewById(R.id.test).setOnClickListener(v -> isSend = !isSend);
    }


    @Override
    public void onCameraFrame(byte[] data) {
        mEncodeManager.onEncodeData(data);
    }

    @Override
    public void onVideoData(byte[] data) {
        mDecodeManager.onDecodeData(data);
    }


    @Override
    public void onAudioData(byte[] data) {

    }

    /*@OnClick(R.id.btn)
    public void onViewClicked() {
        yourVideoView.setDisplayOrientation(180);
    }*/
}
