package com.mobile.fe_bankproject;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.mobile.fe_bankproject.dto.AccountRegister;
import com.mobile.fe_bankproject.dto.Address;
import com.mobile.fe_bankproject.dto.Gender;
import com.mobile.fe_bankproject.network.RetrofitClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText etCCCDNumber, etIssueDate, etPlaceOfIssue;
    private TextInputEditText etOriginStreet, etOriginWard, etOriginDistrict, etOriginCity;
    private TextInputEditText etResidenceStreet, etResidenceWard, etResidenceDistrict, etResidenceCity;
    private TextInputEditText etFullName, etDateOfBirth, etEmail, etPhone;
    private TextInputEditText etPassword1, etPassword2;
    private RadioGroup rgGender;
    private Button btnRegister;
    private Calendar calendar;
    private SimpleDateFormat displayDateFormat;
    private SimpleDateFormat apiDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        calendar = Calendar.getInstance();
        displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        initializeViews();
        setupDatePickers();
        setupButtons();
    }

    private void initializeViews() {
        // CCCD Information
        etCCCDNumber = findViewById(R.id.etCCCDNumber);
        etIssueDate = findViewById(R.id.etIssueDate);
        etPlaceOfIssue = findViewById(R.id.etPlaceOfIssue);

        // Place of Origin
        etOriginStreet = findViewById(R.id.etOriginStreet);
        etOriginWard = findViewById(R.id.etOriginWard);
        etOriginDistrict = findViewById(R.id.etOriginDistrict);
        etOriginCity = findViewById(R.id.etOriginCity);

        // Place of Residence
        etResidenceStreet = findViewById(R.id.etResidenceStreet);
        etResidenceWard = findViewById(R.id.etResidenceWard);
        etResidenceDistrict = findViewById(R.id.etResidenceDistrict);
        etResidenceCity = findViewById(R.id.etResidenceCity);

        // Personal Information
        etFullName = findViewById(R.id.etFullName);
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        rgGender = findViewById(R.id.rgGender);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);

        // Account Information
        etPassword1 = findViewById(R.id.etPassword1);
        etPassword2 = findViewById(R.id.etPassword2);
        btnRegister = findViewById(R.id.btnRegister);
    }

    private void setupDatePickers() {
        DatePickerDialog.OnDateSetListener issueDateListener = (view, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            etIssueDate.setText(displayDateFormat.format(calendar.getTime()));
        };

        DatePickerDialog.OnDateSetListener birthDateListener = (view, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            etDateOfBirth.setText(displayDateFormat.format(calendar.getTime()));
        };

        etIssueDate.setOnClickListener(v -> {
            new DatePickerDialog(this, issueDateListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        etDateOfBirth.setOnClickListener(v -> {
            new DatePickerDialog(this, birthDateListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void setupButtons() {
        btnRegister.setOnClickListener(v -> {
            if (validateInputs()) {
                registerAccount();
            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Validate CCCD Information
        if (TextUtils.isEmpty(etCCCDNumber.getText())) {
            etCCCDNumber.setError("Vui lòng nhập số CCCD");
            isValid = false;
        }
        if (TextUtils.isEmpty(etIssueDate.getText())) {
            etIssueDate.setError("Vui lòng chọn ngày cấp");
            isValid = false;
        }
        if (TextUtils.isEmpty(etPlaceOfIssue.getText())) {
            etPlaceOfIssue.setError("Vui lòng nhập nơi cấp");
            isValid = false;
        }

        // Validate Place of Origin
        if (TextUtils.isEmpty(etOriginStreet.getText())) {
            etOriginStreet.setError("Vui lòng nhập đường/phố");
            isValid = false;
        }
        if (TextUtils.isEmpty(etOriginWard.getText())) {
            etOriginWard.setError("Vui lòng nhập phường/xã");
            isValid = false;
        }
        if (TextUtils.isEmpty(etOriginDistrict.getText())) {
            etOriginDistrict.setError("Vui lòng nhập quận/huyện");
            isValid = false;
        }
        if (TextUtils.isEmpty(etOriginCity.getText())) {
            etOriginCity.setError("Vui lòng nhập tỉnh/thành phố");
            isValid = false;
        }

        // Validate Place of Residence
        if (TextUtils.isEmpty(etResidenceStreet.getText())) {
            etResidenceStreet.setError("Vui lòng nhập đường/phố");
            isValid = false;
        }
        if (TextUtils.isEmpty(etResidenceWard.getText())) {
            etResidenceWard.setError("Vui lòng nhập phường/xã");
            isValid = false;
        }
        if (TextUtils.isEmpty(etResidenceDistrict.getText())) {
            etResidenceDistrict.setError("Vui lòng nhập quận/huyện");
            isValid = false;
        }
        if (TextUtils.isEmpty(etResidenceCity.getText())) {
            etResidenceCity.setError("Vui lòng nhập tỉnh/thành phố");
            isValid = false;
        }

        // Validate Personal Information
        if (TextUtils.isEmpty(etFullName.getText())) {
            etFullName.setError("Vui lòng nhập họ và tên");
            isValid = false;
        }
        if (TextUtils.isEmpty(etDateOfBirth.getText())) {
            etDateOfBirth.setError("Vui lòng chọn ngày sinh");
            isValid = false;
        }
        if (rgGender.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        if (TextUtils.isEmpty(etEmail.getText())) {
            etEmail.setError("Vui lòng nhập email");
            isValid = false;
        }
        if (TextUtils.isEmpty(etPhone.getText())) {
            etPhone.setError("Vui lòng nhập số điện thoại");
            isValid = false;
        }

        // Validate Account Information
        if (TextUtils.isEmpty(etPassword1.getText())) {
            etPassword1.setError("Vui lòng nhập mật khẩu");
            isValid = false;
        }
        if (TextUtils.isEmpty(etPassword2.getText())) {
            etPassword2.setError("Vui lòng xác nhận mật khẩu");
            isValid = false;
        }
        if (!TextUtils.equals(etPassword1.getText(), etPassword2.getText())) {
            etPassword2.setError("Mật khẩu không khớp");
            isValid = false;
        }

        return isValid;
    }

    private void registerAccount() {
        // Create Address objects
        Address placeOfOrigin = new Address();
        placeOfOrigin.setVilage(etOriginStreet.getText().toString());
        placeOfOrigin.setCommune(etOriginWard.getText().toString());
        placeOfOrigin.setDistrict(etOriginDistrict.getText().toString());
        placeOfOrigin.setProvince(etOriginCity.getText().toString());

        Address placeOfResidence = new Address();
        placeOfResidence.setVilage(etResidenceStreet.getText().toString());
        placeOfResidence.setCommune(etResidenceWard.getText().toString());
        placeOfResidence.setDistrict(etResidenceDistrict.getText().toString());
        placeOfResidence.setProvince(etResidenceCity.getText().toString());

        // Create AccountRegister object
        AccountRegister registerRequest = new AccountRegister();
        registerRequest.setNumber(etCCCDNumber.getText().toString());
        registerRequest.setPersonalId("123456789"); // Default value as it's not in the form
        
        // Convert date string to ISO format
        try {
            String issueDateStr = etIssueDate.getText().toString();
            Date issueDate = displayDateFormat.parse(issueDateStr);
            String apiDateStr = apiDateFormat.format(issueDate); // Format to yyyy-MM-dd
            registerRequest.setIssueDate(apiDateStr); // Send as string
            
            String birthDateStr = etDateOfBirth.getText().toString();
            Date birthDate = displayDateFormat.parse(birthDateStr);
            String apiBirthDateStr = apiDateFormat.format(birthDate); // Format to yyyy-MM-dd
            registerRequest.setDateOfBirth(apiBirthDateStr);
        } catch (ParseException e) {
            Toast.makeText(this, "Lỗi định dạng ngày tháng", Toast.LENGTH_SHORT).show();
            return;
        }
        
        registerRequest.setPlaceOfIssue(etPlaceOfIssue.getText().toString());
        registerRequest.setPlaceOfOrigin(placeOfOrigin);
        registerRequest.setPlaceOfResidence(placeOfResidence);
        registerRequest.setFullName(etFullName.getText().toString());
        registerRequest.setGender(rgGender.getCheckedRadioButtonId() == R.id.rbMale ? "MALE" : "FEMALE");
        registerRequest.setEmail(etEmail.getText().toString());
        registerRequest.setPhone(etPhone.getText().toString());
        registerRequest.setPassword1(etPassword1.getText().toString());
        registerRequest.setPassword2(etPassword2.getText().toString());

        // Log request body
        android.util.Log.d("RegisterActivity", "Request body: " + new com.google.gson.Gson().toJson(registerRequest));

        // Call API
        RetrofitClient.getInstance().getApiService().register(registerRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Navigate to OTP confirmation screen
                    Intent intent = new Intent(RegisterActivity.this, ConfirmOTPActivity.class);
                    intent.putExtra("email", etEmail.getText().toString());
                    startActivity(intent);
                    finish();
                } else {
                    String errorMessage = "Đăng ký thất bại";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            android.util.Log.e("RegisterActivity", "Error response: " + errorBody);
                            errorMessage += ": " + errorBody;
                        } else {
                            errorMessage += ": " + response.message();
                        }
                    } catch (Exception e) {
                        android.util.Log.e("RegisterActivity", "Error reading response", e);
                        errorMessage += ": " + e.getMessage();
                    }
                    Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                android.util.Log.e("RegisterActivity", "Network error", t);
                String errorMessage = "Lỗi kết nối: " + t.getMessage();
                Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}