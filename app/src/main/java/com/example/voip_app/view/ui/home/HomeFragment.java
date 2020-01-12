package com.example.voip_app.view.ui.home;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voip_app.App;
import com.example.voip_app.R;
import com.example.voip_app.adapter.ContactAdapter;
import com.example.voip_app.databinding.FragmentHomeBinding;
import com.example.voip_app.model.Account;
import com.example.voip_app.repository.ContactRepository;
import com.example.voip_app.service.eventBus.CallEvent;

import org.greenrobot.eventbus.EventBus;

import Model.DataSocket;

import static com.example.voip_app.service.eventBus.CallEvent.GUI;

public class HomeFragment extends Fragment implements ContactAdapter.ContactListener, ContactRepository.GetContactListener {
    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private Context context;
    private ContactAdapter adapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);

        // Get Contacts
        homeViewModel.getContacts(this).observe(this, accountList -> adapter.setContactList(accountList));

    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        View view = binding.getRoot();
        recyclerView = binding.rvContact;
        binding.setAccount(App.getAccount());
        initRecyclerView();
        return view;
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        adapter = new ContactAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onContactClick(Account account) {
        Account me = App.getAccount();
        Model.Account nguoiGui = new Model.Account(me.getId(), me.getPhoneNumber(), me.getName());
        Model.Account nguoiNhan = new Model.Account(account.getId(), account.getPhoneNumber(), account.getName());
        DataSocket dataSocket = new DataSocket();
        dataSocket.setNguoiGui(nguoiGui);
        dataSocket.setNguoiNhan(nguoiNhan);
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.call_dialog);
        Button btnVoice, btnVideo;
        btnVoice = dialog.findViewById(R.id.btn_voice);
        btnVideo = dialog.findViewById(R.id.btn_video);
        btnVoice.setOnClickListener(v -> EventBus.getDefault().post(new CallEvent(GUI, dataSocket)));
        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        dialog.show();
    }

    @Override
    public void onGetContactError(String exception) {
        Toast.makeText(context, exception, Toast.LENGTH_SHORT).show();
    }
}