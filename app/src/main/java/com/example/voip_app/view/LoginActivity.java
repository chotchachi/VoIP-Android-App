package com.example.voip_app.view;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.example.voip_app.R;
import com.example.voip_app.databinding.ActivityLoginBinding;
import com.example.voip_app.model.Account;
import com.example.voip_app.util.retrofit.LoginListener;
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
                ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();
                loginViewModel.login(new LoginListener() {
                    @Override
                    public void onLoginSuccess(Account account) {

                    }

                    @Override
                    public void onPhoneOrPassWrong() {
                        Toast.makeText(LoginActivity.this, "Sai tên đăng nhập hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onUserNotRegister() {

                    }

                    @Override
                    public void getMessageError(String e) {
                        Toast.makeText(LoginActivity.this, e, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                });
            }
        });

    }
}