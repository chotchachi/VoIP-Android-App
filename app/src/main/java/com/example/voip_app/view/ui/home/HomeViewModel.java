package com.example.voip_app.view.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.voip_app.model.Account;
import com.example.voip_app.repository.ContactRepository;

import java.util.List;

public class HomeViewModel extends ViewModel {
    private ContactRepository contactRepository;

    public HomeViewModel(){
        this.contactRepository = new ContactRepository();
    }

    public LiveData<List<Account>> getContacts(ContactRepository.GetContactListener listener) {
        return contactRepository.getContacts(listener);
    }
}