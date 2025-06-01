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

public class ChangePinActivity extends AppCompatActivity {
    private EditText  etCardNumber;
    private EditText etOldPin;
    private EditText  etNewPin;
    private Button btnChangePin;
    private Button btnForgotPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pin);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        etCardNumber = findViewById(R.id.etCardNumber);
        etOldPin = findViewById(R.id.etOldPin);
        etNewPin = findViewById(R.id.etNewPin);
        btnChangePin = findViewById(R.id.btnChangePin);
        btnForgotPin = findViewById(R.id.btnForgotPin);
    }

    private void setupClickListeners() {
        btnChangePin.setOnClickListener(v -> {
            if (validateInput()) {
                changePin();
            }
        });

        btnForgotPin.setOnClickListener(v -> {
            Intent intent = new Intent(ChangePinActivity.this, ForgotPinActivity.class);
            startActivity(intent);
        });
    }

    private boolean validateInput() {
        boolean isValid = true;

        String cardNumber = etCardNumber.getText().toString().trim();
        String oldPin = etOldPin.getText().toString().trim();
        String newPin = etNewPin.getText().toString().trim();

        if (TextUtils.isEmpty(cardNumber)) {
            etCardNumber.setError("Vui lòng nhập số thẻ");
            isValid = false;
        } else if (cardNumber.length() != 16) {
            etCardNumber.setError("Số thẻ phải có 16 chữ số");
            isValid = false;
        }

        if (TextUtils.isEmpty(oldPin)) {
            etOldPin.setError("Vui lòng nhập mã PIN cũ");
            isValid = false;
        } else if (oldPin.length() != 6) {
            etOldPin.setError("Mã PIN phải có 6 chữ số");
            isValid = false;
        }

        if (TextUtils.isEmpty(newPin)) {
            etNewPin.setError("Vui lòng nhập mã PIN mới");
            isValid = false;
        } else if (newPin.length() != 6) {
            etNewPin.setError("Mã PIN phải có 6 chữ số");
            isValid = false;
        } else if (newPin.equals(oldPin)) {
            etNewPin.setError("Mã PIN mới không được trùng với mã PIN cũ");
            isValid = false;
        }

        return isValid;
    }

    private void changePin() {
        Map<String, String> request = new HashMap<>();
        request.put("cardNumber", etCardNumber.getText().toString().trim());
        request.put("oldPIN", etOldPin.getText().toString().trim());
        request.put("newPIN", etNewPin.getText().toString().trim());

        RetrofitClient.getInstance().getApiService().changePin(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ChangePinActivity.this, "Đổi mã PIN thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String errorMessage = "Đổi mã PIN thất bại";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(ChangePinActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ChangePinActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}