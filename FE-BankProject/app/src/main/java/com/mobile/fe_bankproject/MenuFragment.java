package com.mobile.fe_bankproject;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import android.widget.LinearLayout;
import android.app.AlertDialog;
import android.content.Intent;
import android.widget.Toast;
import android.widget.Button;
import com.mobile.fe_bankproject.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.HashMap;
import java.util.Map;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MenuFragment extends Fragment {

    private MenuListener menuListener;
    private Button btnTransfer, btnDeposit, btnWithdraw, btnHistory;
    private Button btnEditProfile, btnCloseAccount, btnLogout;
    private TextView tvChangeAvatar, tvChangeBackground, tvChangeInfo;
    private TextView tvChangePassword, tvCloseAccount, tvLogout;
    private TextView btnCloseMenu;

    public interface MenuListener {
        void onMenuCloseRequested();
        void onSelectAvatarRequested();
        void onSelectBackgroundRequested();
        void onLogoutRequested();
    }

    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MenuListener) {
            menuListener = (MenuListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement MenuListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        // Initialize views
        btnCloseMenu = view.findViewById(R.id.btnCloseMenu);
        tvChangeAvatar = view.findViewById(R.id.tvChangeAvatar);
        tvChangeBackground = view.findViewById(R.id.tvChangeBackground);
        tvChangeInfo = view.findViewById(R.id.tvChangeInfo);
        tvChangePassword = view.findViewById(R.id.tvChangePassword);
        tvCloseAccount = view.findViewById(R.id.tvCloseAccount);
        tvLogout = view.findViewById(R.id.tvLogout);

        // Set up click listeners
        btnCloseMenu.setOnClickListener(v -> {
            if (menuListener != null) {
                menuListener.onMenuCloseRequested();
            }
        });

        // Set up click listeners for menu items
        ((View) tvChangeAvatar.getParent()).setOnClickListener(v -> {
            if (menuListener != null) {
                menuListener.onSelectAvatarRequested();
            }
        });

        ((View) tvChangeBackground.getParent()).setOnClickListener(v -> {
            if (menuListener != null) {
                menuListener.onSelectBackgroundRequested();
            }
        });

        ((View) tvChangeInfo.getParent()).setOnClickListener(v -> navigateToEditProfile());

        ((View) tvChangePassword.getParent()).setOnClickListener(v -> {
            // Start ChangePasswordActivity
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            // Pass account number to ChangePasswordActivity
            if (getActivity() instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) getActivity();
                intent.putExtra("account_number", mainActivity.getAccountNumber());
            }
            startActivity(intent);
            // Close menu after selecting option
            if (menuListener != null) {
                menuListener.onMenuCloseRequested();
            }
        });

        ((View) tvCloseAccount.getParent()).setOnClickListener(v -> showCloseAccountConfirmationDialog());

        ((View) tvLogout.getParent()).setOnClickListener(v -> showLogoutConfirmationDialog());

        return view;
    }

    private void navigateToTransfer() {
        // TODO: Implement transfer navigation
    }

    private void navigateToDeposit() {
        // TODO: Implement deposit navigation
    }

    private void navigateToWithdraw() {
        // TODO: Implement withdraw navigation
    }

    private void navigateToHistory() {
        // TODO: Implement history navigation
    }

    private void navigateToEditProfile() {
        Intent intent = new Intent(getActivity(), EditProfileActivity.class);
        // Pass account number to EditProfileActivity
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            intent.putExtra("account_number", mainActivity.getAccountNumber());
        }
        startActivity(intent);
        
        // Close menu after navigation
        if (menuListener != null) {
            menuListener.onMenuCloseRequested();
        }
    }

    private void showCloseAccountConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
            .setTitle("Đóng tài khoản")
            .setMessage("Bạn có chắc chắn muốn đóng tài khoản? Hành động này không thể hoàn tác.")
            .setPositiveButton("Có", (dialog, which) -> {
                if (getActivity() instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    String accountNumber = mainActivity.getAccountNumber();
                    if (accountNumber != null) {
                        ConfirmPasswordFragment fragment = ConfirmPasswordFragment.newInstance(accountNumber);
                        fragment.show(getParentFragmentManager(), "confirm_password");
                    } else {
                        Toast.makeText(getContext(), 
                            "Không tìm thấy thông tin tài khoản", 
                            Toast.LENGTH_SHORT).show();
                    }
                }
            })
            .setNegativeButton("Không", null)
            .show();
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
            .setTitle("Đăng xuất")
            .setMessage("Bạn có muốn đăng xuất không?")
            .setPositiveButton("Có", (dialog, which) -> {
                if (menuListener != null) {
                    menuListener.onLogoutRequested();
                }
            })
            .setNegativeButton("Không", null)
            .show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        menuListener = null;
    }
} 