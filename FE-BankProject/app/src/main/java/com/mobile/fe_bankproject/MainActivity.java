package com.mobile.fe_bankproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
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
import android.database.Cursor;
import android.content.SharedPreferences;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.MultipartBody;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Build;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

import com.mobile.fe_bankproject.dto.AccountResponse;
import com.mobile.fe_bankproject.network.RetrofitClient;
import com.mobile.fe_bankproject.dto.ImageUploadResponse;

public class MainActivity extends AppCompatActivity implements MenuFragment.MenuListener {

    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_BACKGROUND_REQUEST = 2;
    private static final String AVATAR_FILE_NAME = "avatar.png";
    private static final String BACKGROUND_FILE_NAME = "background.png";
    private static final int MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB in bytes
    private static final int COMPRESSION_QUALITY = 80; // Compression quality (0-100)

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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupBottomNavigationView();
        setupMenu();
        loadUserData();
        loadImageFromInternalStorage(); // Load avatar on app start
        loadBackgroundFromInternalStorage(); // Load background on app start
    }

    private void initializeViews() {
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

        // Find the LinearLayout for "Chuyển tiền" and set click listener
        LinearLayout layoutTransferMoney = findViewById(R.id.layout_transfer_money);
        if (layoutTransferMoney != null) {
            layoutTransferMoney.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create an Intent to start TransferMoneyActivity
                    Intent intent = new Intent(MainActivity.this, TransferMoney.class);
                    // Pass the accountResponse object to the next activity

                    intent.putExtra("account_response", accountResponse);
                    startActivity(intent);
                }
            });
        }

        // Find the LinearLayout for "Nạp điện thoại" and set click listener
        LinearLayout layoutTopUp = findViewById(R.id.layout_top_up);
        if (layoutTopUp != null) {
            layoutTopUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create an Intent to start TopUpActivity
                    Intent intent = new Intent(MainActivity.this, TopUpActivity.class);
                    // Pass the accountResponse object to the next activity
                    intent.putExtra("account_response", accountResponse);
                    startActivity(intent);
                }
            });
        }

//        // Find the LinearLayout for "Vay tiền" and set click listener
        LinearLayout layoutLoan = findViewById(R.id.layout_loan);
        if (layoutLoan != null) {
           layoutLoan.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   // Create an Intent to start LoanActivity
                   Intent intent = new Intent(MainActivity.this, LoanActivity.class);
                   intent.putExtra("account_response", accountResponse);
                   startActivity(intent);
               }
           });
        }

//        // Find the LinearLayout for "Quét QR" and set click listener
//        LinearLayout layoutScanQR = findViewById(R.id.layout_scan_qr);
//        if (layoutScanQR != null) {
//            layoutScanQR.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // Create an Intent to start ScanQRActivity
//                    Intent intent = new Intent(MainActivity.this, ScanQRActivity.class);
//                    intent.putExtra("account_response", accountResponse);
//                    startActivity(intent);
//                }
//            });
//        }

        // Find the LinearLayout for "Thanh toán" and set click listener
        LinearLayout layoutPayment = findViewById(R.id.layout_payment);
        if (layoutPayment != null) {
            layoutPayment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get userId from SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
                    int userId = sharedPreferences.getInt("userId", -1);
                    Log.d(TAG, "Retrieved userId from SharedPreferences: " + userId);

                    if (userId == -1) {
                        Log.e(TAG, "Could not get userId from SharedPreferences");
                        Toast.makeText(MainActivity.this, "Lỗi: Không thể lấy thông tin người dùng", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Create an Intent to start PaymentActivity
                    Intent intent = new Intent(MainActivity.this, PaymentActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }
            });
        }

        // Find the LinearLayout for "Dịch vụ thẻ" and set click listener
        LinearLayout layoutCardService = findViewById(R.id.layout_card_service);
        if (layoutCardService != null) {
            layoutCardService.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create an Intent to start CardServiceActivity
                    Intent intent = new Intent(MainActivity.this, CardManagementActivity.class);
                    intent.putExtra("account_response", accountResponse);
                    startActivity(intent);
                }
            });
        }

        // Log the intent extras for debugging
        if (getIntent().getExtras() != null) {
            Log.d(TAG, "Intent extras: " + getIntent().getExtras().toString());
            accountResponse = (AccountResponse) getIntent().getExtras().getSerializable("account_response");
            Log.d(TAG, "AccountResponse received: " + (accountResponse != null ? "not null" : "null"));

            if (accountResponse != null) {
                Log.d(TAG, "AccountResponse details:");
                Log.d(TAG, "- AccountNumber: " + accountResponse.getAccountNumber());
                Log.d(TAG, "- AccountName: " + accountResponse.getAccountName());
                Log.d(TAG, "- Balance: " + accountResponse.getBalance());
                Log.d(TAG, "- UserResponse: " + (accountResponse.getUserResponse() != null ? "not null" : "null"));
                
                if (accountResponse.getUserResponse() != null) {
                    Log.d(TAG, "UserResponse details:");
                    Log.d(TAG, "- ID: " + accountResponse.getUserResponse().getId());
                    Log.d(TAG, "- FullName: " + accountResponse.getUserResponse().getFullName());
                    Log.d(TAG, "- Email: " + accountResponse.getUserResponse().getEmail());
                    tvUserName.setText(accountResponse.getUserResponse().getFullName());
                } else {
                    Log.e(TAG, "UserResponse is null in AccountResponse!");
                    Toast.makeText(this, "Lỗi: Không nhận được thông tin người dùng", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
            } else {
                Log.e(TAG, "AccountResponse is null!");
                Toast.makeText(this, "Lỗi: Không nhận được thông tin tài khoản", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        } else {
            Log.e(TAG, "No extras in intent!");
            Toast.makeText(this, "Lỗi: Không nhận được thông tin người dùng", Toast.LENGTH_LONG).show();
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
    }

    private void setupBottomNavigationView() {
        // Implementation of setupBottomNavigationView method
    }

    private void setupMenu() {
        // Implementation of setupMenu method
    }

    private void loadUserData() {
        // Implementation of loadUserData method
    }

    private void loadImageFromInternalStorage() {
        if (accountResponse != null && accountResponse.getUserResponse() != null && accountResponse.getUserResponse().getUrlAvatar() != null) {
            String avatarUrl = accountResponse.getUserResponse().getUrlAvatar();
            Glide.with(this)
                .load(avatarUrl)
                .circleCrop() // For avatar
                .placeholder(R.drawable.default_avatar) // Placeholder while loading
                .error(R.drawable.default_avatar) // Error image if load fails
                .into(ivAvatar);
        } else {
            // Set default avatar if no URL available
            ivAvatar.setImageResource(R.drawable.default_avatar);
        }
    }

    private void loadBackgroundFromInternalStorage() {
        if (accountResponse != null && accountResponse.getUserResponse() != null && accountResponse.getUserResponse().getUrlBackground() != null) {
            String backgroundUrl = accountResponse.getUserResponse().getUrlBackground();
            // Load background image
            Glide.with(this)
                .load(backgroundUrl)
                .placeholder(R.drawable.default_background)
                .error(R.drawable.default_background)
                .into(ivBackground);

            // Also set as background for header section
            Glide.with(this)
                .load(backgroundUrl)
                .placeholder(R.drawable.default_background)
                .error(R.drawable.default_background)
                .into(new com.bumptech.glide.request.target.ViewTarget<View, android.graphics.drawable.Drawable>(headerSection) {
                    @Override
                    public void onResourceReady(android.graphics.drawable.Drawable resource, com.bumptech.glide.request.transition.Transition<? super android.graphics.drawable.Drawable> transition) {
                        headerSection.setBackground(resource);
                    }
                });
        } else {
            // Set default background if no URL available
            ivBackground.setImageResource(R.drawable.default_background);
            headerSection.setBackgroundResource(R.drawable.default_background);
        }
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

    private void saveImageToInternalStorage(Uri uri, String fileName) {
        // This method is no longer needed as we're using server storage
    }

    private File compressImage(File imageFile) throws Exception {
        // Read the image file
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        if (bitmap == null) {
            throw new Exception("Không thể đọc file ảnh");
        }

        // Check if compression is needed
        if (imageFile.length() <= MAX_IMAGE_SIZE) {
            return imageFile;
        }

        // Create a temporary file for the compressed image
        File compressedFile = new File(getCacheDir(), "compressed_" + imageFile.getName());
        
        // Compress the image
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, outputStream);
        
        // Write the compressed image to the temporary file
        FileOutputStream fileOutputStream = new FileOutputStream(compressedFile);
        fileOutputStream.write(outputStream.toByteArray());
        fileOutputStream.close();
        
        // Check if the compressed file is still too large
        if (compressedFile.length() > MAX_IMAGE_SIZE) {
            throw new Exception("Kích thước ảnh quá lớn, vui lòng chọn ảnh khác");
        }

        return compressedFile;
    }

    private void uploadImage(Uri imageUri, boolean isAvatar) {
        try {
            // Get the file from URI
            File file = new File(getRealPathFromUri(imageUri));
            if (!file.exists()) {
                Toast.makeText(this, "Không tìm thấy file ảnh", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check and compress image if needed
            File imageToUpload = compressImage(file);

            // Create request body
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageToUpload);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", imageToUpload.getName(), requestFile);

            // Get account number from SharedPreferences
            SharedPreferences prefs = getSharedPreferences("UserInfo", MODE_PRIVATE);
            String accountNumber = prefs.getString("accountNumber", "");
            if (accountNumber.isEmpty()) {
                Toast.makeText(this, "Không tìm thấy thông tin tài khoản", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show loading
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Đang tải lên...");
            progressDialog.show();

            // Call appropriate API
            Call<ImageUploadResponse> call;
            if (isAvatar) {
                call = RetrofitClient.getInstance().getAccountService().uploadAvatar(accountNumber, body);
            } else {
                call = RetrofitClient.getInstance().getAccountService().uploadBackground(accountNumber, body);
            }

            call.enqueue(new Callback<ImageUploadResponse>() {
                @Override
                public void onResponse(Call<ImageUploadResponse> call, Response<ImageUploadResponse> response) {
                    progressDialog.dismiss();
                    if (response.isSuccessful() && response.body() != null) {
                        ImageUploadResponse uploadResponse = response.body();
                        if ("success".equals(uploadResponse.getStatus())) {
                            // Save to internal storage
                            try {
                                if (isAvatar) {
                                    saveImageToInternalStorage(imageUri, AVATAR_FILE_NAME);
                                } else {
                                    saveImageToInternalStorage(imageUri, BACKGROUND_FILE_NAME);
                                }
                                
                                // Load image from URL using Glide
                                if (isAvatar) {
                                    Glide.with(MainActivity.this)
                                        .load(uploadResponse.getUrl())
                                        .apply(new RequestOptions()
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                                            .skipMemoryCache(true)
                                            .circleCrop())
                                        .into(ivAvatar);
                                } else {
                                    Glide.with(MainActivity.this)
                                        .load(uploadResponse.getUrl())
                                        .apply(new RequestOptions()
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                                            .skipMemoryCache(true))
                                        .into(new CustomViewTarget<ConstraintLayout, Drawable>(headerSection) {
                                            @Override
                                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                                getView().setBackground(resource);
                                            }

                                            @Override
                                            protected void onResourceCleared(@Nullable Drawable placeholder) {
                                                getView().setBackground(placeholder);
                                            }

                                            @Override
                                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                                 // Handle failure (optional)
                                                 getView().setBackground(errorDrawable);
                                            }
                                        });
                                }
                                
                                Toast.makeText(MainActivity.this, uploadResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Log.e(TAG, "Error saving image to internal storage", e);
                                Toast.makeText(MainActivity.this, "Lỗi khi lưu ảnh", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, uploadResponse.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        String errorMessage = "Cập nhật ảnh thất bại";
                        try {
                            if (response.errorBody() != null) {
                                errorMessage += ": " + response.errorBody().string();
                            }
                        } catch (Exception e) {
                            errorMessage += ": " + e.getMessage();
                        }
                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    Log.e(TAG, "Network error", t);
                    Toast.makeText(MainActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error preparing image for upload", e);
            Toast.makeText(this, "Lỗi khi chuẩn bị ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            try {
                // Get the file from URI
                File imageFile = new File(getRealPathFromUri(selectedImageUri));
                
                if (requestCode == PICK_IMAGE_REQUEST) {
                    // Upload to server
                    MenuFragment menuFragment = (MenuFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.menu_fragment_container);
                    if (menuFragment != null) {
                        menuFragment.uploadAvatar(imageFile, new Callback<ImageUploadResponse>() {
                            @Override
                            public void onResponse(Call<ImageUploadResponse> call, Response<ImageUploadResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    String imageUrl = response.body().getUrl();
                                    // Load image using Glide
                                    Glide.with(MainActivity.this)
                                        .load(imageUrl)
                                        .circleCrop() // For avatar
                                        .into(ivAvatar);
                                }
                            }

                            @Override
                            public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Lỗi khi tải ảnh lên server", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else if (requestCode == PICK_BACKGROUND_REQUEST) {
                    // Upload to server
                    MenuFragment menuFragment = (MenuFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.menu_fragment_container);
                    if (menuFragment != null) {
                        menuFragment.uploadBackground(imageFile, new Callback<ImageUploadResponse>() {
                            @Override
                            public void onResponse(Call<ImageUploadResponse> call, Response<ImageUploadResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    String imageUrl = response.body().getUrl();
                                    // Load image using Glide
                                    Glide.with(MainActivity.this)
                                        .load(imageUrl)
                                        .into(ivBackground);
                                    // Also set as background for header section
                                    Glide.with(MainActivity.this)
                                        .load(imageUrl)
                                        .into(new com.bumptech.glide.request.target.ViewTarget<View, android.graphics.drawable.Drawable>(headerSection) {
                                            @Override
                                            public void onResourceReady(android.graphics.drawable.Drawable resource, com.bumptech.glide.request.transition.Transition<? super android.graphics.drawable.Drawable> transition) {
                                                headerSection.setBackground(resource);
                                            }
                                        });
                                }
                            }

                            @Override
                            public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Lỗi khi tải ảnh lên server", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error processing image", e);
                Toast.makeText(this, "Lỗi khi xử lý ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getRealPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return uri.getPath();
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

    public String getAccountNumber() {
        if (accountResponse != null && accountResponse != null) {
            return accountResponse.getAccountNumber();
        }
        return null;
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 and above
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) 
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        PERMISSION_REQUEST_CODE);
            }
        } else {
            // Below Android 13
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) 
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with image picking
                Toast.makeText(this, "Quyền truy cập đã được cấp", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Cần quyền truy cập để chọn ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBalance();
    }

    private void updateBalance() {
        RetrofitClient.getInstance().getAccountService()
                .updateBalance(accountResponse.getAccountNumber())
                .enqueue(new Callback<AccountResponse>() {
                    @Override
                    public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            accountResponse = response.body();
                            // Lưu số dư vào SharedPreferences
                            SharedPreferences sharedPreferences = getSharedPreferences("AccountInfo", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putFloat("balance", (float) accountResponse.getBalance());
                            editor.apply();
                        }
                    }

                    @Override
                    public void onFailure(Call<AccountResponse> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}