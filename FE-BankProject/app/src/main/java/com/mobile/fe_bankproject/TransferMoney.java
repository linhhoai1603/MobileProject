package com.mobile.fe_bankproject;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.ImageButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.mobile.fe_bankproject.dto.AccountResponse;
import com.google.android.material.textfield.TextInputEditText;
import android.text.TextWatcher;
import android.text.Editable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.mobile.fe_bankproject.network.ApiService;
import com.mobile.fe_bankproject.network.ApiClient;
import com.mobile.fe_bankproject.dto.AccountLookupResponse;
import java.io.IOException;

public class TransferMoney extends AppCompatActivity {

    private static final String TAG = "TransferMoney";

    private TextView tvAccountNumber;
    private TextView tvBalance;
    private AccountResponse accountResponse;

    private TextInputEditText edtAccountNumber;
    private TextInputEditText edtAccountName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transfer_money);

        // Find TextViews for account details
        tvAccountNumber = findViewById(R.id.tvAccountNumber);
        tvBalance = findViewById(R.id.tvBalance);

        // Find EditTexts for transfer details
        edtAccountNumber = findViewById(R.id.edtAccountNumber);
        edtAccountName = findViewById(R.id.edtAccountName);

        // Get AccountResponse from Intent extras
        if (getIntent().getExtras() != null) {
            accountResponse = (AccountResponse) getIntent().getExtras().getSerializable("account_response");
            if (accountResponse != null) {
                // Update TextViews with account details
                String accountNumberAndName = accountResponse.getAccountNumber() + " - " + accountResponse.getUser().getFullName();
                tvAccountNumber.setText(accountNumberAndName);

                // Format and set balance
                String formattedBalance = String.format("%,.0f VND", accountResponse.getBalance());
                tvBalance.setText(formattedBalance);
                Log.d(TAG, "Account details set successfully.");
            } else {
                Log.e(TAG, "AccountResponse is null in Intent extras.");
                Toast.makeText(this, "Không thể tải thông tin tài khoản.", Toast.LENGTH_LONG).show();
                // Optionally finish the activity if essential data is missing
                // finish();
            }
        } else {
            Log.e(TAG, "No extras in Intent for TransferMoney.");
            Toast.makeText(this, "Không có dữ liệu tài khoản được truyền.", Toast.LENGTH_LONG).show();
            // Optionally finish the activity if essential data is missing
            // finish();
        }

        // Add TextWatcher to edtAccountNumber
        edtAccountNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed for this functionality
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Clear account name when account number changes
                edtAccountName.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {
                String accountNumber = s.toString().trim();
                if (accountNumber.length() >= 10) { // Assuming account number is at least 10 digits
                    // Call API to look up account name
                    lookupAccountNameRetrofit(accountNumber);
                } else {
                    // Optionally clear account name if input is too short
                    edtAccountName.setText("");
                }
            }
        });
    }

    // --- START: Actual Retrofit Implementation --- //
    private void lookupAccountNameRetrofit(String accountNumber) {
        Log.d(TAG, "Looking up account name for: " + accountNumber + " using Retrofit");

        // Clear previous lookup result
        edtAccountName.setText("Đang tra cứu..."); // Optional: show loading state

        ApiService apiService = ApiClient.getClient().create(ApiService.class); // Lấy instance ApiService
        Call<AccountLookupResponse> call = apiService.lookupAccountName(accountNumber);

        call.enqueue(new Callback<AccountLookupResponse>() {
            @Override
            public void onResponse(Call<AccountLookupResponse> call, Response<AccountLookupResponse> response) {
                if (response.isSuccessful()) {
                    // API call thành công (HTTP status code 2xx)
                    AccountLookupResponse lookupResponse = response.body();
                    if (lookupResponse != null && lookupResponse.getAccountName() != null && !lookupResponse.getAccountName().isEmpty()) {
                        edtAccountName.setText(lookupResponse.getAccountName());
                    } else {
                         // Trường hợp API trả về 200 nhưng body hoặc tên tài khoản rỗng/null
                         edtAccountName.setText("Không tìm thấy tài khoản");
                         Toast.makeText(TransferMoney.this, "Không tìm thấy tên tài khoản.", Toast.LENGTH_SHORT).show();
                    }
                } else if (response.code() == 404) {
                    // API trả về 404 Not Found
                    edtAccountName.setText("Không tìm thấy tài khoản");
                    Toast.makeText(TransferMoney.this, "Không tìm thấy tài khoản với SỐ này.", Toast.LENGTH_SHORT).show();
                } else {
                    // Các lỗi HTTP khác (500, 400, v.v.)
                    edtAccountName.setText("Lỗi tra cứu");
                    try {
                         String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                         Log.e(TAG, "API Error: " + response.code() + " - " + errorBody);
                         Toast.makeText(TransferMoney.this, "Lỗi khi tra cứu tài khoản: " + response.code(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                         Log.e(TAG, "Error reading error body", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<AccountLookupResponse> call, Throwable t) {
                // Lỗi mạng hoặc các lỗi khác trong quá trình gọi API
                Log.e(TAG, "API Call Failed", t);
                edtAccountName.setText("Lỗi kết nối");
                Toast.makeText(TransferMoney.this, "Lỗi kết nối khi tra cứu tài khoản.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    // --- END: Actual Retrofit Implementation --- //

}