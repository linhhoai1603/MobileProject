package com.mobile.fe_bankproject.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.mobile.fe_bankproject.R;

public class SelectPhoneFragment extends Fragment {
    public interface OnPhoneSelectedListener {
        void onPhoneSelected(String phoneNumber);
    }
    private OnPhoneSelectedListener listener;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_phone, container, false);
        EditText edtPhone = view.findViewById(R.id.edtPhone);
        Button btnSelect = view.findViewById(R.id.btnSelect);
        btnSelect.setOnClickListener(v -> {
            String phone = edtPhone.getText().toString().trim();
            if (TextUtils.isEmpty(phone)) {
                Toast.makeText(getContext(), "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
                return;
            }
            if (listener != null) listener.onPhoneSelected(phone);
        });
        return view;
    }
    public void setOnPhoneSelectedListener(OnPhoneSelectedListener listener) {
        this.listener = listener;
    }
} 