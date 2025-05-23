package com.mobile.fe_bankproject;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import com.mobile.fe_bankproject.dto.AccountResponse;

public class AccountDetailsFragment extends BottomSheetDialogFragment {

    private static final String ARG_ACCOUNT_RESPONSE = "account_response";

    private AccountResponse accountResponse;

    public AccountDetailsFragment() {
        // Required empty public constructor
    }

    public static AccountDetailsFragment newInstance(AccountResponse accountResponse) {
        AccountDetailsFragment fragment = new AccountDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ACCOUNT_RESPONSE, accountResponse);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            accountResponse = (AccountResponse) getArguments().getSerializable(ARG_ACCOUNT_RESPONSE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account_details, container, false);

        TextView tvAccountNumber = view.findViewById(R.id.tvAccountNumber);
        TextView tvBalance = view.findViewById(R.id.tvBalance);
        ImageView ivCopyAccountNumber = view.findViewById(R.id.ivCopyAccountNumber);

        if (accountResponse != null) {
            tvAccountNumber.setText(accountResponse.getAccountNumber());
            tvBalance.setText(String.valueOf(accountResponse.getBalance()));
        }

        ivCopyAccountNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyAccountNumber();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the parent view (BottomSheetDialog's FrameLayout)
        FrameLayout bottomSheet = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);

            // Get screen height
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenHeight = displayMetrics.heightPixels;

            // Calculate 1/3 screen height
            int desiredHeight = screenHeight / 3;

            // Set the height of the bottom sheet
            ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
            layoutParams.height = desiredHeight;
            bottomSheet.setLayoutParams(layoutParams);

            // Set the state to expanded to reach the desired height
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    private void copyAccountNumber() {
        if (accountResponse != null && accountResponse.getAccountNumber() != null) {
            ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Số tài khoản", accountResponse.getAccountNumber());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getActivity(), "Đã sao chép số tài khoản", Toast.LENGTH_SHORT).show();
        }
    }
} 