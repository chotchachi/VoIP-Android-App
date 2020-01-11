package com.example.voip_app.viewModel;

import androidx.lifecycle.ViewModel;

import com.example.voip_app.App;
import com.example.voip_app.service.eventBus.CallEvent;

import org.greenrobot.eventbus.EventBus;

import static com.example.voip_app.service.eventBus.CallEvent.KET_THUC;


public class MakeCallViewModel extends ViewModel {

    public void onEndCall() {
        App.CALLING = false;
        EventBus.getDefault().post(new CallEvent(KET_THUC, null));
    }
}
