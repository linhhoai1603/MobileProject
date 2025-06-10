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
import okhttp3.RequestBody;
import okhttp3.MediaType;
import org.json.JSONObject;

public class ChangePinActivity extends AppCompatActivity {
    private EditText etCardNumber;
    private EditText etOldPin;
    private EditText etNewPin;
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
        try {
            // Lấy dữ liệu từ các trường nhập liệu
            String cardNumber = etCardNumber.getText().toString().trim();
            String oldPin = etOldPin.getText().toString().trim();
            String newPin = etNewPin.getText().toString().trim();

            // Log để debug
            android.util.Log.d("ChangePinActivity", "Card Number: " + cardNumber);
            android.util.Log.d("ChangePinActivity", "Old PIN: " + oldPin);
            android.util.Log.d("ChangePinActivity", "New PIN: " + newPin);

            // Kiểm tra dữ liệu đầu vào
            if (TextUtils.isEmpty(cardNumber) || TextUtils.isEmpty(oldPin) || TextUtils.isEmpty(newPin)) {
                etCardNumber.setError("Vui lòng nhập đầy đủ thông tin");
                return;
            }

            // Tạo Map request
            Map<String, String> requestMap = new HashMap<>();
            requestMap.put("cardNumber", cardNumber);
            requestMap.put("oldPin", oldPin);  // PIN cũ
            requestMap.put("newPin", newPin);  // PIN mới

            // Log request map
            android.util.Log.d("ChangePinActivity", "Request Map: " + requestMap.toString());

            // Gọi API đổi mã PIN
            RetrofitClient.getInstance().getAccountService().changePin(requestMap).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        runOnUiThread(() -> {
                            Toast.makeText(ChangePinActivity.this, "Đổi mã PIN thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    } else {
                        String errorMessage = "Đổi mã PIN thất bại";
                        try {
                            if (response.errorBody() != null) {
                                String errorBody = response.errorBody().string();
                                // Log error response
                                android.util.Log.e("ChangePinActivity", "Error Response: " + errorBody);
                                // Parse JSON error response
                                org.json.JSONObject jsonError = new org.json.JSONObject(errorBody);
                                String message = jsonError.optString("message", errorMessage);
                                if (!TextUtils.isEmpty(message)) {
                                    errorMessage = message;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            android.util.Log.e("ChangePinActivity", "Error parsing response: " + e.getMessage());
                        }
                        final String finalErrorMessage = errorMessage;
                        runOnUiThread(() -> {
                            etOldPin.setError(finalErrorMessage);
                        });
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    String errorMessage = "Lỗi kết nối: ";
                    if (t.getMessage() != null) {
                        errorMessage += t.getMessage();
                    } else {
                        errorMessage += "Không thể kết nối đến máy chủ";
                    }
                    android.util.Log.e("ChangePinActivity", "Network Error: " + errorMessage);
                    final String finalErrorMessage = errorMessage;
                    runOnUiThread(() -> {
                        etOldPin.setError(finalErrorMessage);
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            android.util.Log.e("ChangePinActivity", "Exception: " + e.getMessage());
            final String errorMessage = "Có lỗi xảy ra: " + e.getMessage();
            runOnUiThread(() -> {
                etOldPin.setError(errorMessage);
            });
        }
    }
}