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

public class MenuFragment extends Fragment {

    private MenuListener menuListener;

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

        // Add click listener for close button
        TextView btnCloseMenu = view.findViewById(R.id.btnCloseMenu);
        btnCloseMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menuListener != null) {
                    menuListener.onMenuCloseRequested();
                }
            }
        });

        // Add click listener for Change Avatar item
        LinearLayout llChangeAvatar = view.findViewById(R.id.tvChangeAvatar).getParent() instanceof LinearLayout ?
                                        (LinearLayout) view.findViewById(R.id.tvChangeAvatar).getParent() : null;

        if (llChangeAvatar != null) {
            llChangeAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (menuListener != null) {
                        menuListener.onSelectAvatarRequested();
                    }
                }
            });
        }

        // Add click listener for Change Background item
        LinearLayout llChangeBackground = view.findViewById(R.id.tvChangeBackground).getParent() instanceof LinearLayout ?
                                        (LinearLayout) view.findViewById(R.id.tvChangeBackground).getParent() : null;

        if (llChangeBackground != null) {
            llChangeBackground.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (menuListener != null) {
                        menuListener.onSelectBackgroundRequested();
                    }
                }
            });
        }

        // Add click listener for logout item
        TextView tvLogout = view.findViewById(R.id.tvLogout);
        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutConfirmationDialog();
            }
        });

        return view;
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