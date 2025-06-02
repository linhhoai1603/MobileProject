package com.mobile.fe_bankproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

public class Data4GFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_data_4g, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        com.google.android.material.tabs.TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        if (tabLayout != null && tabLayout.getTabCount() > 1) {
            tabLayout.getTabAt(1).select(); // Chọn tab DATA 4G mặc định
        }
    }
} 