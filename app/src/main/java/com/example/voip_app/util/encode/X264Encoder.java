package com.example.voip_app.util.encode;

import com.example.voip_app.Contants;
import com.example.voip_app.util.base.EncodeManager;

import example.sszpf.x264.x264sdk;

public class X264Encoder extends EncodeManager {
    private x264sdk x264sdkEncode;

    public X264Encoder() {
        super(Contants.WIDTH,Contants.HEIGHT,Contants.VBITRATE,Contants.FRAMERATE);
        initEncode();
    }

    public X264Encoder(int vWidth, int vHeight, int vBitrate, int vFrameRate) {
        super(vWidth, vHeight, vBitrate, vFrameRate);
        initEncode();
    }

    @Override
    protected void initEncode() {
        x264sdkEncode = new x264sdk(vWidth, vHeight, vFrameRate, vBitrate, (buffer, length) -> {
            if (mEncodeCallback!=null){
                mEncodeCallback.onEncodeCallback(buffer);
            }
        });
    }

    @Override
    protected void destory() {
        if (x264sdkEncode!=null){
            x264sdkEncode.CloseX264Encode();
            x264sdkEncode = null;
        }
    }

    @Override
    public void onEncodeData(byte[] data) {
        if (x264sdkEncode!=null){
            x264sdkEncode.PushOriStream(data,data.length);
        }
    }
}
