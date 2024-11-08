package com.project.course.subscription.notification;

import java.util.regex.Pattern;

public class EmailValidator {

    // Regular expression for validating email addresses with specific TLDs
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+(com|org|net|edu)$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    /**
     * Validates the given email address.
     * 
     * @param email the email address to validate
     * @return true if the email is valid, false otherwise
     */
    public static boolean isValid(String email) {
        if (email == null) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static void main(String[] args) {
        // Test cases
        System.out.println(isValid("test@example.com")); // true
        System.out.println(isValid("invalid-email")); // false
        System.out.println(isValid("another.test@domain.co")); // false
        System.out.println(isValid("test@sub.domain.com")); // true
        System.out.println(isValid("test@sub.domain.edu")); // true
        System.out.println(isValid("test@sub.domain.xyz")); // false
    }
}
