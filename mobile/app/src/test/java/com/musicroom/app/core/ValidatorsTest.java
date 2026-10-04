package com.musicroom.app.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.time.LocalDate;

public class ValidatorsTest {

    @Test
    public void email() {
        assertTrue(Validators.isEmail("ayoub@student.42.fr"));
        assertFalse(Validators.isEmail("ayoub@localhost"));
        assertFalse(Validators.isEmail("ayoub student@42.fr"));
        assertFalse(Validators.isEmail(""));
        assertFalse(Validators.isEmail(null));
    }

    @Test
    public void passwordNeedsLengthLetterAndDigit() {
        assertTrue(Validators.isStrongPassword("musicr00m"));
        assertFalse(Validators.isStrongPassword("short1"));
        assertFalse(Validators.isStrongPassword("onlyletters"));
        assertFalse(Validators.isStrongPassword("12345678"));
        assertFalse(Validators.isStrongPassword(null));
    }

    @Test
    public void verificationCodeIsSixDigits() {
        assertTrue(Validators.isVerificationCode("042517"));
        assertFalse(Validators.isVerificationCode("42517"));
        assertFalse(Validators.isVerificationCode("04251a"));
    }

    @Test
    public void birthDateMustBeIsoAndNotInFuture() {
        assertTrue(Validators.isPastIsoDate("2000-02-29"));
        assertFalse(Validators.isPastIsoDate("29/02/2000"));
        assertFalse(Validators.isPastIsoDate(LocalDate.now().plusDays(1).toString()));
        assertFalse(Validators.isPastIsoDate(null));
    }
}
