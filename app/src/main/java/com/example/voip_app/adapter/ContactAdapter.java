package com.example.voip_app.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voip_app.R;
import com.example.voip_app.databinding.ItemContactRowBinding;
import com.example.voip_app.model.Account;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private List<Account> contactList;
    private LayoutInflater layoutInflater;
    private ContactListener listener;

    class ContactViewHolder extends RecyclerView.ViewHolder {
        private final ItemContactRowBinding binding;

        ContactViewHolder(final ItemContactRowBinding itemBinding) {
            super(itemBinding.getRoot());
            this.binding = itemBinding;
        }
    }

    public ContactAdapter(ContactListener listener) {
        this.listener = listener;
        this.contactList = new ArrayList<>();
    }

    public void setContactList(List<Account> contactList) {
        this.contactList = contactList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        ItemContactRowBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_contact_row, parent, false);
        return new ContactViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, final int position) {
        holder.binding.setAccount(contactList.get(position));
        holder.binding.getRoot().setOnClickListener(v -> {
            if (listener != null) {
                listener.onContactClick(contactList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public interface ContactListener {
        void onContactClick(Account account);
    }
}