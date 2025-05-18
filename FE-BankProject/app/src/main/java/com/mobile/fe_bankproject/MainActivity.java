package com.mobile.fe_bankproject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.fe_bankproject.dto.AccountResponse;

public class MainActivity extends BaseAuthenticatedActivity {

    private static final String TAG = "MainActivity";
    private TextView tvUserName;
    private Button btnViewAccount;
    private AccountResponse accountResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvUserName = findViewById(R.id.tvUserName);
        btnViewAccount = findViewById(R.id.btnViewAccount);

        // Log the intent extras for debugging
        if (getIntent().getExtras() != null) {
            Log.d(TAG, "Intent extras: " + getIntent().getExtras().toString());
            accountResponse = (AccountResponse) getIntent().getExtras().getSerializable("account_response");
            Log.d(TAG, "AccountResponse received: " + (accountResponse != null ? "not null" : "null"));
            
            if (accountResponse != null) {
                Log.d(TAG, "User full name: " + accountResponse.getUser().getFullName());
                tvUserName.setText(accountResponse.getUser().getFullName());
            } else {
                Log.e(TAG, "AccountResponse is null!");
                Toast.makeText(this, "Lỗi: Không nhận được thông tin người dùng", Toast.LENGTH_LONG).show();
                // Navigate back to login if no user data
                finish();
                return;
            }
        } else {
            Log.e(TAG, "No extras in intent!");
            Toast.makeText(this, "Lỗi: Không nhận được thông tin người dùng", Toast.LENGTH_LONG).show();
            // Navigate back to login if no extras
            finish();
            return;
        }

        // Add click listener for "Xem tài khoản" button
        btnViewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accountResponse != null) {
                    AccountDetailsFragment bottomSheetFragment = AccountDetailsFragment.newInstance(accountResponse);
                    bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
                } else {
                    Toast.makeText(MainActivity.this, "Không có thông tin tài khoản để hiển thị", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Optional: Add click listener for the user name TextView as well
        tvUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Xem thông tin tài khoản", Toast.LENGTH_SHORT).show();
            }
        });
    }
}