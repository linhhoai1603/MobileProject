package com.mobile.fe_bankproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mobile.fe_bankproject.dto.UpdateProfileRequest;
import com.mobile.fe_bankproject.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private static final String PREF_NAME = "UserInfo";
    private static final String KEY_FULL_NAME = "fullName";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_ACCOUNT_NUMBER = "accountNumber";

    private TextInputEditText etFullName;
    private TextInputEditText etEmail;
    private TextInputEditText etPhone;
    private TextInputEditText etPin;
    private TextInputLayout tilFullName;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPhone;
    private TextInputLayout tilPin;
    private Button btnSave;
    private String accountNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Get account number from intent
        accountNumber = getIntent().getStringExtra("account_number");
        if (accountNumber == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin tài khoản", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        setupClickListeners();
        loadCurrentProfile();
    }

    private void initializeViews() {
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPin = findViewById(R.id.etPin);
        tilFullName = findViewById(R.id.tilFullName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPhone = findViewById(R.id.tilPhone);
        tilPin = findViewById(R.id.tilPin);
        btnSave = findViewById(R.id.btnSave);
    }

    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> {
            if (validateInput()) {
                updateProfile();
            }
        });
    }

    private void loadCurrentProfile() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        
        // Get user info from SharedPreferences
        String fullName = sharedPreferences.getString(KEY_FULL_NAME, "");
        String email = sharedPreferences.getString(KEY_EMAIL, "");
        String phone = sharedPreferences.getString(KEY_PHONE, "");

        // Set values to EditText fields
        etFullName.setText(fullName);
        etEmail.setText(email);
        etPhone.setText(phone);
    }

    private boolean validateInput() {
        boolean isValid = true;

        if (TextUtils.isEmpty(etFullName.getText())) {
            tilFullName.setError("Vui lòng nhập họ và tên");
            isValid = false;
        } else {
            tilFullName.setError(null);
        }

        if (TextUtils.isEmpty(etEmail.getText())) {
            tilEmail.setError("Vui lòng nhập email");
            isValid = false;
        } else {
            tilEmail.setError(null);
        }

        if (TextUtils.isEmpty(etPhone.getText())) {
            tilPhone.setError("Vui lòng nhập số điện thoại");
            isValid = false;
        } else {
            tilPhone.setError(null);
        }

        if (TextUtils.isEmpty(etPin.getText())) {
            tilPin.setError("Vui lòng nhập mã PIN");
            isValid = false;
        } else {
            tilPin.setError(null);
        }

        return isValid;
    }

    private void updateProfile() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setAccountNumber(accountNumber);
        request.setFullName(etFullName.getText().toString());
        request.setEmail(etEmail.getText().toString());
        request.setPhone(etPhone.getText().toString());
        request.setPin(etPin.getText().toString());

        RetrofitClient.getInstance().getAccountService().updateProfile(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Update SharedPreferences with new values
                    SharedPreferences.Editor editor = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
                    editor.putString(KEY_FULL_NAME, etFullName.getText().toString());
                    editor.putString(KEY_EMAIL, etEmail.getText().toString());
                    editor.putString(KEY_PHONE, etPhone.getText().toString());
                    editor.apply();

                    Toast.makeText(EditProfileActivity.this, 
                        "Cập nhật thông tin thành công", 
                        Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditProfileActivity.this, 
                        "Cập nhật thông tin thất bại", 
                        Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, 
                    "Lỗi kết nối: " + t.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }
} 