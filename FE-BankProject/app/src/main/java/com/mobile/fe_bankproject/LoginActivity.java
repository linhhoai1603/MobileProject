package com.mobile.fe_bankproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.mobile.fe_bankproject.dto.AccountLogin;
import com.mobile.fe_bankproject.dto.AccountResponse;
import com.mobile.fe_bankproject.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
    private EditText etPassword;
    private ImageButton btnTogglePassword;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize password visibility toggle
        etPassword = findViewById(R.id.etPassword);
        btnTogglePassword = findViewById(R.id.btnTogglePassword);
        btnTogglePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    // Hide password
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    btnTogglePassword.setImageResource(R.drawable.ic_visibility_off);
                } else {
                    // Show password
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    btnTogglePassword.setImageResource(R.drawable.ic_visibility);
                }
                isPasswordVisible = !isPasswordVisible;
                // Move cursor to end of text
                etPassword.setSelection(etPassword.getText().length());
            }
        });

        // Sự kiện click cho 'Quên mật khẩu?'
        TextView tvForgot = findViewById(R.id.tvForgot);
        tvForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
        // Sự kiện click cho 'Đăng ký tài khoản?'
        TextView tvRegister = findViewById(R.id.tvRegister);
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // Sự kiện click cho nút LOGIN
        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = ((EditText) findViewById(R.id.etPhone)).getText().toString();
                String password = ((EditText) findViewById(R.id.etPassword)).getText().toString();
                if (phone.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }
                login(phone, password);
            }
        });
    }

    private void login(String phone, String password) {
        AccountLogin loginRequest = new AccountLogin(phone, password);
        RetrofitClient.getInstance().getAccountService().login(loginRequest)
                .enqueue(new Callback<AccountResponse>() {
                    @Override
                    public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {
                        // Log response details
                        Log.d("LoginDebug", "=== RESPONSE DATA ===");
                        Log.d("LoginDebug", "Response code: " + response.code());
                        Log.d("LoginDebug", "Response URL: " + call.request().url());
                        Log.d("LoginDebug", "Response headers: " + response.headers());

                        if (response.isSuccessful() && response.body() != null) {
                            AccountResponse accountResponse = response.body();
                            Log.d("LoginDebug", "Login successful. Full Response: " + new Gson().toJson(accountResponse));
                            
                            // Check userResponse
                            if (accountResponse.getUserResponse() == null) {
                                Log.e("LoginDebug", "userResponse is null in accountResponse!");
                                Toast.makeText(LoginActivity.this, 
                                    "Lỗi: Không nhận được thông tin người dùng", Toast.LENGTH_LONG).show();
                                return;
                            }

                            Log.d("LoginDebug", "UserResponse details:");
                            Log.d("LoginDebug", "- ID: " + accountResponse.getUserResponse().getId());
                            Log.d("LoginDebug", "- FullName: " + accountResponse.getUserResponse().getFullName());
                            Log.d("LoginDebug", "- Email: " + accountResponse.getUserResponse().getEmail());

                            try {
                                // Lưu thông tin người dùng vào SharedPreferences
                                SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("accountNumber", accountResponse.getAccountNumber());
                                editor.putString("accountName", accountResponse.getAccountName());
                                editor.putString("phone", accountResponse.getPhone());
                                editor.putString("balance", String.valueOf(accountResponse.getBalance()));
                                editor.putString("fullName", accountResponse.getUserResponse().getFullName());
                                editor.putString("email", accountResponse.getUserResponse().getEmail());
                                editor.putInt("userId", accountResponse.getUserResponse().getId());
                                editor.apply();

                                Log.d("LoginDebug", "Saved userId to SharedPreferences: " + accountResponse.getUserResponse().getId());

                                // Log the AccountResponse before passing it
                                Log.d("LoginDebug", "AccountResponse before passing to MainActivity:");
                                Log.d("LoginDebug", "- AccountNumber: " + accountResponse.getAccountNumber());
                                Log.d("LoginDebug", "- AccountName: " + accountResponse.getAccountName());
                                Log.d("LoginDebug", "- Balance: " + accountResponse.getBalance());
                                Log.d("LoginDebug", "- UserResponse: " + (accountResponse.getUserResponse() != null ? "not null" : "null"));
                                if (accountResponse.getUserResponse() != null) {
                                    Log.d("LoginDebug", "  - User ID: " + accountResponse.getUserResponse().getId());
                                    Log.d("LoginDebug", "  - User FullName: " + accountResponse.getUserResponse().getFullName());
                                    Log.d("LoginDebug", "  - User Email: " + accountResponse.getUserResponse().getEmail());
                                }

                                // Chuyển đến màn hình chính
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("account_response", accountResponse);
                                startActivity(intent);
                                finish();
                            } catch (Exception e) {
                                Log.e("LoginDebug", "Error saving user data: " + e.getMessage());
                                Toast.makeText(LoginActivity.this, 
                                    "Lỗi khi lưu thông tin người dùng", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            String errorMessage = "Đăng nhập thất bại";
                            try {
                                if (response.errorBody() != null) {
                                    errorMessage += ": " + response.errorBody().string();
                                }
                            } catch (IOException e) {
                                errorMessage += ": " + e.getMessage();
                            }
                            Log.e("LoginDebug", errorMessage);
                            Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AccountResponse> call, Throwable t) {
                        Log.e("LoginDebug", "Network error: " + t.getMessage());
                        Toast.makeText(LoginActivity.this, 
                            "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

}