package com.example.voip_app.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.example.voip_app.App;
import com.example.voip_app.R;
import com.example.voip_app.databinding.ActivityLoginBinding;
import com.example.voip_app.model.Account;
import com.example.voip_app.util.retrofit.LoginListener;
import com.example.voip_app.util.retrofit.RegisterListener;
import com.example.voip_app.viewModel.LoginViewModel;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    private ActivityLoginBinding binding;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initBinding();
        initProgressDialog();

        loginViewModel.getUser().observe(this, loginUser -> {
            if (TextUtils.isEmpty(Objects.requireNonNull(loginUser).getPhoneNumber())) {
                binding.edtPhone.setError("Điền số điện thoại");
            } else if (TextUtils.isEmpty(Objects.requireNonNull(loginUser).getPassword())) {
                binding.edtPass.setError("Điền mật khẩu");
            } else if (!loginUser.isPasswordLengthGreaterThan5()) {
                binding.edtPass.setError("Mật khẩu phải trên 5 ký tự");
            } else {
                loginUser();
            }
        });

        if (App.getAccount() != null){
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }
    }

    private void loginUser() {
        progressDialog.show();
        loginViewModel.login(new LoginListener() {
            @Override
            public void onLoginSuccess(Account account) {
                loginSuccess(account);
            }

            @Override
            public void onPhoneOrPassWrong() {
                Toast.makeText(LoginActivity.this, "Sai tên đăng nhập hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onUserNotRegister() {
                registerUser();
                progressDialog.dismiss();
            }

            @Override
            public void getMessageError(String e) {
                Toast.makeText(LoginActivity.this, e, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void loginSuccess(Account account) {
        loginViewModel.storeLoginSession(account);
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        finish();
    }

    private void initBinding() {
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        binding = DataBindingUtil.setContentView(LoginActivity.this, R.layout.activity_login);
        binding.setLifecycleOwner(this);
        binding.setLoginViewModel(loginViewModel);
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage(getString(R.string.loading));
    }

    private void registerUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Tài khoản chưa đăng ký");
        builder.setMessage("Điền tên của bạn và đăng ký với số điện thoại và mật khẩu đã nhập");
        EditText input = new EditText(LoginActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton(android.R.string.yes, (dialog, which) -> {
            progressDialog.show();
            loginViewModel.register(input.getText().toString(), new RegisterListener() {
                @Override
                public void onRegisterSuccess(Account account) {
                    loginSuccess(account);
                }

                @Override
                public void onRegisterFailed() {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Register Failed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void getMessageError(String e) {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, e, Toast.LENGTH_SHORT).show();
                }
            });
        });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss());

        builder.show();
    }
}