package com.mobile.fe_bankproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.mobile.fe_bankproject.dto.AccountResponse;

public class TopUpActivity extends AppCompatActivity {
    private AccountResponse accountResponse;
    private ImageButton backButton;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_top_up);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get accountResponse from intent
        accountResponse = (AccountResponse) getIntent().getSerializableExtra("account_response");

        initializeViews();
        setupListeners();

        // Load initial fragment
        if (savedInstanceState == null) {
            loadPhoneCardFragment();
        }
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        tabLayout = findViewById(R.id.tabLayout);
    }

    private void setupListeners() {
        // Back button click listener
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(TopUpActivity.this, MainActivity.class);
            intent.putExtra("account_response", accountResponse);
            startActivity(intent);
            finish();
        });

        // Tab layout listener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) { // DATA 4G tab
                    loadDataFragment();
                } else if (tab.getPosition() == 0) { // Thẻ điện thoại tab
                    loadPhoneCardFragment();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Do nothing
            }
        });
    }

    private void loadPhoneCardFragment() {
        Fragment fragment = PhoneCardFragment.newInstance(accountResponse);
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit();
    }

    private void loadDataFragment() {
        Fragment fragment = DataFragment.newInstance(accountResponse);
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit();
    }
} 