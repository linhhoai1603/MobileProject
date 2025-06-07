package com.mobile.fe_bankproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TransferSuccess extends AppCompatActivity {

    private TextView tvAmount;
    private TextView tvRecipientName;
    private TextView tvRecipientAccount;
    private TextView tvSenderAccount;
    private TextView tvTime;
    private TextView tvDescription;
    private TextView tvTransactionFee;

    private String fromAccountNumber;
    private String toAccountNumber;
    private double amount;
    private String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transfer_success);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvAmount = findViewById(R.id.money);
        tvRecipientName = findViewById(R.id.tvRecipientNameSuccess);
        tvRecipientAccount = findViewById(R.id.tvRecipientAccountSuccess);
        tvSenderAccount = findViewById(R.id.tvSenderAccountSuccess);
        tvTime = findViewById(R.id.tvTimeSuccess);
        tvDescription = findViewById(R.id.tvDescriptionSuccess);
        tvTransactionFee = findViewById(R.id.tvTransactionFeeSuccess);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            fromAccountNumber = intent.getExtras().getString("fromAccountNumber");
            toAccountNumber = intent.getExtras().getString("toAccountNumber");
            amount = intent.getExtras().getDouble("amount", 0.0);
            description = intent.getExtras().getString("description");
            String fromAccountName = intent.getExtras().getString("fromAccountName");
            String toAccountName = intent.getExtras().getString("toAccountName");

            if (tvAmount != null) {
                DecimalFormat formatter = new DecimalFormat("###,###,##0.##");
                tvAmount.setText(formatter.format(amount) + " VND");
            }

            if (tvRecipientName != null && toAccountName != null) {
                tvRecipientName.setText(toAccountName);
            } else if (tvRecipientName != null) {
                tvRecipientName.setText("Thông tin người nhận không có");
            }

            if (tvRecipientAccount != null && toAccountNumber != null) {
                tvRecipientAccount.setText(toAccountNumber);
            } else if (tvRecipientAccount != null) {
                tvRecipientAccount.setText("Số tài khoản không có");
            }

            if (tvSenderAccount != null && fromAccountName != null) {
                tvSenderAccount.setText(fromAccountName);
            } else if (tvSenderAccount != null && fromAccountNumber != null) {
                tvSenderAccount.setText(fromAccountNumber);
            } else if (tvSenderAccount != null) {
                tvSenderAccount.setText("Tài khoản nguồn không có");
            }

            if (tvTime != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss, dd/MM/yyyy", Locale.getDefault());
                String currentTime = sdf.format(new Date());
                tvTime.setText(currentTime);
            }

            if (tvDescription != null && description != null) {
                tvDescription.setText(description);
            } else if (tvDescription != null) {
                tvDescription.setText("Không có mô tả");
            }

            if (tvTransactionFee != null) {
                tvTransactionFee.setText("Miễn phí");
            }

        } else {
            Log.e("TransferSuccess", "No extras in Intent for TransferSuccess.");
        }
    }
}