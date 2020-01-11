package com.example.voip_app.view.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.voip_app.service.eventBus.CallRequestEvent;

import org.greenrobot.eventbus.EventBus;

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
        EventBus.getDefault().post(new CallRequestEvent(nguoiGui, nguoiNhan));
    }

    @Override
    public void onGetContactError(String exception) {
        Toast.makeText(context, exception, Toast.LENGTH_SHORT).show();
    }
}