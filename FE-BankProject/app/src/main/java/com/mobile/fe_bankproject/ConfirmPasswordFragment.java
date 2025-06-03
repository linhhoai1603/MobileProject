package com.mobile.fe_bankproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mobile.fe_bankproject.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.HashMap;
import java.util.Map;

public class ConfirmPasswordFragment extends DialogFragment {
    private static final String TAG = "ConfirmPasswordFragment";

    private String accountNumber;
    private TextInputEditText etPassword;
    private TextInputLayout tilPassword;
    private Button btnConfirm;
    private Button btnCancel;

    public static ConfirmPasswordFragment newInstance(String accountNumber) {
        ConfirmPasswordFragment fragment = new ConfirmPasswordFragment();
        Bundle args = new Bundle();
        args.putString("account_number", accountNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Base_Theme_FEBankProject);
        if (getArguments() != null) {
            accountNumber = getArguments().getString("account_number");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confirm_password, container, false);

        etPassword = view.findViewById(R.id.etPassword);
        tilPassword = view.findViewById(R.id.tilPassword);
        btnConfirm = view.findViewById(R.id.btnConfirm);
        btnCancel = view.findViewById(R.id.btnCancel);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString().trim();
                if (validateInput(password)) {
                    closeAccount(password);
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    private boolean validateInput(String password) {
        if (password.isEmpty()) {
            tilPassword.setError("Vui lòng nhập mật khẩu");
            return false;
        }
        tilPassword.setError(null);
        return true;
    }

    private void closeAccount(String password) {
        // Show loading state
        btnConfirm.setEnabled(false);
        btnConfirm.setText("Đang xử lý...");

        // Create request body
        Map<String, String> request = new HashMap<>();
        request.put("accountNumber", accountNumber);
        request.put("password", password);

        // Log request details
        Log.d(TAG, "Account number: " + accountNumber);
        Log.d(TAG, "Request body: " + request.toString());

        // Call API to close account
        RetrofitClient.getInstance().getAccountService().closeAccount(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // Reset button state
                btnConfirm.setEnabled(true);
                btnConfirm.setText("Xác nhận");

                // Log response
                Log.d(TAG, "Response code: " + response.code());
                if (!response.isSuccessful() && response.errorBody() != null) {
                    try {
                        Log.e(TAG, "Error response: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                }

                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), 
                        "Đóng tài khoản thành công", 
                        Toast.LENGTH_SHORT).show();
                    
                    // Navigate to login screen
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    Toast.makeText(getContext(), 
                        "Mật khẩu không đúng", 
                        Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Reset button state
                btnConfirm.setEnabled(true);
                btnConfirm.setText("Xác nhận");

                // Log error
                Log.e(TAG, "API call failed", t);

                Toast.makeText(getContext(), 
                    "Lỗi kết nối, vui lòng thử lại", 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }
} 