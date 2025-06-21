package com.mobile.fe_bankproject;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobile.fe_bankproject.adapter.BillAdapter;
import com.mobile.fe_bankproject.dto.AccountResponse;
import com.mobile.fe_bankproject.dto.BillRequest;
import com.mobile.fe_bankproject.dto.BillResponse;
import com.mobile.fe_bankproject.dto.BillStatus;
import com.mobile.fe_bankproject.network.BillService;
import com.mobile.fe_bankproject.network.RetrofitClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity implements BillAdapter.OnBillClickListener {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvNoData;
    private RadioGroup rgBillStatus;
    private BillAdapter billAdapter;
    private List<BillResponse> bills = new ArrayList<>();
    private AccountResponse accountResponse;
    private BillService billService;
    private static final String TAG = "PaymentActivity";
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Initialize views
        recyclerView = findViewById(R.id.rvBills);
        progressBar = findViewById(R.id.progressBar);
        tvNoData = findViewById(R.id.tvNoData);
        rgBillStatus = findViewById(R.id.rgBillStatus);

        // Get userId from Intent
        if (getIntent().hasExtra("userId")) {
            userId = getIntent().getIntExtra("userId", -1);
            Log.d(TAG, "Retrieved userId from Intent: " + userId);

            if (userId == -1) {
                Log.e(TAG, "Invalid userId received from Intent");
                Toast.makeText(this, "Lỗi: Không thể lấy thông tin người dùng", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        } else {
            Log.e(TAG, "No userId in Intent");
            Toast.makeText(this, "Lỗi: Không thể lấy thông tin người dùng", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        billAdapter = new BillAdapter(new ArrayList<>(), this);
        Log.d(TAG, "BillAdapter initialized");
        recyclerView.setAdapter(billAdapter);
        Log.d(TAG, "BillAdapter set to RecyclerView");

        // Setup RadioGroup listener
        rgBillStatus.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbUnpaid) {
                loadBills("UNPAID");
            } else if (checkedId == R.id.rbPaid) {
                loadBills("PAID");
            } else if (checkedId == R.id.rbOverdue) {
                loadBills("OVERDUE");
            }
        });

        // Load bills
        loadBills("UNPAID");
    }

    private void loadBills(String status) {
        Log.d(TAG, "Loading bills with status: " + status + " for userId: " + userId);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        tvNoData.setVisibility(View.GONE);

        billService = RetrofitClient.getInstance().getBillService();
        Call<List<BillResponse>> call = billService.getBillsByStatusAndUser(userId, BillStatus.valueOf(status));
        Log.d(TAG, "API call created for status: " + status);

        call.enqueue(new Callback<List<BillResponse>>() {
            @Override
            public void onResponse(Call<List<BillResponse>> call, Response<List<BillResponse>> response) {
                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "Response received - isSuccessful: " + response.isSuccessful() + ", code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    List<BillResponse> bills = response.body();
                    Log.d(TAG, "Number of bills received: " + bills.size());
                    
                    if (bills.isEmpty()) {
                        Log.d(TAG, "No bills found, showing no data message");
                        tvNoData.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        Log.d(TAG, "Bills found, updating adapter");
                        tvNoData.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        billAdapter.updateBills(bills);
                    }
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorBody = "Could not read error body: " + e.getMessage();
                    }
                    Log.e(TAG, "Error loading bills. Code: " + response.code() + ", Error: " + errorBody);
                    Toast.makeText(PaymentActivity.this, "Lỗi khi tải danh sách hóa đơn: " + errorBody, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<BillResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Network error loading bills", t);
                Toast.makeText(PaymentActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onPayClick(BillResponse bill) {
        showPaymentDialog(bill);
    }

    private void showPaymentDialog(BillResponse bill) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận thanh toán");
        builder.setMessage("Bạn có chắc chắn muốn thanh toán hóa đơn này?\nSố tiền: " + bill.getTotalAmount() + " VND");

        final EditText input = new EditText(this);
        input.setHint("Nhập mã PIN");
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("Thanh toán", (dialog, which) -> {
            String pin = input.getText().toString();
            if (pin.length() == 6) {
                processPayment(bill, pin);
            } else {
                Toast.makeText(PaymentActivity.this, "Mã PIN phải có 6 chữ số", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void processPayment(BillResponse bill, String pin) {
        progressBar.setVisibility(View.VISIBLE);

        BillRequest request = new BillRequest();
        request.setBillCode(bill.getBillCode());
        request.setUserId(userId);
        request.setPin(pin);

        billService.payment(request).enqueue(new Callback<BillResponse>() {
            @Override
            public void onResponse(Call<BillResponse> call, Response<BillResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(PaymentActivity.this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();
                    loadBills("UNPAID"); // Refresh the list
                } else {
                    Toast.makeText(PaymentActivity.this, "Thanh toán thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BillResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(PaymentActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
} 