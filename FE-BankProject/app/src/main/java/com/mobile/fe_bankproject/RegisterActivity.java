package com.mobile.fe_bankproject;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.mobile.fe_bankproject.dto.AccountRegister;
import com.mobile.fe_bankproject.dto.Address;
import com.mobile.fe_bankproject.dto.DistrictDTO;
import com.mobile.fe_bankproject.dto.ProvinceDTO;
import com.mobile.fe_bankproject.dto.WardDTO;
import com.mobile.fe_bankproject.network.AccountService;
import com.mobile.fe_bankproject.network.AddressService;
import com.mobile.fe_bankproject.network.RetrofitClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    private TextInputEditText etCCCDNumber, etIssueDate, etPlaceOfIssue;
    private Spinner spOriginProvince, spOriginDistrict, spOriginWard;
    private Spinner spResidenceProvince, spResidenceDistrict, spResidenceWard;
    private TextInputEditText etOriginStreet, etResidenceStreet;
    private TextInputEditText etFullName, etDateOfBirth, etEmail, etPhone;
    private TextInputEditText etPassword1, etPassword2;
    private Spinner spGender;
    private Button btnRegister;
    private Calendar calendar;
    private SimpleDateFormat displayDateFormat;
    private SimpleDateFormat apiDateFormat;
    private TextView loginLink;
    private AddressService addressService;
    private AccountService accountService;

    private List<ProvinceDTO> provinces = new ArrayList<>();
    private List<DistrictDTO> districts = new ArrayList<>();
    private List<WardDTO> wards = new ArrayList<>();

    private ArrayAdapter<ProvinceDTO> provinceAdapter;
    private ArrayAdapter<DistrictDTO> districtAdapter;
    private ArrayAdapter<WardDTO> wardAdapter;

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
        setupGenderSpinner();
        setupAddressSpinners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clear adapters
        if (provinceAdapter != null) {
            provinceAdapter.clear();
        }
        if (districtAdapter != null) {
            districtAdapter.clear();
        }
        if (wardAdapter != null) {
            wardAdapter.clear();
        }
        // Clear lists
        provinces.clear();
        districts.clear();
        wards.clear();
    }

    private void initializeViews() {
        // CCCD Information
        etCCCDNumber = findViewById(R.id.etCCCDNumber);
        etIssueDate = findViewById(R.id.etIssueDate);
        etPlaceOfIssue = findViewById(R.id.etPlaceOfIssue);

        // Place of Origin
        spOriginProvince = findViewById(R.id.spOriginProvince);
        spOriginDistrict = findViewById(R.id.spOriginDistrict);
        spOriginWard = findViewById(R.id.spOriginWard);
        etOriginStreet = findViewById(R.id.etOriginStreet);

        // Place of Residence
        spResidenceProvince = findViewById(R.id.spResidenceProvince);
        spResidenceDistrict = findViewById(R.id.spResidenceDistrict);
        spResidenceWard = findViewById(R.id.spResidenceWard);
        etResidenceStreet = findViewById(R.id.etResidenceStreet);

        // Personal Information
        etFullName = findViewById(R.id.etFullName);
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        spGender = findViewById(R.id.spGender);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);

        // Account Information
        etPassword1 = findViewById(R.id.etPassword1);
        etPassword2 = findViewById(R.id.etPassword2);
        btnRegister = findViewById(R.id.btnRegister);
        loginLink = findViewById(R.id.loginLink);
        
        addressService = RetrofitClient.getInstance().getAddressService();
        accountService = RetrofitClient.getInstance().getAccountService();
    }

    private void setupGenderSpinner() {
        String[] genders = {"Nam", "Nữ"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                genders);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGender.setAdapter(adapter);
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
        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
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
        if (spOriginProvince.getSelectedItem() == null) {
            Toast.makeText(this, "Vui lòng chọn tỉnh/thành phố quê quán", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        if (spOriginDistrict.getSelectedItem() == null) {
            Toast.makeText(this, "Vui lòng chọn quận/huyện quê quán", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        if (spOriginWard.getSelectedItem() == null) {
            Toast.makeText(this, "Vui lòng chọn phường/xã quê quán", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        if (TextUtils.isEmpty(etOriginStreet.getText())) {
            etOriginStreet.setError("Vui lòng nhập đường/phố quê quán");
            isValid = false;
        }

        // Validate Place of Residence
        if (spResidenceProvince.getSelectedItem() == null) {
            Toast.makeText(this, "Vui lòng chọn tỉnh/thành phố nơi thường trú", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        if (spResidenceDistrict.getSelectedItem() == null) {
            Toast.makeText(this, "Vui lòng chọn quận/huyện nơi thường trú", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        if (spResidenceWard.getSelectedItem() == null) {
            Toast.makeText(this, "Vui lòng chọn phường/xã nơi thường trú", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        if (TextUtils.isEmpty(etResidenceStreet.getText())) {
            etResidenceStreet.setError("Vui lòng nhập đường/phố nơi thường trú");
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
        if (spGender.getSelectedItemPosition() == -1) {
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
        placeOfOrigin.setCommune(((WardDTO)spOriginWard.getSelectedItem()).getName());
        placeOfOrigin.setDistrict(((DistrictDTO)spOriginDistrict.getSelectedItem()).getName());
        placeOfOrigin.setProvince(((ProvinceDTO)spOriginProvince.getSelectedItem()).getName());

        Address placeOfResidence = new Address();
        placeOfResidence.setVilage(etResidenceStreet.getText().toString());
        placeOfResidence.setCommune(((WardDTO)spResidenceWard.getSelectedItem()).getName());
        placeOfResidence.setDistrict(((DistrictDTO)spResidenceDistrict.getSelectedItem()).getName());
        placeOfResidence.setProvince(((ProvinceDTO)spResidenceProvince.getSelectedItem()).getName());

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
        registerRequest.setGender(spGender.getSelectedItem().toString().equals("Nam") ? "MALE" : "FEMALE");
        registerRequest.setEmail(etEmail.getText().toString());
        registerRequest.setPhone(etPhone.getText().toString());
        registerRequest.setPassword1(etPassword1.getText().toString());
        registerRequest.setPassword2(etPassword2.getText().toString());

        // Log request body
        android.util.Log.d("RegisterActivity", "Request body: " + new com.google.gson.Gson().toJson(registerRequest));

        // Call API using AccountService
        accountService.register(registerRequest).enqueue(new Callback<Void>() {
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

    @SuppressLint("ClickableViewAccessibility")
    private void setupAddressSpinners() {
        // Origin Province Spinner
        spOriginProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < provinces.size()) {
                    ProvinceDTO selectedProvince = provinces.get(position);
                    loadDistricts(selectedProvince.getCode()+"", spOriginDistrict);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Origin District Spinner
        spOriginDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < districts.size()) {
                    DistrictDTO selectedDistrict = districts.get(position);
                    loadWards(selectedDistrict.getCode()+"", spOriginWard);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Residence Province Spinner
        spResidenceProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < provinces.size()) {
                    ProvinceDTO selectedProvince = provinces.get(position);
                    loadDistricts(selectedProvince.getCode()+"", spResidenceDistrict);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Residence District Spinner
        spResidenceDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < districts.size()) {
                    DistrictDTO selectedDistrict = districts.get(position);
                    loadWards(selectedDistrict.getCode()+"", spResidenceWard);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Load provinces initially
        loadProvinces();
    }

    private void loadProvinces() {
        Log.d(TAG, "Bắt đầu load danh sách tỉnh");
        try {
            addressService.getProvinces().enqueue(new Callback<List<ProvinceDTO>>() {
                @Override
                public void onResponse(Call<List<ProvinceDTO>> call, Response<List<ProvinceDTO>> response) {
                    Log.d(TAG, "Response code: " + response.code());
                    if (response.isSuccessful() && response.body() != null) {
                        provinces = response.body();
                        Log.d(TAG, "Số lượng tỉnh nhận được: " + provinces.size());
                        
                        ArrayAdapter<ProvinceDTO> adapter = new ArrayAdapter<ProvinceDTO>(
                                RegisterActivity.this,
                                android.R.layout.simple_spinner_item,
                                provinces
                        ) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                TextView textView = (TextView) super.getView(position, convertView, parent);
                                textView.setText(provinces.get(position).getName());
                                return textView;
                            }

                            @Override
                            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                                TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                                textView.setText(provinces.get(position).getName());
                                return textView;
                            }
                        };
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        
                        Log.d(TAG, "Đang set adapter cho spinner tỉnh");
                        spOriginProvince.setAdapter(adapter);
                        spResidenceProvince.setAdapter(adapter);

                        // Set default selection
                        spOriginProvince.setSelection(0);
                        spResidenceProvince.setSelection(0);
                        Log.d(TAG, "Đã set adapter và selection xong");
                    } else {
                        String errorBody = "";
                        try {
                            if (response.errorBody() != null) {
                                errorBody = response.errorBody().string();
                            }
                        } catch (Exception e) {
                            errorBody = e.getMessage();
                        }
                        Log.e(TAG, "Lỗi response: " + response.code() + " - " + response.message() + "\nError body: " + errorBody);
                        Toast.makeText(RegisterActivity.this, "Lỗi khi tải danh sách tỉnh: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<ProvinceDTO>> call, Throwable t) {
                    Log.e(TAG, "Lỗi khi gọi API tỉnh: " + t.getMessage(), t);
                    Toast.makeText(RegisterActivity.this, "Lỗi khi tải danh sách tỉnh: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi khởi tạo request: " + e.getMessage(), e);
            Toast.makeText(RegisterActivity.this, "Lỗi khi khởi tạo request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadDistricts(String codeProvince, Spinner districtSpinner) {
        Log.d(TAG, "Bắt đầu load danh sách huyện cho tỉnh: " + codeProvince);
        addressService.getDistricts(codeProvince).enqueue(new Callback<List<DistrictDTO>>() {
            @Override
            public void onResponse(Call<List<DistrictDTO>> call, Response<List<DistrictDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    districts = response.body();
                    Log.d(TAG, "Số lượng huyện nhận được: " + districts.size());
                    
                    ArrayAdapter<DistrictDTO> adapter = new ArrayAdapter<DistrictDTO>(
                            RegisterActivity.this,
                            android.R.layout.simple_spinner_item,
                            districts
                    ) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            TextView textView = (TextView) super.getView(position, convertView, parent);
                            textView.setText(districts.get(position).getName());
                            return textView;
                        }

                        @Override
                        public View getDropDownView(int position, View convertView, ViewGroup parent) {
                            TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                            textView.setText(districts.get(position).getName());
                            return textView;
                        }
                    };
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    
                    Log.d(TAG, "Đang set adapter cho spinner huyện");
                    districtSpinner.setAdapter(adapter);
                    districtSpinner.setSelection(0);
                    Log.d(TAG, "Đã set adapter và selection xong");
                } else {
                    Log.e(TAG, "Lỗi response: " + response.code() + " - " + response.message());
                    Toast.makeText(RegisterActivity.this, "Lỗi khi tải danh sách huyện", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<DistrictDTO>> call, Throwable t) {
                Log.e(TAG, "Lỗi khi gọi API huyện: " + t.getMessage());
                Toast.makeText(RegisterActivity.this, "Lỗi khi tải danh sách huyện", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadWards(String districtCode, Spinner wardSpinner) {
        Log.d(TAG, "Bắt đầu load danh sách xã cho huyện: " + districtCode);
        addressService.getWards(districtCode).enqueue(new Callback<List<WardDTO>>() {
            @Override
            public void onResponse(Call<List<WardDTO>> call, Response<List<WardDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    wards = response.body();
                    Log.d(TAG, "Số lượng xã nhận được: " + wards.size());
                    
                    ArrayAdapter<WardDTO> adapter = new ArrayAdapter<WardDTO>(
                            RegisterActivity.this,
                            android.R.layout.simple_spinner_item,
                            wards
                    ) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            TextView textView = (TextView) super.getView(position, convertView, parent);
                            textView.setText(wards.get(position).getName());
                            return textView;
                        }

                        @Override
                        public View getDropDownView(int position, View convertView, ViewGroup parent) {
                            TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                            textView.setText(wards.get(position).getName());
                            return textView;
                        }
                    };
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    
                    Log.d(TAG, "Đang set adapter cho spinner xã");
                    wardSpinner.setAdapter(adapter);
                    wardSpinner.setSelection(0);
                    Log.d(TAG, "Đã set adapter và selection xong");
                } else {
                    Log.e(TAG, "Lỗi response: " + response.code() + " - " + response.message());
                    Toast.makeText(RegisterActivity.this, "Lỗi khi tải danh sách xã", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<WardDTO>> call, Throwable t) {
                Log.e(TAG, "Lỗi khi gọi API xã: " + t.getMessage());
                Toast.makeText(RegisterActivity.this, "Lỗi khi tải danh sách xã", Toast.LENGTH_SHORT).show();
            }
        });
    }
}