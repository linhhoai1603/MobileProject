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

public class DataFragment extends Fragment {
    private String[] dataPackages = {"1GB - 10.000đ", "2GB - 18.000đ", "5GB - 40.000đ", "10GB - 70.000đ", "20GB - 120.000đ"};
    private int[] dataPrices = {10000, 18000, 40000, 70000, 120000};
    private int selectedIndex = 0;
    private TextView tvTotal;
    private GridLayout gridDataPackages;
    private String phoneNumber = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data, container, false);
        tvTotal = view.findViewById(R.id.tvTotal);
        gridDataPackages = view.findViewById(R.id.gridDataPackages);
        Button btnRechargeData = view.findViewById(R.id.btnRechargeData);
        TextView tvPhone = view.findViewById(R.id.tvPhone);

        if (getArguments() != null) {
            phoneNumber = getArguments().getString("phone_number", "");
        }
        tvPhone.setText(phoneNumber);

        for (int i = 0; i < dataPackages.length; i++) {
            Button btn = new Button(getContext());
            btn.setText(dataPackages[i]);
            int index = i;
            btn.setOnClickListener(v -> {
                selectedIndex = index;
                updateTotal();
                highlightSelectedButton();
            });
            gridDataPackages.addView(btn);
        }
        updateTotal();
        highlightSelectedButton();

        btnRechargeData.setOnClickListener(v -> {
            if (phoneNumber.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng chọn số điện thoại", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(getContext(), "Nạp " + dataPackages[selectedIndex] + " cho " + phoneNumber, Toast.LENGTH_SHORT).show();
        });
        return view;
    }
    private void updateTotal() {
        tvTotal.setText("Tổng tiền: " + dataPrices[selectedIndex] + "đ");
    }
    private void highlightSelectedButton() {
        for (int i = 0; i < gridDataPackages.getChildCount(); i++) {
            Button btn = (Button) gridDataPackages.getChildAt(i);
            if (i == selectedIndex) {
                btn.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
            } else {
                btn.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            }
        }
    }
} 