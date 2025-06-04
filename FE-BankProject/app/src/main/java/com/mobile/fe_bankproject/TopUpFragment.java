package com.mobile.fe_bankproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;

public class TopUpFragment extends Fragment {
    private TextView totalAmountText;
    private MaterialButton amount10k, amount20k, amount30k;
    private MaterialButton amount50k, amount100k, amount200k;
    private MaterialButton amount300k, amount500k;
    private View contactsButton;
    private View autoTopupLayout;
    private MaterialButton topUpButton;
    private long selectedAmount = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_up, container, false);
        initViews(view);
        setClickListeners();
        return view;
    }

    private void initViews(View view) {
        totalAmountText = view.findViewById(R.id.totalAmountText);
        contactsButton = view.findViewById(R.id.contactsButton);
        autoTopupLayout = view.findViewById(R.id.autoTopupLayout);
        topUpButton = view.findViewById(R.id.topUpButton);

        // Initialize amount buttons
        amount10k = view.findViewById(R.id.amount10k);
        amount20k = view.findViewById(R.id.amount20k);
        amount30k = view.findViewById(R.id.amount30k);
        amount50k = view.findViewById(R.id.amount50k);
        amount100k = view.findViewById(R.id.amount100k);
        amount200k = view.findViewById(R.id.amount200k);
        amount300k = view.findViewById(R.id.amount300k);
        amount500k = view.findViewById(R.id.amount500k);
    }

    private void setClickListeners() {
        View.OnClickListener amountClickListener = v -> {
            MaterialButton button = (MaterialButton) v;
            String amountText = button.getText().toString().replace(".000đ", "");
            selectedAmount = Long.parseLong(amountText) * 1000;
            updateTotalAmount();
            updateButtonStates(button);
        };

        amount10k.setOnClickListener(amountClickListener);
        amount20k.setOnClickListener(amountClickListener);
        amount30k.setOnClickListener(amountClickListener);
        amount50k.setOnClickListener(amountClickListener);
        amount100k.setOnClickListener(amountClickListener);
        amount200k.setOnClickListener(amountClickListener);
        amount300k.setOnClickListener(amountClickListener);
        amount500k.setOnClickListener(amountClickListener);

        contactsButton.setOnClickListener(v -> openContacts());
        autoTopupLayout.setOnClickListener(v -> openAutoTopup());
        topUpButton.setOnClickListener(v -> handleTopUp());
    }

    private void updateButtonStates(MaterialButton selectedButton) {
        MaterialButton[] buttons = {amount10k, amount20k, amount30k, amount50k, 
                                  amount100k, amount200k, amount300k, amount500k};
        
        for (MaterialButton button : buttons) {
            button.setBackgroundTintList(requireContext().getColorStateList(
                button == selectedButton ? R.color.selected_amount_button : R.color.unselected_amount_button
            ));
        }
    }

    private void updateTotalAmount() {
        totalAmountText.setText(String.format("%,d đ", selectedAmount));
    }

    private void handleTopUp() {
        if (selectedAmount == 0) {
            Toast.makeText(requireContext(), "Vui lòng chọn số tiền", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(requireContext(), "Đang xử lý nạp tiền...", Toast.LENGTH_SHORT).show();
    }

    private void openContacts() {
        Toast.makeText(requireContext(), "Mở danh bạ", Toast.LENGTH_SHORT).show();
    }

    private void openAutoTopup() {
        Toast.makeText(requireContext(), "Mở cài đặt nạp tiền tự động", Toast.LENGTH_SHORT).show();
    }
} 