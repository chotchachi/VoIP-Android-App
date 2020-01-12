package com.example.voip_app.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.voip_app.service.eventBus.CallEvent;

import org.greenrobot.eventbus.EventBus;

import Model.DataSocket;

import static com.example.voip_app.service.eventBus.CallEvent.NGUOI_GUI_END;


public class MakeCallViewModel extends ViewModel {
    private MutableLiveData<DataSocket> dataSocketMutableLiveData = new MutableLiveData<>();

    public void onEndCall() {
        EventBus.getDefault().post(new CallEvent(NGUOI_GUI_END, dataSocketMutableLiveData.getValue()));
    }

    public void setDataSocket(DataSocket dataSocket){
        dataSocketMutableLiveData.setValue(dataSocket);
    }
}
