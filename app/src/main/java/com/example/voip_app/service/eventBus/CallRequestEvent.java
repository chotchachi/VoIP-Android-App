package com.example.voip_app.service.eventBus;

import Model.Account;

public class CallRequestEvent {
    public Account nguoiGui;
    public Account nguoiNhan;

    public CallRequestEvent(Account nguoiGui, Account nguoiNhan) {
        this.nguoiGui = nguoiGui;
        this.nguoiNhan = nguoiNhan;
    }

    public Account getNguoiGui() {
        return nguoiGui;
    }

    public void setNguoiGui(Account nguoiGui) {
        this.nguoiGui = nguoiGui;
    }

    public Account getNguoiNhan() {
        return nguoiNhan;
    }

    public void setNguoiNhan(Account nguoiNhan) {
        this.nguoiNhan = nguoiNhan;
    }
}
