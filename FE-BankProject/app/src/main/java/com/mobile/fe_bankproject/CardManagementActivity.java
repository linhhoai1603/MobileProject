package com.mobile.fe_bankproject;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.mobile.fe_bankproject.network.RetrofitClient;
import com.mobile.fe_bankproject.network.CardService;
import com.mobile.fe_bankproject.dto.CardResponse;
import com.mobile.fe_bankproject.dto.AccountResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
    private LinearLayout btnNewCard;
    private LinearLayout btnCardInfo;
    private boolean isCardLocked = false;
    private CardService cardService;
    private String originalCardNumber;
    private String originalCardHolder;
    private static final String PREF_NAME = "CardPrefs";
    private static final String KEY_CARD_NUMBER = "card_number";
    private static final String KEY_ACCOUNT_NUMBER = "account_number";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_management);

        // Khởi tạo views
        cardNumber = findViewById(R.id.cardNumber);
        cardHolder = findViewById(R.id.cardHolder);
        btnLock = findViewById(R.id.btnLock);
        btnNewCard = findViewById(R.id.btnNewCard);
        btnCardInfo = findViewById(R.id.btnCardInfo);
        cardService = RetrofitClient.getInstance().getCardService();

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
        }

        btnLock.setOnClickListener(v -> showPinDialog());
        btnNewCard.setOnClickListener(v -> handleNewCardRequest());
        btnCardInfo.setOnClickListener(v -> showCardInfoDialog());
    }

    private void loadCardInfo(String cardNum) {
        cardService.getCardInfo(cardNum).enqueue(new Callback<CardResponse>() {
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
                }
            }

            @Override
            public void onFailure(Call<CardResponse> call, Throwable t) {
                Toast.makeText(CardManagementActivity.this, 
                    "Không thể tải thông tin thẻ: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void handleNewCardRequest() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String accountNumber = prefs.getString(KEY_ACCOUNT_NUMBER, null);

        if (accountNumber == null) {
            Toast.makeText(this, "Không tìm thấy thông tin tài khoản", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> request = new HashMap<>();
        request.put("accountNumber", accountNumber);

        cardService.createCard(request).enqueue(new Callback<CardResponse>() {
            @Override
            public void onResponse(Call<CardResponse> call, Response<CardResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CardResponse newCard = response.body();
                    showNewCardDialog(newCard);
                    
                    // Lưu số thẻ mới vào SharedPreferences
                    SharedPreferences.Editor editor = getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit();
                    editor.putString(KEY_CARD_NUMBER, newCard.getCardNumber());
                    editor.apply();
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Toast.makeText(CardManagementActivity.this, 
                            errorBody, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Toast.makeText(CardManagementActivity.this, 
                            "Không thể tạo thẻ mới", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<CardResponse> call, Throwable t) {
                Toast.makeText(CardManagementActivity.this, 
                    "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showNewCardDialog(CardResponse card) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_new_card_info);

        TextView tvCardNumber = dialog.findViewById(R.id.tvCardNumber);
        TextView tvCardHolder = dialog.findViewById(R.id.tvCardHolder);
        TextView tvPin = dialog.findViewById(R.id.tvPin);
        Button btnOk = dialog.findViewById(R.id.btnOk);

        tvCardNumber.setText(formatCardNumber(card.getCardNumber()));
        tvCardHolder.setText(card.getCardHolder());
        tvPin.setText(card.getDefaultPin());

        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            // Refresh card info với thẻ mới
            loadCardInfo(card.getCardNumber());
        });

        dialog.show();
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

            cardService.lockCard(requestBody).enqueue(new Callback<ResponseBody>() {
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

            cardService.unlockCard(requestBody).enqueue(new Callback<ResponseBody>() {
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

    private void showCardInfoDialog() {
        if (originalCardNumber == null) {
            Toast.makeText(this, "Không có thông tin thẻ", Toast.LENGTH_SHORT).show();
            return;
        }

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_card_info);

        TextView tvCardNumber = dialog.findViewById(R.id.tvCardNumber);
        TextView tvCardHolder = dialog.findViewById(R.id.tvCardHolder);
        TextView tvStatus = dialog.findViewById(R.id.tvStatus);
        TextView tvBalance = dialog.findViewById(R.id.tvBalance);
        Button btnClose = dialog.findViewById(R.id.btnClose);

        // Hiển thị thông tin thẻ
        tvCardNumber.setText(formatCardNumber(originalCardNumber));
        tvCardHolder.setText(originalCardHolder);
        tvStatus.setText(isCardLocked ? "THẺ ĐÃ BỊ KHÓA" : "ĐANG HOẠT ĐỘNG");
        tvStatus.setTextColor(isCardLocked ? 
            getResources().getColor(android.R.color.holo_red_dark) : 
            getResources().getColor(android.R.color.holo_green_dark));

        // Lấy số dư từ tài khoản
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String accountNumber = prefs.getString(KEY_ACCOUNT_NUMBER, null);
        if (accountNumber != null) {
            RetrofitClient.getInstance().getAccountService()
                .getAccountInfo(accountNumber).enqueue(new Callback<AccountResponse>() {
                    @Override
                    public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            AccountResponse account = response.body();
                            String balance = String.format("%,d VNĐ", (long)account.getBalance());
                            tvBalance.setText(balance);
                        }
                    }

                    @Override
                    public void onFailure(Call<AccountResponse> call, Throwable t) {
                        tvBalance.setText("Không thể lấy thông tin số dư");
                    }
                });
        }

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
} 