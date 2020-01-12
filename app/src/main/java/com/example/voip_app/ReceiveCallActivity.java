package com.example.voip_app;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.example.voip_app.databinding.ActivityReceiveCallBinding;
import com.example.voip_app.service.eventBus.CallEvent;
import com.example.voip_app.viewModel.ReceiveCallViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import Model.Account;
import Model.DataSocket;

import static com.example.voip_app.service.eventBus.CallEvent.BAN_KET_THUC;
import static com.example.voip_app.service.eventBus.CallEvent.KET_THUC_VIEW;
import static com.example.voip_app.service.eventBus.CallEvent.TOI_DONG_Y;
import static com.example.voip_app.service.eventBus.CallEvent.TOI_TU_CHOI;
import static com.example.voip_app.util.CommonConstants.EXTRA_CONTACT;
import static com.example.voip_app.util.CommonConstants.EXTRA_DATA_SOCKET;

public class ReceiveCallActivity extends AppCompatActivity {
    private ActivityReceiveCallBinding binding;
    private ReceiveCallViewModel viewModel;

    private Account sendAccount;
    private DataSocket dataSocket;

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onCallEvent(CallEvent event) {
        if (event.getAction() == BAN_KET_THUC){
            finish();
        } else if (event.getAction() == TOI_TU_CHOI){
            finish();
        } else if (event.getAction() == TOI_DONG_Y){
            binding.btnAccept.setVisibility(View.INVISIBLE);
            binding.btnReject.setVisibility(View.INVISIBLE);
            binding.btnEndCall.setVisibility(View.VISIBLE);
        } else if (event.getAction() == KET_THUC_VIEW){
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initBinding();

        Bundle bundle = getIntent().getExtras();
        sendAccount = (Account) bundle.getSerializable(EXTRA_CONTACT);
        dataSocket = (DataSocket) bundle.getSerializable(EXTRA_DATA_SOCKET);

        binding.setAccount(sendAccount);

        viewModel.setAccount(sendAccount);
        viewModel.setDataSocket(dataSocket);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void initBinding() {
        viewModel = ViewModelProviders.of(this).get(ReceiveCallViewModel.class);
        binding = DataBindingUtil.setContentView(ReceiveCallActivity.this, R.layout.activity_receive_call);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
    }
}
