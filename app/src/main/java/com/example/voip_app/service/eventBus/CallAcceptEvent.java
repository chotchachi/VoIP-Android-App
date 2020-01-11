package com.example.voip_app.service.eventBus;

import Model.DataSocket;

public class CallAcceptEvent {
    public DataSocket dataSocket;

    public CallAcceptEvent(DataSocket dataSocket) {
        this.dataSocket = dataSocket;
    }

    public DataSocket getDataSocket() {
        return dataSocket;
    }

    public void setDataSocket(DataSocket dataSocket) {
        this.dataSocket = dataSocket;
    }
}
