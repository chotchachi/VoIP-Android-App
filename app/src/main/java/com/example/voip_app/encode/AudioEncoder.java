package com.example.voip_app.encode;

import android.util.Log;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * 音频编码器
 *
 * @author lqm
 */
public class AudioEncoder implements Runnable {
    String LOG = "AudioEncoder";
    //单例模式构造对象
    private static AudioEncoder encoder;
    //是否正在编码
    private boolean isEncoding = false;


    //每一帧的音频数据的集合
    private List<AudioData> dataList = null;

    public static AudioEncoder getInstance() {
        if (encoder == null) {
            encoder = new AudioEncoder();
        }
        return encoder;
    }

    private AudioEncoder() {
        dataList = Collections.synchronizedList(new LinkedList<AudioData>());
    }

    public void addData(short[] data, int size) {
        AudioData rawData = new AudioData();
        rawData.setSize(size);
        short[] tempData = new short[size];
        System.arraycopy(data, 0, tempData, 0, size);
        rawData.setRealData(tempData);
        dataList.add(rawData);
    }

    /**
     * start encoding 开始编码
     */

    public void startEncoding() {
        System.out.println(LOG + "start encode thread");
        if (isEncoding) {
            Log.e(LOG, "encoder has been started  !!!");
            return;
        }
        //开子线程
        new Thread(this).start();
    }

    /**
     * end encoding	停止编码
     */
    public void stopEncoding() {
        this.isEncoding = false;
    }

    @Override
    public void run() {


    }

}