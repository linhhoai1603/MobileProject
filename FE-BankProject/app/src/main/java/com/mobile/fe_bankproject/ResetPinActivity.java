package com.mobile.fe_bankproject;

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

public class ResetPinActivity extends AppCompatActivity {
    private EditText  etNewPin;
    private EditText etConfirmPin;
    private Button btnResetPin;
    private String cardNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pin);

        cardNumber = getIntent().getStringExtra("cardNumber");
        if (cardNumber == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin thẻ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        etNewPin = findViewById(R.id.etNewPin);
        etConfirmPin = findViewById(R.id.etConfirmPin);
        btnResetPin = findViewById(R.id.btnResetPin);
    }

    private void setupClickListeners() {
        btnResetPin.setOnClickListener(v -> {
            if (validateInput()) {
                resetPin();
            }
        });
    }

    private boolean validateInput() {
        String newPin = etNewPin.getText().toString().trim();
        String confirmPin = etConfirmPin.getText().toString().trim();

        if (TextUtils.isEmpty(newPin)) {
            etNewPin.setError("Vui lòng nhập mã PIN mới");
            return false;
        }

        if (newPin.length() != 6) {
            etNewPin.setError("Mã PIN phải có 6 chữ số");
            return false;
        }

        if (TextUtils.isEmpty(confirmPin)) {
            etConfirmPin.setError("Vui lòng nhập lại mã PIN mới");
            return false;
        }

        if (!newPin.equals(confirmPin)) {
            etConfirmPin.setError("Mã PIN không khớp");
            return false;
        }

        return true;
    }

    private void resetPin() {
        Map<String, String> request = new HashMap<>();
        request.put("cardNumber", cardNumber);
        request.put("newPIN", etNewPin.getText().toString().trim());

        RetrofitClient.getInstance().getApiService().resetPin(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ResetPinActivity.this, "Đặt lại mã PIN thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String errorMessage = "Đặt lại mã PIN thất bại";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(ResetPinActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ResetPinActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}