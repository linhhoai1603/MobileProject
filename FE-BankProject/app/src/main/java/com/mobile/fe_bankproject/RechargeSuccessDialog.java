package com.mobile.fe_bankproject;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.mobile.fe_bankproject.dto.RechargeResponse;

public class RechargeSuccessDialog extends DialogFragment {
    private RechargeResponse rechargeResponse;
    private OnDismissListener onDismissListener;

    public interface OnDismissListener {
        void onDismiss();
    }

    public static RechargeSuccessDialog newInstance(RechargeResponse response, OnDismissListener listener) {
        RechargeSuccessDialog dialog = new RechargeSuccessDialog();
        dialog.rechargeResponse = response;
        dialog.onDismissListener = listener;
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_FEBankProject);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_recharge_success, container, false);

        TextView phoneNumberText = view.findViewById(R.id.phoneNumberText);
        TextView telcoText = view.findViewById(R.id.telcoText);
        TextView amountText = view.findViewById(R.id.amountText);
        Button closeButton = view.findViewById(R.id.closeButton);

        phoneNumberText.setText(rechargeResponse.getPhoneNumber());
        telcoText.setText(rechargeResponse.getTelcoProvider().toString());
        amountText.setText(String.format("%,dđ", rechargeResponse.getCardAmount()).replace(",", "."));

        closeButton.setOnClickListener(v -> {
            dismiss();
            if (onDismissListener != null) {
                onDismissListener.onDismiss();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
} 