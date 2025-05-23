package com.mobile.bebankproject.dto;

import com.mobile.bebankproject.model.Address;
import com.mobile.bebankproject.model.Gender;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class AccountRegister {
    // CCCD
    private String number;
    private String personalId; // đặc điểm nhận dạng
    private Date issueDate;
    private String placeOfIssue;
    private Address placeOfOrigin;
    private Address placeOfResidence;
    // User
    private String fullName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String email;
    private String phone;
    // Account
    private String password1;
    private String password2;
}
