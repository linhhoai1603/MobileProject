package com.mobile.fe_bankproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.mobile.fe_bankproject.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.HashMap;
import java.util.Map;

public class ForgotPinActivity extends AppCompatActivity {
    private EditText etCardNumber;
    private Button btnSendOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pin);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        etCardNumber = findViewById(R.id.etCardNumber);
        btnSendOtp = findViewById(R.id.btnSendOtp);
    }

    private void setupClickListeners() {
        btnSendOtp.setOnClickListener(v -> {
            if (validateInput()) {
                sendOtp();
            }
        });
    }

    private boolean validateInput() {
        String cardNumber = etCardNumber.getText().toString().trim();

        if (TextUtils.isEmpty(cardNumber)) {
            etCardNumber.setError("Vui lòng nhập số thẻ");
            return false;
        }

        if (cardNumber.length() != 16) {
            etCardNumber.setError("Số thẻ phải có 16 chữ số");
            return false;
        }

        return true;
    }

    private void sendOtp() {
        Map<String, String> request = new HashMap<>();
        request.put("cardNumber", etCardNumber.getText().toString().trim());

        RetrofitClient.getInstance().getAccountService().sendOtpForPin(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ForgotPinActivity.this, "Mã OTP đã được gửi!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ForgotPinActivity.this, ConfirmOTPActivity.class);
                    intent.putExtra("cardNumber", etCardNumber.getText().toString().trim());
                    intent.putExtra("type", "PIN");
                    startActivity(intent);
                } else {
                    String errorMessage = "Không thể gửi mã OTP";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(ForgotPinActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ForgotPinActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}