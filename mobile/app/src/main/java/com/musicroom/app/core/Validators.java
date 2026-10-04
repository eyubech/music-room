package com.musicroom.app.core;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/** Client-side checks for instant feedback. The backend validates everything again. */
public final class Validators {

    public static final int PASSWORD_MIN_LENGTH = 8;

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]{2,}$");
    private static final Pattern CODE = Pattern.compile("^\\d{6}$");

    private Validators() {
    }

    public static boolean isEmail(String value) {
        return value != null && EMAIL.matcher(value).matches();
    }

    /** At least 8 characters, with at least one letter and one digit. */
    public static boolean isStrongPassword(String value) {
        if (value == null || value.length() < PASSWORD_MIN_LENGTH) {
            return false;
        }
        boolean letter = false;
        boolean digit = false;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            letter |= Character.isLetter(c);
            digit |= Character.isDigit(c);
        }
        return letter && digit;
    }

    public static boolean isVerificationCode(String value) {
        return value != null && CODE.matcher(value).matches();
    }

    /** ISO date (YYYY-MM-DD) not in the future. */
    public static boolean isPastIsoDate(String value) {
        try {
            return !LocalDate.parse(value).isAfter(LocalDate.now());
        } catch (DateTimeParseException | NullPointerException e) {
            return false;
        }
    }
}
