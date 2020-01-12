package com.example.voip_app.decode;

import com.example.voip_app.encode.AudioData;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class AudioDecoder implements Runnable {

    String LOG = "AudioDecoder";
    private static AudioDecoder decoder;

    private static final int MAX_BUFFER_SIZE = 2048;

    private short[] decodedData;
    private boolean isDecoding = false;
    private List<AudioData> dataList = null;

    public static AudioDecoder getInstance() {
        if (decoder == null) {
            decoder = new AudioDecoder();
        }
        return decoder;
    }

    private AudioDecoder() {
        this.dataList = Collections
                .synchronizedList(new LinkedList<AudioData>());
        startDecoding();
    }


    public void addData(byte[] data, int size) {
        AudioData adata = new AudioData();
        adata.setSize(size);
        byte[] tempData = new byte[size];
        System.arraycopy(data, 0, tempData, 0, size);
        adata.setReceiverdata(tempData);
        dataList.add(adata);
    }

    /**
     * start decode AMR data
     */
    public void startDecoding() {
        System.out.println(LOG + "开始解码");
        if (isDecoding) {
            return;
        }
        new Thread(this).start();
    }

    @Override
    public void run() {

    }

    public void stopDecoding() {
        this.isDecoding = false;
    }
}