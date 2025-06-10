package com.mobile.bebankproject.controller;

import com.mobile.bebankproject.dto.AccountLogin;
import com.mobile.bebankproject.dto.AccountRegister;
import com.mobile.bebankproject.dto.AccountResponse;
import com.mobile.bebankproject.dto.ChangePasswordRequest;
import com.mobile.bebankproject.dto.FundTransferRequest;
import com.mobile.bebankproject.dto.FundTransferConfirmRequest;
import com.mobile.bebankproject.model.Account;
import com.mobile.bebankproject.service.AccountService;
import com.mobile.bebankproject.util.PasswordValidator;
import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.results.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mobile.bebankproject.dto.FundTransferPreview;
import com.mobile.bebankproject.dto.UpdateProfileRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private ImageKit imageKit;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AccountRegister accountRegister) {
        accountService.createAccount(accountRegister);
        return ResponseEntity.ok("Registration successful. Please check your email for OTP to activate your account.");
    }

    @PostMapping("/login")
    public ResponseEntity<AccountResponse> login(@RequestBody AccountLogin accountLogin) {
        AccountResponse account = accountService.login(accountLogin.getPhone(), accountLogin.getPassword());
        return ResponseEntity.ok(account);
    }

    @GetMapping("/balance/{accountNumber}")
    public ResponseEntity<AccountResponse> balance(@PathVariable String accountNumber) {
        AccountResponse account = accountService.getAccount(accountNumber);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/send-otp")
    public ResponseEntity<Void> sendOTP(@RequestBody String email) {
        accountService.sendOTPToChangePassword(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/change-password")
    public ResponseEntity<Boolean> changePassword(@RequestParam String pass1, @RequestParam String pass2) {
        boolean result = accountService.changePassword(pass1, pass2);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/confirm-account")
    public ResponseEntity<String> confirmAccount(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        
        if (email == null || otp == null) {
            return ResponseEntity.badRequest().body("Email and OTP are required");
        }

        boolean result = accountService.confirmAccount(email, otp);
        if (result) {
            return ResponseEntity.ok("Account activated successfully");
        }
        return ResponseEntity.badRequest().body("Invalid OTP or account already activated");
    }

    @PostMapping("/check-otp")
    public ResponseEntity<Boolean> checkOTP(@RequestParam String otp) {
        boolean result = accountService.checkOTPToChangePassword(otp);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/list")
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        List<AccountResponse> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    @PostMapping( "/change-password-logined")
    public ResponseEntity<?> changePasswordLogined(@RequestBody ChangePasswordRequest request) {
        try {
            // Validate request data
            if (request.getAccountNumber() == null || request.getCurrentPass() == null || request.getNewPass() == null) {
                return ResponseEntity.badRequest().body("All fields are required");
            }

            // Validate new password security requirements
            if (!PasswordValidator.isValidPassword(request.getNewPass())) {
                return ResponseEntity.badRequest().body("New password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character");
            }

            // Validate current password and account status
            boolean isValid = accountService.validateAccountAndPassword(request.getAccountNumber(), request.getCurrentPass());
            if (!isValid) {
                return ResponseEntity.badRequest().body("Current password is incorrect or account is not active");
            }

            // Change password
            boolean passwordChanged = accountService.changePasswordLogined(request.getAccountNumber(), request.getNewPass());
            if (passwordChanged) {
                return ResponseEntity.ok("Password changed successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to change password");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/close")
    public ResponseEntity<?> closeAccount(@RequestBody Map<String, String> request) {
        try {
            String accountNumber = request.get("accountNumber");
            String password = request.get("password");

            if (accountNumber == null || password == null) {
                return ResponseEntity.badRequest().body("Account number and password are required");
            }

            boolean result = accountService.closeAccount(accountNumber, password);
            if (result) {
                return ResponseEntity.ok("Account closed successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to close account");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // method upload ảnh đại diện
    @PostMapping("/avatar/{accountNumber}")
    public ResponseEntity<?> uploadAvatar(
            @PathVariable String accountNumber,
            @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Please select a file to upload");
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body("Only image files are allowed");
            }

            // Validate file size (max 5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body("File size must be less than 5MB");
            }

            // Upload to ImageKit (no delete)
            byte[] fileBytes = file.getBytes();
            String base64 = java.util.Base64.getEncoder().encodeToString(fileBytes);
            String fileName = "avatar_" + accountNumber + ".jpg";

            FileCreateRequest fileCreateRequest = new FileCreateRequest(base64, fileName);
            fileCreateRequest.setFolder("/avatars");
            fileCreateRequest.setUseUniqueFileName(true);

            Result result = imageKit.upload(fileCreateRequest);
            String imageUrl = result.getUrl();

            // Update avatar URL in database
            boolean updated = accountService.updateAvatar(accountNumber, imageUrl);
            if (updated) {
                return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Avatar updated successfully",
                    "url", imageUrl
                ));
            } else {
                return ResponseEntity.badRequest().body("Failed to update avatar URL in database");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to upload image: " + e.getMessage());
        }
    }

    @PostMapping("/background/{accountNumber}")
    public ResponseEntity<?> uploadBackground(
            @PathVariable String accountNumber,
            @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Please select a file to upload");
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body("Only image files are allowed");
            }

            // Validate file size (max 10MB for background images)
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.badRequest().body("File size must be less than 10MB");
            }

            // Upload to ImageKit
            byte[] fileBytes = file.getBytes();
            String base64 = java.util.Base64.getEncoder().encodeToString(fileBytes);
            String fileName = "background_" + accountNumber + ".jpg";

            FileCreateRequest fileCreateRequest = new FileCreateRequest(base64, fileName);
            fileCreateRequest.setFolder("/backgrounds");
            fileCreateRequest.setUseUniqueFileName(true);

            Result result = imageKit.upload(fileCreateRequest);
            String imageUrl = result.getUrl();

            // Update background URL in database
            boolean updated = accountService.updateBackground(accountNumber, imageUrl);
            if (updated) {
                return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Background updated successfully",
                    "url", imageUrl
                ));
            } else {
                return ResponseEntity.badRequest().body("Failed to update background URL in database");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to upload image: " + e.getMessage());
        }
    }

//    @PostMapping("/transfer")
//    public ResponseEntity<String> transfer(@RequestBody FundTransferRequest request) {
//        boolean result = accountService.transferFund(
//            request.getFromAccountNumber(),
//            request.getToAccountNumber(),
//            request.getAmount(),
//            request.getDescription()
//        );
//        if (result) {
//            return ResponseEntity.ok("Transfer successful");
//        } else {
//            return ResponseEntity.badRequest().body("Transfer failed");
//        }
//    }

//    Gửi thông tin chuyển khoản ban đầu để hệ thống kiểm tra và trả về bản tóm tắt giao dịch trước khi user xác nhận.
    @PostMapping("/transfer/preview")
    public ResponseEntity<FundTransferPreview> previewTransfer(@RequestBody FundTransferRequest request) {
        FundTransferPreview preview = accountService.previewFundTransfer(
                request.getFromAccountNumber(),
                request.getToAccountNumber(),
                request.getAmount(),
                request.getDescription()
        );
        return ResponseEntity.ok(preview);
    }

    //    Sau khi user xác nhận thông tin ở bước preview, gọi endpoint này để hệ thống sinh OTP, lưu pending transaction và gửi email chứa OTP.
    // Endpoint cho yêu cầu gửi OTP qua Email
    @PostMapping("/transfer/request/email") // Đổi tên endpoint để rõ ràng hơn
    public ResponseEntity<String> requestEmailTransfer(@RequestBody FundTransferRequest request) {
        accountService.requestFundTransfer( // Sử dụng phương thức requestFundTransfer cũ
                request.getFromAccountNumber(),
                request.getToAccountNumber(),
                request.getAmount(),
                request.getDescription()
        );
        return ResponseEntity.ok("OTP đã được gửi về email. Vui lòng xác nhận để hoàn tất chuyển khoản.");
    }

    // Endpoint mới cho yêu cầu gửi OTP qua Firebase
    @PostMapping("/transfer/request/firebase")
    public ResponseEntity<String> requestFirebaseTransfer(@RequestBody FundTransferRequest request) {
        accountService.requestFirebaseOtp(
                request.getFromAccountNumber(),
                request.getToAccountNumber(),
                request.getAmount(),
                request.getDescription()
        );
        return ResponseEntity.ok("OTP đã được gửi qua SMS đến số điện thoại liên kết với tài khoản.");
    }

    @PostMapping("/transfer/confirm")
    public ResponseEntity<String> confirmTransfer(@RequestBody FundTransferConfirmRequest request) {
        boolean result = accountService.confirmFundTransfer(
            request.getFromAccountNumber(),
            request.getToAccountNumber(),
            request.getAmount(),
            request.getOtp()
        );
        if (result) {
            return ResponseEntity.ok("Chuyển khoản thành công!");
        } else {
            return ResponseEntity.badRequest().body("Xác nhận OTP thất bại hoặc giao dịch không hợp lệ.");
        }
    }

//   chỉ check otp, không thực hiện chuyển tiền
    @PostMapping("transfer/check-otp")
    public ResponseEntity<Boolean> checkOtpForTransfer(@RequestBody FundTransferConfirmRequest request) {
        boolean result =  accountService.checkOtpForFundTransfer(
                request.getFromAccountNumber(),
                request.getToAccountNumber(),
                request.getAmount(),
                request.getOtp()
        );
        return ResponseEntity.ok(result);
    }

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileRequest request) {
        try {
            boolean result = accountService.updateProfile(request);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Profile updated successfully"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/lookup-name")
    public ResponseEntity<?> lookupAccountName(@RequestParam String accountNumber) {
        try {
            String accountName = accountService.findAccountNameByNumber(accountNumber);
            if (accountName != null) {
                return ResponseEntity.ok(Map.of("accountName", accountName));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Account not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error looking up account", "error", e.getMessage()));
        }
    }
}
