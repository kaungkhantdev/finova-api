package com.financial.api.util;

import org.springframework.stereotype.Component;

@Component
public class EmailParser {

    public EmailParts parse(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }

        String[] parts = email.split("@", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }

        return new EmailParts(parts[0], parts[1]);
    }
}
