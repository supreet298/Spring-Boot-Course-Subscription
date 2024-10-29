package com.project.course.subscription.notification;

import java.util.regex.Pattern;

public class PhoneNumberValidation {

    // Regular expression for validating phone numbers
    //private static final String PHONE_NUMBER_REGEX = "^\\+91\\d{10}$";
    private static final String PHONE_NUMBER_REGEX = "^\\+\\d{1,3}\\d{10}$";


    // Pattern for efficient matching
    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile(PHONE_NUMBER_REGEX);

    /**
     * Validates the given phone number.
     *
     * @param phoneNumber the phone number to validate
     * @return true if the phone number is valid, false otherwise
     */
    public static boolean isValid(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false;
        }
        return PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches();
    }
}