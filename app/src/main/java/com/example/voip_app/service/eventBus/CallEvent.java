package com.example.voip_app.service.eventBus;

import Model.DataSocket;

public class CallEvent {
    public static final int GUI = 1;
    public static final int NHAN = 2;

    public static final int TU_CHOI = 4;
    public static final int BAN_DONG_Y = 5;
    public static final int KET_THUC = 6;

    public static final int TOI_DONG_Y = 7;

    public static final int START_MIC = 8;

    private int action;
    private DataSocket dataSocket;

    public CallEvent(int action, DataSocket dataSocket) {
        this.action = action;
        this.dataSocket = dataSocket;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public DataSocket getDataSocket() {
        return dataSocket;
    }

    public void setDataSocket(DataSocket dataSocket) {
        this.dataSocket = dataSocket;
    }
}
