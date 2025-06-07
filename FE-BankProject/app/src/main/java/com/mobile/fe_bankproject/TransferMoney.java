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
import android.widget.Button;

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
import android.text.TextUtils;

import com.mobile.fe_bankproject.dto.FundTransferRequest;
import com.mobile.fe_bankproject.dto.FundTransferPreview;

public class TransferMoney extends AppCompatActivity {

    private static final String TAG = "TransferMoney";

    private TextView tvAccountNumber;
    private TextView tvBalance;
    private AccountResponse accountResponse;

    private TextInputEditText edtAccountNumber;
    private TextInputEditText edtAccountName;
    private TextInputEditText edtAmount;
    private TextInputEditText edtTransferContent;
    private Button btnContinue;

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
        edtAmount = findViewById(R.id.edtAmount);
        edtTransferContent = findViewById(R.id.edtTransferContent);
        btnContinue = findViewById(R.id.btnContinue);

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

        // Add OnClickListener to btnContinue
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gọi hàm xử lý preview chuyển tiền
                transferPreview();
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

    // Hàm xử lý gọi API preview chuyển tiền
    private void transferPreview() {
        // 1. Lấy dữ liệu từ các ô nhập liệu
        String toAccountNumber = edtAccountNumber.getText().toString().trim();
        String amountStr = edtAmount.getText().toString().trim();
        String description = edtTransferContent.getText().toString().trim();
        String fromAccountNumber = null; // Số tài khoản nguồn

        // Lấy số tài khoản nguồn từ accountResponse
        if (accountResponse != null && accountResponse.getAccountNumber() != null) {
            fromAccountNumber = accountResponse.getAccountNumber();
        }

        // 2. Kiểm tra dữ liệu đầu vào (Validation cơ bản)
        if (TextUtils.isEmpty(fromAccountNumber)) {
             Toast.makeText(this, "Không tìm thấy tài khoản nguồn.", Toast.LENGTH_SHORT).show();
             Log.e(TAG, "Source account number is null or empty.");
             return;
        }
        if (TextUtils.isEmpty(toAccountNumber)) {
            edtAccountNumber.setError("Số tài khoản đích không được để trống");
            return;
        }
        if (TextUtils.isEmpty(amountStr)) {
            edtAmount.setError("Số tiền không được để trống");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                 edtAmount.setError("Số tiền phải lớn hơn 0");
                 return;
            }
            // Optional: Check if transfer amount exceeds source account balance
            if (accountResponse != null && amount > accountResponse.getBalance()) {
                 edtAmount.setError("Số tiền vượt quá số dư tài khoản");
                 Toast.makeText(this, "Số tiền chuyển vượt quá số dư.", Toast.LENGTH_SHORT).show();
                 return;
            }

        } catch (NumberFormatException e) {
            edtAmount.setError("Số tiền không hợp lệ");
            return;
        }
        // Description có thể rỗng, không cần validate

        Log.d(TAG, "Calling transferPreview with:" +
                   " from=" + fromAccountNumber +
                   ", to=" + toAccountNumber +
                   ", amount=" + amount +
                   ", desc=" + description);


        // 3. Tạo đối tượng FundTransferRequest
        FundTransferRequest request = new FundTransferRequest(fromAccountNumber, toAccountNumber, amount, description);

        // 4. Gọi API sử dụng Retrofit
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<FundTransferPreview> call = apiService.transferPreview(request);

        // Thực hiện gọi bất đồng bộ
        call.enqueue(new Callback<FundTransferPreview>() {
            @Override
            public void onResponse(Call<FundTransferPreview> call, Response<FundTransferPreview> response) {
                if (response.isSuccessful()) {
                    // Xử lý khi API gọi thành công (HTTP status code 2xx)
                    FundTransferPreview preview = response.body();
                    if (preview != null) {
                        Log.d(TAG, "Transfer Preview Successful: " + preview.getToAccountName());

                        // START: Logic chuyển hướng và truyền dữ liệu
                        Intent intent = new Intent(TransferMoney.this, confirmInfor.class);

                        // Truyền các thông tin từ preview qua Intent
                        intent.putExtra("fromAccountNumber", preview.getFromAccountNumber());
                        intent.putExtra("fromAccountName", preview.getFromAccountName());
                        intent.putExtra("toAccountNumber", preview.getToAccountNumber());
                        intent.putExtra("toAccountName", preview.getToAccountName());
                        intent.putExtra("amount", preview.getAmount());
                        intent.putExtra("description", preview.getDescription());

                        // Có thể truyền thêm các thông tin khác từ preview nếu có
                        // intent.putExtra("fee", preview.getFee()); // Ví dụ

                        startActivity(intent); // Bắt đầu Activity xác nhận

                        // END: Logic chuyển hướng và truyền dữ liệu

                    } else {
                        Log.e(TAG, "Transfer Preview Response body is null");
                        Toast.makeText(TransferMoney.this, "Không nhận được dữ liệu xem trước.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Xử lý khi API trả về lỗi (HTTP status codes khác 2xx)
                    try {
                         String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                         Log.e(TAG, "Transfer Preview API Error: " + response.code() + " - " + errorBody);
                         Toast.makeText(TransferMoney.this, "Lỗi xem trước chuyển tiền: " + response.code(), Toast.LENGTH_LONG).show();
                         // TODO: Hiển thị thông báo lỗi chi tiết hơn cho người dùng dựa vào errorBody nếu có cấu trúc lỗi cụ thể từ BE
                    } catch (IOException e) {
                         Log.e(TAG, "Error reading error body from transfer preview response", e);
                         Toast.makeText(TransferMoney.this, "Lỗi xử lý phản hồi từ máy chủ.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<FundTransferPreview> call, Throwable t) {
                // Xử lý khi có lỗi mạng hoặc lỗi trong quá trình gọi API
                Log.e(TAG, "Transfer Preview API Call Failed", t);
                Toast.makeText(TransferMoney.this, "Lỗi kết nối khi xem trước chuyển tiền.", Toast.LENGTH_LONG).show();
                // TODO: Xử lý lỗi kết nối mạng
            }
        });
    }


}