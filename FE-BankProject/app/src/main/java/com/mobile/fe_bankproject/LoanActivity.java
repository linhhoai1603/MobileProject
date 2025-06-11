package com.mobile.fe_bankproject;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
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
import com.mobile.fe_bankproject.dto.AccountResponse;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AdapterView;
import com.mobile.fe_bankproject.dto.LoanRequest;
import com.mobile.fe_bankproject.dto.LoanResponse;
import com.mobile.fe_bankproject.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoanActivity extends AppCompatActivity {
    private EditText edtSoTien, edtNoiDung;
    private Button btnTiepTuc;
    private Spinner spThoiHan;
    private TextView tvSoTienPhaiTra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan);

        TextView tvAccountNumber = findViewById(R.id.tvAccountNumber);
        TextView tvBalance = findViewById(R.id.tvBalance);
        tvSoTienPhaiTra = findViewById(R.id.tvSoTienPhaiTra);

        AccountResponse accountResponse = (AccountResponse) getIntent().getSerializableExtra("account_response");
        if (accountResponse != null) {
            tvAccountNumber.setText(accountResponse.getAccountNumber() + " - " + accountResponse.getAccountName());
            tvBalance.setText(accountResponse.getBalance() + " VND");
        }
        edtSoTien = findViewById(R.id.edtSoTien);
        edtNoiDung = findViewById(R.id.edtNoiDung);
        btnTiepTuc = findViewById(R.id.btnTiepTuc);
        spThoiHan = findViewById(R.id.spThoiHan);
        edtSoTien.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                tinhSoTienPhaiTra();
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        spThoiHan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tinhSoTienPhaiTra();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
        String[] options = {"3 tháng", "6 tháng", "1 năm"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spThoiHan.setAdapter(adapter);

        btnTiepTuc.setOnClickListener(v -> {
            String soTien = edtSoTien.getText().toString();
            String thoiHan = spThoiHan.getSelectedItem().toString();
            String noiDung = edtNoiDung.getText().toString();
            if (soTien.isEmpty() || thoiHan.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!soTien.matches("\\d+")) {
                Toast.makeText(this, "Số tiền chỉ được nhập số!", Toast.LENGTH_SHORT).show();
                return;
            }
            int n = spThoiHan.getSelectedItemPosition() == 2 ? 12
                    : spThoiHan.getSelectedItemPosition() == 1 ? 6 : 3;

            // build request DTO
            LoanRequest req = new LoanRequest();
            req.setAccountNumber(tvAccountNumber.getText().toString().split(" - ")[0]);
            req.setAmount(Double.parseDouble(soTien));
            req.setTerm(n);
            req.setNote(noiDung);

            // call backend
            RetrofitClient.getInstance().getLoanService()
                .createLoan(req)
                .enqueue(new Callback<LoanResponse>() {
                @Override
                public void onResponse(Call<LoanResponse> call, Response<LoanResponse> res) {
                    if(res.isSuccessful() && res.body()!=null){
                    Toast.makeText(LoanActivity.this,
                        "Loan ID: "+res.body().getId(),
                        Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoanActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                    Toast.makeText(LoanActivity.this,
                        "Failed: "+res.code(),
                        Toast.LENGTH_SHORT).show();
                    }
                }
            @Override
            public void onFailure(Call<LoanResponse> call, Throwable t) {
                Toast.makeText(LoanActivity.this,
                "Error: "+t.getMessage(),
                Toast.LENGTH_SHORT).show();
            }
        });
});
    }

    private void tinhSoTienPhaiTra() {
        String soTienStr = edtSoTien.getText().toString().trim();
        if (!soTienStr.matches("\\d+")) {
            tvSoTienPhaiTra.setText("Số tiền phải trả: ");
            return;
        }
        double P = Double.parseDouble(soTienStr);

        int n = 0;
        int pos = spThoiHan.getSelectedItemPosition();
        if (pos == 0) n = 3;
        else if (pos == 1) n = 6;
        else if (pos == 2) n = 12;

        double soTienPhaiTra = P * Math.pow(1 + 0.2, n);
        tvSoTienPhaiTra.setText("Số tiền phải trả: " + String.format("%,.0f", soTienPhaiTra) + " VND");
    }
}
