package com.example.voip_app.util.base;


import com.example.voip_app.util.encode.IEncoderCallback;

public abstract class DecodeManager {
    protected int vWidth;
    protected int vHeight;
    protected int vBitrate;
    protected int vFrameRate;

    protected IEncoderCallback mEncodeCallback;

    public DecodeManager() {
    }

    public DecodeManager(int vWidth, int vHeight, int vBitrate, int vFrameRate) {
        this.vWidth = vWidth;
        this.vHeight = vHeight;
        this.vBitrate = vBitrate;
        this.vFrameRate = vFrameRate;
    }

    public void setDecodeCallback(IEncoderCallback mEncodeCallback) {
        this.mEncodeCallback = mEncodeCallback;
    }

    protected abstract void initDecode();
    protected abstract void destory();
    public abstract void onDecodeData(byte[] data);
}
