package com.example.voip_app.util.decode;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;

import com.example.voip_app.Contants;
import com.example.voip_app.util.base.DecodeManager;

import java.io.IOException;
import java.nio.ByteBuffer;


public class YuvHardwareDecoder extends DecodeManager {
    private MediaCodec vDeCodec = null;
    private MediaCodec.BufferInfo info = null;

    public YuvHardwareDecoder() {
        super(Contants.WIDTH,Contants.HEIGHT,Contants.VBITRATE,Contants.FRAMERATE);
        initDecode();
    }

    @Override
    protected void initDecode() {
        try {
            vDeCodec = MediaCodec.createDecoderByType(Contants.VIDEO_FORMAT_H264);
        } catch (IOException e) {
            e.printStackTrace();
        }
        info = new MediaCodec.BufferInfo();
        MediaFormat mediaFormat = MediaFormat.createVideoFormat(Contants.VIDEO_FORMAT_H264, vWidth, vHeight);
        vDeCodec.configure(mediaFormat, null, null, 0);
        vDeCodec.start();
    }

    @Override
    protected void destory() {
        if (null != vDeCodec) {
            vDeCodec.stop();
            vDeCodec.release();
            vDeCodec = null;
        }
    }

    @Override
    public void onDecodeData(byte[] h264Data) {
        int inputBufferIndex = vDeCodec.dequeueInputBuffer(Contants.DEFAULT_TIMEOUT_US);
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                inputBuffer = vDeCodec.getInputBuffer(inputBufferIndex);
            } else {
                inputBuffer = vDeCodec.getInputBuffers()[inputBufferIndex];
            }
            if (inputBuffer != null) {
                inputBuffer.clear();
                inputBuffer.put(h264Data, 0, h264Data.length);
                vDeCodec.queueInputBuffer(inputBufferIndex, 0, h264Data.length, 0, 0);
            }
        }
        int outputBufferIndex = vDeCodec.dequeueOutputBuffer(info, Contants.DEFAULT_TIMEOUT_US);
        ByteBuffer outputBuffer;
        while (outputBufferIndex > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                outputBuffer = vDeCodec.getOutputBuffer(outputBufferIndex);
            } else {
                outputBuffer = vDeCodec.getOutputBuffers()[outputBufferIndex];
            }
            if (outputBuffer != null) {
                outputBuffer.position(0);
                outputBuffer.limit(info.offset + info.size);
                byte[] yuvData = new byte[outputBuffer.remaining()];
                outputBuffer.get(yuvData);

                if (null!=mEncodeCallback) {
                    mEncodeCallback.onEncodeCallback(yuvData);
                }
                vDeCodec.releaseOutputBuffer(outputBufferIndex, false);
                outputBuffer.clear();
            }
            outputBufferIndex = vDeCodec.dequeueOutputBuffer(info, Contants.DEFAULT_TIMEOUT_US);
        }
    }
}
