package com.example.voip_app.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.voip_app.service.eventBus.CallEvent;

import org.greenrobot.eventbus.EventBus;

import Model.Account;
import Model.DataSocket;


public class ReceiveCallViewModel extends ViewModel {
    private MutableLiveData<Account> accountMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<DataSocket> dataSocketMutableLiveData = new MutableLiveData<>();

    public void onAccept(){
        DataSocket dataSocket = dataSocketMutableLiveData.getValue();
        /*try {
            AudioCall audioCall = new AudioCall(InetAddress.getByName(dataSocket.getData()[0]), Integer.parseInt(dataSocket.getData()[1]));
            audioCall.startCall();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
*/
        EventBus.getDefault().post(new CallEvent(CallEvent.TOI_DONG_Y, dataSocket));
    }

    public void setAccount(Account account){
        accountMutableLiveData.setValue(account);
    }

    public void setDataSocket(DataSocket dataSocket){
        dataSocketMutableLiveData.setValue(dataSocket);
    }
}
