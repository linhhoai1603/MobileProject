package com.mobile.fe_bankproject;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.mobile.fe_bankproject.dto.AccountRegister;
import com.mobile.fe_bankproject.dto.Address;
import com.mobile.fe_bankproject.dto.Province;
import com.mobile.fe_bankproject.dto.District;
import com.mobile.fe_bankproject.dto.Ward;
import com.mobile.fe_bankproject.network.RetrofitClient;
import com.mobile.fe_bankproject.dto.ProvinceResponse;

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
    private RadioGroup rgGender;
    private Button btnRegister;
    private Calendar calendar;
    private SimpleDateFormat displayDateFormat;
    private SimpleDateFormat apiDateFormat;
    
    private List<Province> provinces;
    private Province selectedOriginProvince;
    private District selectedOriginDistrict;
    private Ward selectedOriginWard;
    private Province selectedResidenceProvince;
    private District selectedResidenceDistrict;
    private Ward selectedResidenceWard;

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
        loadProvinces();
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
        rgGender = findViewById(R.id.rgGender);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);

        // Account Information
        etPassword1 = findViewById(R.id.etPassword1);
        etPassword2 = findViewById(R.id.etPassword2);
        btnRegister = findViewById(R.id.btnRegister);

        // Setup spinners with empty adapters initially
        setupEmptySpinners();
        setupSpinnerListeners();
    }

    private void setupEmptySpinners() {
        ArrayAdapter<String> emptyAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new ArrayList<>());
        emptyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spOriginProvince.setAdapter(emptyAdapter);
        spOriginDistrict.setAdapter(emptyAdapter);
        spOriginWard.setAdapter(emptyAdapter);
        spResidenceProvince.setAdapter(emptyAdapter);
        spResidenceDistrict.setAdapter(emptyAdapter);
        spResidenceWard.setAdapter(emptyAdapter);

        spOriginProvince.setEnabled(true);
        spOriginDistrict.setEnabled(true);
        spOriginWard.setEnabled(true);
        spResidenceProvince.setEnabled(true);
        spResidenceDistrict.setEnabled(true);
        spResidenceWard.setEnabled(true);
    }

    private void setupSpinnerListeners() {
        spOriginProvince.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (provinces != null && position < provinces.size()) {
                    Province newProvince = provinces.get(position);
                    if (selectedOriginProvince == null || !selectedOriginProvince.getCode().equals(newProvince.getCode())) {
                        selectedOriginProvince = newProvince;
                        selectedOriginDistrict = null;
                        selectedOriginWard = null;
                        spOriginDistrict.setAdapter(null);
                        spOriginWard.setAdapter(null);
                        loadDistricts(selectedOriginProvince.getCode(), true);
                    }
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        spOriginDistrict.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (selectedOriginProvince != null) {
                    RetrofitClient.getInstance().getAddressService().getDistricts(selectedOriginProvince.getCode())
                        .enqueue(new Callback<List<District>>() {
                            @Override
                            public void onResponse(Call<List<District>> call, Response<List<District>> response) {
                                if (response.isSuccessful() && response.body() != null && position < response.body().size()) {
                                    District newDistrict = response.body().get(position);
                                    if (selectedOriginDistrict == null || !selectedOriginDistrict.getCode().equals(newDistrict.getCode())) {
                                        selectedOriginDistrict = newDistrict;
                                        selectedOriginWard = null;
                                        spOriginWard.setAdapter(null);
                                        loadWards(selectedOriginDistrict.getCode(), true);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<List<District>> call, Throwable t) {
                                Log.e(TAG, "Failed to load districts", t);
                            }
                        });
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        spOriginWard.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (selectedOriginDistrict != null) {
                    RetrofitClient.getInstance().getAddressService().getWards(selectedOriginDistrict.getCode())
                        .enqueue(new Callback<List<Ward>>() {
                            @Override
                            public void onResponse(Call<List<Ward>> call, Response<List<Ward>> response) {
                                if (response.isSuccessful() && response.body() != null && position < response.body().size()) {
                                    selectedOriginWard = response.body().get(position);
                                }
                            }

                            @Override
                            public void onFailure(Call<List<Ward>> call, Throwable t) {
                                Log.e(TAG, "Failed to load wards", t);
                            }
                        });
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        spResidenceProvince.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (provinces != null && position < provinces.size()) {
                    Province newProvince = provinces.get(position);
                    if (selectedResidenceProvince == null || !selectedResidenceProvince.getCode().equals(newProvince.getCode())) {
                        selectedResidenceProvince = newProvince;
                        selectedResidenceDistrict = null;
                        selectedResidenceWard = null;
                        spResidenceDistrict.setAdapter(null);
                        spResidenceWard.setAdapter(null);
                        loadDistricts(selectedResidenceProvince.getCode(), false);
                    }
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        spResidenceDistrict.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (selectedResidenceProvince != null) {
                    RetrofitClient.getInstance().getAddressService().getDistricts(selectedResidenceProvince.getCode())
                        .enqueue(new Callback<List<District>>() {
                            @Override
                            public void onResponse(Call<List<District>> call, Response<List<District>> response) {
                                if (response.isSuccessful() && response.body() != null && position < response.body().size()) {
                                    District newDistrict = response.body().get(position);
                                    if (selectedResidenceDistrict == null || !selectedResidenceDistrict.getCode().equals(newDistrict.getCode())) {
                                        selectedResidenceDistrict = newDistrict;
                                        selectedResidenceWard = null;
                                        spResidenceWard.setAdapter(null);
                                        loadWards(selectedResidenceDistrict.getCode(), false);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<List<District>> call, Throwable t) {
                                Log.e(TAG, "Failed to load districts", t);
                            }
                        });
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        spResidenceWard.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (selectedResidenceDistrict != null) {
                    RetrofitClient.getInstance().getAddressService().getWards(selectedResidenceDistrict.getCode())
                        .enqueue(new Callback<List<Ward>>() {
                            @Override
                            public void onResponse(Call<List<Ward>> call, Response<List<Ward>> response) {
                                if (response.isSuccessful() && response.body() != null && position < response.body().size()) {
                                    selectedResidenceWard = response.body().get(position);
                                }
                            }

                            @Override
                            public void onFailure(Call<List<Ward>> call, Throwable t) {
                                Log.e(TAG, "Failed to load wards", t);
                            }
                        });
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void loadProvinces() {
        Log.d(TAG, "Starting to load provinces...");
        RetrofitClient.getInstance().getAddressService().getProvinces().enqueue(new Callback<ProvinceResponse>() {
            @Override
            public void onResponse(Call<ProvinceResponse> call, Response<ProvinceResponse> response) {
                Log.d(TAG, "Response code: " + response.code());
                Log.d(TAG, "Response message: " + response.message());
                
                if (!response.isSuccessful() || response.body() == null || response.body().getResults() == null) {
                    String errorMessage = "Error: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error body: " + errorBody);
                            errorMessage += " - " + errorBody;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    return;
                }

                provinces = response.body().getResults();
                Log.d(TAG, "Received " + provinces.size() + " provinces");
                
                if (provinces.isEmpty()) {
                    Log.e(TAG, "Provinces list is empty");
                    Toast.makeText(RegisterActivity.this, "Không có dữ liệu địa chỉ", Toast.LENGTH_LONG).show();
                    return;
                }

                runOnUiThread(() -> {
                    List<String> provinceNames = new ArrayList<>();
                    for (Province province : provinces) {
                        provinceNames.add(province.getName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(RegisterActivity.this,
                        android.R.layout.simple_spinner_item,
                        provinceNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spOriginProvince.setAdapter(adapter);
                    spResidenceProvince.setAdapter(adapter);
                    
                    spOriginProvince.setEnabled(true);
                    spResidenceProvince.setEnabled(true);
                    
                    spOriginProvince.setSelection(0);
                    spResidenceProvince.setSelection(0);
                });
            }

            @Override
            public void onFailure(Call<ProvinceResponse> call, Throwable t) {
                Log.e(TAG, "Network error", t);
                String errorMessage = "Lỗi kết nối: " + t.getMessage();
                if (t instanceof java.net.UnknownHostException) {
                    errorMessage = "Không thể kết nối đến server. Vui lòng kiểm tra kết nối mạng.";
                } else if (t instanceof java.net.SocketTimeoutException) {
                    errorMessage = "Kết nối đến server bị timeout. Vui lòng thử lại.";
                }
                Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadDistricts(String provinceCode, boolean isOrigin) {
        Log.d(TAG, "Loading districts for province code: " + provinceCode);
        RetrofitClient.getInstance().getAddressService().getDistricts(provinceCode).enqueue(new Callback<List<District>>() {
            @Override
            public void onResponse(Call<List<District>> call, Response<List<District>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Log.e(TAG, "Failed to load districts");
                    return;
                }

                List<District> districts = response.body();
                Log.d(TAG, "Received " + districts.size() + " districts");

                runOnUiThread(() -> {
                    List<String> districtNames = new ArrayList<>();
                    for (District district : districts) {
                        districtNames.add(district.getName());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(RegisterActivity.this,
                        android.R.layout.simple_spinner_item,
                        districtNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    if (isOrigin) {
                        spOriginDistrict.setAdapter(adapter);
                        spOriginDistrict.setEnabled(true);
                        spOriginDistrict.setSelection(0);
                    } else {
                        spResidenceDistrict.setAdapter(adapter);
                        spResidenceDistrict.setEnabled(true);
                        spResidenceDistrict.setSelection(0);
                    }
                });
            }

            @Override
            public void onFailure(Call<List<District>> call, Throwable t) {
                Log.e(TAG, "Failed to load districts", t);
                Toast.makeText(RegisterActivity.this, "Không thể tải danh sách quận/huyện", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadWards(String districtCode, boolean isOrigin) {
        Log.d(TAG, "Loading wards for district code: " + districtCode);
        RetrofitClient.getInstance().getAddressService().getWards(districtCode).enqueue(new Callback<List<Ward>>() {
            @Override
            public void onResponse(Call<List<Ward>> call, Response<List<Ward>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Log.e(TAG, "Failed to load wards");
                    return;
                }

                List<Ward> wards = response.body();
                Log.d(TAG, "Received " + wards.size() + " wards");

                runOnUiThread(() -> {
                    List<String> wardNames = new ArrayList<>();
                    for (Ward ward : wards) {
                        wardNames.add(ward.getName());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(RegisterActivity.this,
                        android.R.layout.simple_spinner_item,
                        wardNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    if (isOrigin) {
                        spOriginWard.setAdapter(adapter);
                        spOriginWard.setEnabled(true);
                        spOriginWard.setSelection(0);
                    } else {
                        spResidenceWard.setAdapter(adapter);
                        spResidenceWard.setEnabled(true);
                        spResidenceWard.setSelection(0);
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Ward>> call, Throwable t) {
                Log.e(TAG, "Failed to load wards", t);
                Toast.makeText(RegisterActivity.this, "Không thể tải danh sách phường/xã", Toast.LENGTH_SHORT).show();
            }
        });
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
        if (selectedOriginProvince == null || selectedOriginDistrict == null || selectedOriginWard == null) {
            Toast.makeText(this, "Vui lòng chọn đầy đủ địa chỉ quê quán", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        if (TextUtils.isEmpty(etOriginStreet.getText())) {
            etOriginStreet.setError("Vui lòng nhập đường/phố");
            isValid = false;
        }

        // Validate Place of Residence
        if (selectedResidenceProvince == null || selectedResidenceDistrict == null || selectedResidenceWard == null) {
            Toast.makeText(this, "Vui lòng chọn đầy đủ địa chỉ thường trú", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        if (TextUtils.isEmpty(etResidenceStreet.getText())) {
            etResidenceStreet.setError("Vui lòng nhập đường/phố");
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
        placeOfOrigin.setCommune(selectedOriginWard.getName());
        placeOfOrigin.setDistrict(selectedOriginDistrict.getName());
        placeOfOrigin.setProvince(selectedOriginProvince.getName());

        Address placeOfResidence = new Address();
        placeOfResidence.setVilage(etResidenceStreet.getText().toString());
        placeOfResidence.setCommune(selectedResidenceWard.getName());
        placeOfResidence.setDistrict(selectedResidenceDistrict.getName());
        placeOfResidence.setProvince(selectedResidenceProvince.getName());

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

        // Call API using AccountService
        RetrofitClient.getInstance().getAccountService().register(registerRequest).enqueue(new Callback<Void>() {
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