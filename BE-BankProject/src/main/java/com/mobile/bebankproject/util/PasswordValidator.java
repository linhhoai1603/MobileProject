package com.mobile.bebankproject.util;

public class PasswordValidator {
    public static boolean isValidPassword(String password) {
        // Password must be at least 8 characters long
        if (password == null || password.length() < 8) {
            return false;
        }

        // Check for at least one uppercase letter
        boolean hasUpper = false;
        // Check for at least one lowercase letter
        boolean hasLower = false;
        // Check for at least one number
        boolean hasNumber = false;
        // Check for at least one special character
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            if (Character.isLowerCase(c)) hasLower = true;
            if (Character.isDigit(c)) hasNumber = true;
            if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }

        return hasUpper && hasLower && hasNumber && hasSpecial;
    }
} 