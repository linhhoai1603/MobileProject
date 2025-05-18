package com.mobile.fe_bankproject.dto;

import java.io.Serializable;
import lombok.Data;

@Data
public class AccountRegister implements Serializable {
    private String number;
    private String personalId;
    private String issueDate;
    private String placeOfIssue;
    private Address placeOfOrigin;
    private Address placeOfResidence;
    private String fullName;
    private String dateOfBirth;
    private String gender;
    private String email;
    private String phone;
    private String password1;
    private String password2;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPersonalId() {
        return personalId;
    }

    public void setPersonalId(String personalId) {
        this.personalId = personalId;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getPlaceOfIssue() {
        return placeOfIssue;
    }

    public void setPlaceOfIssue(String placeOfIssue) {
        this.placeOfIssue = placeOfIssue;
    }

    public Address getPlaceOfOrigin() {
        return placeOfOrigin;
    }

    public void setPlaceOfOrigin(Address placeOfOrigin) {
        this.placeOfOrigin = placeOfOrigin;
    }

    public Address getPlaceOfResidence() {
        return placeOfResidence;
    }

    public void setPlaceOfResidence(Address placeOfResidence) {
        this.placeOfResidence = placeOfResidence;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }
}
