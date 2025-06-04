package com.mobile.fe_bankproject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mobile.fe_bankproject.dto.ChangePasswordRequest;
import com.mobile.fe_bankproject.network.AccountService;
import com.mobile.fe_bankproject.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {
    private TextInputEditText etCurrentPassword;
    private TextInputEditText etNewPassword;
    private TextInputEditText etConfirmNewPassword;
    private TextInputLayout tilCurrentPassword;
    private TextInputLayout tilNewPassword;
    private TextInputLayout tilConfirmNewPassword;
    private Button btnChangePassword;
    private AccountService apiService;
    private String accountNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Get account number from intent
        accountNumber = getIntent().getStringExtra("account_number");
        if (accountNumber == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin tài khoản", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        apiService = RetrofitClient.getInstance().getAccountService();
        
        // Initialize views
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword);
        tilCurrentPassword = findViewById(R.id.tilCurrentPassword);
        tilNewPassword = findViewById(R.id.tilNewPassword);
        tilConfirmNewPassword = findViewById(R.id.tilConfirmNewPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        // Set up click listener
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    changePassword();
                }
            }
        });
    }

    private boolean validateInput() {
        boolean isValid = true;
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmNewPassword = etConfirmNewPassword.getText().toString().trim();

        // Kiểm tra mật khẩu hiện tại
        if (TextUtils.isEmpty(currentPassword)) {
            tilCurrentPassword.setError("Vui lòng nhập mật khẩu hiện tại");
            isValid = false;
        } else {
            tilCurrentPassword.setError(null);
        }

        // Kiểm tra mật khẩu mới
        if (TextUtils.isEmpty(newPassword)) {
            tilNewPassword.setError("Vui lòng nhập mật khẩu mới");
            isValid = false;
        } else if (newPassword.length() < 6) {
            tilNewPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            isValid = false;
        } else if (newPassword.equals(currentPassword)) {
            tilNewPassword.setError("Mật khẩu mới không được trùng với mật khẩu hiện tại");
            isValid = false;
        } else {
            tilNewPassword.setError(null);
        }

        // Kiểm tra mật khẩu xác nhận
        if (TextUtils.isEmpty(confirmNewPassword)) {
            tilConfirmNewPassword.setError("Vui lòng xác nhận mật khẩu mới");
            isValid = false;
        } else if (!confirmNewPassword.equals(newPassword)) {
            tilConfirmNewPassword.setError("Mật khẩu xác nhận không khớp");
            isValid = false;
        } else {
            tilConfirmNewPassword.setError(null);
        }

        return isValid;
    }

    private void changePassword() {
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();

        // Show loading state
        btnChangePassword.setEnabled(false);
        btnChangePassword.setText("Đang xử lý...");

        // Tạo request thay đổi mật khẩu
        ChangePasswordRequest request = new ChangePasswordRequest(
            accountNumber,
            currentPassword,
            newPassword
        );

        apiService.changePassword(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // Reset button state
                btnChangePassword.setEnabled(true);
                btnChangePassword.setText("Thay đổi mật khẩu");

                if (response.isSuccessful()) {
                    Toast.makeText(ChangePasswordActivity.this, 
                        "Thay đổi mật khẩu thành công", 
                        Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ChangePasswordActivity.this, 
                        "Mật khẩu hiện tại không đúng", 
                        Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Reset button state
                btnChangePassword.setEnabled(true);
                btnChangePassword.setText("Thay đổi mật khẩu");

                Toast.makeText(ChangePasswordActivity.this, 
                    "Lỗi kết nối, vui lòng thử lại", 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }
}