package com.mobile.fe_bankproject.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.mobile.fe_bankproject.R;

public class RechargeFragment extends Fragment {
    private String[] amounts = {"10.000đ", "20.000đ", "30.000đ", "50.000đ", "100.000đ", "200.000đ", "300.000đ", "500.000đ"};
    private int[] values = {10000, 20000, 30000, 50000, 100000, 200000, 300000, 500000};
    private int selectedIndex = 0;
    private TextView tvTotal;
    private GridLayout gridAmounts;
    private String phoneNumber = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recharge, container, false);
        tvTotal = view.findViewById(R.id.tvTotal);
        gridAmounts = view.findViewById(R.id.gridAmounts);
        Button btnRecharge = view.findViewById(R.id.btnRecharge);
        TextView tvPhone = view.findViewById(R.id.tvPhone);

        if (getArguments() != null) {
            phoneNumber = getArguments().getString("phone_number", "");
        }
        tvPhone.setText(phoneNumber);

        for (int i = 0; i < amounts.length; i++) {
            Button btn = new Button(getContext());
            btn.setText(amounts[i]);
            int index = i;
            btn.setOnClickListener(v -> {
                selectedIndex = index;
                updateTotal();
                highlightSelectedButton();
            });
            gridAmounts.addView(btn);
        }
        updateTotal();
        highlightSelectedButton();

        btnRecharge.setOnClickListener(v -> {
            if (phoneNumber.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng chọn số điện thoại", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(getContext(), "Nạp " + amounts[selectedIndex] + " cho " + phoneNumber, Toast.LENGTH_SHORT).show();
        });
        return view;
    }
    private void updateTotal() {
        tvTotal.setText("Tổng tiền: " + values[selectedIndex] + "đ");
    }
    private void highlightSelectedButton() {
        for (int i = 0; i < gridAmounts.getChildCount(); i++) {
            Button btn = (Button) gridAmounts.getChildAt(i);
            if (i == selectedIndex) {
                btn.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
            } else {
                btn.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            }
        }
    }
} 