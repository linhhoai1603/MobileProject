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
        // Tạo AccountLogin object
        AccountLogin loginRequest = new AccountLogin();
        loginRequest.setPhone(phone);
        loginRequest.setPassword(password);

        // Log request data
        String requestJson = new Gson().toJson(loginRequest);
        Log.d("LoginDebug", "=== REQUEST DATA ===");
        Log.d("LoginDebug", "Phone: " + phone);
        Log.d("LoginDebug", "Password: " + password);
        Log.d("LoginDebug", "Request JSON: " + requestJson);

        // Gọi API login
        RetrofitClient.getInstance().getAccountService().login(loginRequest).enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {
                // Log response details
                Log.d("LoginDebug", "=== RESPONSE DATA ===");
                Log.d("LoginDebug", "Response code: " + response.code());
                Log.d("LoginDebug", "Response URL: " + call.request().url());
                Log.d("LoginDebug", "Response headers: " + response.headers());

                if (response.isSuccessful() && response.body() != null) {
                    AccountResponse accountResponse = response.body();
                    Log.d("LoginDebug", "Login successful. Response: " + new Gson().toJson(accountResponse));
                    try {
                        // Lưu thông tin người dùng vào SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("accountNumber", accountResponse.getAccountNumber());
                        editor.putString("accountName", accountResponse.getAccountName());
                        editor.putString("phone", accountResponse.getPhone());
                        editor.putString("balance", String.valueOf(accountResponse.getBalance()));
                        editor.putString("fullName", accountResponse.getUser().getFullName());
                        editor.putString("email", accountResponse.getUser().getEmail());
                        editor.apply();

                        // Log the AccountResponse before passing it
                        Log.d("LoginDebug", "Passing AccountResponse to MainActivity: " + new Gson().toJson(accountResponse));

                        // Chuyển đến màn hình chính
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("account_response", accountResponse);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        Log.e("LoginError", "Error processing response", e);
                        Toast.makeText(LoginActivity.this, "Lỗi xử lý dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String errorMessage = response.errorBody() != null ? response.errorBody().string() : "";
                        Log.e("LoginError", "=== ERROR RESPONSE ===");
                        Log.e("LoginError", "Error code: " + response.code());
                        Log.e("LoginError", "Error message: " + errorMessage);
                        Log.e("LoginError", "Request URL: " + call.request().url());
                        Log.e("LoginError", "Request method: " + call.request().method());
                        Log.e("LoginError", "Request body: " + requestJson);

                        // Xử lý các trường hợp lỗi cụ thể
                        if (response.code() == 404) {
                            Toast.makeText(LoginActivity.this, "Tài khoản không tồn tại", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 401) {
                            Toast.makeText(LoginActivity.this, "Mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Lỗi đăng nhập: " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        Log.e("LoginError", "Error reading error body", e);
                        Toast.makeText(LoginActivity.this, "Lỗi xử lý phản hồi", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<AccountResponse> call, Throwable t) {
                Log.e("LoginError", "=== NETWORK ERROR ===");
                Log.e("LoginError", "Error: " + t.getMessage());
                Log.e("LoginError", "Request URL: " + call.request().url());
                Log.e("LoginError", "Request method: " + call.request().method());
                Log.e("LoginError", "Request body: " + requestJson);
                Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}