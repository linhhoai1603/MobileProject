package com.mobile.fe_bankproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.mobile.fe_bankproject.network.RetrofitClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmOTPActivity extends AppCompatActivity {
    private TextInputEditText etOTP;
    private Button btnConfirmOTP, btnResendOTP;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirm_otp);

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Get email from intent
        email = getIntent().getStringExtra("email");
        if (email == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin email", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        setupButtons();
    }

    private void initializeViews() {
        etOTP = findViewById(R.id.etOTP);
        btnConfirmOTP = findViewById(R.id.btnConfirmOTP);
        btnResendOTP = findViewById(R.id.btnResendOTP);
    }

    private void setupButtons() {
        btnConfirmOTP.setOnClickListener(v -> {
            if (validateOTP()) {
                confirmOTP();
            }
        });

        btnResendOTP.setOnClickListener(v -> resendOTP());
    }

    private boolean validateOTP() {
        if (TextUtils.isEmpty(etOTP.getText())) {
            etOTP.setError("Vui lòng nhập mã OTP");
            return false;
        }
        if (etOTP.getText().length() != 6) {
            etOTP.setError("Mã OTP phải có 6 chữ số");
            return false;
        }
        return true;
    }

    private void confirmOTP() {
        Map<String, String> request = new HashMap<>();
        request.put("email", email);
        request.put("otp", etOTP.getText().toString());

        RetrofitClient.getInstance().getAccountService().confirmAccount(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ConfirmOTPActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    // Navigate to login screen
                    Intent intent = new Intent(ConfirmOTPActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    String errorMessage = "Xác nhận OTP thất bại";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            android.util.Log.e("ConfirmOTPActivity", "Error response: " + errorBody);
                            errorMessage += ": " + errorBody;
                        }
                    } catch (Exception e) {
                        android.util.Log.e("ConfirmOTPActivity", "Error reading response", e);
                    }
                    Toast.makeText(ConfirmOTPActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                android.util.Log.e("ConfirmOTPActivity", "Network error", t);
                String errorMessage = "Lỗi kết nối: " + t.getMessage();
                Toast.makeText(ConfirmOTPActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void resendOTP() {
        RetrofitClient.getInstance().getAccountService().resendOTP(email).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ConfirmOTPActivity.this, "Đã gửi lại mã OTP", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ConfirmOTPActivity.this, "Gửi lại OTP thất bại: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ConfirmOTPActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}