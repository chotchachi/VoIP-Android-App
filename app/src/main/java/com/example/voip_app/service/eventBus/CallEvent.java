package com.example.voip_app.service.eventBus;

import Model.DataSocket;

public class CallEvent {
    public static final int GUI = 1;
    public static final int NHAN = 2;

    public static final int TU_CHOI = 4;
    public static final int BAN_DONG_Y = 5;

    public static final int NGUOI_GUI_END = 6;
    public static final int NGUOI_NHAN_END = 3;

    public static final int BAN_KET_THUC = 9;

    public static final int TOI_DONG_Y = 7;
    public static final int TOI_TU_CHOI = 8;

    public static final int KET_THUC_VIEW = 0;

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
