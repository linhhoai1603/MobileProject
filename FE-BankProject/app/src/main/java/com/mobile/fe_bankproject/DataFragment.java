package com.mobile.fe_bankproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobile.fe_bankproject.adapter.DataPackageAdapter;
import com.mobile.fe_bankproject.dto.AccountResponse;
import com.mobile.fe_bankproject.dto.DataMobile;
import com.mobile.fe_bankproject.dto.DataPackageFilterRequest;
import com.mobile.fe_bankproject.dto.DataPackagePreview;
import com.mobile.fe_bankproject.dto.DataPackageRequest;
import com.mobile.fe_bankproject.dto.DataPackageResponse;
import com.mobile.fe_bankproject.dto.TelcoProvider;
import com.mobile.fe_bankproject.network.DataMobileService;
import com.mobile.fe_bankproject.network.RetrofitClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataFragment extends Fragment implements DataPackageAdapter.OnPackageClickListener {
    private static final String ARG_ACCOUNT_RESPONSE = "account_response";
    private AccountResponse accountResponse;
    private Spinner providerSpinner;
    private Spinner validDateSpinner;
    private Spinner quantitySpinner;
    private RecyclerView recyclerView;
    private DataPackageAdapter adapter;
    private TextView totalAmountText;
    private DataMobile selectedPackage = null;
    private TextView phoneNumberText;
    private View contactsButton;
    private View autoTopupLayout;
    private View topUpButton;
    private View rootView;
    private ProgressBar progressBar;
    private TextView tvNoData;

    public static DataFragment newInstance(AccountResponse accountResponse) {
        DataFragment fragment = new DataFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ACCOUNT_RESPONSE, accountResponse);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            accountResponse = (AccountResponse) getArguments().getSerializable(ARG_ACCOUNT_RESPONSE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_data, container, false);
        initViews(rootView);
        setupSpinners();
        setupRecyclerView();
        setupListeners();
        loadDataPackages();
        return rootView;
    }

    private void initViews(View view) {
        providerSpinner = view.findViewById(R.id.providerSpinner);
        validDateSpinner = view.findViewById(R.id.validDateSpinner);
        quantitySpinner = view.findViewById(R.id.quantitySpinner);
        recyclerView = view.findViewById(R.id.recyclerView);
        totalAmountText = view.findViewById(R.id.totalAmountText);
        phoneNumberText = view.findViewById(R.id.phoneNumberText);
        contactsButton = view.findViewById(R.id.contactsButton);
        autoTopupLayout = view.findViewById(R.id.autoTopupLayout);
        topUpButton = view.findViewById(R.id.topUpButton);
        progressBar = view.findViewById(R.id.progressBar);
        tvNoData = view.findViewById(R.id.tvNoData);

        // Set initial phone number if available
        if (accountResponse != null && accountResponse.getPhone() != null) {
            phoneNumberText.setText(accountResponse.getPhone());
        }
    }

    private void setupSpinners() {
        // Setup provider spinner
        List<String> providers = new ArrayList<>();
        providers.add("Tất cả"); // Add default "All" option
        providers.addAll(Arrays.asList("VIETTEL", "MOBIPHONE", "VINAPHONE"));
        ArrayAdapter<String> providerAdapter = new ArrayAdapter<>(requireContext(), 
            android.R.layout.simple_spinner_item, providers);
        providerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        providerSpinner.setAdapter(providerAdapter);

        // Setup valid date spinner
        List<String> validDates = new ArrayList<>();
        validDates.add("Tất cả"); // Add default "All" option
        validDates.addAll(Arrays.asList("1 ngày", "7 ngày", "30 ngày"));
        ArrayAdapter<String> validDateAdapter = new ArrayAdapter<>(requireContext(), 
            android.R.layout.simple_spinner_item, validDates);
        validDateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        validDateSpinner.setAdapter(validDateAdapter);

        // Setup quantity spinner
        List<String> quantities = new ArrayList<>();
        quantities.add("Tất cả"); // Add default "All" option
        quantities.addAll(Arrays.asList("100MB", "500MB", "700MB", "1000MB", "2000MB", "4000MB", "8000MB", "15000MB"));
        ArrayAdapter<String> quantityAdapter = new ArrayAdapter<>(requireContext(), 
            android.R.layout.simple_spinner_item, quantities);
        quantityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quantitySpinner.setAdapter(quantityAdapter);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new DataPackageAdapter(new ArrayList<>(), this::onPackageClick);
        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        // Setup apply filter button
        rootView.findViewById(R.id.applyFilterButton).setOnClickListener(v -> loadDataPackages());

        // Setup top up button
        topUpButton.setOnClickListener(v -> performTopUp());

        // Setup auto top-up layout click
        autoTopupLayout.setOnClickListener(v -> {
            // TODO: Implement auto top-up functionality
            Toast.makeText(getContext(), "Tính năng đang được phát triển", Toast.LENGTH_SHORT).show();
        });

        // Setup contacts button
        contactsButton.setOnClickListener(v -> {
            // TODO: Implement contacts functionality
            Toast.makeText(getContext(), "Mở danh bạ", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadDataPackages() {
        // Show loading state
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        tvNoData.setVisibility(View.GONE);

        DataPackageFilterRequest request = new DataPackageFilterRequest();
        
        // Get selected provider
        String selectedProvider = providerSpinner.getSelectedItem().toString();
        if (!selectedProvider.equals("Tất cả")) {
            request.setProvider(TelcoProvider.valueOf(selectedProvider.toUpperCase()));
        }

        // Get selected valid date
        String selectedDate = validDateSpinner.getSelectedItem().toString();
        if (!selectedDate.equals("Tất cả")) {
            request.setValidDate(Integer.parseInt(selectedDate.replace(" ngày", "")));
        }

        // Get selected quantity
        String selectedQuantity = quantitySpinner.getSelectedItem().toString();
        if (!selectedQuantity.equals("Tất cả")) {
            // Convert MB to bytes (1MB = 1024 * 1024 bytes)
            int quantityMB = Integer.parseInt(selectedQuantity.replace("MB", ""));
            request.setQuantity(quantityMB);
        }

        DataMobileService service = RetrofitClient.getInstance().getDataMobileService();
        service.getFilteredPackages(request).enqueue(new Callback<List<DataMobile>>() {
            @Override
            public void onResponse(Call<List<DataMobile>> call, Response<List<DataMobile>> response) {
                progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    List<DataMobile> packages = response.body();
                    if (packages.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        tvNoData.setVisibility(View.VISIBLE);
                        tvNoData.setText("Không tìm thấy gói data phù hợp");
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        tvNoData.setVisibility(View.GONE);
                        adapter.updateData(packages);
                    }
                } else {
                    recyclerView.setVisibility(View.GONE);
                    tvNoData.setVisibility(View.VISIBLE);
                    tvNoData.setText("Không thể tải danh sách gói data");
                }
            }

            @Override
            public void onFailure(Call<List<DataMobile>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                tvNoData.setVisibility(View.VISIBLE);
                tvNoData.setText("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    @Override
    public void onPackageClick(DataMobile dataPackage) {
        // Update selected package
        selectedPackage = dataPackage;
        
        // Update UI
        adapter.setSelectedPackage(dataPackage);
        updateTotalAmount(selectedPackage.getPrice());
        
        // Show preview
        previewDataPackage();
    }

    private void updateTotalAmount(double amount) {
        String formattedAmount = String.format("%,.0fđ", amount);
        totalAmountText.setText(formattedAmount);
    }

    private void performTopUp() {
        if (selectedPackage == null) {
            Toast.makeText(requireContext(), "Vui lòng chọn gói data", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show PIN dialog
        showPinDialog();
    }

    private void showPinDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Nhập mã PIN");

        // Set up the input
        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        input.setHint("Nhập mã PIN");
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            String pin = input.getText().toString();
            if (pin.length() != 6) {
                Toast.makeText(requireContext(), "Mã PIN phải có 6 chữ số", Toast.LENGTH_SHORT).show();
                return;
            }
            purchaseDataPackage(pin);
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void previewDataPackage() {
        if (selectedPackage == null) {
            Toast.makeText(requireContext(), "Vui lòng chọn gói data", Toast.LENGTH_SHORT).show();
            return;
        }

        DataPackageRequest request = new DataPackageRequest();
        request.setAccountNumber(accountResponse.getAccountNumber());
        request.setPackageId(selectedPackage.getId());
        request.setPhoneNumber(phoneNumberText.getText().toString());

        DataMobileService service = RetrofitClient.getInstance().getDataMobileService();
        service.preview(request).enqueue(new Callback<DataPackagePreview>() {
            @Override
            public void onResponse(Call<DataPackagePreview> call, Response<DataPackagePreview> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DataPackagePreview previewResponse = response.body();
                    showPreviewDialog(previewResponse);
                } else {
                    Toast.makeText(requireContext(), "Không thể xem trước gói data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataPackagePreview> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPreviewDialog(DataPackagePreview previewResponse) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Xem trước gói data");

        // Create the message
        String message = String.format(
            "Gói: %s\n" +
            "Nhà mạng: %s\n" +
            "Dung lượng: %dMB\n" +
            "Thời hạn: %d ngày\n" +
            "Số điện thoại: %s\n" +
            "Giá tiền: %,.0fđ",
            selectedPackage.getPackageName(),
            selectedPackage.getTelcoProvider(),
            selectedPackage.getQuantity(),
            selectedPackage.getValidDate(),
            phoneNumberText.getText().toString(),
            selectedPackage.getPrice()
        );

        builder.setMessage(message);
        builder.setPositiveButton("Mua ngay", (dialog, which) -> showPinDialog());
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void purchaseDataPackage(String pin) {
        DataPackageRequest request = new DataPackageRequest();
        request.setAccountNumber(accountResponse.getAccountNumber());
        request.setPackageId(selectedPackage.getId());
        request.setPhoneNumber(phoneNumberText.getText().toString());
        request.setPIN(pin);

        DataMobileService service = RetrofitClient.getInstance().getDataMobileService();
        service.purchase(request).enqueue(new Callback<DataPackageResponse>() {
            @Override
            public void onResponse(Call<DataPackageResponse> call, Response<DataPackageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DataPackageResponse purchaseResponse = response.body();
                    if (purchaseResponse.getPackageId() != -1) {
                        // Show success dialog with transaction details
                        showSuccessDialog(purchaseResponse);
                    } else {
                        Toast.makeText(requireContext(), purchaseResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Không thể mua gói data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataPackageResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSuccessDialog(DataPackageResponse response) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Mua gói data thành công");

        // Create the message with transaction details
        String message = String.format(
            "Mã gói cước: %s\n" +
            "Gói data: %s\n" +
            "Nhà mạng: %s\n" +
            "Dung lượng: %dMB\n" +
            "Thời hạn: %d ngày\n" +
            "Số điện thoại: %s\n" +
            "Số tiền: %,.0fđ\n" +
            "Thời gian: %s",
            response.getPackageId(),
            selectedPackage.getPackageName(),
            selectedPackage.getTelcoProvider(),
            selectedPackage.getQuantity(),
            selectedPackage.getValidDate(),
            phoneNumberText.getText().toString(),
            selectedPackage.getPrice(),
            response.getTime()
        );

        builder.setMessage(message);
        builder.setPositiveButton("Về trang chủ", (dialog, which) -> {
            // Save account info to SharedPreferences
            SharedPreferences prefs = requireContext().getSharedPreferences("AccountInfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("accountNumber", accountResponse.getAccountNumber());
            editor.putString("balance", String.valueOf(accountResponse.getBalance()));
            editor.putString("fullName", accountResponse.getUser().getFullName());
            editor.putString("phoneNumber", accountResponse.getPhone());
            editor.putString("email", accountResponse.getUser().getEmail());
            editor.putString("userId", String.valueOf(accountResponse.getUser().getId()));
            editor.apply();

            // Navigate back to home screen
            Intent intent = new Intent(requireContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
        builder.setCancelable(false);
        builder.show();
    }
}