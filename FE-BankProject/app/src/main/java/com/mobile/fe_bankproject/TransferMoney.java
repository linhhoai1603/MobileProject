package com.mobile.fe_bankproject;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.ImageButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.mobile.fe_bankproject.network.api.TransferApi;
import com.mobile.fe_bankproject.network.model.TransferPreviewRequest;
import com.mobile.fe_bankproject.network.model.TransferPreviewResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TransferMoney extends AppCompatActivity {

    private TransferApi transferApi;

    // TODO: Replace with your actual backend base URL
    private static final String BASE_URL = "YOUR_BACKEND_BASE_URL"; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transfer_money);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        transferApi = retrofit.create(TransferApi.class);

        // Thiết lập toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView tvTitle = findViewById(R.id.tvTitle);
        if (tvTitle != null) {
            tvTitle.setText("Chuyển tiền");
        }

        android.view.View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Initialize views
        Button btnContinue = findViewById(R.id.btnContinue);
        TextInputEditText edtAccountNumber = findViewById(R.id.edtAccountNumber);
        TextInputEditText edtAccountName = findViewById(R.id.edtAccountName);
        TextInputEditText edtAmount = findViewById(R.id.edtAmount);
        TextInputEditText edtTransferContent = findViewById(R.id.edtTransferContent);

        // Set OnClickListener for Continue button
        btnContinue.setOnClickListener(v -> {
            // Get input data
            String accountNumber = edtAccountNumber.getText().toString();
            String accountName = edtAccountName.getText().toString();
            String amount = edtAmount.getText().toString();
            String transferContent = edtTransferContent.getText().toString();

            // Create request object
            TransferPreviewRequest request = new TransferPreviewRequest(accountNumber, accountName, amount, transferContent);

            // Call API
            transferApi.previewTransfer(request).enqueue(new Callback<TransferPreviewResponse>() {
                @Override
                public void onResponse(Call<TransferPreviewResponse> call, Response<TransferPreviewResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        // TODO: Handle successful response
                        // Example: Display preview information from response.body()
                        Toast.makeText(TransferMoney.this, "API call successful!", Toast.LENGTH_SHORT).show();
                    } else {
                        // TODO: Handle API error (e.g., show error message)
                        Toast.makeText(TransferMoney.this, "API call failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<TransferPreviewResponse> call, Throwable t) {
                    // TODO: Handle network errors (e.g., show error message)
                    Toast.makeText(TransferMoney.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

}