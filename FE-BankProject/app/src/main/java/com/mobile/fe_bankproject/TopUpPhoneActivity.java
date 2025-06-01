package com.mobile.fe_bankproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.button.MaterialButton;

public class TopUpPhoneActivity extends AppCompatActivity {
    private EditText phoneNumberInput;
    private TextView totalAmountText;
    private Button topUpButton;
    private MaterialButton amount10k, amount20k, amount30k;
    private MaterialButton amount50k, amount100k, amount200k;
    private MaterialButton amount300k, amount500k;
    private View contactsButton;
    private View autoTopupLayout;
    private long selectedAmount = 0;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    private final String[] tabTitles = new String[]{"Thẻ điện thoại", "Data 4G"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up_phone);

        // Initialize views
        initViews();
        setupViewPager();
        setupTabLayout();

        // Set click listeners
        setClickListeners();
    }

    private void initViews() {
        phoneNumberInput = findViewById(R.id.phoneNumberInput);
        totalAmountText = findViewById(R.id.totalAmountText);
        topUpButton = findViewById(R.id.topUpButton);
        contactsButton = findViewById(R.id.contactsButton);
        autoTopupLayout = findViewById(R.id.autoTopupLayout);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        // Initialize amount buttons
        initializeAmountButtons();
    }

    private void setupViewPager() {
        TopUpPagerAdapter pagerAdapter = new TopUpPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
    }

    private void setupTabLayout() {
        new TabLayoutMediator(tabLayout, viewPager,
            (tab, position) -> tab.setText(tabTitles[position])
        ).attach();
    }

    private void initializeAmountButtons() {
        amount10k = findViewById(R.id.amount10k);
        amount20k = findViewById(R.id.amount20k);
        amount30k = findViewById(R.id.amount30k);
        amount50k = findViewById(R.id.amount50k);
        amount100k = findViewById(R.id.amount100k);
        amount200k = findViewById(R.id.amount200k);
        amount300k = findViewById(R.id.amount300k);
        amount500k = findViewById(R.id.amount500k);

        View.OnClickListener amountClickListener = v -> {
            MaterialButton button = (MaterialButton) v;
            String amountText = button.getText().toString().replace(".000đ", "");
            selectedAmount = Long.parseLong(amountText) * 1000;
            updateTotalAmount();
            updateButtonStates(button);
        };

        amount10k.setOnClickListener(amountClickListener);
        amount20k.setOnClickListener(amountClickListener);
        amount30k.setOnClickListener(amountClickListener);
        amount50k.setOnClickListener(amountClickListener);
        amount100k.setOnClickListener(amountClickListener);
        amount200k.setOnClickListener(amountClickListener);
        amount300k.setOnClickListener(amountClickListener);
        amount500k.setOnClickListener(amountClickListener);
    }

    private void setClickListeners() {
        topUpButton.setOnClickListener(v -> handleTopUp());
        contactsButton.setOnClickListener(v -> openContacts());
        autoTopupLayout.setOnClickListener(v -> openAutoTopup());

        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.homeButton).setOnClickListener(v -> finish());
    }

    private void updateTotalAmount() {
        totalAmountText.setText(String.format("%,dđ", selectedAmount));
    }

    private void updateButtonStates(MaterialButton selectedButton) {
        MaterialButton[] buttons = {amount10k, amount20k, amount30k, amount50k, 
                                  amount100k, amount200k, amount300k, amount500k};
        
        for (MaterialButton button : buttons) {
            button.setBackgroundTintList(getColorStateList(
                button == selectedButton ? R.color.selected_amount_button : R.color.unselected_amount_button
            ));
        }
    }

    private void handleTopUp() {
        String phoneNumber = phoneNumberInput.getText().toString();
        if (phoneNumber.isEmpty() || selectedAmount == 0) {
            Toast.makeText(this, "Vui lòng nhập số điện thoại và chọn số tiền", Toast.LENGTH_SHORT).show();
            return;
        }
        // Process the top-up
        Toast.makeText(this, "Đang xử lý nạp tiền...", Toast.LENGTH_SHORT).show();
    }

    private void openContacts() {
        // Handle opening contacts
        Toast.makeText(this, "Mở danh bạ", Toast.LENGTH_SHORT).show();
    }

    private void openAutoTopup() {
        // Handle opening auto top-up settings
        Toast.makeText(this, "Mở cài đặt nạp tiền tự động", Toast.LENGTH_SHORT).show();
    }
} 