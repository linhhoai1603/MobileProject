package com.mobile.fe_bankproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.mobile.fe_bankproject.dto.AccountResponse;
import com.mobile.fe_bankproject.dto.PhoneCardDTO;
import com.mobile.fe_bankproject.network.PhoneCardService;
import com.mobile.fe_bankproject.network.RetrofitClient;
import com.mobile.fe_bankproject.dto.RechargeRequest;
import com.mobile.fe_bankproject.dto.RechargePreviewDTO;
import com.mobile.fe_bankproject.dto.TelcoProvider;
import com.mobile.fe_bankproject.dto.RechargeResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhoneCardFragment extends Fragment {
    private AccountResponse accountResponse;
    private EditText phoneNumberInput;
    private ImageButton contactsButton;
    private RadioButton card10k, card20k, card30k, card50k, card100k, card200k, card300k, card500k;
    private RadioGroup cardAmountGroup;
    private MaterialButton topUpButton;
    private Spinner providerSpinner;
    private String selectedProvider = "VIETTEL";
    private int selectedAmount = 0;
    private Map<Integer, Integer> cardQuantities = new HashMap<>();
    private List<RadioButton> cardButtons = new ArrayList<>();
    private List<Integer> cardAmounts = new ArrayList<>();

    public static PhoneCardFragment newInstance(AccountResponse accountResponse) {
        PhoneCardFragment fragment = new PhoneCardFragment();
        Bundle args = new Bundle();
        args.putSerializable("account_response", accountResponse);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            accountResponse = (AccountResponse) getArguments().getSerializable("account_response");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_phone_card, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        phoneNumberInput = view.findViewById(R.id.phoneNumberInput);
        contactsButton = view.findViewById(R.id.contactsButton);
        cardAmountGroup = view.findViewById(R.id.cardAmountGroup);
        card10k = view.findViewById(R.id.card10k);
        card20k = view.findViewById(R.id.card20k);
        card30k = view.findViewById(R.id.card30k);
        card50k = view.findViewById(R.id.card50k);
        card100k = view.findViewById(R.id.card100k);
        card200k = view.findViewById(R.id.card200k);
        card300k = view.findViewById(R.id.card300k);
        card500k = view.findViewById(R.id.card500k);
        topUpButton = view.findViewById(R.id.topUpButton);
        providerSpinner = view.findViewById(R.id.providerSpinner);

        // Initialize card buttons list
        cardButtons = Arrays.asList(card10k, card20k, card30k, card50k, card100k, card200k, card300k, card500k);
        cardAmounts = Arrays.asList(10000, 20000, 30000, 50000, 100000, 200000, 300000, 500000);

        // Set initial phone number if available
        if (accountResponse != null && accountResponse.getPhone() != null) {
            phoneNumberInput.setText(accountResponse.getPhone());
        }

        // Set up provider spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, 
            new String[]{"VIETTEL", "VINAPHONE", "MOBIFONE"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        providerSpinner.setAdapter(adapter);

        providerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedProvider = parent.getItemAtPosition(position).toString();
                loadPhoneCards(selectedProvider);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedProvider = "VIETTEL";
                loadPhoneCards(selectedProvider);
            }
        });

        // Set up contacts button
        contactsButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Chức năng đang phát triển", Toast.LENGTH_SHORT).show();
        });

        // Set up card amount selection
        cardAmountGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedButton = view.findViewById(checkedId);
            if (selectedButton != null) {
                String amountText = selectedButton.getText().toString().replace("đ", "").replace(".", "");
                selectedAmount = Integer.parseInt(amountText);
                Log.d("PhoneCardFragment", "Selected amount: " + selectedAmount);
            } else {
                selectedAmount = 0;
                Log.d("PhoneCardFragment", "No amount selected");
            }
        });

        // Set up top up button
        topUpButton.setOnClickListener(v -> {
            Log.d("PhoneCardFragment", "Current selected amount: " + selectedAmount);
            previewRecharge();
        });
    }

    private void loadPhoneCards(String provider) {
        RetrofitClient.getInstance().getPhoneCardService()
                .getPhoneCardByTelcoProvider(provider)
                .enqueue(new Callback<List<PhoneCardDTO>>() {
                    @Override
                    public void onResponse(Call<List<PhoneCardDTO>> call, Response<List<PhoneCardDTO>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<PhoneCardDTO> cards = response.body();
                            cardQuantities.clear();
                            
                            // Cập nhật số lượng thẻ và set tag cho các RadioButton
                            for (PhoneCardDTO card : cards) {
                                cardQuantities.put(card.getAmount(), card.getQuantity());
                                
                                // Tìm RadioButton tương ứng và set tag là idPhoneCard
                                for (int i = 0; i < cardAmounts.size(); i++) {
                                    if (cardAmounts.get(i) == card.getAmount()) {
                                        cardButtons.get(i).setTag(card.getId());
                                        break;
                                    }
                                }
                            }
                            
                            updateCardButtons();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<PhoneCardDTO>> call, Throwable t) {
                        Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateCardButtons() {
        for (int i = 0; i < cardButtons.size(); i++) {
            RadioButton button = cardButtons.get(i);
            int amount = cardAmounts.get(i);
            int quantity = cardQuantities.getOrDefault(amount, 0);
            
            // Format amount as "XX.XXXđ"
            String formattedAmount = String.format("%,dđ", amount).replace(",", ".");
            
            button.setText(formattedAmount);
            button.setEnabled(quantity > 0);
            button.setAlpha(quantity > 0 ? 1.0f : 0.5f);
        }
    }

    private void previewRecharge() {
        if (phoneNumberInput.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra xem có mệnh giá nào được chọn không
        int checkedId = cardAmountGroup.getCheckedRadioButtonId();
        if (checkedId == -1) {
            Toast.makeText(getContext(), "Vui lòng chọn mệnh giá thẻ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy idPhoneCard từ RadioButton đã chọn
        RadioButton selectedButton = getView().findViewById(checkedId);
        if (selectedButton == null) {
            Toast.makeText(getContext(), "Không tìm thấy thẻ phù hợp", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy idPhoneCard từ tag của RadioButton
        Integer idPhoneCard = (Integer) selectedButton.getTag();
        if (idPhoneCard == null) {
            Toast.makeText(getContext(), "Không tìm thấy thẻ phù hợp", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("PhoneCardFragment", "Selected idPhoneCard: " + idPhoneCard);

        RechargeRequest request = new RechargeRequest();
        request.setPhoneNumber(phoneNumberInput.getText().toString().trim());
        request.setIdPhoneCard(idPhoneCard);
        request.setAccountNumber(accountResponse.getAccountNumber());

        RetrofitClient.getInstance().getPhoneCardService()
                .previewPhoneCard(request)
                .enqueue(new Callback<RechargePreviewDTO>() {
                    @Override
                    public void onResponse(Call<RechargePreviewDTO> call, Response<RechargePreviewDTO> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            showPreviewDialog(response.body(), request);
                        } else {
                            Toast.makeText(getContext(), "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<RechargePreviewDTO> call, Throwable t) {
                        Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showPreviewDialog(RechargePreviewDTO preview, RechargeRequest request) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Xác nhận nạp thẻ");

        String message = String.format(
                "Số điện thoại: %s\n" +
                "Nhà mạng: %s\n" +
                "Mệnh giá: %,dđ\n" +
                "Số dư trước: %,dđ\n" +
                "Số dư sau: %,dđ",
                preview.getPhoneNumber(),
                preview.getTelcoProvider(),
                preview.getAmount(),
                (int)preview.getBalanceBefore(),
                (int)preview.getBalanceAfter()
        ).replace(",", ".");

        builder.setMessage(message);
        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            showPinDialog(request);
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void showPinDialog(RechargeRequest request) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Nhập mã PIN");

        final EditText pinInput = new EditText(getContext());
        pinInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        pinInput.setHint("Nhập mã PIN");
        builder.setView(pinInput);

        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            String pin = pinInput.getText().toString();
            if (pin.length() != 6) {
                Toast.makeText(getContext(), "Mã PIN phải có 6 số", Toast.LENGTH_SHORT).show();
                return;
            }
            request.setPin(pin);
            request.setAccountNumber(accountResponse.getAccountNumber());
            purchasePhoneCard(request);
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void purchasePhoneCard(RechargeRequest request) {
        RetrofitClient.getInstance().getPhoneCardService()
                .purchasePhoneCard(request)
                .enqueue(new Callback<RechargeResponse>() {
                    @Override
                    public void onResponse(Call<RechargeResponse> call, Response<RechargeResponse> response) {
                        Log.d("PhoneCardFragment", "onResponse called");
                        Log.d("PhoneCardFragment", "isSuccessful: " + response.isSuccessful());
                        
                        if (!isAdded()) {
                            Log.d("PhoneCardFragment", "Fragment not added");
                            return;
                        }
                        
                        if (response.isSuccessful() && response.body() != null) {
                            RechargeResponse rechargeResponse = response.body();
                            Log.d("PhoneCardFragment", "Response: " + rechargeResponse.getStatus());
                            
                            if ("SUCCESS".equals(rechargeResponse.getStatus())) {
                                // Reset form
                                phoneNumberInput.setText("");
                                cardAmountGroup.clearCheck();
                                selectedAmount = 0;
                                
                                // Hiển thị dialog thành công
                                RechargeSuccessDialog.newInstance(rechargeResponse, () -> {
                                    // Quay lại màn hình chính khi dialog đóng
                                    if (isAdded()) {
                                        requireActivity().onBackPressed();
                                    }
                                }).show(getChildFragmentManager(), "RechargeSuccessDialog");
                            } else {
                                Log.d("PhoneCardFragment", "Response status not success: " + rechargeResponse.getMessage());
                                Toast.makeText(requireContext(), rechargeResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d("PhoneCardFragment", "Response not successful");
                            Toast.makeText(requireContext(), "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<RechargeResponse> call, Throwable t) {
                        Log.d("PhoneCardFragment", "onFailure called: " + t.getMessage());
                        if (!isAdded()) return;
                        Toast.makeText(requireContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                });
    }
} 