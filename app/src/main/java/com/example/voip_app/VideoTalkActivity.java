package com.example.voip_app;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.voip_app.base.DecodeManager;
import com.example.voip_app.base.EncodeManager;
import com.example.voip_app.decode.YuvHardwareDecoder;
import com.example.voip_app.device.CameraManager;
import com.example.voip_app.encode.IEncoderCallback;
import com.example.voip_app.encode.X264Encoder;
import com.example.voip_app.net.udp.Message;
import com.example.voip_app.net.udp.NettyClient;
import com.example.voip_app.net.udp.NettyReceiverHandler;
import com.lkl.opengl.MyGLSurfaceView;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class VideoTalkActivity extends Activity implements CameraManager.OnFrameCallback, NettyReceiverHandler.FrameResultedCallback {
    private SurfaceHolder mHoder;
    private SurfaceView myVideoView;

    private CameraManager manager;

    private MyGLSurfaceView mGLSurfaceView;
    //编码
    private EncodeManager mEncodeManager;
    //解码
    private DecodeManager mDecodeManager;
    //netty传输
    private NettyClient mNettyClient;
    private boolean isSend = false;
    private String ip;
    private int port;
    private int localPort;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);
        ButterKnife.bind(this);
        ip = "192.168.1.21";
        port = 8887;
        localPort = 8887;

        myVideoView = findViewById(R.id.surface);
        manager = new CameraManager(myVideoView);

        mGLSurfaceView = (MyGLSurfaceView) findViewById(R.id.glv);
        mGLSurfaceView.setYuvDataSize(Contants.WIDTH, Contants.HEIGHT);
        mDecodeManager = new YuvHardwareDecoder();
        mDecodeManager.setDecodeCallback(new IEncoderCallback() {
            @Override
            public void onEncodeCallback(byte[] data) {
                mGLSurfaceView.feedData(data, 1);
            }
        });
//        initSurface(playView);
        //初始化编码器
        mEncodeManager = new X264Encoder();
        //编码后数据
        mEncodeManager.setEncodeCallback(new IEncoderCallback() {
            @Override
            public void onEncodeCallback(byte[] data) {
                //传输
                mNettyClient.sendData(data, Message.MES_TYPE_VIDEO);
                //不传输直接渲染
//                mDecodeManager.onDecodeData(data);
            }
        });
        //使用netty传输接收
        mNettyClient = new NettyClient.
                Builder().targetIp(ip)
                .targetPort(port)
                .localPort(localPort)
                .frameResultedCallback(this)
                .build();


        manager.setOnFrameCallback(VideoTalkActivity.this);

        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSend = !isSend;
            }
        });

    }

    /**
     * 摄像头回调数据
     *
     * @param data
     */
    @Override
    public void onCameraFrame(byte[] data) {
        if (isSend) {
            mEncodeManager.onEncodeData(data);
        }
    }


    /**
     * 初始化预览界面
     *
     * @param mSurfaceView
     */
    private void initSurface(SurfaceView mSurfaceView) {
        mDecodeManager = new YuvHardwareDecoder();
        mDecodeManager.setDecodeCallback(new IEncoderCallback() {
            @Override
            public void onEncodeCallback(byte[] data) {
                mGLSurfaceView.feedData(data, 0);
            }
        });
        mHoder = mSurfaceView.getHolder();
        mHoder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mHoder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {

            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            }
        });
    }

    /**
     * 接收视频回调
     *
     * @param data
     */
    @Override
    public void onVideoData(byte[] data) {
        mDecodeManager.onDecodeData(data);

    }

    /**
     * 接收音频回调
     *
     * @param data
     */
    @Override
    public void onAudioData(byte[] data) {

    }

    @OnClick(R.id.btn)
    public void onViewClicked() {
        mGLSurfaceView.setDisplayOrientation(180);
    }
}
