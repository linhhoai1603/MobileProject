package com.mobile.fe_bankproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.FrameLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.provider.MediaStore;
import android.app.Activity;
import android.net.Uri;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.File;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;

import com.mobile.fe_bankproject.dto.AccountResponse;

public class MainActivity extends AppCompatActivity implements MenuFragment.MenuListener {

    private static final String TAG = "MainActivity";
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_BACKGROUND_REQUEST = 2;
    private static final String AVATAR_FILE_NAME = "avatar.png";
    private static final String BACKGROUND_FILE_NAME = "background.png";

    private TextView tvUserName;
    private Button btnViewAccount;
    private AccountResponse accountResponse;
    private TextView ivMenuIcon;
    private FrameLayout menuFragmentContainer;
    private View overlay;
    private ConstraintLayout mainLayout;
    private ImageView ivAvatar;
    private ImageView ivBackground;
    private ConstraintLayout headerSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLayout = findViewById(R.id.mainLayout);
        overlay = findViewById(R.id.overlay);
        menuFragmentContainer = findViewById(R.id.menu_fragment_container);
        ivAvatar = findViewById(R.id.ivAvatar);
        ivBackground = findViewById(R.id.ivBackground);
        headerSection = findViewById(R.id.headerSection);

        // Initially hide the menu container and overlay
        menuFragmentContainer.setVisibility(View.GONE);
        overlay.setVisibility(View.GONE);

        tvUserName = findViewById(R.id.tvUserName);
        btnViewAccount = findViewById(R.id.btnViewAccount);
        ivMenuIcon = findViewById(R.id.ivMenuIcon);

        // Log the intent extras for debugging
        if (getIntent().getExtras() != null) {
            Log.d(TAG, "Intent extras: " + getIntent().getExtras().toString());
            accountResponse = (AccountResponse) getIntent().getExtras().getSerializable("account_response");
            Log.d(TAG, "AccountResponse received: " + (accountResponse != null ? "not null" : "null"));

            if (accountResponse != null) {
                Log.d(TAG, "User full name: " + accountResponse.getUser().getFullName());
                tvUserName.setText(accountResponse.getUser().getFullName());
            } else {
                Log.e(TAG, "AccountResponse is null!");
                Toast.makeText(this, "Lỗi: Không nhận được thông tin người dùng", Toast.LENGTH_LONG).show();
                // Navigate back to login if no user data
                finish();
                return;
            }
        } else {
            Log.e(TAG, "No extras in intent!");
            Toast.makeText(this, "Lỗi: Không nhận được thông tin người dùng", Toast.LENGTH_LONG).show();
            // Navigate back to login if no extras
            finish();
            return;
        }

        // Add click listener for "Xem tài khoản" button
        btnViewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accountResponse != null) {
                    AccountDetailsFragment bottomSheetFragment = AccountDetailsFragment.newInstance(accountResponse);
                    bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
                } else {
                    Toast.makeText(MainActivity.this, "Không có thông tin tài khoản để hiển thị", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Optional: Add click listener for the user name TextView as well
        tvUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Xem thông tin tài khoản", Toast.LENGTH_SHORT).show();
            }
        });

        // Add click listener for the menu icon
        ivMenuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenuFragment();
            }
        });

        // Handle click on the overlay to close the menu
        overlay.setOnClickListener(v -> hideMenu());

        // Make the main layout clickable to dismiss menu
        mainLayout.setOnClickListener(v -> {
            if (menuFragmentContainer.getVisibility() == View.VISIBLE) {
                hideMenu();
            }
        });

        // Load saved images on startup
        loadImageFromInternalStorage();
        loadBackgroundFromInternalStorage();
    }

    private void showMenuFragment() {
        // Add the fragment to the container
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Use custom animation for sliding from the left
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_left);

        MenuFragment menuFragment = new MenuFragment();
        fragmentTransaction.replace(R.id.menu_fragment_container, menuFragment);
        fragmentTransaction.addToBackStack(null); // Allow back button to close fragment
        fragmentTransaction.commit();

        // Show the menu container and overlay
        menuFragmentContainer.setVisibility(View.VISIBLE);
        overlay.setVisibility(View.VISIBLE);
    }

    private void hideMenu() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment menuFragment = fragmentManager.findFragmentById(R.id.menu_fragment_container);
        if (menuFragment != null) {
            fragmentManager.beginTransaction().remove(menuFragment).commit();
        }

        // Hide the menu container and overlay
        menuFragmentContainer.setVisibility(View.GONE);
        overlay.setVisibility(View.GONE);
    }

    @Override
    public void onMenuCloseRequested() {
        hideMenu();
    }

    @Override
    public void onSelectAvatarRequested() {
        // Open gallery to select image
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
        hideMenu(); // Close menu after selecting option
    }

    @Override
    public void onSelectBackgroundRequested() {
        // Open gallery to select background image
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_BACKGROUND_REQUEST);
        hideMenu(); // Close menu after selecting option
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            try {
                if (requestCode == PICK_IMAGE_REQUEST) {
                    saveImageToInternalStorage(selectedImageUri, AVATAR_FILE_NAME);
                    loadImageFromInternalStorage();
                } else if (requestCode == PICK_BACKGROUND_REQUEST) {
                    saveImageToInternalStorage(selectedImageUri, BACKGROUND_FILE_NAME);
                    loadBackgroundFromInternalStorage();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error saving or loading image", e);
                Toast.makeText(this, "Lỗi khi lưu hoặc tải ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveImageToInternalStorage(Uri uri, String fileName) throws Exception {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        FileOutputStream fileOutputStream = openFileOutput(fileName, MODE_PRIVATE);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, bytesRead);
        }
        fileOutputStream.close();
        inputStream.close();
        Log.d(TAG, "Image saved to internal storage: " + fileName);
    }

    private void loadImageFromInternalStorage() {
        try {
            File file = new File(getFilesDir(), AVATAR_FILE_NAME);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeStream(openFileInput(AVATAR_FILE_NAME));
                ivAvatar.setImageBitmap(bitmap);
                Log.d(TAG, "Image loaded from internal storage: " + AVATAR_FILE_NAME);
            } else {
                Log.d(TAG, "Avatar file not found in internal storage.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading image from internal storage", e);
        }
    }

    private void loadBackgroundFromInternalStorage() {
        try {
            File file = new File(getFilesDir(), BACKGROUND_FILE_NAME);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeStream(openFileInput(BACKGROUND_FILE_NAME));
                ivBackground.setImageBitmap(bitmap);
                headerSection.setBackground(new android.graphics.drawable.BitmapDrawable(getResources(), bitmap));
                Log.d(TAG, "Background loaded from internal storage: " + BACKGROUND_FILE_NAME);
            } else {
                Log.d(TAG, "Background file not found in internal storage.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading background from internal storage", e);
        }
    }

    // Handle back button press
    @Override
    public void onBackPressed() {
        if (menuFragmentContainer.getVisibility() == View.VISIBLE) {
            hideMenu();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onLogoutRequested() {
        // Clear any saved data
        accountResponse = null;
        
        // Navigate back to login screen
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}