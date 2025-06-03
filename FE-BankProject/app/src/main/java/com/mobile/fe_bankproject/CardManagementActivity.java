package com.mobile.fe_bankproject;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.mobile.fe_bankproject.network.RetrofitClient;
import com.mobile.fe_bankproject.network.ApiService;
import com.mobile.fe_bankproject.dto.CardResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CardManagementActivity extends AppCompatActivity {
    private TextView cardNumber;
    private TextView cardHolder;
    private Button btnLock;
    private boolean isCardLocked = false;
    private ApiService apiService;
    private String originalCardNumber;
    private String originalCardHolder;
    private static final String PREF_NAME = "CardPrefs";
    private static final String KEY_CARD_NUMBER = "card_number";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_management);

        // Khởi tạo views
        cardNumber = findViewById(R.id.cardNumber);
        cardHolder = findViewById(R.id.cardHolder);
        btnLock = findViewById(R.id.btnLock);
        apiService = RetrofitClient.getInstance().getApiService();

        // Lấy số thẻ từ Intent hoặc SharedPreferences
        String cardNum = getIntent().getStringExtra("card_number");
        if (cardNum == null) {
            // Nếu không có trong Intent, thử lấy từ SharedPreferences
            SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            cardNum = prefs.getString(KEY_CARD_NUMBER, null);
        }

        if (cardNum != null) {
            // Lưu số thẻ vào SharedPreferences để sử dụng sau này
            SharedPreferences.Editor editor = getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit();
            editor.putString(KEY_CARD_NUMBER, cardNum);
            editor.apply();

            // Lấy thông tin thẻ từ database
            loadCardInfo(cardNum);
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin thẻ", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnLock.setOnClickListener(v -> showPinDialog());
    }

    private void loadCardInfo(String cardNum) {
        apiService.getCardInfo(cardNum).enqueue(new Callback<CardResponse>() {
            @Override
            public void onResponse(Call<CardResponse> call, Response<CardResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CardResponse card = response.body();
                    originalCardNumber = card.getCardNumber();
                    originalCardHolder = card.getCardHolder();
                    isCardLocked = "LOCKED".equals(card.getStatus());
                    
                    // Format số thẻ thành nhóm 4 số
                    String formattedCardNumber = formatCardNumber(originalCardNumber);
                    cardNumber.setText(formattedCardNumber);
                    cardHolder.setText(originalCardHolder);
                    
                    updateCardDisplay();
                } else {
                    Toast.makeText(CardManagementActivity.this, 
                        "Không thể tải thông tin thẻ", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<CardResponse> call, Throwable t) {
                Toast.makeText(CardManagementActivity.this, 
                    "Không thể tải thông tin thẻ: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private String formatCardNumber(String number) {
        if (number == null) return "";
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < number.length(); i += 4) {
            if (i > 0) formatted.append(" ");
            formatted.append(number.substring(i, Math.min(i + 4, number.length())));
        }
        return formatted.toString();
    }

    private void checkCardStatus() {
        if (originalCardNumber == null) return;
        
        apiService.getCardStatus(originalCardNumber).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        isCardLocked = "LOCKED".equals(jsonObject.getString("status"));
                        updateCardDisplay();
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(CardManagementActivity.this, 
                    "Không thể kiểm tra trạng thái thẻ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPinDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_enter_pin);

        TextInputEditText etPin = dialog.findViewById(R.id.etPin);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnConfirm.setOnClickListener(v -> {
            String pin = etPin.getText().toString();
            if (pin.length() != 6) {
                etPin.setError("Mã PIN phải có 6 số");
                return;
            }
            dialog.dismiss();
            if (isCardLocked) {
                unlockCard(pin);
            } else {
                lockCard(pin);
            }
        });

        dialog.show();
    }

    private void lockCard(String pin) {
        if (originalCardNumber == null) return;
        
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cardNumber", originalCardNumber);
            jsonObject.put("pin", pin);

            RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json"), jsonObject.toString());

            apiService.lockCard(requestBody).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        isCardLocked = true;
                        updateCardDisplay();
                        Toast.makeText(CardManagementActivity.this, 
                            "Thẻ đã được khóa thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CardManagementActivity.this, 
                            "Mã PIN không đúng", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(CardManagementActivity.this, 
                        "Không thể khóa thẻ", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void unlockCard(String pin) {
        if (originalCardNumber == null) return;
        
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cardNumber", originalCardNumber);
            jsonObject.put("pin", pin);

            RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json"), jsonObject.toString());

            apiService.unlockCard(requestBody).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        isCardLocked = false;
                        updateCardDisplay();
                        Toast.makeText(CardManagementActivity.this, 
                            "Thẻ đã được mở khóa thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CardManagementActivity.this, 
                            "Mã PIN không đúng", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(CardManagementActivity.this, 
                        "Không thể mở khóa thẻ", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateCardDisplay() {
        if (isCardLocked) {
            cardNumber.setText("**** **** **** ****");
            cardHolder.setText("THẺ ĐÃ BỊ KHÓA");
            btnLock.setText("Mở khóa thẻ");
        } else {
            cardNumber.setText(formatCardNumber(originalCardNumber));
            cardHolder.setText(originalCardHolder);
            btnLock.setText("Khóa thẻ");
        }
    }
} 