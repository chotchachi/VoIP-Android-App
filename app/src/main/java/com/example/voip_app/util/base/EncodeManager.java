package com.example.voip_app.util.base;

import com.example.voip_app.util.encode.IEncoderCallback;

public abstract class EncodeManager {
    protected int vWidth;
    protected int vHeight;
    protected int vBitrate;
    protected int vFrameRate;
    protected IEncoderCallback mEncodeCallback;

    public EncodeManager() {
    }

    public EncodeManager(int vWidth, int vHeight, int vBitrate, int vFrameRate) {
        this.vWidth = vWidth;
        this.vHeight = vHeight;
        this.vBitrate = vBitrate;
        this.vFrameRate = vFrameRate;
    }

    public void setEncodeCallback(IEncoderCallback mEncodeCallback) {
        this.mEncodeCallback = mEncodeCallback;
    }

    protected abstract void initEncode();
    protected abstract void destory();
    public abstract void onEncodeData(byte[] data);
}
