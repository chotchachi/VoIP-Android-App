package com.example.voip_app.view;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.example.voip_app.R;
import com.example.voip_app.databinding.ActivityLoginBinding;
import com.example.voip_app.viewModel.LoginViewModel;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);

        binding = DataBindingUtil.setContentView(LoginActivity.this, R.layout.activity_login);

        binding.setLifecycleOwner(this);

        binding.setLoginViewModel(loginViewModel);

        loginViewModel.getUser().observe(this, loginUser -> {
            if (TextUtils.isEmpty(Objects.requireNonNull(loginUser).getPhoneNumber())) {
                binding.edtPhone.setError("Điền số điện thoại");
            } else if (TextUtils.isEmpty(Objects.requireNonNull(loginUser).getPassword())) {
                binding.edtPass.setError("Điền mật khẩu");
            } else if (!loginUser.isPasswordLengthGreaterThan5()) {
                binding.edtPass.setError("Mật khẩu phải trên 5 ký tự");
            } else {
                loginViewModel.login();
            }
        });

    }
}