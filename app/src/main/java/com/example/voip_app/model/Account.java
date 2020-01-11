package com.example.voip_app.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

public class Account implements Parcelable {
    private int id;
    private String phoneNumber;
    private String name;

    public Account(){

    }

    public Account(int id, String phoneNumber, String name) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.name = name;
    }

    protected Account(Parcel in) {
        id = in.readInt();
        phoneNumber = in.readString();
        name = in.readString();
    }

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @BindingAdapter("phoneNumber")
    public static void setPhoneContact(TextView view, String phoneNumber) {
        view.setText(phoneNumber);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @BindingAdapter("name")
    public static void setNameContact(TextView view, String name) {
        view.setText(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(phoneNumber);
        dest.writeString(name);
    }
}
