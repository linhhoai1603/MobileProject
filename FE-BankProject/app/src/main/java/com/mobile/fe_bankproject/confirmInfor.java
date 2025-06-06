package com.mobile.fe_bankproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DecimalFormat;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.widget.Toolbar;

// Import các lớp cần thiết cho Retrofit
import com.mobile.fe_bankproject.dto.FundTransferRequest;
import com.mobile.fe_bankproject.network.ApiClient; // Giả định bạn có lớp ApiClient để lấy Retrofit instance
import com.mobile.fe_bankproject.network.ApiService; // Import interface ApiService

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class confirmInfor extends AppCompatActivity {

    private TextView tvAmountNumber;
    private TextView tvSenderName;
    private TextView tvSenderAccountNumber;
    private TextView tvRecipientName;
    private TextView tvRecipientAccountNumber;
    private TextView tvAmountText;
    private TextView tvDescription;
    private TextView tvTransactionFee;
    private TextView tvTransferMethod;

    // Thêm instance của API Service
    private ApiService apiService;

    // Biến để lưu thông tin giao dịch để truyền sang màn hình OTP
    private String fromAccountNumber;
    private String toAccountNumber;
    private double amount;
    private String description;
    private String fromAccountName;
    private String toAccountName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirm_infor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        Toolbar toolbar = findViewById(R.id.myToolbar);
//        setSupportActionBar(toolbar);
//        TextView tvTitle = findViewById(R.id.tvTitle);
//        if (tvTitle != null) {
//            tvTitle.setText("Xác nhận thông tin");
//        }

        tvAmountNumber = findViewById(R.id.tvAmountNumber);
        tvSenderName = findViewById(R.id.tvSenderName);
        tvSenderAccountNumber = findViewById(R.id.tvSenderAccountNumber);
        tvRecipientName = findViewById(R.id.tvRecipientName);
        tvRecipientAccountNumber = findViewById(R.id.tvRecipientAccountNumber);
        tvAmountText = findViewById(R.id.tvAmountText);
        tvDescription = findViewById(R.id.tvDescription);
        tvTransactionFee = findViewById(R.id.tvTransactionFee);
        tvTransferMethod = findViewById(R.id.tvTransferMethod);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            fromAccountNumber = intent.getExtras().getString("fromAccountNumber");
            fromAccountName = intent.getExtras().getString("fromAccountName");
            toAccountNumber = intent.getExtras().getString("toAccountNumber");
            toAccountName = intent.getExtras().getString("toAccountName");
            amount = intent.getExtras().getDouble("amount", 0.0);
            description = intent.getExtras().getString("description");

            DecimalFormat formatter = new DecimalFormat("###,###,##0.##");
            tvAmountNumber.setText(formatter.format(amount) + " VND");

            if (tvSenderName != null && fromAccountName != null) {
                tvSenderName.setText(fromAccountName);
            }

            if (tvSenderAccountNumber != null && fromAccountNumber != null) {
                tvSenderAccountNumber.setText(fromAccountNumber);
            }

            if (tvRecipientName != null && toAccountName != null) {
                tvRecipientName.setText(toAccountName);
            }

            if (tvRecipientAccountNumber != null && toAccountNumber != null) {
                tvRecipientAccountNumber.setText(toAccountNumber);
            }

            if (tvAmountText != null) {
                String amountText = convertAmountToVietnameseText((long) amount);
                tvAmountText.setText(amountText);
            }

            if (tvDescription != null && description != null) {
                tvDescription.setText(description);
            }

        } else {
            Log.e("confirmInfor", "No extras in Intent for confirmInfor.");
            Toast.makeText(this, "Không nhận được dữ liệu xác nhận chuyển tiền.", Toast.LENGTH_LONG).show();
        }

        // Khởi tạo Retrofit Service
        // Bạn cần thay thế ApiClient.getApiClient() bằng cách bạn lấy instance Retrofit trong project của mình
        apiService = ApiClient.getClient().create(ApiService.class);

        Button btnConfirm = findViewById(R.id.btnContinue);
        if (btnConfirm != null) {
            btnConfirm.setOnClickListener(v -> {
                // Kiểm tra xem dữ liệu cần thiết có tồn tại không trước khi gọi API
                if (fromAccountNumber == null || toAccountNumber == null || description == null || fromAccountName == null || toAccountName == null) {
                     Toast.makeText(confirmInfor.this, "Thiếu thông tin giao dịch.", Toast.LENGTH_SHORT).show();
                     Log.e("confirmInfor", "Missing transaction data for API call.");
                     return; // Ngừng xử lý nếu thiếu dữ liệu
                }

                // Tạo request body
                FundTransferRequest request = new FundTransferRequest(
                        fromAccountNumber,
                        toAccountNumber,
                        amount,
                        description
                );

                // Gọi API gửi OTP
                Call<String> call = apiService.requestEmailTransfer(request);

                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String apiResponse = response.body();
                            // Kiểm tra nội dung response theo định dạng backend trả về
                            if ("OTP đã được gửi về email. Vui lòng xác nhận để hoàn tất chuyển khoản.".equals(apiResponse)) {
                                // API call thành công và nhận được phản hồi mong muốn
                                Toast.makeText(confirmInfor.this, "Yêu cầu gửi OTP thành công", Toast.LENGTH_SHORT).show();

                                // Chuyển sang màn hình xác nhận OTP (fill_OTP.java)
                                Intent otpIntent = new Intent(confirmInfor.this, fill_OTP.class);
                                // Truyền dữ liệu cần thiết sang màn hình OTP nếu có
                                // Truyền lại thông tin giao dịch để màn hình OTP có thể sử dụng
                                otpIntent.putExtra("fromAccountNumber", fromAccountNumber);
                                otpIntent.putExtra("fromAccountName", fromAccountName);
                                otpIntent.putExtra("toAccountNumber", toAccountNumber);
                                otpIntent.putExtra("toAccountName", toAccountName);
                                otpIntent.putExtra("amount", amount);
                                otpIntent.putExtra("description", description);
                                // Nếu cần truyền các thông tin khác cho màn hình OTP, thêm vào đây
                                startActivity(otpIntent);
                                finish(); // Đóng màn hình hiện tại nếu không muốn quay lại
                            } else {
                                // API call thành công nhưng response body không như mong đợi
                                // Có thể hiển thị thông báo lỗi từ backend nếu response body chứa thông tin lỗi
                                Toast.makeText(confirmInfor.this, "Phản hồi API không mong muốn: " + apiResponse, Toast.LENGTH_LONG).show();
                                Log.e("confirmInfor", "API Response: " + apiResponse);
                            }
                        } else {
                            // API call không thành công (status code lỗi)
                            String errorBody = null;
                            try {
                                errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                                // Cố gắng parse error body nếu backend trả về JSON lỗi
                                // Ví dụ: ErrorResponse error = new Gson().fromJson(errorBody, ErrorResponse.class);
                                // Toast.makeText(confirmInfor.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                errorBody = "Error reading error body";
                                Log.e("confirmInfor", "Error reading error body", e);
                            }
                            Toast.makeText(confirmInfor.this, "Gọi API thất bại: " + response.code() + (errorBody != null ? " - " + errorBody : ""), Toast.LENGTH_LONG).show();
                            Log.e("confirmInfor", "API Call failed: " + response.code() + (errorBody != null ? " - " + errorBody : ""));
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        // Lỗi trong quá trình gọi API (mất mạng, parsing error, v.v.)
                        Toast.makeText(confirmInfor.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("confirmInfor", "API Call failed", t);
                    }
                });
            });
        }
    }

    // --- Start: Hàm chuyển đổi số sang chữ tiếng Việt (Cơ bản) ---
    // Hàm này chỉ xử lý các số nguyên dương cơ bản.
    private String convertAmountToVietnameseText(long amount) {
        if (amount == 0) return "Không đồng";

        String[] units = {"", "nghìn", "triệu", "tỷ"};
        String[] ones = {"", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"};
        String[] tens = {"", "mười", "hai mươi", "ba mươi", "bốn mươi", "năm mươi", "sáu mươi", "bảy mươi", "tám mươi", "chín mươi"};
        String[] hundreds = {"", "một trăm", "hai trăm", "ba trăm", "bốn trăm", "năm trăm", "sáu trăm", "bảy trăm", "tám trăm", "chín trăm"};

        String result = "";
        int unitIndex = 0;

        // Xử lý số tiền nguyên (không có phần thập phân)
        long integerPart = amount;

        if (integerPart < 0) {
            result = "âm ";
            integerPart = -integerPart;
        }

        while (integerPart > 0) {
            long thousand = integerPart % 1000;
            integerPart /= 1000;

            String thousandText = "";
            long h = thousand / 100;
            long t = (thousand % 100) / 10;
            long o = thousand % 10;

            if (h > 0) {
                thousandText += hundreds[(int) h] + " ";
            }

            if (t > 1) {
                thousandText += tens[(int) t] + " ";
                if (o == 1) {
                    thousandText += "mốt";
                } else if (o == 5 && h == 0 && t == 0 && o == 5) { // Xử lý trường hợp "lăm" khi đứng một mình
                    thousandText += ones[(int)o];
                }
                else if (o == 5) {
                    thousandText += "lăm";
                }
                else {
                    thousandText += ones[(int)o];
                }
            } else if (t == 1) {
                thousandText += "mười ";
                if (o == 5) {
                    thousandText += "lăm";
                } else {
                    thousandText += ones[(int)o];
                }

            } else { // t == 0
                if (h > 0 || integerPart > 0) { // nếu có trăm hoặc có các đơn vị lớn hơn
                    if (o > 0) {
                        thousandText += "lẻ " + ones[(int) o];
                    }
                } else { // số chỉ có 1 hoặc 2 chữ số ở nhóm cuối
                    if (o > 0) {
                        thousandText += ones[(int)o];
                    }
                }
            }


            if (!thousandText.trim().isEmpty()) {
                result = thousandText.trim() + " " + units[unitIndex] + " " + result;
            }

            unitIndex++;
        }

        // Làm sạch kết quả
        result = result.trim();
        if (!result.isEmpty()) {
            result += " đồng";
        }


        // Capitalize first letter
        if (!result.isEmpty()) {
            result = result.substring(0, 1).toUpperCase() + result.substring(1);
        }


        return result;
    }
}