package com.mobile.fe_bankproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mobile.fe_bankproject.dto.FundTransferConfirmRequest;
import com.mobile.fe_bankproject.network.ApiService;
import com.mobile.fe_bankproject.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class fill_OTP extends AppCompatActivity {

    private EditText edtOtp;
    private Button btnContinue;

    private String fromAccountNumber;
    private String toAccountNumber;
    private double amount;
    private String description;
    private String fromAccountName;
    private String toAccountName;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fill_otp);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtOtp = findViewById(R.id.edtAccountNumber);
        btnContinue = findViewById(R.id.btnContinue);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            fromAccountNumber = intent.getExtras().getString("fromAccountNumber");
            fromAccountName = intent.getExtras().getString("fromAccountName");
            toAccountNumber = intent.getExtras().getString("toAccountNumber");
            toAccountName = intent.getExtras().getString("toAccountName");
            amount = intent.getExtras().getDouble("amount", 0.0);
            description = intent.getExtras().getString("description");

            Log.d("fill_OTP", "Received data: fromAcc=" + fromAccountNumber + ", fromName=" + fromAccountName + ", toAcc=" + toAccountNumber + ", toName=" + toAccountName + ", amount=" + amount + ", desc=" + description);

        } else {
            Log.e("fill_OTP", "No extras in Intent for fill_OTP.");
            Toast.makeText(this, "Không nhận được dữ liệu giao dịch.", Toast.LENGTH_LONG).show();
        }

        apiService = RetrofitClient.getInstance().getApiService();

        if (btnContinue != null) {
            btnContinue.setOnClickListener(v -> {
                String otp = edtOtp.getText().toString().trim();

                if (otp.isEmpty()) {
                    Toast.makeText(fill_OTP.this, "Vui lòng nhập mã OTP.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (fromAccountNumber == null || toAccountNumber == null || fromAccountName == null || toAccountName == null || description == null) {
                     Toast.makeText(fill_OTP.this, "Thiếu thông tin giao dịch.", Toast.LENGTH_SHORT).show();
                     Log.e("fill_OTP", "Missing transaction data for API call.");
                     return;
                }

                FundTransferConfirmRequest request = new FundTransferConfirmRequest(
                        fromAccountNumber,
                        toAccountNumber,
                        amount,
                        otp
                );

                Call<String> call = apiService.confirmTransfer(request);

                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String apiResponse = response.body();
                            if ("Chuyển khoản thành công!".equals(apiResponse)) {
                                Toast.makeText(fill_OTP.this, "Chuyển khoản thành công!", Toast.LENGTH_LONG).show();

                                Intent successIntent = new Intent(fill_OTP.this, TransferSuccess.class);
                                successIntent.putExtra("fromAccountNumber", fromAccountNumber);
                                successIntent.putExtra("fromAccountName", fromAccountName);
                                successIntent.putExtra("toAccountNumber", toAccountNumber);
                                successIntent.putExtra("toAccountName", toAccountName);
                                successIntent.putExtra("amount", amount);
                                successIntent.putExtra("description", description);
                                startActivity(successIntent);
                                finish();

                            } else {
                                Toast.makeText(fill_OTP.this, "Xác nhận OTP thất bại: " + apiResponse, Toast.LENGTH_LONG).show();
                                Log.e("fill_OTP", "API Response (Business Logic Error): " + apiResponse);
                            }
                        } else {
                            String errorBody = null;
                            try {
                                errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                            } catch (Exception e) {
                                errorBody = "Error reading error body";
                                Log.e("fill_OTP", "Error reading error body", e);
                            }
                            Toast.makeText(fill_OTP.this, "Gọi API thất bại: " + response.code() + (errorBody != null ? " - " + errorBody : ""), Toast.LENGTH_LONG).show();
                            Log.e("fill_OTP", "API Call failed: " + response.code() + (errorBody != null ? " - " + errorBody : ""));
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(fill_OTP.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("fill_OTP", "API Call failed", t);
                    }
                });
            });
        }
    }
}