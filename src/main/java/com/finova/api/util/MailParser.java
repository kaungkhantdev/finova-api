package com.finova.api.util;

import org.springframework.stereotype.Component;

@Component
public class MailParser {

    public MailParts parse(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }

        String[] parts = email.split("@", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }

        return new MailParts(parts[0], parts[1]);
    }
}
